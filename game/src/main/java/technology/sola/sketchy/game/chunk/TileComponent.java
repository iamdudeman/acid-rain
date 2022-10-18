package technology.sola.sketchy.game.chunk;

import technology.sola.ecs.Component;

import java.util.Random;

public class TileComponent implements Component {
  private static final Random RANDOM = new Random();
  private final ChunkId chunkId;
  private TileType tileType;
  private int wetness;
  private boolean hasPickup = false;

  public TileComponent(ChunkId chunkId, TileType tileType) {
    this.chunkId = chunkId;
    this.tileType = tileType;
    if (chunkId.rowIndex() == 0 && chunkId.columnIndex() == 0) {
      this.wetness = 0;
    } else {
      this.wetness = RANDOM.nextInt(48);
    }
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
