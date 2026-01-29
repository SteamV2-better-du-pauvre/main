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
include("platform:composeApp")
include("game-editor")
include("game-editor:composeApp")
include("player")
include("player:composeApp")
