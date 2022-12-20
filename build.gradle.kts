import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.veupathdb.lib.gradle.container.util.Logger.Level
import java.net.URL

plugins {
  kotlin("jvm") version "1.7.0"
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "4.6.0"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}


// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  EDA Common Management                                                  ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//

val EdaCommonVersion = "10.0.0"
val EdaCommonRAMLURL = "https://raw.githubusercontent.com/VEuPathDB/EdaCommon/v${EdaCommonVersion}/schema/library.raml"


// ╔═════════════════════════════════════════════════════════════════════════╗//
// ║                                                                         ║//
// ║  Container Project Configuration                                        ║//
// ║                                                                         ║//
// ╚═════════════════════════════════════════════════════════════════════════╝//


// configure VEupathDB container plugin
containerBuild {

  // Change if debugging the build process is necessary.
  logLevel = Level.Info

  // General project level configuration.
  project {

    // Project Name
    name = "eda-compute"

    // Project Group
    group = "org.veupathdb.service"

    // Project Version
    version = "1.0.0"

    // Project Root Package
    projectPackage = "org.veupathdb.service.eda"

    // Main Class Name
    mainClassName = "compute.service.Main"
  }

  // Docker build configuration.
  docker {

    // Docker build context
    context = "."

    // Name of the target docker file
    dockerFile = "Dockerfile"

    // Resulting image tag
    imageName = "eda-compute"

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

val containerCore = "6.8.0"

dependencies {

  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk7"))
  implementation(kotlin("stdlib-jdk8"))

  implementation("org.veupathdb.lib:jaxrs-container-core:${containerCore}")
  implementation(findProject(":edaCommon") ?: "org.veupathdb.service.eda:eda-common:$EdaCommonVersion")
  implementation("org.veupathdb.lib:compute-platform:1.3.4")

  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.1.0")

  // Pico CLI
  implementation("info.picocli:picocli:4.7.0")

  // Job IDs
  implementation("org.veupathdb.lib:hash-id:1.1.0")

  // RServe
  implementation("org.rosuda.REngine:Rserve:1.8.1")

  // Logging
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-core:2.19.0")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

  // Jackson
  implementation("org.veupathdb.lib:jackson-singleton:3.0.0")

  // FgpUtil
  implementation("org.gusdb:fgputil-client:2.8.0-jakarta")

  // Prometheus Metrics
  implementation("io.prometheus:simpleclient:0.16.0")
  implementation("io.prometheus:simpleclient_common:0.16.0")

  // JUnit 5
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

  // Mockito Test Mocking
  testImplementation("org.mockito:mockito-core:4.8.0")
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

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs = listOf(
      "-Xjvm-default=all"
    )
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

/**
 * Fetch EDA Common Schema
 *
 * Custom task that fetches the contents of the EDA Common RAML library file and
 * spits it out on STDOUT.
 */
tasks.register("fetch-eda-common-schema") {
  // use local EdaCommon compiled schema if project exists, else use released version;
  //    this mirrors the way we use local EdaCommon code library if available
  val edaCommonLocalProjectDir = findProject(":edaCommon")?.projectDir
  if (edaCommonLocalProjectDir != null)
    File("${edaCommonLocalProjectDir}/schema/library.raml").inputStream().use { it.transferTo(System.out) }
  else
    URL(EdaCommonRAMLURL).openStream().use { it.transferTo(System.out) }
}
