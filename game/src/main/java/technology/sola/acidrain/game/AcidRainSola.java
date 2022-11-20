package technology.sola.acidrain.game;

import technology.sola.ecs.World;
import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.Sola;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.core.module.graphics.SolaGraphics;
import technology.sola.engine.core.module.physics.SolaPhysics;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;
import technology.sola.acidrain.game.system.ChunkSystem;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.acidrain.game.system.CameraSystem;
import technology.sola.acidrain.game.system.PlayerSystem;
import technology.sola.acidrain.game.rendering.RainRenderer;
import technology.sola.acidrain.game.system.RainSystem;
import technology.sola.acidrain.game.rendering.GameUiRenderer;
import technology.sola.acidrain.game.system.GameStateSystem;

public class AcidRainSola extends Sola {
  public static final int CANVAS_WIDTH = 360;
  public static final int HALF_CANVAS_WIDTH = CANVAS_WIDTH / 2;
  public static final int CANVAS_HEIGHT = 240;
  public static final int HALF_CANVAS_HEIGHT = CANVAS_HEIGHT / 2;
  private final RainRenderer rainRenderer = new RainRenderer();
  private GameUiRenderer gameUiRenderer;
  private SolaGraphics solaGraphics;
  private SolaPhysics solaPhysics;

  public AcidRainSola() {
    super(SolaConfiguration.build("Acid Rain", CANVAS_WIDTH, CANVAS_HEIGHT).withTargetUpdatesPerSecond(30));
  }

  @Override
  protected void onInit() {
    solaInitialization.useAsyncInitialization();

    // Initialize physics stuff
    solaPhysics = SolaPhysics.createInstance(eventHub, solaEcs);
    solaPhysics.getGravitySystem().setActive(false);
    eventHub.add(GameStateEvent.class, gameStateEvent -> solaPhysics.getCollisionDetectionSystem().setActive(gameStateEvent.getMessage() == GameState.RESTART));

    // Initialize stuff for rendering
    solaGraphics = SolaGraphics.createInstance(solaEcs, platform.getRenderer(), assetLoaderProvider);
    gameUiRenderer = new GameUiRenderer(eventHub, assetLoaderProvider.get(SpriteSheet.class));
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
    platform.getRenderer().createLayers(
      "sprites",
      "rain",
      "ui"
    );
    // solaGraphics.setRenderDebug(true);

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

        solaInitialization.completeAsyncInitialization();
        eventHub.emit(new GameStateEvent(GameState.RESTART));
      });
  }

  @Override
  protected void onRender(Renderer renderer) {
    renderer.clear();

    World world = solaEcs.getWorld();

    solaGraphics.render();

    renderer.drawToLayer("rain", r -> rainRenderer.render(r, world));
    renderer.drawToLayer("ui", r -> gameUiRenderer.render(r, world));
  }
}
