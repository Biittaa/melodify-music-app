buildscript {
    ext {
        compose_version = '1.5.0'
        kotlin_version = '1.9.0'
        hilt_version = '2.48'
        firebase_version = '32.5.0'
        room_version = '2.5.2'
        datastore_version = '1.0.0'
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.0.2"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
        classpath "com.google.gms:google-services:4.3.15"
    }
}