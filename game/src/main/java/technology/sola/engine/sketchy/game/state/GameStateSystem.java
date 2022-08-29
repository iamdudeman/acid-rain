package technology.sola.engine.sketchy.game.state;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.SolaEcs;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.event.EventListener;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.components.CameraComponent;
import technology.sola.engine.graphics.components.CircleRendererComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.input.MouseButton;
import technology.sola.engine.input.MouseInput;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.physics.event.CollisionManifoldEvent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.chunk.Chunk;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.math.linear.Vector2D;

public class GameStateSystem extends EcsSystem implements EventListener<GameStateEvent> {
  private final SolaEcs solaEcs;
  private final MouseInput mouseInput;
  private final EventHub eventHub;
  private final float rendererHalfWidth;
  private final float rendererHalfHeight;
  private boolean shouldRemovePlayer = false;
  private boolean isGameOver = false;

  public GameStateSystem(SolaEcs solaEcs, MouseInput mouseInput, EventHub eventHub, int rendererWidth, int rendererHeight) {
    this.solaEcs = solaEcs;
    this.mouseInput = mouseInput;
    this.eventHub = eventHub;
    this.rendererHalfWidth = rendererWidth / 2f;
    this.rendererHalfHeight = rendererHeight / 2f;

    eventHub.add(this, GameStateEvent.class);

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
        if (Math.abs(collisionManifoldEvent.getMessage().penetration()) > 5) {
          Vector2D playerTranslate = player.getComponent(TransformComponent.class).getTranslate();
          float distanceX = Math.abs(playerTranslate.x - rendererHalfWidth);
          float distanceY = Math.abs(playerTranslate.y - rendererHalfHeight);
          float score = (distanceX / Chunk.TILE_SIZE) + (distanceY / Chunk.TILE_SIZE);

          eventHub.emit(new GameStateEvent(GameState.GAME_OVER, score));
        }
      }
    ), CollisionManifoldEvent.class);
  }

  // todo this whole logic is awful, clean it up!
  @Override
  public void update(World world, float v) {
    if (shouldRemovePlayer) {
      world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(Entity::destroy);
      shouldRemovePlayer = false;
    }

    if (isGameOver && mouseInput.isMouseClicked(MouseButton.PRIMARY)) {
      eventHub.emit(new GameStateEvent(GameState.RESTART));
      isGameOver = false;
    }
  }

  @Override
  public void onEvent(GameStateEvent gameStateEvent) {
    if (gameStateEvent.getMessage() == GameState.GAME_OVER) {
      shouldRemovePlayer = true;
      isGameOver = true;
    } else if (gameStateEvent.getMessage() == GameState.RESTART) {
      shouldRemovePlayer = false;
      isGameOver = false;
      solaEcs.setWorld(buildWorld());
    }
  }

  @Override
  public int getOrder() {
    return -500;
  }

  private World buildWorld() {
    World world = new World(10000);

    world.createEntity(
      new TransformComponent(rendererHalfWidth, rendererHalfHeight, 15),
      new CircleRendererComponent(Color.RED, true),
      new LayerComponent(Constants.Layers.FOREGROUND),
      ColliderComponent.circle()
    ).setName(Constants.EntityNames.PLAYER);

    world.createEntity(
      new TransformComponent(),
      new CameraComponent()
    ).setName(Constants.EntityNames.CAMERA);

    return world;
  }
}
