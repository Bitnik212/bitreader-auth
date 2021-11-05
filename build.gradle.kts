plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.allopen") version "1.5.31"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin:2.3.0.Final")
    implementation("io.quarkus:quarkus-smallrye-jwt:2.3.0.Final")
    implementation("io.quarkus:quarkus-smallrye-jwt-build:2.3.0.Final")
    implementation("io.quarkus:quarkus-resteasy-jsonb:2.3.0.Final")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc:2.3.0.Final")
    implementation("io.quarkus:quarkus-resteasy:2.3.0.Final")
    implementation("org.projectlombok:lombok:1.18.12")
    testImplementation("io.quarkus:quarkus-junit5:2.3.0.Final")
    testImplementation("io.rest-assured:rest-assured:4.4.0")
}

group = "ru.bitreader.auth"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}
