package technology.sola.engine.sketchy.game.event;

import technology.sola.engine.event.Event;

public class GameStateEvent implements Event<GameState> {
  private final GameState gameState;

  public GameStateEvent(GameState gameState) {
    this.gameState = gameState;
  }

  @Override
  public GameState getMessage() {
    return gameState;
  }
}
