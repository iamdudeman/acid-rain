package technology.sola.engine.sketchy.game.player;

import technology.sola.ecs.Component;
import technology.sola.engine.sketchy.game.chunk.TileComponent;

public record PickupComponent(TileComponent hostTile) implements Component {
}
