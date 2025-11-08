import com.android.build.gradle.BaseExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("jacoco")
}

android {
    namespace = "com.openclassrooms.rebonnte"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.openclassrooms.rebonnte"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${project.findProperty("WEB_CLIENT_ID")}\""
        )
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Jacoco config
val androidExtension = extensions.getByType<BaseExtension>()
tasks.register<JacocoReport>("jacocoUnitTestReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports for unit tests (debug)"

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoUnitTestReport/jacocoUnitTestReport.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoUnitTestReport/html"))
    }

    val mainSrc = files("src/main/java")

    val javaClasses = fileTree("build/intermediates/javac/debug/classes") {
        exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*")
    }
    val kotlinClasses = fileTree("build/tmp/kotlin-classes/debug") {
        exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*")
    }

    classDirectories.setFrom(files(javaClasses, kotlinClasses))
    sourceDirectories.setFrom(mainSrc)

    // Seuls les tests unitaires produisent le .exec
    executionData.setFrom(fileTree("build/outputs/unit_test_code_coverage/debugUnitTest") {
        include("*.exec")
    })
}
// Rapport pour les tests instrumentés
tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    dependsOn("connectedDebugAndroidTest") // lance les tests instrumentés sur un émulateur
    group = "Reporting"
    description = "Generate Jacoco coverage reports for instrumentation tests"
    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoAndroidTestReport/jacocoAndroidTestReport.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoAndroidTestReport/html"))
    }
    val mainSrc = files("src/main/java")
    val javaClasses = fileTree("build/intermediates/javac/debug/classes") {
        exclude("**/R.class","**/R$*.class","**/BuildConfig.*","**/Manifest*.*","**/*Test*.*")
    }
    val kotlinClasses = fileTree("build/tmp/kotlin-classes/debug") {
        exclude("**/R.class","**/R$*.class","**/BuildConfig.*","**/Manifest*.*","**/*Test*.*")
    }
    classDirectories.setFrom(files(javaClasses,kotlinClasses))
    sourceDirectories.setFrom(mainSrc)
    executionData.setFrom(fileTree("build/outputs/code_coverage/debugAndroidTest/connected") {
        include("**/*.ec") // fichiers générés par les tests instrumentés
    })
}

dependencies {
    // Hilt
    implementation(libs.hilt)
    implementation(libs.lifecycle.viewmodel.savedstate.android)
    implementation(libs.ui.test.junit4.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.work.runtime.ktx)
    ksp(libs.hilt.compiler)
    // Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    // Utils
    implementation(libs.coil.compose)
    implementation(libs.material.icons.extended)
    implementation(libs.material)
    implementation(libs.androidx.material)
    implementation(libs.compose.material3)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test)
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation (libs.google.firebase.firestore)
    testImplementation (libs.mockito.core.v451)
    testImplementation (libs.core.testing)
    testImplementation (libs.mockk)
    debugImplementation(libs.ui.test.manifest)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.firebase.messaging.ktx)
}