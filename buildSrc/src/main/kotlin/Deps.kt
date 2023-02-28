object Deps {

    object Kotlin {
        private const val Version = Versions.Kotlin
        const val Compiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$Version"
        const val Jvm = "org.jetbrains.kotlin:kotlin-stdlib:$Version"
        const val Reflect = "org.jetbrains.kotlin:kotlin-reflect:$Version"
        const val Gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:$Version"
        const val GradleApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:$Version"
        const val AllOpen = "org.jetbrains.kotlin:kotlin-allopen:$Version"
    }

    object Slf4j {
        private const val Version = Versions.Slf4j
        const val Api = "org.slf4j:slf4j-api:$Version"
        const val Impl = "org.slf4j:slf4j-log4j12:$Version"
    }

    object Junit {
        private const val Version = Versions.Junit
        const val JupiterEngine = "org.junit.jupiter:junit-jupiter-engine:$Version"
        const val JupiterApi = "org.junit.jupiter:junit-jupiter-api:$Version"
        const val Jupiter = "org.junit.jupiter:junit-jupiter:$Version"
    }

    const val Kotlinx = "org.jetbrains.kotlinx:kotlinx-cli:${Versions.Kotlinx}"
    const val Asm = "org.ow2.asm:asm:${Versions.Asm}"
}