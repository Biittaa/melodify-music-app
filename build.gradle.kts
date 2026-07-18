// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext {
        // -------------------- Versions --------------------
        // Kotlin & Compose
        kotlin_version = '1.9.0'
        compose_version = '1.5.0'
        compose_compiler_version = '1.5.0'

        // AndroidX
        core_ktx_version = '1.10.1'
        lifecycle_version = '2.6.1'
        activity_compose_version = '1.7.2'

        // Hilt (Dependency Injection)
        hilt_version = '2.48'

        // Firebase
        firebase_bom_version = '32.5.0'

        // Room (Database)
        room_version = '2.5.2'

        // DataStore
        datastore_version = '1.0.0'

        // Paging
        paging_version = '3.2.1'

        // ExoPlayer (Media Playback)
        exoplayer_version = '2.19.1'

        // WorkManager (Background Tasks)
        work_version = '2.8.1'

        // Coroutines
        coroutines_version = '1.7.3'

        // Compose Navigation
        navigation_compose_version = '2.7.2'

        // Coil (Image Loading)
        coil_version = '2.4.0'

        // Accompanist (Compose Utilities)
        accompanist_version = '0.30.1'
    }

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.devneeds.ir")
        }
    }

    dependencies {
        classpath "com.android.tools.build:gradle:8.0.2"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
        classpath "com.google.gms:google-services:4.3.15"
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.devneeds.ir")
        }
    }
}

tasks.register('clean', Delete) {
    delete rootProject.buildDir
}