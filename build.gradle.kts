import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    //alias(libs.plugins.ksp)
    alias(libs.plugins.koin.compiler)
}

group = "com.bashkevich"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Telegram Bot
    implementation(libs.telegram.bot.core)
    implementation(libs.telegram.bot.ktor)

    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    compileOnly(libs.koin.annotations)
    //ksp(libs.koin.ksp.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // DateTime
    implementation(libs.kotlinx.datetime)

    // Logging
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.bashkevich.ApplicationKt")
}

kotlin {
    jvmToolchain(19)
}

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_19)
}

sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.bashkevich.ApplicationKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("TelegramBotSample-${version}.jar")
}
