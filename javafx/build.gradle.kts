plugins {
  id("application")
  id("sola.java-conventions")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

application {
  mainClass.set("${project.properties["basePackage"]}.javafx.JavaFxMain")
}

javafx {
  modules("javafx.controls")
  version = "17"
}

repositories {
  mavenCentral()

  maven {
    url = uri("https://jitpack.io")
  }
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-javafx:${project.properties["solaVersion"]}")
  implementation(project(":game"))

  val osClassifier = getOsClassifier()

  runtimeOnly("org.openjfx", "javafx-base", "17.0.2", classifier = osClassifier)
  runtimeOnly("org.openjfx", "javafx-controls", "17.0.2", classifier = osClassifier)
  runtimeOnly("org.openjfx", "javafx-graphics", "17.0.2", classifier = osClassifier)
}

fun getOsClassifier(): String {
  if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)) {
    return "mac"
  } else if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_UNIX)) {
    return "linux"
  }

  return "win"
}
