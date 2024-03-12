package technology.sola.acidrain.browser;

import technology.sola.engine.platform.browser.tools.SolaBrowserFileBuilder;

public class GenerateBrowserFilesMain {
  public static void main(String[] args) {
    String buildDirectory = args.length > 0 ? args[0] : "browser/build";
    String jarFile = args.length > 1 ? args[1] : "browser-0.0.1-SNAPSHOT.jar";

    System.out.println("Generating html and js for BrowserMain using [build/libs/" + jarFile + "]");
    System.out.println("Output at:");

    SolaBrowserFileBuilder solaBrowserFileBuilder = new SolaBrowserFileBuilder(buildDirectory);

    solaBrowserFileBuilder.transpileSolaJar(
      "build/libs/" + jarFile,
      BrowserMain.class.getName(),
      false // todo re-enable when bug in teavm is fixed
    );

    solaBrowserFileBuilder.createIndexHtmlWithOverlay();
  }
}
