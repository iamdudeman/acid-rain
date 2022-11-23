package technology.sola.acidrain.game;

import technology.sola.engine.physics.component.ColliderComponent;

public class Constants {
  public static final class EntityNames {
    public static final String CAMERA = "camera";
    public static final String PLAYER = "player";

    private EntityNames() {
    }
  }

  public enum ColliderTags implements ColliderComponent.ColliderTag {
    TILE
  }

  public static final class Assets {
    public static final class Sprites {
      public static final String SPRITE_SHEET_ID = "sprites";
      public static final String GRASS = "grass-1";
      public static final String GRASS2 = "grass-2";
      public static final String GRASS3 = "grass-3";
      public static final String DIRT = "dirt-1";
      public static final String DIRT2 = "dirt-2";
      public static final String DIRT3 = "dirt-3";
      public static final String CLIFF = "cliff";
      public static final String ERASED = "erased";
      public static final String DUCK = "duck";

      public static final String DONUT = "donut";

      private Sprites() {
      }
    }

    public static final class Audio {
      public static final String GAME = "game";
      public static final String QUACK = "quack";

      private Audio() {
      }
    }

    private Assets() {
    }
  }

  private Constants() {
  }
}
