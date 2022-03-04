import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "de.leonheuer.skycave.minievents"
version = "1.3.3-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

val javaVersion = "17"
tasks {
    test {
        useJUnit()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}