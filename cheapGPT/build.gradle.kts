buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.0")


    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id ("androidx.navigation.safeargs.kotlin")
    id ("com.google.devtools.ksp")
}