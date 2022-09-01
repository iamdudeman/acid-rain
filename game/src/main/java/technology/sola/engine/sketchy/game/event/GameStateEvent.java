package technology.sola.engine.sketchy.game.event;

import technology.sola.engine.event.Event;

public class GameStateEvent implements Event<GameState> {
  private final GameState gameState;
  private final float distancedTraveled;
  private final int donutsConsumed;

  public GameStateEvent(GameState gameState) {
    this(gameState, 0, 0);
  }

  public GameStateEvent(GameState gameState, float distancedTraveled, int donutsConsumed) {
    this.gameState = gameState;
    this.distancedTraveled = distancedTraveled;
    this.donutsConsumed = donutsConsumed;
  }

  @Override
  public GameState getMessage() {
    return gameState;
  }

  public float getDistanceTraveled() {
    return distancedTraveled;
  }

  public int getDonutsConsumed() {
    return donutsConsumed;
  }
}
