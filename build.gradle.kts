plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.40")
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.javaparser:javaparser-core:3.14.11")
    implementation("com.github.ajalt:clikt:2.0.0")
    implementation("edu.stanford.nlp:stanford-corenlp:3.9.2")
    implementation("edu.stanford.nlp:stanford-corenlp:3.9.2:models")
    implementation("com.opencsv:opencsv:4.0")
    implementation("com.google.googlejavaformat:google-java-format:1.7")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "AppKt"
}
