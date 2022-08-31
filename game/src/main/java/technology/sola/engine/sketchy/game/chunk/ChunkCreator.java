package technology.sola.engine.sketchy.game.chunk;

import technology.sola.math.linear.Vector2D;

import java.util.Random;

import static technology.sola.engine.sketchy.game.chunk.Chunk.TILE_SIZE;

public class ChunkCreator {
  // TODO don't commit this random seed
  private static final Random RANDOM = new Random(1337);
  private static final int cultureGenerations = 5;
  private static final int joinCliffsTilesAway = 2;
  private static final int minCliffTilesFromPlayer = 10;
  private static final float baseInitCliffPercent = 0.01f;
  private static final float baseInitDirtPercent = 0.03f;
  private static final float baseCultureCliffPercent = 0.10f;
  private static final float baseCultureCliffClearPercent = 0.05f;
  private static final float baseCultureDirtPercent = 0.03f;
  private static final float baseCultureDirtClearPercent = 0.05f;

  public Chunk createChunk(ChunkId chunkId, Vector2D playerTranslate) {
    TileComponent[][] tileComponents = new TileComponent[Chunk.COLUMNS][Chunk.ROWS];

    stepInitialize(chunkId, playerTranslate, tileComponents);
    stepCulture(tileComponents, 0);
    stepShapeCliffs(tileComponents);
    stepPlacePickups(tileComponents);
    stepTextureGrassAndDirt(tileComponents);
    stepCleanup(tileComponents);

    return new Chunk(chunkId, tileComponents);
  }

