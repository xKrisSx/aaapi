import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.panda-lang.org/releases")
}

dependencies {
    implementation(project(":core"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("dev.rollczi:litecommands-bukkit:3.10.9")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        archiveFileName.set("aaapi-example-litecommands.jar")
        manifest {
            attributes["Encoding"] = "UTF-8"
        }
    }
}