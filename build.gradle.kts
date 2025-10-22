

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.2.2"
    kotlin("plugin.serialization") version "2.2.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}


application {mainClass.set("ru.ksenia.bot.telegram.TelegramKt")
}
