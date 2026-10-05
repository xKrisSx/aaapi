plugins {
    java
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":core"))
    // api: Injector is part of GuiceInstanceProvider's public constructor
    api("com.google.inject:guice:7.0.0")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
