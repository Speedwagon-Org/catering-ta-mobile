plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.speedwagon.cato"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.speedwagon.cato"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //noinspection GradleDependency
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.firebase:geofire-android:3.2.0")
    implementation ("com.firebase:geofire-android-common:3.2.0")
    // coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // maps
    //implementation ("com.google.android.gms:play-services-maps:18.2.0")
//    implementation("com.tomtom.sdk.maps:map-display:0.50.3")
    implementation ("org.osmdroid:osmdroid-android:6.1.14")
    // retrofit
//    implementation ("com.squareup.retrofit2:retrofit:2.6.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.6.0")
//    implementation ("com.squareup.okhttp3:logging-interceptor:4.0.1")
}