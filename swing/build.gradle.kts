import technology.sola.plugins.SolaJavaDistributionPluginExtension

plugins {
  id("sola.java-conventions")
}

apply(plugin = "technology.sola.plugins.sola-java-distribution")

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-swing:${project.properties["solaVersion"]}")
  implementation(project(":game"))
}

configure<SolaJavaDistributionPluginExtension> {
  mainClass = "${project.properties["basePackage"]}.${project.name}.SwingMain"
}
