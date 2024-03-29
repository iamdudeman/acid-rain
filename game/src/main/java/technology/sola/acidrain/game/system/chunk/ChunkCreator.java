package technology.sola.acidrain.game.system.chunk;

import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.component.TileComponent;
import technology.sola.math.linear.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkCreator {
  private static final Random RANDOM = new Random();
  private static final int CULTURE_GENERATIONS = 5;
  private static final int JOIN_CLIFFS_TILES_AWAY = 2;
  private static final int MIN_CLIFF_TILES_FROM_PLAYER = 6;
  private static final float BASE_INIT_CLIFF_PERCENT = 0.14f;
  private static final float BASE_INIT_DIRT_PERCENT = 0.06f;
  private static final float BASE_CULTURE_CLIFF_PERCENT = 0.05f;
  private static final float BASE_CULTURE_CLIFF_CLEAR_PERCENT = 0.05f;
  private static final float BASE_CULTURE_DIRT_PERCENT = 0.02f;
  private static final float BASE_CULTURE_DIRT_CLEAR_PERCENT = 0.05f;
  private static final float DIRT_TILE_FILL_PERCENTAGE = 0.5f;
  private static final int MAX_PICKUPS_PER_CHUNK = 5;
  private static final float TEXTURE_2_PERCENT = 0.10f;
  private static final float TEXTURE_3_PERCENT = 0.10f;
  private static final int CLIFF_SAFETY_GAP = 1;

  public Chunk createChunk(ChunkId chunkId, Vector2D playerTranslate) {
    TileComponent[][] tileComponents = new TileComponent[Chunk.COLUMNS][Chunk.ROWS];

    stepInitialize(chunkId, playerTranslate, tileComponents);
    stepCulture(tileComponents, 0, false);
    stepShapeCliffs(tileComponents);
    stepAddDirtAroundCliffs(tileComponents);
    stepPlacePickups(tileComponents);
    stepTextureGrassAndDirt(tileComponents);

    return new Chunk(chunkId, tileComponents);
  }

  private void stepInitialize(ChunkId chunkId, Vector2D playerTranslate, TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileType initialTileType;

        initialTileType = RANDOM.nextFloat() < BASE_INIT_DIRT_PERCENT ? TileType.DIRT : TileType.GRASS;

        float x = chunkId.getX(column);
        float y = chunkId.getY(row);

        if (Math.abs(x - playerTranslate.x()) > Chunk.TILE_SIZE * MIN_CLIFF_TILES_FROM_PLAYER || Math.abs(y - playerTranslate.y()) > Chunk.TILE_SIZE * MIN_CLIFF_TILES_FROM_PLAYER) {
          if (RANDOM.nextFloat() < BASE_INIT_CLIFF_PERCENT) {
            if (column > CLIFF_SAFETY_GAP && row > CLIFF_SAFETY_GAP && column + CLIFF_SAFETY_GAP < Chunk.COLUMNS && row + CLIFF_SAFETY_GAP < Chunk.ROWS) {
              initialTileType = TileType.CLIFF_CENTER;
            }
          }
        }

        tileComponents[column][row] = new TileComponent(chunkId, initialTileType);
      }
    }
  }

  private void stepCulture(TileComponent[][] tileComponents, int depth, boolean skipCliff) {
    if (depth > CULTURE_GENERATIONS - 1) {
      return;
    }

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];
        TileType tileType = tileComponent.getTileType();

        if (!skipCliff && tileType == TileType.CLIFF_CENTER) {
          float value = RANDOM.nextFloat();

          if (value < BASE_CULTURE_CLIFF_PERCENT) {
            propagateTileType(tileComponents, row, column, TileType.CLIFF_CENTER);
          } else if (value < BASE_CULTURE_CLIFF_PERCENT + BASE_CULTURE_CLIFF_CLEAR_PERCENT) {
            tileComponent.setTileType(TileType.GRASS);
          }
        }

        if (tileType == TileType.DIRT) {
          float value = RANDOM.nextFloat();

          if (value < BASE_CULTURE_DIRT_PERCENT) {
            propagateTileType(tileComponents, row, column, TileType.DIRT);
          } else if (value < BASE_CULTURE_DIRT_PERCENT + BASE_CULTURE_DIRT_CLEAR_PERCENT) {
            tileComponent.setTileType(TileType.GRASS);
          }
        }
      }
    }

    stepCulture(tileComponents, depth + 1, skipCliff);
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

    List<ShapeLeftRightChange> leftRightChanges = new ArrayList<>();

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];

        if (tileComponent.getTileType() == TileType.CLIFF_CENTER) {
          shapeCliffLeftAndRight(leftRightChanges, tileComponents, row, column);
        }
      }
    }

    for (ShapeLeftRightChange change : leftRightChanges) {
      tileComponents[change.column][change.row].setTileType(change.tileType);
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

  private void stepAddDirtAroundCliffs(TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];
        TileType tileType = tileComponent.getTileType();

        if (tileType == TileType.GRASS) {
          TileComponent tileToCheck = peak(tileComponents, row - 1, column);
          if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
            if (RANDOM.nextFloat() < DIRT_TILE_FILL_PERCENTAGE) {
              tileComponent.setTileType(TileType.DIRT);
            }
            continue;
          }
          tileToCheck = peak(tileComponents, row + 1, column);
          if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
            if (RANDOM.nextFloat() < DIRT_TILE_FILL_PERCENTAGE) {
              tileComponent.setTileType(TileType.DIRT);
            }
            continue;
          }
          tileToCheck = peak(tileComponents, row, column - 1);
          if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
            if (RANDOM.nextFloat() < DIRT_TILE_FILL_PERCENTAGE) {
              tileComponent.setTileType(TileType.DIRT);
            }
            continue;
          }
          tileToCheck = peak(tileComponents, row, column + 1);
          if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
            if (RANDOM.nextFloat() < DIRT_TILE_FILL_PERCENTAGE) {
              tileComponent.setTileType(TileType.DIRT);
            }
          }
        }
      }
    }

    stepCulture(tileComponents, 0, true);
  }

  private void stepPlacePickups(TileComponent[][] tileComponents) {
    int pickupsToPlace = MAX_PICKUPS_PER_CHUNK;

    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        if (pickupsToPlace <= 0) {
          return;
        }

        TileComponent currentTile = tileComponents[column][row];

        if (currentTile.getTileType() == TileType.GRASS) {
          int numberOfDirt = countSurroundingDirt(tileComponents, row, column);

          if (numberOfDirt >= 3) {
            currentTile.setHasPickup(true);
            pickupsToPlace--;
          }

          int numberOfCliff = countSurroundingCliff(tileComponents, row, column);

          if (numberOfCliff == 3) {
            currentTile.setHasPickup(true);
            pickupsToPlace--;
          }
        }
      }
    }
  }

  private void stepTextureGrassAndDirt(TileComponent[][] tileComponents) {
    for (int row = 0; row < Chunk.ROWS; row++) {
      for (int column = 0; column < Chunk.COLUMNS; column++) {
        TileComponent currentTile = tileComponents[column][row];

        if (currentTile.getTileType() == TileType.GRASS) {
          float value = RANDOM.nextFloat();

          if (value < TEXTURE_2_PERCENT) {
            currentTile.setTileType(TileType.GRASS2);
          } else if (value < TEXTURE_2_PERCENT + TEXTURE_3_PERCENT) {
            currentTile.setTileType(TileType.GRASS3);
          }
        } else if (currentTile.getTileType() == TileType.DIRT) {
          float value = RANDOM.nextFloat();

          if (value < TEXTURE_2_PERCENT) {
            currentTile.setTileType(TileType.DIRT2);
          } else if (value < TEXTURE_2_PERCENT + TEXTURE_3_PERCENT) {
            currentTile.setTileType(TileType.DIRT3);
          }
        }
      }
    }
  }


  private void propagateTileType(TileComponent[][] tileComponents, int startRow, int startColumn, TileType tileTypeToPropagate) {
    int direction = RANDOM.nextInt(4); // up, down, left, right
    int rowOffset = direction == 0 ? -1 : direction == 1 ? 1 : 0; // 0 -> -1, 1 -> 1, else 0
    int columnOffset = direction == 2 ? -1 : direction == 3 ? 1 : 0; // 2 -> -1, 3 -> 1, else 0

    TileComponent tileComponent = peak(tileComponents, startRow + rowOffset, startColumn + columnOffset);

    if (tileComponent != null && isReplaceable(tileComponent.getTileType())) {
      tileComponent.setTileType(tileTypeToPropagate);
    }
  }

  private void joinNearbyCliffs(TileComponent[][] tileComponents, int startRow, int startColumn) {
    for (int i = JOIN_CLIFFS_TILES_AWAY + 1; i > 1; i--) {
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

  private void shapeCliffLeftAndRight(List<ShapeLeftRightChange> leftRightChanges, TileComponent[][] tileComponents, int startRow, int startColumn) {
    TileComponent leftTile = peak(tileComponents, startRow, startColumn - 1);

    if (leftTile != null) {
      switch (leftTile.getTileType()) {
        case GRASS, DIRT -> {
          leftRightChanges.add(new ShapeLeftRightChange(startRow, startColumn - 1, TileType.CLIFF_LEFT));
        }
        case CLIFF_TOP -> {
          leftRightChanges.add(new ShapeLeftRightChange(startRow, startColumn - 1, TileType.CLIFF_CENTER));
          TileComponent topLeftTile = peak(tileComponents, startRow - 1, startColumn - 1);
          if (topLeftTile != null && isReplaceable(topLeftTile.getTileType())) {
            leftRightChanges.add(new ShapeLeftRightChange(startRow - 1, startColumn - 1, TileType.CLIFF_TOP_LEFT));
          }
        }
        case CLIFF_BOTTOM -> {
          leftRightChanges.add(new ShapeLeftRightChange(startRow, startColumn - 1, TileType.CLIFF_CENTER));

          TileComponent bottomLeftTile = peak(tileComponents, startRow + 1, startColumn - 1);
          if (bottomLeftTile != null && isReplaceable(bottomLeftTile.getTileType())) {
            leftRightChanges.add(new ShapeLeftRightChange(startRow + 1, startColumn - 1, TileType.CLIFF_BOTTOM_LEFT));
          }
        }
      }
    }

    TileComponent rightTile = peak(tileComponents, startRow, startColumn + 1);

    if (rightTile != null) {
      switch (rightTile.getTileType()) {
        case GRASS, DIRT -> {
          leftRightChanges.add(new ShapeLeftRightChange(startRow, startColumn + 1, TileType.CLIFF_RIGHT));
        }
        case CLIFF_TOP -> {
          leftRightChanges.add(new ShapeLeftRightChange(startRow, startColumn + 1, TileType.CLIFF_CENTER));
          TileComponent topRightTile = peak(tileComponents, startRow - 1, startColumn + 1);
          if (topRightTile != null && isReplaceable(topRightTile.getTileType())) {
            leftRightChanges.add(new ShapeLeftRightChange(startRow - 1, startColumn + 1, TileType.CLIFF_TOP_RIGHT));
          }
        }
        case CLIFF_BOTTOM -> {
          leftRightChanges.add(new ShapeLeftRightChange(startRow, startColumn + 1, TileType.CLIFF_CENTER));
          TileComponent bottomRightTile = peak(tileComponents, startRow + 1, startColumn + 1);
          if (bottomRightTile != null && isReplaceable(bottomRightTile.getTileType())) {
            leftRightChanges.add(new ShapeLeftRightChange(startRow + 1, startColumn + 1, TileType.CLIFF_BOTTOM_RIGHT));
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

  private int countSurroundingDirt(TileComponent[][] tileComponents, int row, int column) {
    int count = 0;
    TileComponent tileToCheck = peak(tileComponents, row - 1, column);
    if (tileToCheck != null && tileToCheck.getTileType() == TileType.DIRT) {
      count++;
    }
    tileToCheck = peak(tileComponents, row + 1, column);
    if (tileToCheck != null && tileToCheck.getTileType() == TileType.DIRT) {
      count++;
    }
    tileToCheck = peak(tileComponents, row, column - 1);
    if (tileToCheck != null && tileToCheck.getTileType() == TileType.DIRT) {
      count++;
    }
    tileToCheck = peak(tileComponents, row, column + 1);
    if (tileToCheck != null && tileToCheck.getTileType() == TileType.DIRT) {
      count++;
    }
    return count;
  }

  private int countSurroundingCliff(TileComponent[][] tileComponents, int row, int column) {
    int count = 0;
    TileComponent tileToCheck = peak(tileComponents, row - 1, column);
    if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
      count++;
    }
    tileToCheck = peak(tileComponents, row + 1, column);
    if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
      count++;
    }
    tileToCheck = peak(tileComponents, row, column - 1);
    if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
      count++;
    }
    tileToCheck = peak(tileComponents, row, column + 1);
    if (tileToCheck != null && tileToCheck.getTileType().assetId.equals(Constants.Assets.Sprites.CLIFF)) {
      count++;
    }
    return count;
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

  private record ShapeLeftRightChange(int row, int column, TileType tileType) {

  }
}
