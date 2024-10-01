import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
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
    // Biblioteca padrão do Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Biblioteca de corrotinas do Kotlin (para programação assíncrona)
    implementation(libs.kotlinx.coroutines.core)

    // Driver JDBC para SQL Server (comentar caso não esteja sendo usado)
    // implementation("com.microsoft.sqlserver:mssql-jdbc:11.2.2.jre11")

    // Driver jTDS para acesso a bancos de dados (incluindo SQL Server)
    implementation(libs.jtds)

    // Biblioteca para gráficos e visualizações de dados
    implementation(libs.charts)

    // Biblioteca para gráficos específicos do Android
    implementation(libs.mpandroidchart)

    // Extensões do Kotlin para Android (facilitam o uso da API Android)
    implementation("androidx.core:core-ktx:1.13.1")

    // Biblioteca de compatibilidade para versões anteriores do Android
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Material Design Components versão 190 (para UI)
    implementation(libs.material.v190)

    // Biblioteca para layout com ConstraintLayout
    implementation(libs.androidx.constraintlayout)

    // Biblioteca para RecyclerView (lista de itens que podem ser rolados)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Biblioteca para ProgressBar circular (indica progresso em tarefas)
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")

    // Biblioteca Maskara para formatação de texto de entrada
    implementation("com.github.santalu:maskara:1.0.0")

    // Biblioteca Gson para manipulação de JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Biblioteca ThreeTenABP para manipulação de datas e horários
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.7")

    // Retrofit para realizar requisições HTTP
    implementation(libs.retrofit)
    // Conversor de JSON para objetos com Retrofit
    implementation(libs.converter.gson)

    // Extensões do LiveData do AndroidX
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Extensões do ViewModel do AndroidX
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Extensões para Fragment do AndroidX
    implementation(libs.androidx.fragment.ktx)
    // Biblioteca legacy de suporte para compatibilidade
    implementation(libs.androidx.legacy.support.v4)

    // Biblioteca OkHttp para fazer requisições HTTP
    implementation(libs.okhttp)

    // Room: biblioteca para persistência de dados
    val room_version = "2.6.1"
    // Dependência de runtime do Room
    implementation("androidx.room:room-runtime:$room_version")
    // Extensões do Room para Kotlin
    implementation("androidx.room:room-ktx:$room_version")
    // Compilador do Room (usado apenas no tempo de compilação)
    kapt("androidx.room:room-compiler:$room_version")

    // Dagger: biblioteca para injeção de dependência
    implementation("com.google.dagger:dagger:2.52")
    // Processador de anotação do Dagger (para gerar o código necessário)
    annotationProcessor("com.google.dagger:dagger-compiler:2.52")

    // Koin: biblioteca para injeção de dependência no Android
    implementation("io.insert-koin:koin-android:3.2.0")

    // Conector MySQL (para interações com banco de dados MySQL)
    implementation("mysql:mysql-connector-java:5.1.49")

    // Mais dependências do AndroidX (apenas se necessário)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.ui.android)

    // Dependência para testes unitários
    testImplementation(libs.junit)
    // Dependência para testes instrumentais
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}