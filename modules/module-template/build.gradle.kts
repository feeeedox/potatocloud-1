plugins {
    alias(libs.plugins.shadow)
}

group = "net.potatocloud.module.template"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":api"))
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("potatocloud-module-template")
    archiveVersion.set("")
    archiveClassifier.set("")

    mergeServiceFiles()
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")

    dependencies {
        exclude(dependency("net.potatocloud:api"))
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}