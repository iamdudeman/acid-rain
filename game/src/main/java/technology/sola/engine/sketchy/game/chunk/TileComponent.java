package technology.sola.engine.sketchy.game.chunk;

import technology.sola.ecs.Component;

public class TileComponent implements Component {
  private final ChunkId chunkId;
  private TileType tileType;
  private int wetness;
  private boolean hasPickup = false;

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

  public void setTileType(TileType tileType) {
    this.tileType = tileType;
  }

  public void setHasPickup(boolean hasPickup) {
    this.hasPickup = hasPickup;
  }

  public boolean hasPickup() {
    return hasPickup;
  }

  public void consumePickup() {
    hasPickup = false;
  }

  public int getWetness() {
    return wetness;
  }

  public void increaseWetness() {
    wetness++;
  }
}
