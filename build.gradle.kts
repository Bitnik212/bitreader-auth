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
    implementation("io.quarkus:quarkus-kotlin:2.5.0.Final")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation("io.quarkus:quarkus-smallrye-jwt:2.5.0.Final")
    implementation("io.quarkus:quarkus-smallrye-jwt-build:2.5.0.Final")
    //json
    implementation("io.quarkus:quarkus-resteasy-jsonb:2.5.0.Final")
    implementation("io.quarkus:quarkus-resteasy-jackson:2.5.0.Final")
    implementation("io.quarkus:quarkus-resteasy:2.4.1.Final")
    //qraphql
    implementation("io.quarkus:quarkus-smallrye-graphql:2.5.0.Final")
    implementation("io.quarkus:quarkus-smallrye-graphql-client:2.5.0.Final")
    //db
    implementation("io.quarkus:quarkus-hibernate-orm-panache:2.5.0.Final")
    implementation("org.hibernate:hibernate-spatial:5.6.1.Final")

    implementation("io.quarkus:quarkus-smallrye-openapi:2.5.0.Final")
    implementation("io.quarkus:quarkus-hibernate-validator:2.5.0.Final")
    implementation("io.quarkus:quarkus-arc:2.5.0.Final")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("org.postgresql:postgresql:42.3.1")
    testImplementation("io.quarkus:quarkus-junit5:2.5.0.Final")
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
