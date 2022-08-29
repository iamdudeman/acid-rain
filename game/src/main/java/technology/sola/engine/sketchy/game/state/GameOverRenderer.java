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

  public void render(Renderer renderer) {
    if (shouldDraw) {
      renderer.drawToLayer(Constants.Layers.FOREGROUND, r -> {
        Font.TextDimensions textDimensions = renderer.getFont().getDimensionsForText(gameOverText);
        Font.TextDimensions textDimensionsTwo = renderer.getFont().getDimensionsForText(playAgainText);
        renderer.setBlendMode(BlendMode.NORMAL);
        renderer.fillRect(
          3, 3,
          textDimensionsTwo.width(), textDimensions.height() + textDimensionsTwo.height() + 3,
          new Color(150, 255, 255, 255)
        );
        renderer.drawString(gameOverText, 3, 3, Color.BLACK);
        renderer.drawString(playAgainText, 3, textDimensions.height() + 6, Color.BLACK);
      });
    }
  }

  @Override
  public void onEvent(GameStateEvent gameStateEvent) {
    shouldDraw = gameStateEvent.getMessage() == GameState.GAME_OVER;
  }
}
