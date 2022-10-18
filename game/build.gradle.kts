plugins {
  id("sola.java-conventions")
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:sola-engine:${project.properties["solaVersion"]}")
}
