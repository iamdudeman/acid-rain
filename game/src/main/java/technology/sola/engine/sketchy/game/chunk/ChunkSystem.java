package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.sketchy.game.Constants;

import java.util.HashMap;
import java.util.Map;

public class ChunkSystem extends EcsSystem {
  private Map<ChunkId, Chunk> chunkCache = new HashMap<>();
  private ChunkId lastPlayerChunk = null;

  @Override
  public void update(World world, float v) {
    world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
      TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

      ChunkId playerChunkId = getChunkIdForPlayer(playerTransform);
      boolean hasPlayerChunkChanged = !playerChunkId.equals(lastPlayerChunk);

      if (hasPlayerChunkChanged) {
        Chunk chunk = chunkCache.get(playerChunkId);

        // todo check if new chunk needs to be created or loaded or already is loaded
        if (chunk == null) {
          chunk = new Chunk(playerChunkId);

          chunkCache.put(playerChunkId, chunk);

          chunk.applyToWorld(world);
        }

        // todo check what chunks are in viewport and delete entities if not
      }
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

  private void clearChunksOutsideRange(World world) {

  }

  private void createOrLoadChunk(World world, ChunkId chunkId) {

  }
}
