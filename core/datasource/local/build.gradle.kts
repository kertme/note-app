plugins {
    id("noteapp.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.noteapp.datasource.local"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    // Room
    api(libs.room.runtime)
    api(libs.room.ktx)
    api(libs.room.paging)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.core)
    ksp(libs.hilt.compiler)
}