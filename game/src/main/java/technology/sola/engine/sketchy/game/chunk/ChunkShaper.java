package technology.sola.engine.sketchy.game.chunk;

import java.util.Random;

public class ChunkShaper {
  private static final Random RANDOM = new Random();

  public Chunk shapeChunk(ChunkId chunkId, int grassPercent) {
    return new Chunk(chunkId, randomShaping(grassPercent));
  }

  private TileType[][] randomShaping(int grassPercent) {
    TileType[][] tileTypes = new TileType[Chunk.COLUMNS][Chunk.ROWS];

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        int value = RANDOM.nextInt(100);

        if (value < 10) {
          tileTypes[column][row] = TileType.CLIFF_CENTER;
        } else if (value < grassPercent) {
          tileTypes[column][row] = TileType.GRASS;
        } else {
          tileTypes[column][row] = TileType.DIRT;
        }
      }
    }

    return tileTypes;
  }
}
