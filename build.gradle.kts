plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "com.bashkevich"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Telegram Bot API
    implementation(libs.tgbotapi)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Logging
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.bashkevich.MainKt")
}

kotlin {
    jvmToolchain(19)
}

tasks.test {
    useJUnitPlatform()
}

// Task to create a fat JAR with all dependencies
tasks.register<Jar>("fatJar") {
    archiveFileName.set("simple-tgbot.jar")
    destinationDirectory.set(layout.projectDirectory.dir("libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes("Main-Class" to "com.bashkevich.MainKt")
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}
