import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
}

group = "de.leonheuer.skycave.minievents"
version = "1.1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}


tasks {
    test {
        useJUnit()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}