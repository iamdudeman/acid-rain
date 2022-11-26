package technology.sola.acidrain.game.rendering.gui.elements;

import technology.sola.acidrain.game.component.PlayerComponent;
import technology.sola.engine.core.module.graphics.gui.SolaGui;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.properties.GuiElementGlobalProperties;
import technology.sola.engine.graphics.gui.properties.GuiElementProperties;
import technology.sola.engine.graphics.renderer.BlendMode;
import technology.sola.engine.graphics.renderer.Renderer;

public class SunlightBarElement extends GuiElement<SunlightBarElement.Properties> {
  public static final int SUNLIGHT_BAR_HEIGHT = 12;
  private static final int SUNLIGHT_BAR_WIDTH = 220;
  private static final Color SUNLIGHT_BAR_COLOR = new Color(200, 255, 215, 0);
  private PlayerComponent playerComponent;

  public SunlightBarElement(SolaGui solaGui, Properties properties) {
    super(solaGui, properties);
    properties.setFocusable(false);

    setOnMouseDownCallback(mouseEvent -> playerComponent.setUsingSunlight(!playerComponent.isUsingSunlight()));
  }

  @Override
  public int getContentWidth() {
    return SUNLIGHT_BAR_WIDTH;
  }

  @Override
  public int getContentHeight() {
    return SUNLIGHT_BAR_HEIGHT;
  }

  @Override
  public void recalculateLayout() {

  }

  @Override
  public void renderSelf(Renderer renderer, int x, int y) {
    renderer.setBlendMode(BlendMode.NORMAL);
    renderer.fillRect(x, y, properties().filledPercentage * SUNLIGHT_BAR_WIDTH, SUNLIGHT_BAR_HEIGHT, SUNLIGHT_BAR_COLOR);
    renderer.setBlendMode(BlendMode.NO_BLENDING);
    renderer.drawRect(x, y, SUNLIGHT_BAR_WIDTH, SUNLIGHT_BAR_HEIGHT, Color.BLACK);
  }

  public static class Properties extends GuiElementProperties {
    private float filledPercentage;

    public Properties(GuiElementGlobalProperties globalProperties) {
      super(globalProperties);
    }

    public float getFilledPercentage() {
      return filledPercentage;
    }

    public Properties setFilledPercentage(float filledPercentage) {
      this.filledPercentage = filledPercentage;

      return this;
    }
  }
}