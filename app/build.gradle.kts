import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.debts"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.debts"
        minSdk = 24
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

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    //implementation("com.microsoft.sqlserver:mssql-jdbc:11.2.2.jre11") // driver SQL Server
    implementation (libs.kotlinx.coroutines.core)
    //implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    //implementation(files("C:\\Users\\Leo\\AndroidStudioProjects\\debts\\app\\libs\\mssql-jdbc-12.8.0.jre11.jar"))
    //implementation(files("C:\\Users\\Leo\\AndroidStudioProjects\\debts\\app\\libs\\jtds-1.3.1.jar"))
    implementation(libs.jtds)

    implementation(libs.charts)
    implementation (libs.mpandroidchart)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.material.v190)
    implementation(libs.androidx.constraintlayout)

    implementation ("androidx.recyclerview:recyclerview:1.3.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.ui.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}