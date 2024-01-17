package technology.sola.acidrain.game.system;

import technology.sola.acidrain.game.system.chunk.Chunk;
import technology.sola.acidrain.game.system.chunk.ChunkCreator;
import technology.sola.acidrain.game.system.chunk.ChunkId;
import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.event.EventListener;
import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.math.linear.Vector2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkSystem extends EcsSystem implements EventListener<GameStateEvent> {
  private final Map<ChunkId, Chunk> chunkCache = new HashMap<>();
  private final ChunkCreator chunkCreator = new ChunkCreator();
  private ChunkId lastPlayerChunkId = new ChunkId(0, 0);
  private boolean isInitialized = false;

  public ChunkSystem(EventHub eventHub) {
    eventHub.add(GameStateEvent.class, this);
  }

  @Override
  public void update(World world, float v) {
    Entity playerEntity = world.findEntityByName(Constants.EntityNames.PLAYER);

    if (playerEntity != null) {
      TransformComponent playerTransform = playerEntity.getComponent(TransformComponent.class);
      Vector2D playerTranslate = playerTransform.getTranslate();

      if (isInitialized) {
        ChunkId playerChunkId = getChunkIdForPlayer(playerTransform);
        boolean hasPlayerChunkChanged = !playerChunkId.equals(lastPlayerChunkId);

        if (hasPlayerChunkChanged) {
          processPlayerPositionChange(world, playerChunkId, playerTranslate);

          lastPlayerChunkId = playerChunkId;
        }
      } else {
        chunkCache.clear();
        processPlayerPositionChange(world, lastPlayerChunkId, playerTranslate);
        isInitialized = true;
      }
    }
  }

  @Override
  public void onEvent(GameStateEvent gameStateEvent) {
    if (gameStateEvent.gameState() == GameState.RESTART) {
      lastPlayerChunkId = new ChunkId(0, 0);
      isInitialized = false;
      setActive(true);
    } else if (gameStateEvent.gameState() == GameState.GAME_OVER) {
      setActive(false);
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

  private void processPlayerPositionChange(World world, ChunkId playerChunkId, Vector2D playerTranslate) {
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
      loadChunk(world, chunkId, playerTranslate);
    }
  }

  private void loadChunk(World world, ChunkId chunkId, Vector2D playerTranslate) {
    Chunk chunk = chunkCache.get(chunkId);

    if (chunk == null) {
      chunk = chunkCreator.createChunk(chunkId, playerTranslate);

      chunkCache.put(chunkId, chunk);
    }

    chunk.loadChunk(world);
  }
}
