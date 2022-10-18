package technology.sola.acidrain.javafx;

import technology.sola.acidrain.game.AcidRainSola;
import technology.sola.engine.platform.javafx.JavaFxSolaPlatform;

public class JavaFxMain {
  public static void main(String[] args) {
    JavaFxSolaPlatform solaPlatform = new JavaFxSolaPlatform();

    solaPlatform.setWindowSize(960, 640);
    solaPlatform.play(new AcidRainSola());
  }
}
