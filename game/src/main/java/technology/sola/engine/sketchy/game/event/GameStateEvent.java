package technology.sola.engine.sketchy.game.event;

import technology.sola.engine.event.Event;

public class GameStateEvent implements Event<GameState> {
  private final GameState gameState;
  private final float distancedTraveled;

  public GameStateEvent(GameState gameState) {
    this(gameState, 0);
  }

  public GameStateEvent(GameState gameState, float distancedTraveled) {
    this.gameState = gameState;
    this.distancedTraveled = distancedTraveled;
  }

  @Override
  public GameState getMessage() {
    return gameState;
  }

  public float getDistanceTraveled() {
    return distancedTraveled;
  }
}
