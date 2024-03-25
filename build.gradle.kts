import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.veupathdb.lib.gradle.container.util.Logger.Level
import java.io.FileOutputStream
import java.net.URL

plugins {
  kotlin("jvm") version "1.9.20"
  java
  id("org.veupathdb.lib.gradle.container.container-utils") version "4.8.9"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

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

// project versions
val containerCore = "7.0.6"
val edaCommon =     "11.7.2"

// use local EdaCommon compiled schema if project exists, else use released version;
//    this mirrors the way we use local EdaCommon code if available
val edaCommonLocalProjectDir = findProject(":edaCommon")?.projectDir
val commonRamlOutFileName = "$projectDir/schema/eda-common-lib.raml"

tasks.named("merge-raml") {
  // Hook into merge-raml to download or fetch EDA Common RAML before merging
  doFirst {
    val commonRamlOutFile = File(commonRamlOutFileName)
    commonRamlOutFile.delete()

    // use local EdaCommon compiled schema if project exists, else use released version;
    // this mirrors the way we use local EdaCommon code if available
    if (edaCommonLocalProjectDir != null) {
      val commonRamlFile = File("${edaCommonLocalProjectDir}/schema/library.raml")
      logger.lifecycle("Copying file from ${commonRamlFile.path} to ${commonRamlOutFile.path}")
      commonRamlFile.copyTo(commonRamlOutFile)
    } else {
      commonRamlOutFile.createNewFile()
      val edaCommonRamlUrl = "https://raw.githubusercontent.com/VEuPathDB/EdaCommon/v${edaCommon}/schema/library.raml"
      logger.lifecycle("Downloading file contents from $edaCommonRamlUrl")
      URL(edaCommonRamlUrl).openStream().use { it.transferTo(FileOutputStream(commonRamlOutFile)) }
    }
  }

  // After merge is complete, delete the EDA Common RAML from this project.
  doLast {
    logger.lifecycle("Deleting file $commonRamlOutFileName")
    File(commonRamlOutFileName).delete()
  }
}

// ensures changing and dynamic modules are never cached
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

  implementation("org.veupathdb.lib:jaxrs-container-core:${containerCore}")
  implementation(findProject(":edaCommon") ?: "org.veupathdb.service.eda:eda-common:${edaCommon}")
  implementation("org.veupathdb.lib:compute-platform:1.7.2")


  // Jersey
  implementation("org.glassfish.jersey.core:jersey-server:3.1.1")

  // Pico CLI
  implementation("info.picocli:picocli:4.7.3")

  // Job IDs
  implementation("org.veupathdb.lib:hash-id:1.1.0")

  // RServe
  implementation("org.rosuda.REngine:Rserve:1.8.1")

  // Logging
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("org.apache.logging.log4j:log4j-core:2.20.0")
  runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

  // Jackson
  implementation("org.veupathdb.lib:jackson-singleton:3.0.0")

  // FgpUtil
  implementation("org.gusdb:fgputil-client:2.12.9-jakarta")

  // Prometheus Metrics
  implementation("io.prometheus:simpleclient:0.16.0")
  implementation("io.prometheus:simpleclient_common:0.16.0")

  // JUnit 5
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

  // Mockito Test Mocking
  testImplementation("org.mockito:mockito-core:5.2.0")
}
