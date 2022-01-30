import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

val grpcVersion = "1.38.0"
val grpcKotlinVersion = "1.1.0"
val protobufVersion = "3.17.1"
val coroutinesVersion = "1.5.0"

ext["jvmVersion"] = "1.34.0"

plugins {
    application
    kotlin("jvm") version "1.5.0" // 1.6.0 causes compile error JvmField has no effect on a private property
    id("com.google.protobuf") version "0.8.16"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")

    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.beust:klaxon:5.5")

    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("info.ls.grpc.StartServerKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.register<JavaExec>("HelloClient") {
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("info.ls.grpc.v1.HelloClientKt")
}

val otherStartScripts = tasks.register<CreateStartScripts>("otherStartScripts") {
    mainClass.set("info.ls.grpc.v1.HelloClientKt")
    applicationName = "HelloClientKt"
    outputDir = tasks.named<CreateStartScripts>("startScripts").get().outputDir
    classpath = tasks.named<CreateStartScripts>("startScripts").get().classpath
}

tasks.named("startScripts") {
    dependsOn(otherStartScripts)
}

task("stage").dependsOn("installDist")

tasks.replace("assemble").dependsOn(":installDist")
