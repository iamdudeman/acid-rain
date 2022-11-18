plugins {
  id("sola.java-conventions")
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-swing:${project.properties["solaVersion"]}")
  implementation(project(":game"))
}

tasks.withType<Zip> {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Tar> {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

task("distFatJar", Jar::class) {
  group = "distribution"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

  archiveBaseName.set("${project.properties["gameName"]}-${project.name}")

  manifest {
    attributes["Main-Class"] = "${project.properties["basePackage"]}.swing.SwingMain"
  }

  val dependencies = configurations.runtimeClasspath.get().map(::zipTree)

  from(dependencies)
  from("${project.rootDir}/assets") {
    into("assets")
  }
  with(tasks.jar.get())
  dependsOn(configurations.runtimeClasspath)
}
