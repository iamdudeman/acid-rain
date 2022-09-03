package technology.sola.engine.sketchy.game;

import technology.sola.ecs.World;
import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.Sola;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.core.module.graphics.gui.SolaGui;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;
import technology.sola.engine.sketchy.game.chunk.ChunkSystem;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.engine.sketchy.game.gui.MainMenuGui;
import technology.sola.engine.sketchy.game.player.CameraSystem;
import technology.sola.engine.sketchy.game.player.PlayerCollisionDetectionSystem;
import technology.sola.engine.sketchy.game.player.PlayerSystem;
import technology.sola.engine.sketchy.game.rain.RainRenderer;
import technology.sola.engine.sketchy.game.rain.RainSystem;
import technology.sola.engine.sketchy.game.state.GameUiRenderer;
import technology.sola.engine.sketchy.game.state.GameStateSystem;

public class SketchyLifeSola extends Sola {
  public static final int CANVAS_WIDTH = 480;
  public static final int HALF_CANVAS_WIDTH = CANVAS_WIDTH / 2;
  public static final int CANVAS_HEIGHT = 320;
  public static final int HALF_CANVAS_HEIGHT = CANVAS_HEIGHT / 2;
  private final RainRenderer rainRenderer = new RainRenderer();
  private GameSettings gameSettings;
  private SolaGui solaGui;
  private GameUiRenderer gameUiRenderer;
  private SketchyLifeRenderer sketchyLifeRenderer;

  @Override
  protected SolaConfiguration getConfiguration() {
    return new SolaConfiguration("Sketchy Life", CANVAS_WIDTH, CANVAS_HEIGHT, 30, false);
  }

  @Override
  protected void onInit() {
    solaInitialization.useAsyncInitialization();
    gameSettings = new GameSettings(solaEcs);

    // Initialize stuff for rendering
    sketchyLifeRenderer = new SketchyLifeRenderer(assetLoaderProvider.get(SpriteSheet.class));
    gameUiRenderer = new GameUiRenderer(eventHub);
    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);

    // Ecs setup
    ChunkSystem chunkSystem = new ChunkSystem();
    PlayerSystem playerSystem = new PlayerSystem(eventHub, keyboardInput, mouseInput, assetLoaderProvider.get(AudioClip.class));
    PlayerCollisionDetectionSystem collisionDetectionSystem = new PlayerCollisionDetectionSystem(eventHub);
    eventHub.add(chunkSystem, GameStateEvent.class);
    solaEcs.addSystems(
      chunkSystem,
      new GameStateSystem(solaEcs, mouseInput, eventHub),
      new RainSystem(),
      new CameraSystem(),
      playerSystem,
      collisionDetectionSystem
    );
    eventHub.emit(new GameStateEvent(GameState.RESTART));
    eventHub.add(gameStateEvent -> collisionDetectionSystem.setActive(gameStateEvent.getMessage() == GameState.RESTART), GameStateEvent.class);

    // Initialize gui stuff
    solaGui = SolaGui.createInstance(assetLoaderProvider, platform);
    solaGui.setGuiRoot(MainMenuGui.buildRootElement(solaGui, gameSettings));

    // Load assets
    new BulkAssetLoader(assetLoaderProvider)
      .addAsset(SpriteSheet.class, Constants.Assets.Sprites.SPRITE_SHEET_ID, "assets/sprites.json")
      .addAsset(AudioClip.class, Constants.Assets.Audio.GAME, "assets/Boopbooploopable.wav")
      .addAsset(AudioClip.class, Constants.Assets.Audio.QUACK, "assets/Quack.wav")
      .loadAll()
      .onComplete(assets -> {
        // TODO play menu music first
        if (assets[1] instanceof AudioClip audioClip) {
          audioClip.setVolume(.5f);

          audioClip.loop(-1);
        }

        // TODO temporarily not showing the menu first
        // gameSettings.showMenu();

        // TODO consider some sort of game loading splash screen state of some sort?
        solaInitialization.completeAsyncInitialization();
      });
  }

  @Override
  protected void onRender(Renderer renderer) {
    renderer.clear();

    if (gameSettings.isPlaying()) {
      World world = solaEcs.getWorld();
      sketchyLifeRenderer.render(renderer, world);
      rainRenderer.render(renderer, world);
      gameUiRenderer.render(renderer, world);
    }

    if (gameSettings.isShowMenu()) {
      solaGui.render();
    }
  }
}
