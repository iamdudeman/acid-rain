plugins {
  id("sola.java-conventions")
}

repositories {
  mavenCentral()
}

dependencies {
  api(files("../libs/sola-engine-browser-fat-${project.properties["solaVersion"]}.jar"))
  implementation(project(":game"))

  // teavm
  runtimeOnly("org.teavm:teavm-classlib:0.7.0")
  runtimeOnly("org.teavm:teavm-extras-slf4j:0.7.0")
}

task("generateBrowserExampleHtmlAndJs", type = JavaExec::class) {
  group = "build"

  dependsOn(tasks.getByPath("assemble"))

  classpath = sourceSets.main.get().runtimeClasspath
  setArgsString("build ${project.name}-${project.version}.jar")
  mainClass.set("${project.properties["basePackage"]}.browser.GenerateBrowserFilesMain")
}
