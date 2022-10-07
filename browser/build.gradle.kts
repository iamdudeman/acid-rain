plugins {
  id("sola.java-conventions")
}

repositories {
  mavenCentral()

  maven {
    url = uri("https://jitpack.io")
  }
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-browser:${project.properties["solaVersion"]}")
  implementation(project(":game"))
}

task("generateBrowserExampleHtmlAndJs", type = JavaExec::class) {
  group = "build"

  dependsOn(tasks.getByPath("assemble"))

  classpath = sourceSets.main.get().runtimeClasspath
  setArgsString("build ${project.name}-${project.version}.jar")
  mainClass.set("${project.properties["basePackage"]}.browser.GenerateBrowserFilesMain")
}
