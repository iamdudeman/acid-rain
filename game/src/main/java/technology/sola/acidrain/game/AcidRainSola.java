package technology.sola.acidrain.game;

import technology.sola.acidrain.game.rendering.RainRendererGraphicsModule;
import technology.sola.acidrain.game.rendering.gui.GuiBuilder;
import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.defaults.SolaWithDefaults;
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
    defaultsConfigurator.useGraphics().usePhysics().useGui();

    GameStatistics.setEventHub(eventHub);

    // Initialize physics stuff
    solaPhysics.getGravitySystem().setActive(false);
    eventHub.add(GameStateEvent.class, gameStateEvent -> solaPhysics.getCollisionDetectionSystem().setActive(gameStateEvent.gameState() == GameState.RESTART));

    // Initialize rendering stuff
    solaGraphics.addGraphicsModules(new RainRendererGraphicsModule());
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
    platform.getRenderer().createLayers(
      "sprites",
      "rain",
      "ui"
    );

    // Ecs setup
    ChunkSystem chunkSystem = new ChunkSystem();
    PlayerSystem playerSystem = new PlayerSystem(eventHub, keyboardInput, mouseInput, assetLoaderProvider.get(AudioClip.class));
    eventHub.add(GameStateEvent.class, chunkSystem);
    solaEcs.addSystems(
      chunkSystem,
      new GameStateSystem(solaEcs, mouseInput, keyboardInput, eventHub),
      new RainSystem(),
      new CameraSystem(),
      playerSystem
    );
  }

  @Override
  protected void onAsyncInit(Runnable completeAsyncInit) {
    // Load assets
    new BulkAssetLoader(assetLoaderProvider)
      .addAsset(SpriteSheet.class, Constants.Assets.Sprites.SPRITE_SHEET_ID, "assets/sprites.json")
      .addAsset(AudioClip.class, Constants.Assets.Audio.GAME, "assets/Boopbooploopable.wav")
      .addAsset(AudioClip.class, Constants.Assets.Audio.QUACK, "assets/Quack.wav")
      .loadAll()
      .onComplete(assets -> {
        if (assets[1] instanceof AudioClip audioClip) {
          audioClip.setVolume(.5f);

          audioClip.loop(AudioClip.CONTINUOUS_LOOPING);
        }

        completeAsyncInit.run();
        solaGuiDocument.setGuiRoot(new GuiBuilder(solaGuiDocument).getInitialRoot());
        eventHub.emit(new GameStateEvent(GameState.RESTART));
      });
  }
}
