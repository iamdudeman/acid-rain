plugins {
  id("sola.java-conventions")
}

apply(plugin = "technology.sola.plugins.sola-web-distribution")

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-browser:${project.properties["solaVersion"]}")
  implementation(project(":game"))
}
