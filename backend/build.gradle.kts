plugins {
  kotlin("jvm") version "2.1.10"
  kotlin("plugin.serialization") version "2.1.10"
  id("io.ktor.plugin") version "3.1.1"
  id("com.ncorti.ktfmt.gradle") version "0.22.0"
  id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

group = "com.lute"

version = "0.0.1"

application { mainClass.set("com.lute.ApplicationKt") }

repositories { mavenCentral() }

kotlin { jvmToolchain(21) }

ktor { fatJar { archiveFileName.set("lute-backend.jar") } }

val ktorVersion = "3.1.1"
val exposedVersion = "0.58.0"
val koinVersion = "4.0.2"

dependencies {
  // Ktor Server
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-netty:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
  implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
  implementation("io.ktor:ktor-server-cors:$ktorVersion")
  implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
  implementation("io.ktor:ktor-server-call-logging:$ktorVersion")

  // Exposed ORM
  implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

  // SQLite
  implementation("org.xerial:sqlite-jdbc:3.47.1.0")

  // Koin DI
  implementation("io.insert-koin:koin-ktor:$koinVersion")
  implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

  // Logging
  implementation("ch.qos.logback:logback-classic:1.5.15")

  // Testing
  testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
  testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test { useJUnitPlatform() }
