package technology.sola.acidrain.game;

import technology.sola.acidrain.game.component.PlayerComponent;
import technology.sola.acidrain.game.event.GameStatEvent;
import technology.sola.acidrain.game.event.GameStatType;
import technology.sola.acidrain.game.rendering.RainRendererGraphicsModule;
import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.assets.graphics.gui.GuiJsonDocument;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.defaults.SolaWithDefaults;
import technology.sola.engine.graphics.guiv2.elements.SectionGuiElement;
import technology.sola.engine.graphics.guiv2.elements.TextGuiElement;
import technology.sola.engine.graphics.guiv2.style.BaseStyles;
import technology.sola.engine.graphics.guiv2.style.ConditionalStyle;
import technology.sola.engine.graphics.screen.AspectMode;
import technology.sola.acidrain.game.system.ChunkSystem;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.acidrain.game.system.CameraSystem;
import technology.sola.acidrain.game.system.PlayerSystem;
import technology.sola.acidrain.game.system.RainSystem;
import technology.sola.acidrain.game.system.GameStateSystem;

public class AcidRainSola extends SolaWithDefaults {
  public static final int CANVAS_WIDTH = 360;
  public static final int HALF_CANVAS_WIDTH = CANVAS_WIDTH / 2;
  public static final int CANVAS_HEIGHT = 240;
  public static final int HALF_CANVAS_HEIGHT = CANVAS_HEIGHT / 2;

  public AcidRainSola() {
    super(SolaConfiguration.build("Acid Rain", CANVAS_WIDTH, CANVAS_HEIGHT).withTargetUpdatesPerSecond(30));
  }

  @Override
  protected void onInit(DefaultsConfigurator defaultsConfigurator) {
    defaultsConfigurator.useGraphics().usePhysics().useGuiV2();

    GameStatistics.setEventHub(eventHub);

    // Initialize physics stuff
    solaPhysics.getGravitySystem().setActive(false);
    eventHub.add(GameStateEvent.class, gameStateEvent -> solaPhysics.getCollisionDetectionSystem().setActive(gameStateEvent.gameState() == GameState.RESTART));

    // Initialize rendering stuff
    solaGraphics.addGraphicsModules(new RainRendererGraphicsModule());
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
    platform.getRenderer().createLayers(
      Constants.Layers.FOREGROUND
    );

    // Ecs setup
    solaEcs.addSystems(
      new ChunkSystem(eventHub),
      new GameStateSystem(solaEcs, mouseInput, keyboardInput, eventHub),
      new RainSystem(),
      new CameraSystem(),
      new PlayerSystem(eventHub, keyboardInput, mouseInput, assetLoaderProvider.get(AudioClip.class))
    );
  }

  @Override
  protected void onAsyncInit(Runnable completeAsyncInit) {
    // Load assets
    new BulkAssetLoader(assetLoaderProvider)
      .addAsset(SpriteSheet.class, Constants.Assets.Sprites.SPRITE_SHEET_ID, "assets/sprites.json")
      .addAsset(AudioClip.class, Constants.Assets.Audio.GAME, "assets/Boopbooploopable.wav")
      .addAsset(AudioClip.class, Constants.Assets.Audio.QUACK, "assets/Quack.wav")
      .addAsset(GuiJsonDocument.class, Constants.Assets.Gui.IN_GAME, "assets/gui/in_game.json")
      .addAsset(GuiJsonDocument.class, Constants.Assets.Gui.GAME_OVER, "assets/gui/game_over.json")
      .loadAll()
      .onComplete(assets -> {
        if (assets[1] instanceof AudioClip audioClip) {
          audioClip.setVolume(.5f);

          audioClip.loop(AudioClip.CONTINUOUS_LOOPING);
        }

        if (assets[3] instanceof GuiJsonDocument inGameDocument) {
          if (assets[4] instanceof GuiJsonDocument gameOverDocument) {
            addGuiEventListeners(inGameDocument, gameOverDocument);
            guiDocument.setRootElement(inGameDocument.rootElement());
          }
        }

        completeAsyncInit.run();
        eventHub.emit(new GameStateEvent(GameState.RESTART));
      });
  }

  private ConditionalStyle<BaseStyles> widthStyle = ConditionalStyle.always(BaseStyles.create().setWidth("0").build());

  private void addGuiEventListeners(GuiJsonDocument inGameDocument, GuiJsonDocument gameOverDocument) {
     eventHub.add(GameStatEvent.class, event -> {
      if (event.type() == GameStatType.SUNLIGHT) {
        var newWidth = Math.round(100.0 * event.newValue() / (double) PlayerComponent.MAX_SUNLIGHT);

        if (widthStyle.style().width().getValue(100) != newWidth) {
          var styles = inGameDocument.rootElement().findElementById("sunlight", SectionGuiElement.class).getStyles();

          styles.removeStyle(widthStyle);
          widthStyle = ConditionalStyle.always(BaseStyles.create().setWidth(newWidth + "%").build());
          styles.addStyle(widthStyle);
          styles.invalidate();
        }
      }
    });

    eventHub.add(GameStateEvent.class, event -> {
      if (event.gameState() == GameState.GAME_OVER) {
        gameOverDocument.rootElement().findElementById("distance", technology.sola.engine.graphics.guiv2.elements.TextGuiElement.class)
          .setText("Distance traveled: " + Math.round(GameStatistics.getDistanceTraveled()));

        guiDocument.setRootElement(gameOverDocument.rootElement());
      } else if (event.gameState() == GameState.RESTART) {
        guiDocument.setRootElement(inGameDocument.rootElement());
      }
    });

    eventHub.add(GameStatEvent.class, event -> {
      switch (event.type()) {
        case DONUTS_EATED -> inGameDocument.rootElement().findElementById("donuts", TextGuiElement.class).setText("Donuts: " + event.newValue());
        case INTENSITY -> inGameDocument.rootElement().findElementById("intensity", TextGuiElement.class).setText("Intensity: " + event.newValue());
      }
    });
  }
}
