plugins {
  id("sola.java-conventions")
}

dependencies {
  implementation("com.github.iamdudeman.sola-game-engine:platform-browser:${project.properties["solaVersion"]}")
  implementation(project(":game"))
}

task("generateWebHtmlAndJs", type = JavaExec::class) {
  group = "build"

  dependsOn(tasks.getByPath("assemble"))

  classpath = sourceSets.main.get().runtimeClasspath
  setArgsString("build ${project.name}-${project.version}.jar")
  mainClass.set("${project.properties["basePackage"]}.browser.GenerateBrowserFilesMain")
}

tasks.assemble {
  finalizedBy(tasks.getByName("generateWebHtmlAndJs"))
}
