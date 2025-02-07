plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.devtools.ksp)

}

android {
    namespace = "com.jdacodes.mvicomposedemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jdacodes.mvicomposedemo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["facebook_app_id"] = project.properties["facebook_app_id"] ?: ""
        manifestPlaceholders["facebook_client_token"] =
            project.properties["facebook_client_token"] ?: ""
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "CLIENT_TOKEN_FACEBOOK",
                "\"4aea8c1d2c71e37ab14ba42009f021ca\""
            )
            buildConfigField("String", "APP_ID_FACEBOOK", "\"1082904020000792\"")
            buildConfigField(
                "String",
                "WEB_CLIENT_ID_FIREBASE",
                "\"287275380843-o1jddct5j9bhvm1jgd36kaetrjui1uta.apps.googleusercontent.com\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField(
                "String",
                "CLIENT_TOKEN_FACEBOOK",
                "\"4aea8c1d2c71e37ab14ba42009f021ca\""
            )
            buildConfigField("String", "APP_ID_FACEBOOK", "\"1082904020000792\"")
            buildConfigField(
                "String",
                "WEB_CLIENT_ID_FIREBASE",
                "\"287275380843-o1jddct5j9bhvm1jgd36kaetrjui1uta.apps.googleusercontent.com\""
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
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.roomtesting)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material3.adaptive.layout)
    implementation(libs.androidx.material3.adaptive.navigation)
    implementation(libs.androidx.material3.windowsize)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.bundles.koin)
    implementation(libs.timber)
    implementation(libs.facebook.android.sdk)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.auth)
    implementation(libs.google.googleid)
    implementation(libs.play.services.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}