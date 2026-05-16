group = "net.potatocloud.common"

dependencies {
    compileOnly(project(":api"))

    compileOnly(libs.jackson.core)
    compileOnly(libs.jackson.databind)
    compileOnly(libs.jackson.yaml)
}