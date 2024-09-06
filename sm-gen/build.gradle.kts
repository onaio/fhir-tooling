import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.0"
  id("com.diffplug.spotless") version "6.25.0"
}

group = "org.example"

version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  testImplementation(kotlin("test"))
  implementation("com.github.ajalt.clikt:clikt:3.4.0")
  implementation("org.apache.poi:poi:3.17")
  implementation("org.apache.poi:poi-ooxml:3.17")
  implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:5.4.0")
  implementation("ca.uhn.hapi.fhir:hapi-fhir-validation:5.4.0")
  implementation(kotlin("stdlib-jdk8"))
}

tasks.test { useJUnitPlatform() }

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

val fatJar =
  task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    // manifest Main-Class attribute is optional.
    // (Used only to provide default main class for executable jar)
    manifest {
      attributes["Main-Class"] =
        "example.HelloWorldKt" // fully qualified class name of default main class
    }
    from(configurations.compileClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
  }

tasks { "build" { dependsOn(fatJar) } }

kotlin {
  jvmToolchain { (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11)) }
}

spotless {
  kotlin {
    target("**/*.kt")
    ktlint("0.49.0")
    ktfmt().googleStyle()
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktlint("0.49.0")
    ktfmt().googleStyle()
  }

  format("xml") {
    target("**/*.xml")
    indentWithSpaces()
    trimTrailingWhitespace()
    endWithNewline()
  }

  format("json") {
    target("**/*.json")
    indentWithSpaces(2)
    trimTrailingWhitespace()
  }
}
