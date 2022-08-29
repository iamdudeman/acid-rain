package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.sketchy.game.Constants;

import java.util.Random;

public class Chunk {
  public static final int TILE_SIZE = 20;
  public static final int COLUMNS = 24;
  public static final int ROWS = 16;

  private static final Random RANDOM = new Random();
  private final ChunkId chunkId;
  private final String[][] tileAssetIds = new String[COLUMNS][ROWS];
  private boolean isLoaded = false;

  public static Chunk create(ChunkId chunkId, int grassPercent) {
    Chunk chunk = new Chunk(chunkId);

    chunk.initialShaping(grassPercent);

    return chunk;
  }

  public void loadChunk(World world) {
    if (isLoaded) {
      return;
    }

    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        String spriteId = tileAssetIds[column][row];
        float x = chunkId.columnIndex() * TILE_SIZE * COLUMNS + column * TILE_SIZE;
        float y = chunkId.rowIndex() * TILE_SIZE * ROWS + row * TILE_SIZE;

        world.createEntity(
          new TileComponent(chunkId),
          new TransformComponent(x, y),
          new SpriteComponent(Constants.Assets.Sprites.SPRITE_SHEET_ID, spriteId),
          new LayerComponent(Constants.Layers.BACKGROUND)
        );
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

  private void initialShaping(int grassPercent) {
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        boolean isGrass = RANDOM.nextInt(100) <= grassPercent;

        tileAssetIds[column][row] = isGrass
          ? Constants.Assets.Sprites.GRASS + "-1"
          : Constants.Assets.Sprites.DIRT + "-1";
      }
    }
  }

  private Chunk(ChunkId chunkId) {
    this.chunkId = chunkId;
  }
}
