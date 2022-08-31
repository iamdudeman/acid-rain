package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.Component;

public class TileComponent implements Component {
  private final ChunkId chunkId;
  private final TileType tileType;
  private int wetness;

  public TileComponent(ChunkId chunkId, TileType tileType) {
    this.chunkId = chunkId;
    this.tileType = tileType;
  }

  public ChunkId getChunkId() {
    return chunkId;
  }

  public TileType getTileType() {
    return tileType;
  }

  public int getWetness() {
    return wetness;
  }

  public void increaseWetness() {
    wetness++;
  }
}
