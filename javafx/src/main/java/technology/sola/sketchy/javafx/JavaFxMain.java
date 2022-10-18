package technology.sola.sketchy.javafx;

import technology.sola.sketchy.game.AcidRainSola;
import technology.sola.engine.platform.javafx.JavaFxSolaPlatform;

public class JavaFxMain {
  public static void main(String[] args) {
    JavaFxSolaPlatform solaPlatform = new JavaFxSolaPlatform();

    solaPlatform.setWindowSize(960, 640);
    solaPlatform.play(new AcidRainSola());
  }
}
