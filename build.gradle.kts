import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    `java-library`
    java
    distribution
    kotlin("jvm") version "1.6.21" apply false
}

group = "com.github.tagwan"
version = "1.0.1"
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
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")

    implementation("org.ow2.asm:asm:9.2")

    implementation("org.slf4j:slf4j-api:1.7.33")
    implementation("org.slf4j:slf4j-log4j12:1.7.33")

    testImplementation("junit:junit:4.12")
}

tasks {
    withType<JavaCompile>() {
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

    jar {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to "1.0.0",
                "Premain-Class" to "com.github.tagwan.japm.BootKt",
                "Agent-Class" to "com.github.tagwan.japm.BootKt",
                "Built-By" to "Espresso"
            )
        }
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
