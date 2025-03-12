plugins {
    kotlin("jvm") version "2.1.10"
    id("me.champeau.jmh") version "0.7.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    jmh("org.openjdk.jmh:jmh-core:1.36")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.36")
    jmh(kotlin("stdlib"))
    jmh(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

jmh {
    profilers.set(listOf("gc"))
    includes.add(".*Benchmark.*")
    failOnError.set(true)
    resultFormat.set("JSON")
    @Suppress("DEPRECATION")
    resultsFile.set(project.file("${project.buildDir}/reports/jmh/results.json"))
}