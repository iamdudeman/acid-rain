package technology.sola.engine.sketchy.game.chunk;

import static technology.sola.engine.sketchy.game.chunk.Chunk.*;

public record ChunkId(int columnIndex, int rowIndex) {
  public float getX(int column) {
    return columnIndex * TILE_SIZE * COLUMNS + column * TILE_SIZE;
  }

  public float getY(int row) {
    return rowIndex * TILE_SIZE * ROWS + row * TILE_SIZE;
  }
}
