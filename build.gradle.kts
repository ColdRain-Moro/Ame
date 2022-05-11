plugins {
    kotlin("jvm") version "1.6.21" apply false
    java
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "ink.coldrain.ame"
version = "1.0-SNAPSHOT"

subprojects {

    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.tabooproject.org/repository/releases") }
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
        compileOnly(fileTree("libs"))
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.register<Copy>("collectJar") {
        into("${rootProject.buildDir}/libs")
        from("${project.buildDir}/libs")
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}