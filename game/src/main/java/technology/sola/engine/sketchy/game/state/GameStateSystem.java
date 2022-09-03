package technology.sola.engine.sketchy.game.state;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.SolaEcs;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.components.CameraComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.input.Key;
import technology.sola.engine.input.KeyboardInput;
import technology.sola.engine.input.MouseButton;
import technology.sola.engine.input.MouseInput;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.AcidRainSola;
import technology.sola.engine.sketchy.game.SpriteCache;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.engine.sketchy.game.player.PickupComponent;
import technology.sola.engine.sketchy.game.player.PlayerComponent;
import technology.sola.math.linear.Vector2D;

public class GameStateSystem extends EcsSystem {
  private final MouseInput mouseInput;
  private final KeyboardInput keyboardInput;
  private final EventHub eventHub;

  public GameStateSystem(SolaEcs solaEcs, MouseInput mouseInput, KeyboardInput keyboardInput, EventHub eventHub) {
    this.mouseInput = mouseInput;
    this.keyboardInput = keyboardInput;
    this.eventHub = eventHub;

    eventHub.add(gameStateEvent -> {
      if (gameStateEvent.getMessage() == GameState.GAME_OVER) {
        setActive(true);
        solaEcs.getWorld().findEntityByName(Constants.EntityNames.PLAYER).ifPresent(Entity::destroy);
        solaEcs.getWorld().findEntitiesWithComponents(PickupComponent.class).forEach(Entity::destroy);
      } else if (gameStateEvent.getMessage() == GameState.RESTART) {
        setActive(false);
        solaEcs.setWorld(buildWorld());
      }
    }, GameStateEvent.class);

    eventHub.add(collisionManifoldEvent -> collisionManifoldEvent.getMessage().conditionallyResolveCollision(
      entity -> Constants.EntityNames.PLAYER.equals(entity.getName()),
      entity -> {
        SpriteComponent spriteComponent = entity.getComponent(SpriteComponent.class);
        if (spriteComponent != null) {
          return spriteComponent.getSpriteId().equals(Constants.Assets.Sprites.ERASED);
        }
        return false;
      },
      (player, erasedTile) -> {
        Vector2D playerTranslate = player.getComponent(TransformComponent.class).getTranslate();
        int donutsConsumed = player.getComponent(PlayerComponent.class).getDonutsConsumed();
        // todo this is really hacky, clean up later
        Vector2D playerTranslateForFallAnimation = playerTranslate.subtract(solaEcs.getWorld().findEntityByName(Constants.EntityNames.CAMERA).get().getComponent(TransformComponent.class).getTranslate());

        eventHub.emit(new GameStateEvent(
          GameState.GAME_OVER,
          playerTranslate.subtract(new Vector2D(AcidRainSola.HALF_CANVAS_WIDTH, AcidRainSola.HALF_CANVAS_HEIGHT)).magnitude(),
          donutsConsumed,
          playerTranslateForFallAnimation,
          player.getComponent(SpriteComponent.class).getSpriteId()
        ));
      }
    ), CollisionManifoldEvent.class);
  }

  @Override
  public void update(World world, float v) {
    if (mouseInput.isMouseClicked(MouseButton.PRIMARY) || keyboardInput.isKeyPressed(Key.SPACE)) {
      eventHub.emit(new GameStateEvent(GameState.RESTART));
    }
  }

  @Override
  public int getOrder() {
    return -500;
  }

  private World buildWorld() {
    World world = new World(10000);

    world.createEntity(
      new TransformComponent(AcidRainSola.HALF_CANVAS_WIDTH, AcidRainSola.HALF_CANVAS_HEIGHT),
      new SpriteComponent(SpriteCache.get(Constants.Assets.Sprites.DUCK, "top")),
      new PlayerComponent(),
      ColliderComponent.circle(5)
    ).setName(Constants.EntityNames.PLAYER);

    world.createEntity(
      new TransformComponent(),
      new CameraComponent()
    ).setName(Constants.EntityNames.CAMERA);

    return world;
  }
}
