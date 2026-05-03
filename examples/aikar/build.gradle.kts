import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    implementation(project(":core"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        archiveFileName.set("aaapi-example-acf.jar")
        manifest {
            attributes["Encoding"] = "UTF-8"
        }
    }
}