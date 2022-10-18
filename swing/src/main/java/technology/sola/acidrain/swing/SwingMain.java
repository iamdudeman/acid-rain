package technology.sola.acidrain.swing;

import technology.sola.engine.platform.swing.SwingSolaPlatform;
import technology.sola.acidrain.game.AcidRainSola;

public class SwingMain {
  public static void main(String[] args) {
    SwingSolaPlatform solaPlatform = new SwingSolaPlatform();

    solaPlatform.setWindowSize(960, 640);
    solaPlatform.play(new AcidRainSola());
  }
}
