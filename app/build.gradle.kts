plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("io.gitlab.arturbosch.detekt")
    id("jacoco")
    id("androidx.baselineprofile")
}

android {
    namespace = "com.urmilabs.shield"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.urmilabs.shield"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

// Jacoco configuration
jacoco {
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*",
        "**/*_Hilt*.*", "**/Hilt_*.*", "**/*_Factory.*",
        "**/*_MembersInjector.*", "**/dagger/**"
    )
    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

// Jacoco coverage verification — 80% minimum
tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*",
        "**/*_Hilt*.*", "**/Hilt_*.*", "**/*_Factory.*",
        "**/*_MembersInjector.*", "**/dagger/**"
    )
    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec")
    })
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

// Detekt configuration
detekt {
    config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
}

dependencies {
    // Multi-module
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    baselineProfile(project(":benchmark"))

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // DI
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("com.google.dagger:hilt-compiler:2.48")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // AI & Worker
    implementation("org.tensorflow:tensorflow-lite-task-audio:0.4.4")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Baseline Profile installer
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    // App Startup
    implementation("androidx.startup:startup-runtime:1.1.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Android Test (Compose UI tests)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Detekt formatting
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.5")
}

// Conditionally apply Firebase plugins only if google-services.json is present
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}
