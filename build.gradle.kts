subprojects {
  buildscript {
    repositories {
      maven {
        url = uri("https://jitpack.io")
      }
    }

    dependencies {
      classpath("com.github.iamdudeman.sola-game-engine:technology.sola.plugins.sola-java-distribution.gradle.plugin:${project.properties["solaVersion"]}")
      classpath("com.github.iamdudeman.sola-game-engine:technology.sola.plugins.sola-web-distribution.gradle.plugin:${project.properties["solaVersion"]}")
    }
  }
}
