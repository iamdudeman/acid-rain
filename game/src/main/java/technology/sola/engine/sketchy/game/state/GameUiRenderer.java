package technology.sola.engine.sketchy.game.state;

import technology.sola.ecs.World;
import technology.sola.engine.assets.AssetLoader;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.assets.graphics.font.Font;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.AffineTransform;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.AcidRainSola;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.engine.sketchy.game.player.PlayerComponent;
import technology.sola.math.linear.Vector2D;

public class GameUiRenderer {
  private static final Color SUNLIGHT_BAR_COLOR = new Color(200, 255, 215, 0);
  private static final String GAME_OVER_TEXT = "Game Over";
  private static final String PLAY_AGAIN_TEXT = "Click anywhere to play again";
  private final int sunlightBarHeight = 12;
  private final int sunlightBarWidth = 220;
  private final int sunlightBarHalfWidth = sunlightBarWidth / 2;
  private final float animationDuration = 100;
  private final AssetLoader<SpriteSheet> spriteSheetAssetLoader;
  private boolean shouldDrawGameOver = false;
  private float distanceTraveled = 0f;
  private int donutsConsumed = 0;
  private int gameOverDuckAnimation = 0;
  private Vector2D duckLastPosition;
  private String spriteId;

  public GameUiRenderer(EventHub eventHub, AssetLoader<SpriteSheet> spriteSheetAssetLoader) {
    this.spriteSheetAssetLoader = spriteSheetAssetLoader;
    eventHub.add(gameStateEvent -> {
      shouldDrawGameOver = gameStateEvent.getMessage() == GameState.GAME_OVER;

      if (shouldDrawGameOver) {
        distanceTraveled = gameStateEvent.getDistanceTraveled();
        donutsConsumed = gameStateEvent.getDonutsConsumed();
        duckLastPosition = gameStateEvent.getPlayerPosition();
        spriteId = gameStateEvent.getSpriteId();
      } else {
        gameOverDuckAnimation = 0;
      }
    }, GameStateEvent.class);
  }

  public void render(Renderer renderer, World world) {
    if (shouldDrawGameOver) {
      String donutsConsumedText = "Donuts eated: " + donutsConsumed;
      String distanceTraveledText = "Distance traveled for noms: " + Math.round(this.distanceTraveled);
      Font font = renderer.getFont();
      Font.TextDimensions gameOverDimensions = font.getDimensionsForText(GAME_OVER_TEXT);
      Font.TextDimensions donutsConsumedDimensions = font.getDimensionsForText(donutsConsumedText);
      Font.TextDimensions distanceTraveledDimensions = font.getDimensionsForText(distanceTraveledText);
      Font.TextDimensions playAgainDimensions = font.getDimensionsForText(PLAY_AGAIN_TEXT);
      float maxWidth = Math.max(playAgainDimensions.width(), distanceTraveledDimensions.width());
      if (gameOverDuckAnimation < animationDuration) {
        spriteSheetAssetLoader.get(Constants.Assets.Sprites.SPRITE_SHEET_ID).executeIfLoaded(spriteSheet -> {
          float size = (animationDuration - gameOverDuckAnimation) / animationDuration;
          // todo translate here seems to be off a bit!
          AffineTransform affineTransform = new AffineTransform()
            .translate(duckLastPosition.x, duckLastPosition.y)
            .scale(size, size)
            ;

          renderer.drawImage(spriteSheet.getSprite(spriteId), affineTransform);
          gameOverDuckAnimation++;
        });
      }
      renderer.setBlendMode(BlendMode.NORMAL);
      renderer.fillRect(
        3, 3,
        maxWidth + 6, gameOverDimensions.height() + donutsConsumedDimensions.height() + playAgainDimensions.height() + distanceTraveledDimensions.height() + 15,
        new Color(150, 255, 255, 255)
      );
      renderer.setBlendMode(BlendMode.NO_BLENDING);
      renderer.drawString(GAME_OVER_TEXT, 6, 3, Color.BLACK);
      renderer.drawString(donutsConsumedText, 6, gameOverDimensions.height() + 6, Color.BLACK);
      renderer.drawString(distanceTraveledText, 6, gameOverDimensions.height() + donutsConsumedDimensions.height() + 9, Color.BLACK);
      renderer.drawString(PLAY_AGAIN_TEXT, 6, gameOverDimensions.height() + donutsConsumedDimensions.height() + distanceTraveledDimensions.height() + 12, Color.BLACK);
    } else {
      world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
        float percentage = playerComponent.getSunlight() / (float) PlayerComponent.MAX_SUNLIGHT;
        float x = AcidRainSola.HALF_CANVAS_WIDTH - sunlightBarHalfWidth;
        float y = AcidRainSola.CANVAS_HEIGHT - sunlightBarHeight - 8;

        String donutsConsumedText = "Donuts: " + playerComponent.getDonutsConsumed();
        renderer.drawString(donutsConsumedText, 3, 3, Color.BLACK);
        renderer.setBlendMode(BlendMode.NORMAL);
        renderer.fillRect(x, y, percentage * sunlightBarWidth, sunlightBarHeight, SUNLIGHT_BAR_COLOR);
        renderer.setBlendMode(BlendMode.NO_BLENDING);
        renderer.drawRect(x, y, sunlightBarWidth, sunlightBarHeight, Color.BLACK);
      });
    }
  }
}
