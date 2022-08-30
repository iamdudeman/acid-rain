package technology.sola.engine.sketchy.game;

public class Constants {
  public static final class EntityNames {
    public static final String CAMERA = "camera";
    public static final String PLAYER = "player";

    private EntityNames() {
    }
  }

  public static final class Layers {
    public static final String BACKGROUND = "back";
    public static final String FOREGROUND = "front";

    private Layers() {
    }
  }

  public static final class Assets {
    public static final class Sprites {
      public static final String SPRITE_SHEET_ID = "sprites";
      public static final String GRASS = "grass";
      public static final String DIRT = "dirt";
      public static final String CLIFF = "cliff";
      public static final String ERASED = "erased";

      private Sprites() {
      }
    }

    public static final class Audio {
      public static final String MAP = "map";

      private Audio() {
      }
    }

    private Assets() {
    }
  }

  private Constants() {
  }
}
