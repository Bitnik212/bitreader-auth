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
val quarkusVersion = "2.6.3.Final"

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    //kolin
    implementation("io.quarkus:quarkus-kotlin:$quarkusVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    //jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation("io.quarkus:quarkus-smallrye-jwt:$quarkusVersion")
    implementation("io.quarkus:quarkus-smallrye-jwt-build:$quarkusVersion")
    //json
    implementation("io.quarkus:quarkus-resteasy:$quarkusVersion")
    //qraphql
    implementation("io.quarkus:quarkus-smallrye-graphql:$quarkusVersion")
    //db
    implementation("io.quarkus:quarkus-hibernate-orm-panache:$quarkusVersion")
    implementation("io.quarkus:quarkus-hibernate-validator:$quarkusVersion")
    implementation("org.postgresql:postgresql:42.3.1")
    //mail
    implementation("io.quarkus:quarkus-mailer:$quarkusVersion")
    //other
    implementation("io.quarkus:quarkus-arc:$quarkusVersion")
    testImplementation("io.quarkus:quarkus-junit5:$quarkusVersion")
    testImplementation("io.rest-assured:rest-assured:4.5.0")
}

group = "ru.bitreader.auth"
version = "0.1.0"

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
