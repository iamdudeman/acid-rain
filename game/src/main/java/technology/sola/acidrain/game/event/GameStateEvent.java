package technology.sola.acidrain.game.event;

import technology.sola.engine.event.Event;

public record GameStateEvent(GameState gameState) implements Event {
}
