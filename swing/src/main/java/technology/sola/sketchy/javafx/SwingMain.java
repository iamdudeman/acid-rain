package technology.sola.sketchy.javafx;

import technology.sola.engine.core.SolaPlatform;
import technology.sola.engine.core.Sola;
import technology.sola.engine.platform.swing.SwingSolaPlatform;
import technology.sola.engine.sketchy.game.SketchyLifeSola;

public class SwingMain {
  public static void main(String[] args) {
    SolaPlatform solaPlatform = new SwingSolaPlatform();
    Sola sola = new SketchyLifeSola();

    solaPlatform.play(sola);
  }
}