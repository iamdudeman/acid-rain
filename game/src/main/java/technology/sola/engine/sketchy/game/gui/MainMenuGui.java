package technology.sola.engine.sketchy.game.gui;

import technology.sola.engine.core.module.graphics.gui.SolaGui;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.container.StreamGuiElementContainer;
import technology.sola.engine.graphics.gui.elements.control.ButtonGuiElement;
import technology.sola.engine.sketchy.game.GameSettings;

public class MainMenuGui {
  public static GuiElement<?> buildRootElement(SolaGui solaGui, GameSettings gameSettings) {
    StreamGuiElementContainer streamGuiElementContainer = solaGui.createElement(
      StreamGuiElementContainer::new,
      StreamGuiElementContainer.Properties::new,
      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL)
        .setPreferredDimensions(480, 320)
        .padding.set(8)
    );

    TextGuiElement temporaryTitle = solaGui.createElement(
      TextGuiElement::new,
      TextGuiElement.Properties::new,
      p -> p.setText("Acid Rain").setColorText(Color.WHITE).margin.setBottom(16)
    );

    ButtonGuiElement playButton = solaGui.createElement(
      ButtonGuiElement::new,
      ButtonGuiElement.Properties::new,
      p -> p.setText("Play")
    );

    playButton.setOnAction(gameSettings::startPlaying);

    streamGuiElementContainer.addChild(
      temporaryTitle,
      playButton
    );

    return streamGuiElementContainer;
  }

  private MainMenuGui() {
  }
}
