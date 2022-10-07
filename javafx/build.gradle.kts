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
}

tasks.withType<Jar>() {
  manifest {
    attributes["Main-Class"] = "${project.properties["basePackage"]}.javafx.JavaFxMain"
  }
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  dependsOn(configurations.runtimeClasspath)

  from({
    configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
  })

  archiveBaseName.set("${project.properties["gameName"]}-${project.name}")
}

tasks.withType<Zip>() {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Tar>() {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
