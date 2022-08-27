package technology.sola.sketchy.browser;

import technology.sola.engine.platform.browser.tools.SimpleSolaBrowserFileServer;

import java.io.IOException;

public class DevServerMain {
  public static void main(String[] args) throws IOException {
    SimpleSolaBrowserFileServer simpleSolaBrowserFileServer = new SimpleSolaBrowserFileServer(
      "browser/build",
      "assets"
    );

    simpleSolaBrowserFileServer.start(1337);
  }
}
