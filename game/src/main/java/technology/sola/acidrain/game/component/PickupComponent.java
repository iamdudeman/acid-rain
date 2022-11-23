package technology.sola.acidrain.game.component;

import technology.sola.ecs.Component;

public record PickupComponent(TileComponent hostTile) implements Component {
}
