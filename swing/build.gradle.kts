plugins {
  id("application")
  id("sola.java-conventions")
}

application {
  mainClass.set("${project.properties["basePackage"]}.swing.SwingMain")
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-swing:${project.properties["solaVersion"]}")
  implementation(project(":game"))
}
