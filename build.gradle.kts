plugins {
    java
    id("com.gradleup.shadow") version "9.3.1" apply false
}

allprojects {
    group = "pl.notkris"
    version = "1.0.0"

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
