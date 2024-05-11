package technology.sola.acidrain.game;

import technology.sola.engine.graphics.components.SpriteKeyFrame;

import java.util.HashMap;
import java.util.Map;

public final class SpriteCache {
  public static final SpriteKeyFrame ERASED = new SpriteKeyFrame(Constants.Assets.Sprites.SPRITE_SHEET_ID, Constants.Assets.Sprites.ERASED);
  private static final Map<SpriteCacheKey, SpriteKeyFrame> SPRITE_CACHE = new HashMap<>();

  public static SpriteKeyFrame get(String assetId, String variation) {
    SpriteCacheKey spriteCacheKey = new SpriteCacheKey(assetId, variation);

    return SPRITE_CACHE.computeIfAbsent(spriteCacheKey, k -> new SpriteKeyFrame(
      Constants.Assets.Sprites.SPRITE_SHEET_ID,
      assetId + "-" + variation
    ));
  }

  private record SpriteCacheKey(String assetId, String variation) {
  }

  private SpriteCache() {
  }
}
