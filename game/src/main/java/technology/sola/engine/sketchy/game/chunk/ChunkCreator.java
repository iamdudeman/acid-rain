package technology.sola.engine.sketchy.game.chunk;

import technology.sola.math.linear.Vector2D;

import java.util.Random;

import static technology.sola.engine.sketchy.game.chunk.Chunk.TILE_SIZE;

public class ChunkCreator {
  private static final Random RANDOM = new Random();
  private static final int cultureGenerations = 5;
  private static final int cliffBlocksToJoin = 2;

  public Chunk createChunk(ChunkId chunkId, Vector2D playerTranslate) {
    TileComponent[][] tileComponents = new TileComponent[Chunk.COLUMNS][Chunk.ROWS];

    stepInitialize(chunkId, playerTranslate, tileComponents);
    stepCulture(tileComponents, 0);
    stepShapeCliffs(tileComponents);
    stepPlacePickups(tileComponents);
    stepCleanup(tileComponents);

    return new Chunk(chunkId, tileComponents);
  }

  private void stepInitialize(ChunkId chunkId, Vector2D playerTranslate, TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileType initialTileType;

        initialTileType = RANDOM.nextFloat() < 0.03
          ? TileType.DIRT
          : TileType.GRASS;

        float x = chunkId.getX(column);
        float y = chunkId.getY(row);

        if (Math.abs(x - playerTranslate.x) > TILE_SIZE * 10 || Math.abs(y - playerTranslate.y) > TILE_SIZE * 10) {
          if (RANDOM.nextFloat() < 0.01) {
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

          if (value < 0.10) {
            propagateTileType(tileComponents, row, column, TileType.CLIFF_CENTER);
          } else if (value < 0.15) {
            tileComponent.setTileType(TileType.GRASS);
          }
        }

        if (tileType == TileType.DIRT) {
          float value = RANDOM.nextFloat();

          if (value < 0.03) {
            propagateTileType(tileComponents, row, column, TileType.DIRT);
          } else if (value < 0.08) {
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
          joinNearbyCliffs(tileComponents, column, row);

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
    // TODO join if "cliffBlocksToJoin" block away
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
