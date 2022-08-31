package technology.sola.engine.sketchy.game.chunk;

import technology.sola.math.linear.Vector2D;

import java.util.Random;

import static technology.sola.engine.sketchy.game.chunk.Chunk.TILE_SIZE;

public class ChunkShaper {
  private static final Random RANDOM = new Random();

  public Chunk shapeChunk(ChunkId chunkId, Vector2D playerTranslate) {
    return new Chunk(chunkId, shape(chunkId, playerTranslate));
  }

  private TileComponent[][] shape(ChunkId chunkId, Vector2D playerTranslate) {
    TileComponent[][] tileComponents = new TileComponent[Chunk.COLUMNS][Chunk.ROWS];

    // initialize to grass
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        tileComponents[column][row] = RANDOM.nextFloat() < 0.03
          ? new TileComponent(chunkId, TileType.DIRT)
          : new TileComponent(chunkId, TileType.GRASS);

        float x = chunkId.getX(column);
        float y = chunkId.getY(row);

        if (Math.abs(x - playerTranslate.x) > TILE_SIZE * 10 || Math.abs(y - playerTranslate.y) > TILE_SIZE * 10) {
          if (RANDOM.nextFloat() < 0.01) {
            tileComponents[column][row] = new TileComponent(chunkId, TileType.CLIFF_CENTER);
          }
        }
      }
    }

    return tileComponents;
  }
}
