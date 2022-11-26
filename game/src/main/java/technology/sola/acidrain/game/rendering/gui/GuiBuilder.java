package technology.sola.acidrain.game.rendering.gui;

import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.rendering.gui.elements.SunlightBarElement;
import technology.sola.ecs.World;
import technology.sola.engine.core.module.graphics.gui.SolaGui;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.container.StreamGuiElementContainer;
import technology.sola.engine.graphics.gui.elements.control.ButtonGuiElement;

public class GuiBuilder {
  public GuiElement<?> buildInGameGui(SolaGui solaGui, World world) {
    StreamGuiElementContainer streamGuiElementContainer = solaGui.createElement(
      StreamGuiElementContainer::new,
      StreamGuiElementContainer.Properties::new,
      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL).padding.set(3)
    );

    StreamGuiElementContainer sunlightBarContainer = solaGui.createElement(
      StreamGuiElementContainer::new,
      StreamGuiElementContainer.Properties::new,
      p -> p.setVerticalAlignment(StreamGuiElementContainer.VerticalAlignment.BOTTOM).setHorizontalAlignment(StreamGuiElementContainer.HorizontalAlignment.CENTER)
        .setHeight(180).setWidth(AcidRainSola.CANVAS_WIDTH)
    );

    sunlightBarContainer.addChild(solaGui.createElement(
      SunlightBarElement::new,
      SunlightBarElement.Properties::new
    ));

    streamGuiElementContainer.addChild(
      solaGui.createElement(
        TextGuiElement::new,
        TextGuiElement.Properties::new,
        p -> p.setText("Intensity: 1")
      ),
      solaGui.createElement(
        TextGuiElement::new,
        TextGuiElement.Properties::new,
        p -> p.setText("Donuts: 0")
      ),
      sunlightBarContainer
    );

    return streamGuiElementContainer;
  }

  public GuiElement<?> buildGameOverGui(SolaGui solaGui, World world) {
    StreamGuiElementContainer streamGuiElementContainer = solaGui.createElement(
      StreamGuiElementContainer::new,
      StreamGuiElementContainer.Properties::new,
      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL).padding.set(3)
    );

    streamGuiElementContainer.addChild(
      solaGui.createElement(
        TextGuiElement::new,
        TextGuiElement.Properties::new,
        p -> p.setText("Game over")
      ),
      solaGui.createElement(
        TextGuiElement::new,
        TextGuiElement.Properties::new,
        p -> p.setText("Donuts eated: 0")
      ),
      solaGui.createElement(
        TextGuiElement::new,
        TextGuiElement.Properties::new,
        p -> p.setText("Distance traveled: 0")
      ),
      solaGui.createElement(
        ButtonGuiElement::new,
        ButtonGuiElement.Properties::new,
        p -> p.setText("Play again?")
      )
    );

    return streamGuiElementContainer;
  }
}
