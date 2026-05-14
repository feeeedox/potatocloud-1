dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":network"))

    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
}
