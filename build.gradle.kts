import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    `java-library`
    java
    distribution
    kotlin("jvm") version "1.5.31" apply false
}

group = "com.github.tagwan"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

apply(plugin = "idea")
apply(plugin = "java")
apply(plugin = "kotlin")
apply(plugin = "java-library")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.slf4j:slf4j-api:1.7.33")
    implementation("org.slf4j:slf4j-log4j12:1.7.33")

    testImplementation("junit:junit:4.12")
    testImplementation("io.mockk:mockk:1.10.0")

}

tasks {
    withType<JavaCompile>() {
        // 启用在单独的daemon进程中编译
        options.isFork = true
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            javaParameters = true
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}
