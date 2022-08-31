package technology.sola.engine.sketchy.game.chunk;

import technology.sola.math.linear.Vector2D;

import java.util.Random;

import static technology.sola.engine.sketchy.game.chunk.Chunk.TILE_SIZE;

public class ChunkCreator {
  // TODO don't commit this random seed
  private static final Random RANDOM = new Random(1337);
  private static final int cultureGenerations = 5;
  private static final int joinCliffsTilesAway = 2;
  private static final int minCliffTilesFromPlayer = 8;
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
    stepFillDirtBetweenCliffs(tileComponents);
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

    // Note: Order matters!
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          patchKittyCornerCenters(tileComponents, row, column);
        }
      }
    }

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          shapeCliffTopAndBottom(tileComponents, row, column);
        }
      }
    }

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          shapeCliffLeftAndRight(tileComponents, row, column);
        }
      }
    }

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          shapeCliffCorners(tileComponents, row, column);
        }
      }
    }
  }

  private void stepFillDirtBetweenCliffs(TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        // todo
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
    // todo replace cliff tiles that are not completed
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

  private void fillRowBetween(TileComponent[][] tileComponents, int row, int start, int end, TileType tileType, float chance) {
    for (int column = start; column < end; column++) {
      if (chance >= 1 || RANDOM.nextFloat() < chance) {
        peak(tileComponents, row, column).setTileType(tileType);
      }
    }
  }

  private void fillColumnBetween(TileComponent[][] tileComponents, int column, int start, int end, TileType tileType, float chance) {
    for (int row = start; row < end; row++) {
      if (chance >= 1 || RANDOM.nextFloat() < chance) {
        peak(tileComponents, row, column).setTileType(tileType);
      }
    }
  }

  // TODO cleanup with fillBetween methods
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

  private void patchKittyCornerCenters(TileComponent[][] tileComponents, int startRow, int startColumn) {
    TileComponent bottomLeftTile = peak(tileComponents, startRow + 1, startColumn - 1);
    TileComponent bottomRightTile = peak(tileComponents, startRow + 1, startColumn + 1);

    boolean mightNeedFilling = (bottomLeftTile != null && bottomLeftTile.getTileType() == TileType.CLIFF_CENTER) ||
      (bottomRightTile != null && bottomRightTile.getTileType() == TileType.CLIFF_CENTER);

    if (mightNeedFilling) {
      TileComponent topTile = peak(tileComponents, startRow - 1, startColumn);
      TileComponent leftTile = peak(tileComponents, startRow, startColumn - 1);
      TileComponent rightTile = peak(tileComponents, startRow, startColumn + 1);

      boolean needsReplacing = topTile != null && topTile.getTileType() != TileType.CLIFF_CENTER
        && leftTile != null && leftTile.getTileType() != TileType.CLIFF_CENTER
        && rightTile != null && rightTile.getTileType() != TileType.CLIFF_CENTER;

      if (needsReplacing) {
        TileComponent bottomTile = peak(tileComponents, startRow + 1, startColumn);

        if (bottomTile != null) {
          bottomTile.setTileType(TileType.CLIFF_CENTER);
        }
      }
    }
  }

  private void shapeCliffTopAndBottom(TileComponent[][] tileComponents, int startRow, int startColumn) {
    TileComponent topTile = peak(tileComponents, startRow - 1, startColumn);

    if (topTile != null && isReplaceable(topTile.getTileType())) {
      topTile.setTileType(TileType.CLIFF_TOP);
    }

    TileComponent bottomTile = peak(tileComponents, startRow + 1, startColumn);

    if (bottomTile != null && isReplaceable(bottomTile.getTileType())) {
      bottomTile.setTileType(TileType.CLIFF_BOTTOM);
    }
  }

  private void shapeCliffLeftAndRight(TileComponent[][] tileComponents, int startRow, int startColumn) {
    TileComponent leftTile = peak(tileComponents, startRow, startColumn - 1);

    if (leftTile != null) {
      switch (leftTile.getTileType()) {
        case GRASS, DIRT -> leftTile.setTileType(TileType.CLIFF_LEFT);
        case CLIFF_TOP -> {
          leftTile.setTileType(TileType.CLIFF_CENTER);
          TileComponent topLeftTile = peak(tileComponents, startRow - 1, startColumn - 1);
          if (topLeftTile != null && isReplaceable(topLeftTile.getTileType())) {
            topLeftTile.setTileType(TileType.CLIFF_TOP_LEFT);
          }
        }
        case CLIFF_BOTTOM -> {
          leftTile.setTileType(TileType.CLIFF_CENTER);
          TileComponent bottomLeftTile = peak(tileComponents, startRow + 1, startColumn - 1);
          if (bottomLeftTile != null && isReplaceable(bottomLeftTile.getTileType())) {
            bottomLeftTile.setTileType(TileType.CLIFF_BOTTOM_LEFT);
          }
        }
      }
    }

    TileComponent rightTile = peak(tileComponents, startRow, startColumn + 1);

    if (rightTile != null) {
      switch (rightTile.getTileType()) {
        case GRASS, DIRT -> rightTile.setTileType(TileType.CLIFF_RIGHT);
        case CLIFF_TOP -> {
          rightTile.setTileType(TileType.CLIFF_CENTER);
          TileComponent topRightTile = peak(tileComponents, startRow - 1, startColumn + 1);
          if (topRightTile != null && isReplaceable(topRightTile.getTileType())) {
            topRightTile.setTileType(TileType.CLIFF_TOP_RIGHT);
          }
        }
        case CLIFF_BOTTOM -> {
          rightTile.setTileType(TileType.CLIFF_CENTER);
          TileComponent bottomRightTile = peak(tileComponents, startRow + 1, startColumn + 1);
          if (bottomRightTile != null && isReplaceable(bottomRightTile.getTileType())) {
            bottomRightTile.setTileType(TileType.CLIFF_BOTTOM_RIGHT);
          }
        }
      }
    }
  }

  private void shapeCliffCorners(TileComponent[][] tileComponents, int startRow, int startColumn) {
    TileComponent topTile = peak(tileComponents, startRow - 1, startColumn);
    TileComponent bottomTile = peak(tileComponents, startRow + 1, startColumn);
    TileComponent leftTile = peak(tileComponents, startRow, startColumn - 1);
    TileComponent rightTile = peak(tileComponents, startRow, startColumn + 1);

    if (topTile != null && leftTile != null && (topTile.getTileType() == TileType.CLIFF_TOP || leftTile.getTileType() == TileType.CLIFF_LEFT)) {
      TileComponent topLeftTile = peak(tileComponents, startRow - 1, startColumn - 1);

      if (topLeftTile != null && isReplaceable(topLeftTile.getTileType())) {
        topLeftTile.setTileType(TileType.CLIFF_TOP_LEFT);
      }
    }

    if (topTile != null && rightTile != null && (topTile.getTileType() == TileType.CLIFF_TOP || rightTile.getTileType() == TileType.CLIFF_RIGHT)) {
      TileComponent topRightTile = peak(tileComponents, startRow - 1, startColumn + 1);

      if (topRightTile != null && isReplaceable(topRightTile.getTileType())) {
        topRightTile.setTileType(TileType.CLIFF_TOP_RIGHT);
      }
    }

    if (bottomTile != null && leftTile != null && (bottomTile.getTileType() == TileType.CLIFF_BOTTOM || leftTile.getTileType() == TileType.CLIFF_LEFT)) {
      TileComponent bottomLeftTile = peak(tileComponents, startRow + 1, startColumn - 1);

      if (bottomLeftTile != null && isReplaceable(bottomLeftTile.getTileType())) {
        bottomLeftTile.setTileType(TileType.CLIFF_BOTTOM_LEFT);
      }
    }

    if (bottomTile != null && rightTile != null && (bottomTile.getTileType() == TileType.CLIFF_BOTTOM || rightTile.getTileType() == TileType.CLIFF_RIGHT)) {
      TileComponent bottomRightTile = peak(tileComponents, startRow + 1, startColumn + 1);

      if (bottomRightTile != null && isReplaceable(bottomRightTile.getTileType())) {
        bottomRightTile.setTileType(TileType.CLIFF_BOTTOM_RIGHT);
      }
    }
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
