// adds repos for gradle plugin resolution and ensures github creds are provided to the build
apply {
  from("https://raw.githubusercontent.com/VEuPathDB/lib-gradle-container-utils/v4.8.9/includes/common.settings.gradle.kts")
}

val edaCommon = file("../EdaCommon")
if (edaCommon.exists()) {
  include(":edaCommon")
  project(":edaCommon").projectDir = edaCommon
}

//val libComputePlatform = file("../lib-compute-platform")
//val libComputePlatformLib = file("../lib-compute-platform/lib")
//if (libComputePlatform.exists()) {
//  include(":libComputePlatform")
//  project(":libComputePlatform").projectDir = libComputePlatform
//  include(":libComputePlatformLib")
//  project(":libComputePlatformLib").projectDir =libComputePlatformLib
//}
