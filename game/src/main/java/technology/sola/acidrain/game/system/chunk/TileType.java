package technology.sola.acidrain.game.system.chunk;

import technology.sola.acidrain.game.Constants;

public enum TileType {
  GRASS(Constants.Assets.Sprites.GRASS, true),
  GRASS2(Constants.Assets.Sprites.GRASS2, true),
  GRASS3(Constants.Assets.Sprites.GRASS3, true),

  DIRT(Constants.Assets.Sprites.DIRT, true),
  DIRT2(Constants.Assets.Sprites.DIRT2, true),
  DIRT3(Constants.Assets.Sprites.DIRT3, true),

  CLIFF_TOP_LEFT(Constants.Assets.Sprites.CLIFF, "top-left"),
  CLIFF_TOP(Constants.Assets.Sprites.CLIFF, "top"),
  CLIFF_TOP_RIGHT(Constants.Assets.Sprites.CLIFF, "top-right"),
  CLIFF_LEFT(Constants.Assets.Sprites.CLIFF, "left"),
  CLIFF_CENTER(Constants.Assets.Sprites.CLIFF, "center"),
  CLIFF_RIGHT(Constants.Assets.Sprites.CLIFF, "right"),
  CLIFF_BOTTOM_LEFT(Constants.Assets.Sprites.CLIFF, "bottom-left"),
  CLIFF_BOTTOM(Constants.Assets.Sprites.CLIFF, "bottom"),
  CLIFF_BOTTOM_RIGHT(Constants.Assets.Sprites.CLIFF, "bottom-right"),
  ;

  public final String assetId;
  public final String variation;
  public final boolean isErasable;

  TileType(String assetId) {
    this(assetId, "1");
  }

  TileType(String assetId, boolean isErasable) {
    this(assetId, "1", isErasable);
  }

  TileType(String assetId, String variation) {
    this(assetId, variation, false);
  }

  TileType(String assetId, String variation, boolean isErasable) {
    this.assetId = assetId;
    this.variation = variation;
    this.isErasable = isErasable;
  }
}
