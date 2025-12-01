plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

android {
    namespace = "com.example.levelupgamer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.levelupgamer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Java 17 + desugaring
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Base / Compose (usa tu BOM del catalog) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Material Icons Extended y LiveData (alineados al BOM)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // CameraX (QR)
    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")

    // ML Kit QR
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // >>> OpenStreetMap (osmdroid) <<<
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // (Opcional) Fused Location si luego quieres ubicar al usuario
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Desugar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    //  TEST DEPENDENCIES (CONFIGURACIÓN CORRECTA Y LIMPIA)

    // Kotest (solo estas 2 son necesarias)
        testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
        testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    // MockK
        testImplementation("io.mockk:mockk:1.13.10")

    // Coroutines Test
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    // AndroidX Test
        testImplementation("androidx.arch.core:core-testing:2.2.0")

    // JUnit 5 (solo engine, Kotest usa JUnit 5)
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // --- Tests / debug ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.withType<Test> {
    useJUnitPlatform()  // <<< NECESARIO

    testLogging {
        events("passed", "failed", "skipped")
    }
}