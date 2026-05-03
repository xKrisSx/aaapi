plugins {
    java
    id("com.gradleup.shadow") version "9.3.1" apply false
}

allprojects {
    group = "com.github.xKrisSx"
    version = "1.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
