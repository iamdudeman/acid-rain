package technology.sola.engine.sketchy.game.state;

import technology.sola.ecs.World;
import technology.sola.engine.assets.graphics.font.Font;
import technology.sola.engine.event.EventHub;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;
import technology.sola.engine.sketchy.game.player.PlayerComponent;

public class GameUiRenderer {
  private static final Color SUNLIGHT_BAR_COLOR = new Color(200, 255, 215, 0);
  private final String gameOverText = "Game Over";
  private final String playAgainText = "Click anywhere to play again";
  private final int sunlightBarHeight = 12;
  private final int sunlightBarWidth = 220;
  private final int sunlightBarHalfWidth = sunlightBarWidth / 2;
  private boolean shouldDrawGameOver = false;
  private float distanceTraveled = 0f;
  private int donutsConsumed = 0;

  public GameUiRenderer(EventHub eventHub) {
    eventHub.add(gameStateEvent -> {
      shouldDrawGameOver = gameStateEvent.getMessage() == GameState.GAME_OVER;

      if (shouldDrawGameOver) {
        distanceTraveled = gameStateEvent.getDistanceTraveled();
        donutsConsumed = gameStateEvent.getDonutsConsumed();
      }
    }, GameStateEvent.class);
  }

  public void render(Renderer renderer, World world) {
    if (shouldDrawGameOver) {
      String donutsConsumedText = "Donuts eated: " + donutsConsumed;
      String distanceTraveledText = "Distance traveled for noms: " + Math.round(this.distanceTraveled);
      Font font = renderer.getFont();
      Font.TextDimensions gameOverDimensions = font.getDimensionsForText(gameOverText);
      Font.TextDimensions donutsConsumedDimensions = font.getDimensionsForText(donutsConsumedText);
      Font.TextDimensions distanceTraveledDimensions = font.getDimensionsForText(distanceTraveledText);
      Font.TextDimensions playAgainDimensions = font.getDimensionsForText(playAgainText);
      float maxWidth = Math.max(playAgainDimensions.width(), distanceTraveledDimensions.width());
      renderer.setBlendMode(BlendMode.NORMAL);
      renderer.fillRect(
        3, 3,
        maxWidth + 6, gameOverDimensions.height() + donutsConsumedDimensions.height() + playAgainDimensions.height() + distanceTraveledDimensions.height() + 15,
        new Color(150, 255, 255, 255)
      );
      renderer.drawString(gameOverText, 6, 3, Color.BLACK);
      renderer.drawString(donutsConsumedText, 6, gameOverDimensions.height() + 6, Color.BLACK);
      renderer.drawString(distanceTraveledText, 6, gameOverDimensions.height() + donutsConsumedDimensions.height() + 9, Color.BLACK);
      renderer.drawString(playAgainText, 6, gameOverDimensions.height() + donutsConsumedDimensions.height() + distanceTraveledDimensions.height() + 12, Color.BLACK);
    } else {
      world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
        float percentage = playerComponent.getSunlight() / (float) PlayerComponent.MAX_SUNLIGHT;
        float x = renderer.getWidth() / 2f - sunlightBarHalfWidth;
        float y = renderer.getHeight() - sunlightBarHeight - 8;

        String donutsConsumedText = "Donuts eated: " + playerComponent.getDonutsConsumed();
        renderer.drawString(donutsConsumedText, 3, 3, Color.BLACK);
        // TODO Note: this is why sprite transparency is working (should use BlendModeComponent later for sure when fixed in engine)
        renderer.setBlendMode(BlendMode.NORMAL);
        renderer.fillRect(x, y, percentage * sunlightBarWidth, sunlightBarHeight, SUNLIGHT_BAR_COLOR);
        renderer.drawRect(x, y, sunlightBarWidth, sunlightBarHeight, Color.BLACK);
      });
    }
  }
}
