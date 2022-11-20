package technology.sola.acidrain.game.system;

import technology.sola.acidrain.game.GameStatistics;
import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.SolaEcs;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.components.BlendModeComponent;
import technology.sola.engine.graphics.components.CameraComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
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

public class GameStateSystem extends EcsSystem {
  private final MouseInput mouseInput;
  private final KeyboardInput keyboardInput;
  private final EventHub eventHub;
  private float allowRestartCounter = 0;

  public GameStateSystem(SolaEcs solaEcs, MouseInput mouseInput, KeyboardInput keyboardInput, EventHub eventHub) {
    this.mouseInput = mouseInput;
    this.keyboardInput = keyboardInput;
    this.eventHub = eventHub;

    eventHub.add(GameStateEvent.class, gameStateEvent -> {
      if (gameStateEvent.getMessage() == GameState.GAME_OVER) {
        setActive(true);
        solaEcs.getWorld().findEntityByName(Constants.EntityNames.PLAYER).ifPresent(Entity::destroy);
        solaEcs.getWorld().findEntitiesWithComponents(PickupComponent.class).forEach(Entity::destroy);
      } else if (gameStateEvent.getMessage() == GameState.RESTART) {
        allowRestartCounter = 0;
        setActive(false);
        GameStatistics.reset();
        solaEcs.setWorld(buildWorld());
      }
    });
  }

  @Override
  public void update(World world, float dt) {
    if (allowRestartCounter > 1) {
      if (mouseInput.isMouseClicked(MouseButton.PRIMARY) || keyboardInput.isKeyPressed(Key.SPACE)) {
        eventHub.emit(new GameStateEvent(GameState.RESTART));
      }
    } else {
      allowRestartCounter += dt;
    }
  }

  @Override
  public int getOrder() {
    return 500;
  }

  private World buildWorld() {
    World world = new World(10000);

    world.createEntity(
      new TransformComponent(AcidRainSola.HALF_CANVAS_WIDTH, AcidRainSola.HALF_CANVAS_HEIGHT),
      new SpriteComponent(SpriteCache.get(Constants.Assets.Sprites.DUCK, "top")),
      new PlayerComponent(),
//      new DynamicBodyComponent(), // TODO reenable this when collider for dirt can be a "trigger"
      new LayerComponent("sprites", -1),
      new BlendModeComponent(BlendMode.MASK),
      ColliderComponent.circle(-2, 0, 6)
    ).setName(Constants.EntityNames.PLAYER);

    world.createEntity(
      new TransformComponent(),
      new CameraComponent()
    ).setName(Constants.EntityNames.CAMERA);

    return world;
  }
}
