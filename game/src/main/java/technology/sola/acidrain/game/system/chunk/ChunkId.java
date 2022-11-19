package technology.sola.acidrain.game.system.chunk;

public record ChunkId(int columnIndex, int rowIndex) {
  public float getX(int column) {
    return columnIndex * Chunk.TILE_SIZE * Chunk.COLUMNS + column * Chunk.TILE_SIZE;
  }

  public float getY(int row) {
    return rowIndex * Chunk.TILE_SIZE * Chunk.ROWS + row * Chunk.TILE_SIZE;
  }
}
