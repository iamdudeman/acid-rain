plugins {
  id("java")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

repositories {
  mavenCentral()

  maven {
    url = uri("https://jitpack.io")
  }
}

dependencies {
  // Test
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
  testImplementation(platform("org.junit:junit-bom:5.10.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}
