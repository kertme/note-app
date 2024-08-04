plugins {
    id("noteapp.android.library")
    id("noteapp.android.library.compose")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.noteapp.home.ui"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:datasource:local"))
    implementation(project(":features:home:domain"))

    implementation(project(":features:note_details:shared"))

    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.compose.paging)

    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)

    implementation(libs.androidx.hilt.navigation.compose)
}