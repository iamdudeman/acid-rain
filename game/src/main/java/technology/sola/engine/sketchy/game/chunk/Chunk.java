package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.SpriteCache;

public class Chunk {
  public static final int TILE_SIZE = 20;
  public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
  public static final int COLUMNS = 24;
  public static final int ROWS = 16;

  private final ChunkId chunkId;
  private final TileType[][] tileTypes;
  private boolean isLoaded = false;

  public Chunk(ChunkId chunkId, TileType[][] tileTypes) {
    this.chunkId = chunkId;
    this.tileTypes = tileTypes;
  }

  public void loadChunk(World world) {
    if (isLoaded) {
      return;
    }

    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        TileType tileType = tileTypes[column][row];
        float x = chunkId.columnIndex() * TILE_SIZE * COLUMNS + column * TILE_SIZE;
        float y = chunkId.rowIndex() * TILE_SIZE * ROWS + row * TILE_SIZE;

        Entity newEntity = world.createEntity(
          new TileComponent(chunkId, tileType),
          new TransformComponent(x, y),
          new SpriteComponent(SpriteCache.get(tileType.assetId, tileType.variation)),
          new LayerComponent(Constants.Layers.BACKGROUND)
        );

        if (tileType.assetId.equals(Constants.Assets.Sprites.CLIFF) || tileType.assetId.equals(Constants.Assets.Sprites.DIRT)) {
          newEntity.addComponent(ColliderComponent.circle(Chunk.HALF_TILE_SIZE));
        }
      }
    }

    isLoaded = true;
  }

  public void unloadChunk(World world) {
    if (!isLoaded) {
      return;
    }

    for (var view : world.createView().of(TileComponent.class)) {
      if (view.c1().getChunkId().equals(chunkId)) {
        view.entity().destroy();
      }
    }

    isLoaded = false;
  }
}
