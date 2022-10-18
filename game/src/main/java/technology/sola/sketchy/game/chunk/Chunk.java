package technology.sola.sketchy.game.chunk;

import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.sketchy.game.Constants;
import technology.sola.sketchy.game.SpriteCache;
import technology.sola.sketchy.game.player.PickupComponent;

public class Chunk {
  public static final int TILE_SIZE = 20;
  public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
  public static final int COLUMNS = 24;
  public static final int ROWS = 16;

  private final ChunkId chunkId;
  private final TileComponent[][] tileComponents;
  private boolean isLoaded = false;

  public Chunk(ChunkId chunkId, TileComponent[][] tileComponents) {
    this.chunkId = chunkId;
    this.tileComponents = tileComponents;
  }

  public void loadChunk(World world) {
    if (isLoaded) {
      return;
    }

    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];
        TileType tileType = tileComponent.getTileType();
        float x = chunkId.getX(column);
        float y = chunkId.getY(row);

        Entity newEntity = world.createEntity(
          tileComponent,
          new TransformComponent(x, y),
          new SpriteComponent(SpriteCache.get(tileType.assetId, tileType.variation))
        );

        if (tileType.assetId.equals(Constants.Assets.Sprites.CLIFF)) {
          newEntity.addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
        }

        if (tileComponent.hasPickup()) {
          world.createEntity(
            new TransformComponent(x + 6, y + 6),
            new PickupComponent(tileComponent),
            new SpriteComponent(SpriteCache.get(Constants.Assets.Sprites.DONUT, "main")),
            ColliderComponent.circle(3)
          );
        }
      }
    }

    isLoaded = true;
  }

  public void unloadChunk(World world) {
    if (!isLoaded) {
      return;
    }

    // TODO (minor bug) should probably cleanup any pickups that were not collected as well
    for (var view : world.createView().of(TileComponent.class)) {
      if (view.c1().getChunkId().equals(chunkId)) {
        view.entity().destroy();
      }
    }

    isLoaded = false;
  }
}
