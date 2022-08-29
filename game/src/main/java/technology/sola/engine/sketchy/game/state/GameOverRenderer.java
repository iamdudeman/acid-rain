package technology.sola.engine.sketchy.game.state;

import technology.sola.engine.assets.graphics.font.Font;
import technology.sola.engine.event.EventListener;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.sketchy.game.Constants;
import technology.sola.engine.sketchy.game.event.GameState;
import technology.sola.engine.sketchy.game.event.GameStateEvent;

public class GameOverRenderer implements EventListener<GameStateEvent> {
  private final String gameOverText = "Game Over";
  private final String playAgainText = "Click anywhere to play again";
  private boolean shouldDraw = false;
  private float score = 0f;

  public void render(Renderer renderer) {
    if (shouldDraw) {
      renderer.drawToLayer(Constants.Layers.FOREGROUND, r -> {
        String scoreText = "Score: " + Math.round(score);
        Font.TextDimensions gameOverDimensions = renderer.getFont().getDimensionsForText(gameOverText);
        Font.TextDimensions playAgainDimensions = renderer.getFont().getDimensionsForText(playAgainText);
        Font.TextDimensions scoreDimensions = renderer.getFont().getDimensionsForText(scoreText);
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
    }
  }

  @Override
  public void onEvent(GameStateEvent gameStateEvent) {
    shouldDraw = gameStateEvent.getMessage() == GameState.GAME_OVER;

    if (shouldDraw) {
      score = gameStateEvent.getScore();
    }
  }
}
