package technology.sola.engine.sketchy.game.sunlight;

import technology.sola.ecs.EcsSystem;
import technology.sola.ecs.World;
import technology.sola.engine.input.*;
import technology.sola.engine.sketchy.game.Constants;

public class SunlightSystem extends EcsSystem {
  private final KeyboardInput keyboardInput;
  private final MouseInput mouseInput;
  public SunlightSystem(KeyboardInput keyboardInput, MouseInput mouseInput)
  {
    this.keyboardInput = keyboardInput;
    this.mouseInput = mouseInput;
  }

  @Override
  public void update(World world, float dt) {
    world.findEntityByName(Constants.EntityNames.SUNLIGHT).ifPresent(sunlightEntity -> {
      SunlightBarComponent sunlightBarComponent = sunlightEntity.getComponent(SunlightBarComponent.class);

      if (keyboardInput.isKeyPressed(Key.SPACE) || (mouseInput.isMouseClicked(MouseButton.PRIMARY) &&
        mouseInput.getMousePosition().y > 320 - 48)) {
        sunlightBarComponent.setIsDraining(!sunlightBarComponent.isDraining());
      }

      if (sunlightBarComponent.isDraining()) {
        sunlightBarComponent.startDraining();
      }
    });
  }
}
