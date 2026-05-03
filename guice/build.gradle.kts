plugins {
    java
    `java-library`
}

dependencies {
    api(project(":core"))
    implementation("com.google.inject:guice:7.0.0")
}
