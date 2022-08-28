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

  private static final Random random = new Random();
  private final ChunkId chunkId;
  private final String[][] tileAssetIds = new String[COLUMNS][ROWS];

  public static Chunk create(ChunkId chunkId, int grassPercent) {
    Chunk chunk = new Chunk(chunkId);

    chunk.initialShaping(grassPercent);

    return chunk;
  }

  public void applyToWorld(World world) {
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        String spriteId = tileAssetIds[column][row];
        float x = chunkId.columnIndex() * TILE_SIZE * COLUMNS + column * TILE_SIZE;
        float y = chunkId.rowIndex() * TILE_SIZE * ROWS + row * TILE_SIZE;

        world.createEntity(
          new TileComponent(chunkId),
          new TransformComponent(x, y),
          new SpriteComponent(Constants.Assets.Sprites.ID, spriteId),
          new LayerComponent(Constants.Layers.BACKGROUND)
        );
      }
    }
  }

  private void initialShaping(int grassPercent) {
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        boolean isGrass = random.nextInt(100) <= grassPercent;

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
