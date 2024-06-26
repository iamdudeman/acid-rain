package technology.sola.acidrain.game.system.chunk;

import technology.sola.acidrain.game.component.TileComponent;
import technology.sola.ecs.Entity;
import technology.sola.ecs.World;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.BlendModeComponent;
import technology.sola.engine.graphics.components.LayerComponent;
import technology.sola.engine.graphics.components.SpriteComponent;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.acidrain.game.Constants;
import technology.sola.acidrain.game.SpriteCache;
import technology.sola.acidrain.game.component.PickupComponent;
import technology.sola.engine.physics.component.collider.ColliderShapeCircle;

public class Chunk {
  public static final int TILE_SIZE = 20;
  public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
  public static final int COLUMNS = 16;
  public static final int ROWS = 16;

  private final ChunkId chunkId;
  private final TileComponent[][] tileComponents;
  private boolean isLoaded = false;

  public Chunk(ChunkId chunkId, TileComponent[][] tileComponents) {
    this.chunkId = chunkId;
    this.tileComponents = tileComponents;
  }

  public void loadChunk(World world) {
    if (isLoaded) {
      return;
    }

    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        TileComponent tileComponent = tileComponents[column][row];
        TileType tileType = tileComponent.getTileType();
        float x = chunkId.getX(column);
        float y = chunkId.getY(row);

        Entity newEntity = world.createEntity(
          tileComponent,
          new TransformComponent(x, y),
          new SpriteComponent(SpriteCache.get(tileType.assetId, tileType.variation))
        );

        if (tileType.assetId.equals(Constants.Assets.Sprites.CLIFF)) {
          newEntity.addComponent(
            new ColliderComponent(new ColliderShapeCircle(Chunk.HALF_TILE_SIZE))
              .setTags(Constants.ColliderTags.TILE)
              .setIgnoreTags(Constants.ColliderTags.TILE)
          );
        }

        if (tileComponent.hasPickup()) {
          world.createEntity(
            new TransformComponent(x + 6, y + 6),
            new PickupComponent(tileComponent),
            new LayerComponent(Constants.Layers.FOREGROUND),
            new SpriteComponent(SpriteCache.get(Constants.Assets.Sprites.DONUT, "main")),
            new BlendModeComponent(BlendMode.MASK),
            new ColliderComponent(new ColliderShapeCircle(3))
              .setSensor(true)
              .setTags(Constants.ColliderTags.TILE)
              .setIgnoreTags(Constants.ColliderTags.TILE)
          );
        }
      }
    }

    isLoaded = true;
  }

  public void unloadChunk(World world) {
    if (!isLoaded) {
      return;
    }

    for (var view : world.createView().of(TileComponent.class).getEntries()) {
      if (view.c1().getChunkId().equals(chunkId)) {
        view.entity().destroy();
      }
    }

    for (var view : world.createView().of(PickupComponent.class).getEntries()) {
      if (view.c1().hostTile().getChunkId().equals(chunkId)) {
        view.entity().destroy();
      }
    }

    isLoaded = false;
  }
}
