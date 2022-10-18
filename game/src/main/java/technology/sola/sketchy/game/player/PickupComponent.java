package technology.sola.sketchy.game.player;

import technology.sola.ecs.Component;
import technology.sola.sketchy.game.chunk.TileComponent;

public record PickupComponent(TileComponent hostTile) implements Component {
}
