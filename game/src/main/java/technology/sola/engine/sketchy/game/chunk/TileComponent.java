package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.Component;

public class TileComponent implements Component {
  private final ChunkId chunkId;
  private int wetness;

  public TileComponent(ChunkId chunkId) {
    this.chunkId = chunkId;
  }

  public ChunkId getChunkId() {
    return chunkId;
  }

  public int getWetness() {
    return wetness;
  }

  public void increaseWetness() {
    wetness++;
  }
}
