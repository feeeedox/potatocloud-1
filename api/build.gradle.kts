plugins {
    id("maven-publish")
}

group = "net.potatocloud.api"

publishing {
    publications {
        create<MavenPublication>("api") {
            from(components["java"])
            groupId = "net.potatocloud.api"
            artifactId = "api"
            version = rootProject.version.toString()
        }
    }
}
