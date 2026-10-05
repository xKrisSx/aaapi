import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":guice")) // exposes Guice transitively (api)
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        archiveFileName.set("aaapi-example-guice.jar")
        manifest {
            attributes["Encoding"] = "UTF-8"
        }

        // Guava (required by Guice) is already provided by the Paper server
        dependencies {
            exclude(dependency("com.google.guava:.*"))
        }
    }
}
