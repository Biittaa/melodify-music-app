plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.melodify.musicapp'
    compileSdk 34

    defaultConfig {
        applicationId "com.melodify.musicapp"
        minSdk 26
        targetSdk 34
        versionCode 1
        versionName "1.0.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
                targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion compose_compiler_version
    }

    // For better build performance
    packaging {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
            excludes += '/META-INF/gradle/incremental.annotation.processors'
        }
    }
}

dependencies {
    // -------------------- Core Android --------------------
    implementation "androidx.core:core-ktx:$core_ktx_version"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"
    implementation "androidx.activity:activity-compose:$activity_compose_version"

    // -------------------- Compose --------------------
    implementation platform("androidx.compose:compose-bom:$compose_version")
    implementation "androidx.compose.ui:ui"
    implementation "androidx.compose.ui:ui-graphics"
    implementation "androidx.compose.ui:ui-tooling-preview"
    implementation "androidx.compose.material3:material3:1.2.0-beta01"
    implementation "androidx.compose.material:material-icons-extended"

    // Compose Navigation
    implementation "androidx.navigation:navigation-compose:$navigation_compose_version"

    // Compose Runtime (for state management)
    implementation "androidx.compose.runtime:runtime"

    // Debug Compose
    debugImplementation "androidx.compose.ui:ui-tooling"
    debugImplementation "androidx.compose.ui:ui-test-manifest"

    // -------------------- Dependency Injection (Hilt) --------------------
    implementation "com.google.dagger:hilt-android:$hilt_version"
    kapt "com.google.dagger:hilt-compiler:$hilt_version"
    // Hilt Navigation Compose
    implementation "androidx.hilt:hilt-navigation-compose:1.0.0"

    // -------------------- Firebase --------------------
    implementation platform("com.google.firebase:firebase-bom:$firebase_bom_version")
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'
    // Google Services (for Firebase Analytics - optional)
    implementation 'com.google.android.gms:play-services-auth:20.5.0'

    // -------------------- Room Database --------------------
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    implementation "androidx.room:room-paging:$room_version"

    // -------------------- DataStore --------------------
    implementation "androidx.datastore:datastore-preferences:$datastore_version"

    // -------------------- Paging 3 --------------------
    implementation "androidx.paging:paging-runtime-ktx:$paging_version"
    implementation "androidx.paging:paging-compose:3.2.1"

    // -------------------- Coroutines --------------------
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines_version"

    // -------------------- ExoPlayer (Media Playback) --------------------
    implementation "com.google.android.exoplayer:exoplayer-core:$exoplayer_version"
    implementation "com.google.android.exoplayer:exoplayer-ui:$exoplayer_version"
    implementation "com.google.android.exoplayer:exoplayer-hls:$exoplayer_version" // HLS support
    implementation "com.google.android.exoplayer:exoplayer-dash:$exoplayer_version" // DASH support
    implementation "com.google.android.exoplayer:exoplayer-smoothstreaming:$exoplayer_version"

    // MediaSession (for playback notifications)
    implementation "androidx.media:media:1.6.0"

    // -------------------- WorkManager (Background Downloads) --------------------
    implementation "androidx.work:work-runtime-ktx:$work_version"

    // -------------------- Image Loading (Coil) --------------------
    implementation "io.coil-kt:coil-compose:$coil_version"

    // -------------------- Accompanist (Compose Utilities) --------------------
    implementation "com.google.accompanist:accompanist-systemuicontroller:$accompanist_version"
    implementation "com.google.accompanist:accompanist-permissions:$accompanist_version"
    implementation "com.google.accompanist:accompanist-navigation-animation:$accompanist_version"

    // -------------------- Palette API (Dynamic Colors) --------------------
    implementation "androidx.palette:palette-ktx:1.0.0"

    // -------------------- Testing --------------------
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation "androidx.compose.ui:ui-test-junit4"
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}