import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    `java-library`
    id("com.mineplex.sdk.plugin") version "1.2.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    kotlin("jvm")
}

group = "com.mineplex.studio.scaffold"
version = "0.0.1"

tasks {
    build {
        dependsOn(named("generatePaperPluginDescription"))
    }
}

paper {
    name = "Scaffold"
    version = project.version.toString()
    main = "com.mineplex.studio.scaffold.OneBlockWarsPlugin"
    apiVersion = "1.20"

    serverDependencies {
        register("StudioEngine") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}