  private void stepInitialize(ChunkId chunkId, Vector2D playerTranslate, TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileType initialTileType;

        initialTileType = RANDOM.nextFloat() < baseInitDirtPercent ? TileType.DIRT : TileType.GRASS;

        float x = chunkId.getX(column);
        float y = chunkId.getY(row);

        if (Math.abs(x - playerTranslate.x) > TILE_SIZE * minCliffTilesFromPlayer || Math.abs(y - playerTranslate.y) > TILE_SIZE * minCliffTilesFromPlayer) {
          if (RANDOM.nextFloat() < baseInitCliffPercent) {
            initialTileType = TileType.CLIFF_CENTER;
          }
        }

        tileComponents[column][row] = new TileComponent(chunkId, initialTileType);
      }
    }
  }

  private void stepCulture(TileComponent[][] tileComponents, int depth) {
    if (depth > cultureGenerations - 1) {
      return;
    }

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];
        TileType tileType = tileComponent.getTileType();

        if (tileType == TileType.CLIFF_CENTER) {
          float value = RANDOM.nextFloat();

          if (value < baseCultureCliffPercent) {
            propagateTileType(tileComponents, row, column, TileType.CLIFF_CENTER);
          } else if (value < baseCultureCliffPercent + baseCultureCliffClearPercent) {
            tileComponent.setTileType(TileType.GRASS);
          }
        }

        if (tileType == TileType.DIRT) {
          float value = RANDOM.nextFloat();

          if (value < baseCultureDirtPercent) {
            propagateTileType(tileComponents, row, column, TileType.DIRT);
          } else if (value < baseCultureDirtPercent + baseCultureDirtClearPercent) {
            tileComponent.setTileType(TileType.GRASS);
          }
        }
      }
    }

    stepCulture(tileComponents, depth + 1);
  }

  private void stepShapeCliffs(TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          joinNearbyCliffs(tileComponents, row, column);
        }
      }
    }

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          if (!shapeCliff(tileComponents, row, column)) {
            tileComponent.setTileType(TileType.GRASS);
          }
        }
      }
    }
  }

  private void stepPlacePickups(TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        // todo
      }
    }
  }

  private void stepTextureGrassAndDirt(TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        // todo
      }
    }
  }

  private void stepCleanup(TileComponent[][] tileComponents) {
    // todo this might not be needed
  }

  private void propagateTileType(TileComponent[][] tileComponents, int startRow, int startColumn, TileType tileType) {
    int direction = RANDOM.nextInt(4); // up, down, left, right
    int rowOffset = direction == 0 ? -1 : direction == 1 ? 1 : 0; // 0 -> -1, 1 -> 1, else 0
    int columnOffset = direction == 2 ? -1 : direction == 3 ? 1 : 0; // 2 -> -1, 3 -> 1, else 0

    TileComponent tileComponent = peak(tileComponents, startRow + rowOffset, startColumn + columnOffset);

    if (tileComponent != null) {
      tileComponent.setTileType(tileType);
    }
  }

  private void joinNearbyCliffs(TileComponent[][] tileComponents, int startRow, int startColumn) {
    for (int i = joinCliffsTilesAway + 1; i > 1; i--) {
      TileComponent upTile = peak(tileComponents, startRow - i, startColumn);

      if (upTile != null && upTile.getTileType() == TileType.CLIFF_CENTER) {
        peak(tileComponents, startRow - i + 1, startColumn).setTileType(TileType.CLIFF_CENTER);
        peak(tileComponents, startRow - i + 2, startColumn).setTileType(TileType.CLIFF_CENTER);
      }

      TileComponent downTile = peak(tileComponents, startRow + i, startColumn);

      if (downTile != null && downTile.getTileType() == TileType.CLIFF_CENTER) {
        peak(tileComponents, startRow + i - 2, startColumn).setTileType(TileType.CLIFF_CENTER);
        peak(tileComponents, startRow + i - 1, startColumn).setTileType(TileType.CLIFF_CENTER);
      }

      TileComponent leftTile = peak(tileComponents, startRow, startColumn - i);

      if (leftTile != null && leftTile.getTileType() == TileType.CLIFF_CENTER) {
        peak(tileComponents, startRow, startColumn - i + 2).setTileType(TileType.CLIFF_CENTER);
        peak(tileComponents, startRow, startColumn - i + 1).setTileType(TileType.CLIFF_CENTER);
      }

      TileComponent rightTile = peak(tileComponents, startRow, startColumn + i);

      if (rightTile != null && rightTile.getTileType() == TileType.CLIFF_CENTER) {
        peak(tileComponents, startRow, startColumn + i - 2).setTileType(TileType.CLIFF_CENTER);
        peak(tileComponents, startRow, startColumn + i - 1).setTileType(TileType.CLIFF_CENTER);
      }
    }
  }

  /**
   *
   * @param tileComponents
   * @param startRow
   * @param startColumn
   * @return false if cliff cannot be shaped
   */
  private boolean shapeCliff(TileComponent[][] tileComponents, int startRow, int startColumn) {
    TileComponent topTile = peak(tileComponents, startRow - 1, startColumn);

    if (topTile == null) {
      return false;
    }

    if (isReplaceable(topTile.getTileType())) {
      topTile.setTileType(TileType.CLIFF_TOP);

      TileComponent topLeftTile = peak(tileComponents, startRow - 1, startColumn - 1);

      if (topLeftTile == null) {
        topTile.setTileType(TileType.GRASS);
        return false;
      }

      if (isReplaceable(topLeftTile.getTileType())) {
        topLeftTile.setTileType(TileType.CLIFF_TOP_LEFT);
      } else if (topLeftTile.getTileType() == TileType.CLIFF_CENTER) {
        return shapeCliff(tileComponents, startRow - 1, startColumn - 1);
      }
    }

    return true;
  }

  private boolean isReplaceable(TileType tileType) {
    return tileType == TileType.GRASS || tileType == TileType.DIRT;
  }

  private TileComponent peak(TileComponent[][] tileComponents, int row, int column) {
    if (column < 0 || column >= tileComponents.length) {
      return null;
    }

    if (row < 0 || row >= tileComponents[column].length) {
      return null;
    }

    return tileComponents[column][row];
  }
}
