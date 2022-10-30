package technology.sola.acidrain.game.event;

import technology.sola.engine.event.Event;
import technology.sola.math.linear.Vector2D;

public class GameStateEvent implements Event {
  private final GameState gameState;
  private final Vector2D playerPosition;
  private final String spriteId;

  public GameStateEvent(GameState gameState) {
    this(gameState, null, null);
  }

  public GameStateEvent(GameState gameState, Vector2D playerPosition, String spriteId) {
    this.gameState = gameState;
    this.playerPosition = playerPosition;
    this.spriteId = spriteId;
  }

  public GameState getMessage() {
    return gameState;
  }

  public Vector2D getPlayerPosition() {
    return playerPosition;
  }

  public String getSpriteId() {
    return spriteId;
  }
}
