plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
<<<<<<< HEAD
=======
    kotlin("kapt") // For Room annotation processing
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
}

android {
    namespace = "com.example.saferoute"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.saferoute"
        minSdk = 24
        targetSdk = 35
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
<<<<<<< HEAD
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
=======

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // AndroidX Core & Lifecycle
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
    implementation("androidx.activity:activity-compose:1.9.3")

    // Jetpack Compose (with BOM)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
<<<<<<< HEAD
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
=======
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

<<<<<<< HEAD
    // Firebase (BOM for version alignment)
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
=======
    // Firebase (Auth, Firestore, Messaging)
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.1.1")

<<<<<<< HEAD
=======
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Google Maps Compose + Maps SDK
    implementation("com.google.maps.android:maps-compose:2.14.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Coil (optional, for images)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Retrofit for HTTP requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Gson converter for Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.activity:activity-ktx:1.7.2")


>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug Tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
<<<<<<< HEAD
}
=======
}
>>>>>>> 9e9e2b4 (Sprint 2 – SafeRoute Authentication & Onboarding)
