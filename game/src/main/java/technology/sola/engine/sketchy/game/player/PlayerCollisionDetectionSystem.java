package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.physics.CollisionManifold;
import technology.sola.engine.physics.CollisionUtils;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.physics.system.PhysicsSystem;
import technology.sola.engine.sketchy.game.Constants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerCollisionDetectionSystem extends EcsSystem {
  public static final int ORDER = PhysicsSystem.ORDER + 1;
  private final EventHub eventHub;

  public PlayerCollisionDetectionSystem(EventHub eventHub) {
    this.eventHub = eventHub;
  }

  @Override
  public int getOrder() {
    return ORDER;
  }

  @Override
  public void update(World world, float deltaTime) {
    Set<CollisionManifold> collisionEventsThisIteration = new HashSet<>();
    List<Entity> entities = world.findEntitiesWithComponents(ColliderComponent.class, TransformComponent.class);

    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
      TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);
      ColliderComponent colliderComponent = playerEntity.getComponent(ColliderComponent.class);

      for (Entity entity : entities) {
        if (entity == playerEntity) {
          continue;
        }

        TransformComponent transformB = entity.getComponent(TransformComponent.class);

        if (playerTransform.getX() + 50 < transformB.getX() || playerTransform.getX() - 50 > transformB.getX()) {
          continue;
        }

        if (playerTransform.getY() + 50 < transformB.getY() || playerTransform.getY() - 50 > transformB.getY()) {
          continue;
        }

        ColliderComponent colliderB = entity.getComponent(ColliderComponent.class);

        CollisionManifold collisionManifoldEvent = CollisionUtils.calculateCollisionManifold(
          playerEntity, entity,
          playerTransform, transformB,
          colliderComponent, colliderB
        );

        if (collisionManifoldEvent != null) {
          collisionEventsThisIteration.add(collisionManifoldEvent);
        }
      }
    });

    // By emitting only events from the set we do not send duplicates
    collisionEventsThisIteration.forEach(collisionManifold -> eventHub.emit(new CollisionManifoldEvent(collisionManifold)));
  }
}
