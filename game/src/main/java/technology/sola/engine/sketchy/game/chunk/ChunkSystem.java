package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventListener;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkSystem extends EcsSystem implements EventListener<GameStateEvent> {
  private final Map<ChunkId, Chunk> chunkCache = new HashMap<>();
  private final ChunkShaper chunkShaper = new ChunkShaper();
  private ChunkId lastPlayerChunkId = new ChunkId(0, 0);
  private boolean isInitialized = false;

  @Override
  public void update(World world, float v) {
    if (isInitialized) {
      world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
        TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);

        ChunkId playerChunkId = getChunkIdForPlayer(playerTransform);
        boolean hasPlayerChunkChanged = !playerChunkId.equals(lastPlayerChunkId);

        if (hasPlayerChunkChanged) {
          processPlayerPositionChange(world, playerChunkId);

          lastPlayerChunkId = playerChunkId;
        }
      });
    } else {
      chunkCache.clear();

      // TODO more creative chunk creation than just 85 percent grass
      Chunk initialChunk = chunkShaper.shapeChunk(lastPlayerChunkId, 85);

      chunkCache.put(lastPlayerChunkId, initialChunk);
      initialChunk.loadChunk(world);
      processPlayerPositionChange(world, lastPlayerChunkId);
      isInitialized = true;
    }
  }

  @Override
  public void onEvent(GameStateEvent gameStateEvent) {
    if (gameStateEvent.getMessage() == GameState.RESTART) {
      isInitialized = false;
    }
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

  private List<ChunkId> getSurroundingChunks(ChunkId centerChunkId) {
    return List.of(
      new ChunkId(centerChunkId.columnIndex() - 1, centerChunkId.rowIndex() - 1),
      new ChunkId(centerChunkId.columnIndex(), centerChunkId.rowIndex() - 1),
      new ChunkId(centerChunkId.columnIndex() + 1, centerChunkId.rowIndex() - 1),
      new ChunkId(centerChunkId.columnIndex() - 1, centerChunkId.rowIndex()),
      centerChunkId,
      new ChunkId(centerChunkId.columnIndex() + 1, centerChunkId.rowIndex()),
      new ChunkId(centerChunkId.columnIndex() - 1, centerChunkId.rowIndex() + 1),
      new ChunkId(centerChunkId.columnIndex(), centerChunkId.rowIndex() + 1),
      new ChunkId(centerChunkId.columnIndex() + 1, centerChunkId.rowIndex() + 1)
    );
  }

  private void processPlayerPositionChange(World world, ChunkId playerChunkId) {
    List<ChunkId> loadChunks = getSurroundingChunks(playerChunkId);
    List<ChunkId> unloadChunks = getSurroundingChunks(lastPlayerChunkId)
      .stream()
      .filter(chunkId -> !loadChunks.contains(chunkId))
      .toList();

    for (ChunkId chunkId : unloadChunks) {
      Chunk chunk = chunkCache.get(chunkId);

      if (chunk != null) {
        chunk.unloadChunk(world);
      }
    }

    for (ChunkId chunkId : loadChunks) {
      loadChunk(world, chunkId);
    }
  }

  private void loadChunk(World world, ChunkId chunkId) {
    Chunk chunk = chunkCache.get(chunkId);

    if (chunk == null) {
      // TODO more creative chunk creation than just 50 percent grass
      chunk = chunkShaper.shapeChunk(chunkId, 50);

      chunkCache.put(chunkId, chunk);
    }

    chunk.loadChunk(world);
  }
}
