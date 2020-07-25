
import com.noto.buildsrc.App
import com.noto.buildsrc.Libraries
import com.noto.buildsrc.Modules

plugins {
    val plugins = com.noto.buildsrc.Plugins
    id(plugins.ANDROID_LIBRARY)
    kotlin(plugins.KOTLIN_ANDROID)
    id(plugins.KOTLIN_ANDROID_EXTENSIONS)
}

android {
    compileSdkVersion(App.COMPILE_SDK)
    buildToolsVersion(App.BUILD_TOOLS)
    defaultConfig {
        minSdkVersion(App.MIN_SDK)
        targetSdkVersion(App.TARGET_SDK)
        versionCode = App.APP_VERSION_CODE
        versionName = App.APP_VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
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
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(Modules.DOMAIN))
    testImplementation(Libraries.Testing.COROUTINES)
    testImplementation(Libraries.Testing.JUNIT)
    testImplementation(Libraries.Testing.KOIN_TEST)
    testImplementation(Libraries.Testing.KOTEST_JUNIT)
    testImplementation(Libraries.Testing.KOTEST_PROPERTY)
    testImplementation(Libraries.Testing.KOTEST_ASSERTION)
    testImplementation(Libraries.Testing.KOTEST_KOIN)

}

tasks.withType<Test> {
    useJUnitPlatform()
}