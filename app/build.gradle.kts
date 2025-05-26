plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.bookverse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bookverse"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes.add("META-INF/NOTICE.md")
            excludes.add("META-INF/LICENSE.md")
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    viewBinding{
        enable= true
    }

    dataBinding {
        enable = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //glide
    implementation (libs.glide) // Phiên bản Glide mới nhất
    annotationProcessor (libs.compiler)

    //Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    //Gson
    implementation (libs.gson)

    //javamail
    implementation (libs.android.mail)
    implementation (libs.android.activation)

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"    )
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    //folio
    //implementation("com.github.FolioReader:FolioReader-Android:0.5.4")

    implementation (libs.fuel)
    implementation (libs.fuel.android)
}