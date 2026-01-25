group = "net.potatocloud.core"

dependencies {
    implementation(project(":api"))

    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.commons.io)
    implementation(libs.netty.handler)
    implementation(libs.netty.epoll)
    implementation(libs.gson)
}
