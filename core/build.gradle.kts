group = "net.potatocloud.core"

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))

    implementation(libs.netty.handler)
    implementation(libs.netty.epoll)

    // TODO
    implementation("tools.jackson.core:jackson-core:3.1.3")
    implementation("tools.jackson.core:jackson-databind:3.1.3")
}
