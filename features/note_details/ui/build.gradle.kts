plugins {
    id("noteapp.android.library")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.noteapp.home.ui"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:datasource:local"))
    implementation(project(":features:note_details:shared"))
    implementation(project(":features:note_details:domain"))

    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
}