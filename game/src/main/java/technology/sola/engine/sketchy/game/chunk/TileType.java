package technology.sola.engine.sketchy.game.chunk;

import technology.sola.engine.sketchy.game.Constants;

public enum TileType {
  GRASS(Constants.Assets.Sprites.GRASS),
  DIRT(Constants.Assets.Sprites.DIRT),
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

  TileType(String assetId) {
    this(assetId, "1");
  }

  TileType(String assetId, String variation) {
    this.assetId = assetId;
    this.variation = variation;
  }
}
