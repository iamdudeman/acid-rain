package technology.sola.engine.sketchy.game;

import technology.sola.ecs.World;
import technology.sola.engine.assets.AssetLoader;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.sketchy.game.chunk.TileComponent;
import technology.sola.engine.sketchy.game.player.PickupComponent;
import technology.sola.engine.sketchy.game.player.PlayerComponent;
import technology.sola.math.linear.Vector2D;

public class SketchyLifeRenderer {
  private final AssetLoader<SpriteSheet> spriteSheetAssetLoader;

  public SketchyLifeRenderer(AssetLoader<SpriteSheet> spriteSheetAssetLoader) {
    this.spriteSheetAssetLoader = spriteSheetAssetLoader;
  }

  public void render(Renderer renderer, World world) {
//    world.findEntityByName(Constants.EntityNames.CAMERA).ifPresent(cameraEntity -> {
//      Vector2D cameraTranslate = cameraEntity.getComponent(TransformComponent.class).getTranslate();
//
//      var spriteEntities = world.createView().of(TransformComponent.class, SpriteComponent.class);
//
//      renderer.setBlendMode(BlendMode.NO_BLENDING);
//      for (var view : spriteEntities) {
//        if (view.entity().hasComponent(TileComponent.class)) {
//          view.c2().getSprite(spriteSheetAssetLoader).executeIfLoaded(solaImage -> {
//            Vector2D translate = view.c1().getTranslate().subtract(cameraTranslate);
//
//            renderer.drawImage(translate.x, translate.y, solaImage);
//          });
//        }
//      }
//
//      renderer.setBlendMode(BlendMode.MASK);
//      for (var view : spriteEntities) {
//        if (view.entity().hasComponent(PickupComponent.class) || view.entity().hasComponent(PlayerComponent.class)) {
//          view.c2().getSprite(spriteSheetAssetLoader).executeIfLoaded(solaImage -> {
//            Vector2D translate = view.c1().getTranslate().subtract(cameraTranslate);
//
//            renderer.drawImage(translate.x, translate.y, solaImage);
//          });
//        }
//      }
//    });



    world.findEntityByName(Constants.EntityNames.CAMERA).ifPresent(cameraEntity -> {
      Vector2D cameraTranslate = cameraEntity.getComponent(TransformComponent.class).getTranslate();

      var spriteEntities = world.findEntitiesWithComponents(TransformComponent.class, SpriteComponent.class);

      renderer.setBlendMode(BlendMode.NO_BLENDING);
      for (var entity : spriteEntities) {
        if (entity.hasComponent(TileComponent.class)) {
          entity.getComponent(SpriteComponent.class).getSprite(spriteSheetAssetLoader).executeIfLoaded(solaImage -> {
            Vector2D translate = entity.getComponent(TransformComponent.class).getTranslate().subtract(cameraTranslate);

            renderer.drawImage(translate.x, translate.y, solaImage);
          });
        }
      }

      renderer.setBlendMode(BlendMode.MASK);
      for (var entity : spriteEntities) {
        if (entity.hasComponent(PickupComponent.class) || entity.hasComponent(PlayerComponent.class)) {
          entity.getComponent(SpriteComponent.class).getSprite(spriteSheetAssetLoader).executeIfLoaded(solaImage -> {
            Vector2D translate = entity.getComponent(TransformComponent.class).getTranslate().subtract(cameraTranslate);

            renderer.drawImage(translate.x, translate.y, solaImage);
          });
        }
      }
    });
  }
}
