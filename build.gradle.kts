plugins {
    kotlin("jvm") version "1.9.22"
    id("io.github.goooler.shadow") version "8.1.5"
}

group = "kim.present.wdpe.unicodefontloader"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.waterdog.dev/snapshots")
    }
    maven {
        name = "opencollab-repo-snapshots-mirror"
        url = uri("https://repo.minjae.dev/opencollab-snapshots-mirror")
    }
    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots")
    }
    maven {
        url = uri("https://repo.opencollab.dev/maven-releases")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("dev.waterdog.waterdogpe:waterdog:2.0.0-SNAPSHOT")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.37")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_21.toString()
        }
    }
}