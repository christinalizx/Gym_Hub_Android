plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "edu.northeastern.gymhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.northeastern.gymhub"
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.scwang.smart:refresh-layout-kernel:2.0.3")
    implementation ("com.scwang.smart:refresh-header-classics:2.0.3")
    implementation ("com.scwang.smart:refresh-header-radar:2.0.3")
    implementation ("com.scwang.smart:refresh-header-falsify:2.0.3")
    implementation ("com.scwang.smart:refresh-header-material:2.0.3")
    implementation ("com.scwang.smart:refresh-header-two-level:2.0.3")
    implementation ("com.scwang.smart:refresh-footer-ball:2.0.3")
    implementation ("com.scwang.smart:refresh-footer-classics:2.0.3")
    implementation ("com.stone.vega.library:VegaLayoutManager:1.0.6")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.tlaabs:TimetableView:1.0.3-fx1")



}