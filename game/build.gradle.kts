plugins {
  id("sola.java-conventions")
}

tasks.jar {
  archiveBaseName.set("${project.properties["gameName"]}-${project.name}")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(files("../libs/sola-engine-fat-0.0.1.jar"))
}
