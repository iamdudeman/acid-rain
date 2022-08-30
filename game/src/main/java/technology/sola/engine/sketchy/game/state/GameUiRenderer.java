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
  private final String gameOverText = "Game Over";
  private final String playAgainText = "Click anywhere to play again";
  private final Color sunlightBarColor = new Color(220, 255, 215, 0);
  private final int sunlightBarHeight = 12;
  private final int sunlightBarWidth = 220;
  private final int sunlightBarHalfWidth = sunlightBarWidth / 2;
  private boolean shouldDrawGameOver = false;
  private float score = 0f;

  public GameUiRenderer(EventHub eventHub) {
    eventHub.add(gameStateEvent -> {
      shouldDrawGameOver = gameStateEvent.getMessage() == GameState.GAME_OVER;

      if (shouldDrawGameOver) {
        score = gameStateEvent.getScore();
      }
    }, GameStateEvent.class);
  }

  public void render(Renderer renderer, World world) {
    if (shouldDrawGameOver) {
      renderer.drawToLayer(Constants.Layers.FOREGROUND, r -> {
        String scoreText = "Score: " + Math.round(score);
        Font font = renderer.getFont();
        Font.TextDimensions gameOverDimensions = font.getDimensionsForText(gameOverText);
        Font.TextDimensions playAgainDimensions = font.getDimensionsForText(playAgainText);
        Font.TextDimensions scoreDimensions = font.getDimensionsForText(scoreText);
        renderer.setBlendMode(BlendMode.NORMAL);
        renderer.fillRect(
          3, 3,
          playAgainDimensions.width() + 6, gameOverDimensions.height() + playAgainDimensions.height() + scoreDimensions.height() + 6,
          new Color(150, 255, 255, 255)
        );
        renderer.drawString(gameOverText, 6, 3, Color.BLACK);
        renderer.drawString(scoreText, 6, gameOverDimensions.height() + 6, Color.BLACK);
        renderer.drawString(playAgainText, 6, gameOverDimensions.height() + scoreDimensions.height() + 9, Color.BLACK);
      });
    } else {
      world.findEntityByName(Constants.EntityNames.PLAYER).ifPresent(playerEntity -> {
        renderer.drawToLayer(Constants.Layers.FOREGROUND, r -> {
          PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
          float percentage = playerComponent.getSunlight() / (float) PlayerComponent.MAX_SUNLIGHT;
          float x = renderer.getWidth() / 2f - sunlightBarHalfWidth;
          float y = renderer.getHeight() - sunlightBarHeight - 8;
          renderer.setBlendMode(BlendMode.NORMAL);
          renderer.fillRect(x, y, percentage * sunlightBarWidth, sunlightBarHeight, sunlightBarColor);
          renderer.drawRect(x, y, sunlightBarWidth, sunlightBarHeight, Color.BLACK);
        });
      });
    }
  }
}
