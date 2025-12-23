plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // 🔥 Plugin do Firebase
}

android {
    namespace = "com.example.carson_umaplicativoparadescartedemedicamentos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carson_umaplicativoparadescartedemedicamentos"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true // 👀 Facilita o acesso às views
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}" // 🚫 Evita conflitos de libs
        }
    }
}

dependencies {
    // 🔧 AndroidX e Material
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 🗺️ Google Maps
    implementation(libs.play.services.maps)

    // 🔥 Firebase (usando BOM para manter versões consistentes)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    // 🧠 (Opcional) Firebase Storage
    // implementation("com.google.firebase:firebase-storage")

    // ⚙️ Utilitários gerais
    implementation("com.google.code.gson:gson:2.11.0") // JSON
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // HTTP requests

    // 📰 Retrofit + Glide (para puxar e exibir notícias)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.location)
    implementation(libs.work.runtime)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // 🧪 Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.work:work-runtime:2.8.1")
}
