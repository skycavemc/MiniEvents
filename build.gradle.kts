import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
}

group = "de.leonheuer.skycave.minievents"
version = "2.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.onarandombox.com/content/groups/public/") }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.3.1")
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