package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.sketchy.game.Constants;

import java.util.HashMap;
import java.util.Map;

public class ChunkSystem extends EcsSystem {
  private final Map<ChunkId, Chunk> chunkCache = new HashMap<>();
  private ChunkId lastPlayerChunkId = new ChunkId(0, 0);
  private boolean isInitialized = false;

  @Override
  public void update(World world, float v) {
    if (!isInitialized) {
      Chunk initialChunk = new Chunk(lastPlayerChunkId);

      chunkCache.put(lastPlayerChunkId, initialChunk);
      initialChunk.applyToWorld(world);
      isInitialized = true;
    }

    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
      TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

      ChunkId playerChunkId = getChunkIdForPlayer(playerTransform);
      boolean hasPlayerChunkChanged = !playerChunkId.equals(lastPlayerChunkId);

      if (hasPlayerChunkChanged) {
        Chunk chunk = chunkCache.get(playerChunkId);

        // Create chunk if not yet cached
        if (chunk == null) {
          chunk = new Chunk(playerChunkId);

          chunkCache.put(playerChunkId, chunk);
        }

        // Apply new chunk
        chunk.applyToWorld(world);

        // Cleanup entities no longer in view
        for (var view : world.createView().of(ChunkComponent.class)) {
          if (view.c1().chunkId().equals(lastPlayerChunkId)) {
            view.entity().destroy();
          }
        }
      }

      lastPlayerChunkId = playerChunkId;
    });
  }

  @Override
  public int getOrder() {
    return -50;
  }

  private ChunkId getChunkIdForPlayer(TransformComponent playerTransform) {
    int chunkWidth = Chunk.COLUMNS * Chunk.TILE_SIZE;
    int chunkHeight = Chunk.ROWS * Chunk.TILE_SIZE;
    int columnIndex = (int) Math.floor(playerTransform.getX() / chunkWidth);
    int rowIndex = (int) Math.floor(playerTransform.getY() / chunkHeight);

    return new ChunkId(columnIndex, rowIndex);
  }
}
