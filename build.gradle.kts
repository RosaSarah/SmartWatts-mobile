// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2") // Última versão estável
        classpath("com.google.gms:google-services:4.4.0")
    }
}


