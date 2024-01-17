package technology.sola.acidrain.game.system;

import technology.sola.acidrain.game.GameStatistics;
import technology.sola.acidrain.game.component.RainCloudComponent;
import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.SolaEcs;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.components.BlendModeComponent;
import technology.sola.engine.graphics.components.CameraComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.components.SpriteComponent;
import technology.sola.engine.graphics.components.animation.TransformAnimatorComponent;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.input.MouseButton;
import technology.sola.engine.input.MouseInput;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.SpriteCache;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.acidrain.game.component.PickupComponent;
import technology.sola.acidrain.game.component.PlayerComponent;
import technology.sola.engine.physics.component.DynamicBodyComponent;
import technology.sola.math.EasingFunction;

public class GameStateSystem extends EcsSystem {
  private static final long fallingAnimationDuration = 1000;
  private final MouseInput mouseInput;
  private final KeyboardInput keyboardInput;
  private final EventHub eventHub;

  public GameStateSystem(SolaEcs solaEcs, MouseInput mouseInput, KeyboardInput keyboardInput, EventHub eventHub) {
    this.mouseInput = mouseInput;
    this.keyboardInput = keyboardInput;
    this.eventHub = eventHub;

    eventHub.add(GameStateEvent.class, gameStateEvent -> {
      if (gameStateEvent.gameState() == GameState.GAME_OVER) {
        setActive(true);
        solaEcs.getWorld().findEntitiesWithComponents(PickupComponent.class).forEach(Entity::destroy);
      } else if (gameStateEvent.gameState() == GameState.RESTART) {
        setActive(false);
        GameStatistics.reset();
        solaEcs.setWorld(buildWorld());
      }
    });
  }

  @Override
  public void update(World world, float dt) {
    Entity playerEntity = world.findEntityByName(Constants.EntityNames.PLAYER);

    if (playerEntity == null) {
      if (mouseInput.isMouseClicked(MouseButton.PRIMARY) || keyboardInput.isKeyPressed(Key.SPACE)) {
        eventHub.emit(new GameStateEvent(GameState.RESTART));
      }
    } else {
      if (!playerEntity.hasComponent(TransformAnimatorComponent.class)) {
        playerEntity.addComponent(
          new TransformAnimatorComponent.Builder(EasingFunction.EASE_OUT, fallingAnimationDuration).withScale(0.01f).build()
            .setAnimationCompleteCallback(playerEntity::destroy)
        );
      }
    }
  }

  @Override
  public int getOrder() {
    return 500;
  }

  private World buildWorld() {
    World world = new World(3500);

    world.createEntity(
      new TransformComponent(AcidRainSola.HALF_CANVAS_WIDTH, AcidRainSola.HALF_CANVAS_HEIGHT),
      new SpriteComponent(SpriteCache.get(Constants.Assets.Sprites.DUCK, "top")),
      new PlayerComponent(),
      new DynamicBodyComponent(),
      new LayerComponent(Constants.Layers.FOREGROUND),
      new BlendModeComponent(BlendMode.MASK),
      ColliderComponent.circle(-2, 0, 6)
    ).setName(Constants.EntityNames.PLAYER);

    world.createEntity(
      new TransformComponent(),
      new RainCloudComponent()
    ).setName(Constants.EntityNames.RAIN);

    world.createEntity(
      new TransformComponent(),
      new CameraComponent()
    ).setName(Constants.EntityNames.CAMERA);

    return world;
  }
}
