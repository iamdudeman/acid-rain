package technology.sola.engine.sketchy.game;

import technology.sola.ecs.SolaEcs;

public class GameSettings {
  private final SolaEcs solaEcs;
  // TODO temporarily always hiding menu by initializing this to true
  private boolean isPlaying = true;
  private boolean isShowMenu = false;

  // TODO controls object with configuration for moving
  // TODO controls option for showing touch controls

  public GameSettings(SolaEcs solaEcs) {
    this.solaEcs = solaEcs;
  }

  public void showMenu() {
    isShowMenu = true;

    solaEcs.systemIterator().forEachRemaining(ecsSystem -> ecsSystem.setActive(false));
  }

  public void hideMenu() {
    isShowMenu = false;

    solaEcs.systemIterator().forEachRemaining(ecsSystem -> ecsSystem.setActive(true));
  }

  public void startPlaying() {
    if (!isPlaying) {
      isPlaying = true;
      hideMenu();
      // TODO stop main menu music and play map music
    }
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  public boolean isShowMenu() {
    return isShowMenu;
  }
}
