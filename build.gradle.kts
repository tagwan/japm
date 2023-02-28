import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    `java-library`
    java
    distribution
    kotlin("jvm") version Versions.Kotlin apply false
}

group = "com.github.tagwan"
version = "1.0.1.RELEASE"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public/")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://plugins.gradle.org/m2/")
}

apply(plugin = "idea")
apply(plugin = "java")
apply(plugin = "kotlin")
apply(plugin = "java-library")

val api by configurations
val testImplementation by configurations

dependencies {
    api(Deps.Kotlin.Reflect)
    api(Deps.Kotlin.Jvm)
    api(Deps.Kotlinx)
    api(Deps.Asm)
    api(Deps.Slf4j.Api)
    api(Deps.Slf4j.Impl)

    testImplementation(Deps.Junit.JupiterEngine)
    testImplementation(Deps.Junit.JupiterApi)
    testImplementation(Deps.Junit.Jupiter)
}

tasks {
    withType<JavaCompile> {
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
