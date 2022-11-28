package technology.sola.acidrain.game.event;

import technology.sola.engine.event.Event;

public record GameStatEvent(GameStatType type, int newValue) implements Event {
}
