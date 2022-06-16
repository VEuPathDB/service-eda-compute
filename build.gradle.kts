import org.veupathdb.lib.gradle.container.util.Logger.Level
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest

plugins {
  kotlin("jvm") version "1.7.0"
  java

  id("org.veupathdb.lib.gradle.container.container-utils") version "3.4.1"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}


// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  Constants                                                              ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//

val EdaCommonVersion = "8.1.0"
val EdaCommonRAMLURL = "https://raw.githubusercontent.com/VEuPathDB/EdaCommon/v${EdaCommonVersion}/schema/library.raml"



// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  Container Project Configuration                                        ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//


// configure VEupathDB container plugin
containerBuild {

  // Change if debugging the build process is necessary.
  logLevel = Level.Trace

  // General project level configuration.
  project {

    // Project Name
    name = "demo-service"

    // Project Group
    group = "org.veupathdb.service"

    // Project Version
    version = "1.0.0"

    // Project Root Package
    projectPackage = "org.veupathdb.service.eda"

    // Main Class Name
    mainClassName = "compute.Main"
  }

  // Docker build configuration.
  docker {

    // Docker build context
    context = "."

    // Name of the target docker file
    dockerFile = "Dockerfile"

    // Resulting image tag
    imageName = "example-service"

  }

  generateJaxRS {
    // List of custom arguments to use in the jax-rs code generation command
    // execution.
    arguments = listOf(/*arg1, arg2, arg3*/)

    // Map of custom environment variables to set for the jax-rs code generation
    // command execution.
    environment = mapOf(/*Pair("env-key", "env-val"), Pair("env-key", "env-val")*/)
  }
}


// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  Dependency Management                                                  ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//


repositories {
  mavenCentral()
  mavenLocal()
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/maven-packages")
    credentials {
      username = if (extra.has("gpr.user")) extra["gpr.user"] as String? else System.getenv("GITHUB_USERNAME")
      password = if (extra.has("gpr.key")) extra["gpr.key"] as String? else System.getenv("GITHUB_TOKEN")
    }
  }
}

configurations.all {
  resolutionStrategy {
    cacheChangingModulesFor(0, TimeUnit.SECONDS)
    cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
  }
}

dependencies {

  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk7"))
  implementation(kotlin("stdlib-jdk8"))

  implementation("org.veupathdb.lib:jaxrs-container-core:6.5.1")
  implementation("org.veupathdb.service.eda:eda-common:$EdaCommonVersion")
  implementation("org.veupathdb.lib:compute-platform:1.0-SNAPSHOT") { isChanging = true }

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.0.4")

  // Pico CLI
  implementation("info.picocli:picocli:4.6.3")

  // Job IDs
  implementation("org.veupathdb.lib:hash-id:1.0.2")

  // Logging
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-core:2.17.2")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")

  // Jackson
  implementation("org.veupathdb.lib:jackson-singleton:3.0.0")


  // Recommended Dependencies
  //
  // These dependencies are not required, but are recommended.

  // JUnit 5
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

  // Mockito Test Mocking
  testImplementation("org.mockito:mockito-core:4.6.1")
}


// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  JVM & Compile Configuration                                            ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//


java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}


// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  Task Configurations                                                    ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//


tasks.shadowJar {
  exclude("**/Log4j2Plugins.dat")
  archiveFileName.set("service.jar")
}

tasks.register("fetch-eda-common-schema") {
  URL(EdaCommonRAMLURL).openStream().use { it.transferTo(System.out) }
}