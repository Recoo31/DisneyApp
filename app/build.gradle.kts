plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "recoo.roxio"
    compileSdk = 34

    defaultConfig {
        applicationId = "recoo.roxio"
        minSdk = 22
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding=true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("io.karn:khttp-android:-SNAPSHOT")

    implementation ("androidx.viewpager2:viewpager2:-SNAPSHOT")

    implementation ("com.makeramen:roundedimageview:2.3.0")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.squareup.picasso:picasso:2.8") //I love Glide but it doesn't work in viewpager

    implementation ("com.google.android.exoplayer:exoplayer:2.18.5")
    implementation ("com.google.android.exoplayer:exoplayer-core:2.18.5")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.18.5")
    implementation ("com.google.android.exoplayer:exoplayer-hls:2.18.5")
    implementation ("com.google.android.exoplayer:exoplayer-dash:2.18.5")
    implementation ("com.google.android.exoplayer:exoplayer-smoothstreaming:2.18.5")
}
