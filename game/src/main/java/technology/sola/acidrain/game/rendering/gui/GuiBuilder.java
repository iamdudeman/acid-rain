package technology.sola.acidrain.game.rendering.gui;

import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.acidrain.game.GameStatistics;
import technology.sola.acidrain.game.event.GameStatEvent;
import technology.sola.acidrain.game.event.GameStatType;
import technology.sola.acidrain.game.event.GameState;
import technology.sola.acidrain.game.event.GameStateEvent;
import technology.sola.acidrain.game.rendering.gui.elements.SunlightBarElement;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.SolaGuiDocument;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.container.StreamGuiElementContainer;

public class GuiBuilder {
//  private final SolaGuiDocument solaGuiDocument;
//  private final GuiElement<?> inGameRoot;
//  private final GuiElement<?> gameOverRoot;
//
//  public GuiBuilder(SolaGuiDocument solaGuiDocument) {
//    this.solaGuiDocument = solaGuiDocument;
//
//    inGameRoot = buildInGameGui();
//    gameOverRoot = buildGameOverGui();
//
//    solaGuiDocument.eventHub.add(GameStateEvent.class, event -> {
//      if (event.gameState() == GameState.GAME_OVER) {
//        ((TextGuiElement) gameOverRoot.getElementById("distance"))
//          .properties().setText("Distance traveled: " + Math.round(GameStatistics.getDistanceTraveled()));
//        solaGuiDocument.setGuiRoot(gameOverRoot);
//      } else if (event.gameState() == GameState.RESTART) {
//        solaGuiDocument.setGuiRoot(inGameRoot);
//      }
//    });
//  }
//
//  public GuiElement<?> getInitialRoot() {
//    return inGameRoot;
//  }
//
//  private GuiElement<?> buildInGameGui() {
//    TextGuiElement intensityElement = solaGuiDocument.createElement(
//      TextGuiElement::new,
//      p -> p.setText("Intensity: 1")
//    );
//    TextGuiElement donutsEatedElement = solaGuiDocument.createElement(
//      TextGuiElement::new,
//      p -> p.setText("Donuts: 0")
//    );
//
//    solaGuiDocument.eventHub.add(GameStatEvent.class, event -> {
//      switch (event.type()) {
//        case DONUTS_EATED -> donutsEatedElement.properties().setText("Donuts: " + event.newValue());
//        case INTENSITY -> intensityElement.properties().setText("Intensity: " + event.newValue());
//      }
//    });
//
//    return solaGuiDocument.createElement(
//      StreamGuiElementContainer::new,
//      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL),
//      solaGuiDocument.createElement(
//        StreamGuiElementContainer::new,
//        p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL).padding.set(3).setBackgroundColor(new Color(150, 255, 255, 255)),
//        intensityElement,
//        donutsEatedElement
//      ),
//      solaGuiDocument.createElement(
//        StreamGuiElementContainer::new,
//        p -> p.setVerticalAlignment(StreamGuiElementContainer.VerticalAlignment.BOTTOM).setHorizontalAlignment(StreamGuiElementContainer.HorizontalAlignment.CENTER)
//          .setHeight(188).setWidth(AcidRainSola.CANVAS_WIDTH),
//        solaGuiDocument.createElement(
//          SunlightBarElement::new
//        )
//      )
//    );
//  }
//
//  private GuiElement<?> buildGameOverGui() {
//    TextGuiElement donutsEatedElement = solaGuiDocument.createElement(
//      TextGuiElement::new,
//      p -> p.setText("Donuts eated: 0")
//    );
//
//    solaGuiDocument.eventHub.add(GameStatEvent.class, event -> {
//      if (event.type() == GameStatType.DONUTS_EATED) {
//        donutsEatedElement.properties().setText("Donuts eated: " + event.newValue());
//      }
//    });
//
//    return solaGuiDocument.createElement(
//      StreamGuiElementContainer::new,
//      p -> p.setDirection(StreamGuiElementContainer.Direction.VERTICAL).padding.set(3).setBackgroundColor(new Color(150, 255, 255, 255)),
//      solaGuiDocument.createElement(
//        TextGuiElement::new,
//        p -> p.setText("Game over")
//      ),
//      donutsEatedElement,
//      solaGuiDocument.createElement(
//        TextGuiElement::new,
//        p -> p.setText("Distance traveled: 0").setId("distance")
//      ),
//      solaGuiDocument.createElement(
//        TextGuiElement::new,
//        p -> p.setText("Space or click to restart")
//      )
//    );
//  }
}
