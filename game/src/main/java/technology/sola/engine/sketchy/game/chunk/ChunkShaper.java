package technology.sola.engine.sketchy.game.chunk;

import java.util.Random;

public class ChunkShaper {
  private static final Random RANDOM = new Random();

  public Chunk shapeChunk(ChunkId chunkId, int grassPercent) {
    return new Chunk(chunkId, randomShaping(chunkId, grassPercent));
  }

  private TileComponent[][] randomShaping(ChunkId chunkId, int grassPercent) {
    TileComponent[][] tileComponents = new TileComponent[Chunk.COLUMNS][Chunk.ROWS];

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        int value = RANDOM.nextInt(100);

        if (value < 10) {
          tileComponents[column][row] = new TileComponent(chunkId, TileType.CLIFF_CENTER);
        } else if (value < grassPercent) {
          boolean spawnPickup = RANDOM.nextInt(100) < 10;

          tileComponents[column][row] = new TileComponent(chunkId, TileType.GRASS, spawnPickup);
        } else {
          tileComponents[column][row] = new TileComponent(chunkId, TileType.DIRT);
        }
      }
    }

    return tileComponents;
  }
}
