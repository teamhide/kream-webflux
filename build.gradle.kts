

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("jacoco")
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.teamhide"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.asyncer:r2dbc-mysql:1.1.0")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("io.micrometer:context-propagation:1.1.1")

    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.6.2")
    testImplementation("io.kotest:kotest-framework-datatest:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation("com.ninja-squad:springmockk:4.0.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.11.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
}

val testAll by tasks.registering {
    dependsOn("test", "jacocoTestReport", "jacocoTestCoverageVerification")
    tasks["test"].mustRunAfter(tasks["ktlintCheck"])
    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}

val snippetsDir by extra { file("build/generated-snippets") }
tasks.test {
    useJUnitPlatform()
    systemProperties["spring.profiles.active"] = "test"
    outputs.dir(snippetsDir)
}

tasks.register("testUnit", Test::class) {
    useJUnitPlatform()
    systemProperties["spring.profiles.active"] = "test"
    exclude("**/*ControllerTest*")
}

tasks.register("teste2e", Test::class) {
    useJUnitPlatform()
    systemProperties["spring.profiles.active"] = "test"
    include("**/*ControllerTest*")
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/*Application*",
                        "**/*logger*",
                        "**/*Logger*",
                        "**/**Logger**.class",
                        "**/**logger**.class",
                        "**logger*",
                    )
                }
            }
        )
    )
}

tasks.jacocoTestCoverageVerification {
    val queryDslClasses = ('A'..'Z').map { "*.Q$it*" }
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
            classDirectories.setFrom(sourceSets.main.get().output.asFileTree)
            excludes = listOf(
                "com.teamhide.kream.KreamApplicationKt",
                "**/*logger*",
                "**/*Logger*",
                "**/**Logger**.class",
                "**/**logger**.class",
                "**logger*",
            ) + queryDslClasses
        }
    }
}
