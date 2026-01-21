pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
rootProject.name = "projet-global"

include("schema-lib")
include("platform") 
include("game-editor")
include("player")
