package technology.sola.acidrain.game.rendering.gui;

import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.event.GameStatEvent;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.acidrain.game.rendering.gui.elements.SunlightBarElement;
import technology.sola.engine.core.module.graphics.gui.SolaGui;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.container.StreamGuiElementContainer;
import technology.sola.engine.graphics.gui.elements.control.ButtonGuiElement;

public class GuiBuilder {
  private final SolaGui solaGui;
  private final GuiElement<?> inGameRoot;
  private final GuiElement<?> gameOverRoot;

  public GuiBuilder(SolaGui solaGui) {
    this.solaGui = solaGui;

    inGameRoot = buildInGameGui();
    gameOverRoot = buildGameOverGui();

    solaGui.eventHub.add(GameStateEvent.class, event -> {
      if (event.gameState() == GameState.GAME_OVER) {
        solaGui.setGuiRoot(gameOverRoot);
      } else if (event.gameState() == GameState.RESTART) {
        solaGui.setGuiRoot(inGameRoot);
      }
    });
  }

  public GuiElement<?> getRoot() {
    return inGameRoot;
  }

  private GuiElement<?> buildInGameGui() {
    TextGuiElement intensityElement = solaGui.createElement(
      TextGuiElement::new,
      TextGuiElement.Properties::new,
      p -> p.setText("Intensity: 1")
    );
    TextGuiElement donutsEatedElement = solaGui.createElement(
      TextGuiElement::new,
      TextGuiElement.Properties::new,
      p -> p.setText("Donuts: 0")
    );

    solaGui.eventHub.add(GameStatEvent.class, event -> {
      switch (event.type()) {
        case DONUTS_EATED -> donutsEatedElement.properties().setText("Donuts: " + event.newValue());
        case INTENSITY -> intensityElement.properties().setText("Intensity: " + event.newValue());
      }
    });

    return solaGui.createElement(
      StreamGuiElementContainer::new,
      StreamGuiElementContainer.Properties::new,
      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL)
    ).addChild(
      solaGui.createElement(
        StreamGuiElementContainer::new,
        StreamGuiElementContainer.Properties::new,
        p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL).padding.set(3).setBackgroundColor(new Color(150, 255, 255, 255))
      ).addChild(
        intensityElement,
        donutsEatedElement
      ),
      solaGui.createElement(
        StreamGuiElementContainer::new,
        StreamGuiElementContainer.Properties::new,
        p -> p.setVerticalAlignment(StreamGuiElementContainer.VerticalAlignment.BOTTOM).setHorizontalAlignment(StreamGuiElementContainer.HorizontalAlignment.CENTER)
          .setHeight(188).setWidth(AcidRainSola.CANVAS_WIDTH)
      ).addChild(
        solaGui.createElement(
          SunlightBarElement::new,
          SunlightBarElement.Properties::new
        )
      )
    );
  }

  // TODO implement this part next
  private GuiElement<?> buildGameOverGui() {
    return solaGui.createElement(
      StreamGuiElementContainer::new,
      StreamGuiElementContainer.Properties::new,
      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL).padding.set(3)
    ).addChild(
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
  }
}
