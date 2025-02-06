import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.21"
  application
  id("maven-publish")
  alias(deps.plugins.spotless.maven.plugin)
  alias(deps.plugins.buildConfig.constants.plugin)
  id("jacoco")
}

repositories {
  mavenLocal()
  mavenCentral()
  google()
  maven { setUrl("https://jitpack.io") }
}

group = "org.smartregister"

version = "2.3.13-SNAPSHOT"

description = "fhircore-tooling (efsity)"

java.sourceCompatibility = JavaVersion.VERSION_11

java.targetCompatibility = JavaVersion.VERSION_11

buildConfig { buildConfigField("String", "RELEASE_VERSION", "\"${version}\"") }

publishing {
  repositories {
    maven {
      name = "Sonatype"
      url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      credentials(PasswordCredentials::class)
    }
  }
  publications.create<MavenPublication>("maven") {
    artifactId = "efsity"
    artifact(tasks.assemble)
  }
}

allprojects {
  apply(plugin = "com.diffplug.spotless")

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("${layout.buildDirectory}/**/*.kt")
      targetExclude("**/Extensions.kt")
      ktfmt().googleStyle()
    }

    kotlinGradle {
      target("*.gradle.kts")
      ktlint()
      ktfmt().googleStyle()
    }
    java {
      importOrder()
      removeUnusedImports()
      googleJavaFormat()
      formatAnnotations()
    }
  }
}

dependencies {
  implementation(deps.bundles.cqf.cql)
  implementation(deps.bundles.hapi.fhir.core)
  implementation(deps.bundles.opencds)
  implementation(deps.bundles.jackson)
  implementation(deps.bundles.jackson)
  implementation(deps.caffeine)
  implementation(deps.commons.compress)
  implementation(deps.gson)
  implementation(deps.hapi.fhir.structures.r4)
  implementation(deps.hapi.fhir.utilities)
  implementation(deps.json)
  implementation(deps.jsonschemafriend)
  implementation(deps.picocli)
  implementation(deps.xstream)
  implementation(deps.icu4j)
  implementation(deps.javafaker) { exclude(group = "org.yaml") }
  implementation(deps.snakeyaml)
  implementation("ca.uhn.hapi.fhir:hapi-fhir-validation:6.8.0")
  implementation("org.smartregister:fhir-common-utils:1.0.2-SNAPSHOT")

  testImplementation(kotlin("test"))
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.mockito:mockito-inline:3.12.4")
}

tasks.withType<JavaCompile> { options.encoding = deps.versions.project.build.sourceEncoding.get() }

tasks.withType<Javadoc> { options.encoding = deps.versions.project.build.sourceEncoding.get() }

tasks.test { useJUnitPlatform() }

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }

tasks.getByName<Zip>("distZip").enabled = false

tasks.getByName<Tar>("distTar").enabled = false

tasks.register<Jar>("releaseJar") {
  dependsOn.addAll(
    listOf("compileJava", "compileKotlin", "processResources"),
  )
  archiveClassifier.set("release")
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  manifest {
    attributes(
      mapOf(
        "Manifest-Version" to "1.0",
        "Main-Class" to application.mainClass,
        "Build-Jdk-Spec" to 11,
        "Created-By" to "Efsity (FHIR Core Tooling)",
      ),
    )
  }
  val sourcesMain = sourceSets.main.get()
  val contents =
    configurations.runtimeClasspath
      .get()
      .filter {
        !it.name.contains("org.hl7.fhir.dstu2016may") &&
          !it.name.contains("hapi-fhir-structures-dstu3") &&
          !it.name.contains("hapi-fhir-structures-hl7org-dstu2")
      }
      .map { if (it.isDirectory) it else zipTree(it) } + sourcesMain.output
  from(contents) {
    exclude(
      "META-INF/*.RSA",
      "META-INF/*.SF",
      "META-INF/*.DSA",
    )
  }
}

tasks.assemble {
  dependsOn(tasks["releaseJar"])
  this.outputs.files(tasks["releaseJar"].outputs)
}

application { mainClass.set("org.smartregister.Main") }

jacoco { toolVersion = "0.8.7" }
