plugins {
  id("sola.java-conventions")
}

tasks.jar {
  archiveBaseName.set("${project.properties["gameName"]}-${project.name}")
}

repositories {
  mavenCentral()

  maven {
    url = uri("https://jitpack.io")
  }
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:sola-engine:${project.properties["solaVersion"]}")
}
