plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.colorviewerontemu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.colorviewerontemu"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.12.0")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.0")
    implementation("com.github.Mirkoddd:Range-SeekBar:1.1.0")
    implementation("com.github.rtugeek:colorseekbar:1.7.7")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.camera.core)
    implementation (libs.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.androidx.camera.video)
    implementation (libs.camera.view)
    implementation (libs.camera.extensions)
}