package technology.sola.sketchy.javafx;

import technology.sola.engine.core.SolaPlatform;
import technology.sola.engine.core.Sola;
import technology.sola.engine.sketchy.game.AcidRainSola;
import technology.sola.engine.platform.javafx.JavaFxSolaPlatform;

public class JavaFxMain {
  public static void main(String[] args) {
    SolaPlatform solaPlatform = new JavaFxSolaPlatform();
    Sola sola = new AcidRainSola();

    solaPlatform.play(sola);
  }
}
