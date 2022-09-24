package technology.sola.engine.sketchy.game.event;

import technology.sola.engine.event.Event;
import technology.sola.math.linear.Vector2D;

public class GameStateEvent implements Event {
  private final GameState gameState;
  private final float distancedTraveled;
  private final int donutsConsumed;
  private final Vector2D playerPosition;
  private final String spriteId;

  public GameStateEvent(GameState gameState) {
    this(gameState, 0, 0, null, null);
  }

  public GameStateEvent(GameState gameState, float distancedTraveled, int donutsConsumed, Vector2D playerPosition, String spriteId) {
    this.gameState = gameState;
    this.distancedTraveled = distancedTraveled;
    this.donutsConsumed = donutsConsumed;
    this.playerPosition = playerPosition;
    this.spriteId = spriteId;
  }

  public GameState getMessage() {
    return gameState;
  }

  public float getDistanceTraveled() {
    return distancedTraveled;
  }

  public int getDonutsConsumed() {
    return donutsConsumed;
  }

  public Vector2D getPlayerPosition() {
    return playerPosition;
  }

  public String getSpriteId() {
    return spriteId;
  }
}
