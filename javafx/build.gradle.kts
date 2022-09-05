plugins {
  id("application")
  id("sola.java-conventions")
}

application {
  mainClass.set("${project.properties["basePackage"]}.javafx.JavaFxMain")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(files("../libs/sola-engine-javafx-fat-${project.properties["solaVersion"]}.jar"))
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
