package technology.sola.engine.sketchy.game.event;

import technology.sola.engine.event.Event;

public class GameStateEvent implements Event<GameState> {
  private final GameState gameState;
  private final float score;

  public GameStateEvent(GameState gameState) {
    this(gameState, 0);
  }

  public GameStateEvent(GameState gameState, float score) {
    this.gameState = gameState;
    this.score = score;
  }

  @Override
  public GameState getMessage() {
    return gameState;
  }

  public float getScore() {
    return score;
  }
}
