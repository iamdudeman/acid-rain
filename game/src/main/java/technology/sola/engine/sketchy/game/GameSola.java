package technology.sola.engine.sketchy.game;

import technology.sola.ecs.Component;
import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.assets.graphics.SpriteSheet;
import technology.sola.engine.core.Sola;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.core.component.TransformComponent;
import technology.sola.engine.core.module.graphics.SolaGraphics;
import technology.sola.engine.core.module.physics.SolaPhysics;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.components.RectangleRendererComponent;
import technology.sola.engine.graphics.components.sprite.SpriteComponent;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.input.Key;
import technology.sola.engine.physics.component.ColliderComponent;
import technology.sola.engine.physics.component.DynamicBodyComponent;

import java.io.Serial;

public class GameSola extends Sola {
  private SolaGraphics solaGraphics;
  private SolaPhysics solaPhysics;

  @Override
  protected SolaConfiguration getConfiguration() {
    return new SolaConfiguration("Game", 800, 600, 30, true);
  }

  @Override
  protected void onInit() {
    solaPhysics = SolaPhysics.createInstance(eventHub, solaEcs);
    solaGraphics = SolaGraphics.createInstance(solaEcs, platform.getRenderer(), assetLoaderProvider);

    assetLoaderProvider.get(SpriteSheet.class)
      .addAssetMapping("test", "assets/test_tiles_spritesheet.json");

    solaEcs.addSystems(new PlayerSystem());
    solaEcs.setWorld(buildWorld());
  }

  @Override
  protected void onRender(Renderer renderer) {
    renderer.clear();

    solaGraphics.render();
  }

  private World buildWorld() {
    World world = new World(100);

    world.createEntity()
      .addComponent(new PlayerComponent())
      .addComponent(new TransformComponent(200, 300))
      .addComponent(new SpriteComponent("test", "blue"))
      .addComponent(ColliderComponent.aabb(16, 16))
      .addComponent(new DynamicBodyComponent())
      .setName("player");

    world.createEntity()
      .addComponent(new TransformComponent(150, 400, 200, 75f))
      .addComponent(new RectangleRendererComponent(Color.WHITE))
      .addComponent(ColliderComponent.aabb());

    return world;
  }

  private record PlayerComponent() implements Component {
    @Serial
    private static final long serialVersionUID = -8026881223157823822L;
  }

  private class PlayerSystem extends EcsSystem {
    @Override
    public void update(World world, float deltaTime) {
      world.createView().of(PlayerComponent.class, DynamicBodyComponent.class)
        .forEach(view -> {
          DynamicBodyComponent dynamicBodyComponent = view.c2();

          if (keyboardInput.isKeyHeld(Key.D) && dynamicBodyComponent.getVelocity().x < 150) {
            dynamicBodyComponent.applyForce(150, 0);
          }
          if (keyboardInput.isKeyHeld(Key.A) && dynamicBodyComponent.getVelocity().x > -150) {
            dynamicBodyComponent.applyForce(-150, 0);
          }
          if (dynamicBodyComponent.isGrounded() && keyboardInput.isKeyHeld(Key.SPACE)) {
            dynamicBodyComponent.applyForce(0, -3000);
          } else if (dynamicBodyComponent.getVelocity().y > 0) {
            dynamicBodyComponent.applyForce(0, 1.5f * solaPhysics.getGravitySystem().getGravityConstant() * dynamicBodyComponent.getMaterial().getMass());
          }
        });
    }

    @Override
    public int getOrder() {
      return 0;
    }
  }
}
