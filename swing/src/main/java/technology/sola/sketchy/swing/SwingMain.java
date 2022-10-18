package technology.sola.sketchy.swing;

import technology.sola.engine.platform.swing.SwingSolaPlatform;
import technology.sola.sketchy.game.AcidRainSola;

public class SwingMain {
  public static void main(String[] args) {
    SwingSolaPlatform solaPlatform = new SwingSolaPlatform();

    solaPlatform.setWindowSize(960, 640);
    solaPlatform.play(new AcidRainSola());
  }
}
