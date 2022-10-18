package technology.sola.acidrain.game.player;

import technology.sola.ecs.Component;
import technology.sola.acidrain.game.chunk.TileComponent;

public record PickupComponent(TileComponent hostTile) implements Component {
}
