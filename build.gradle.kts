plugins {
    java
    application
}

group = "ru.lharyshu.roguelike"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // === Lanterna (terminal UI) ===
    implementation("com.googlecode.lanterna:lanterna:3.1.3")

    // Для JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Для тестов
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("presentation.GameApplication")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}