object Libraries {
    object Main {
        const val Koin = "io.insert-koin:koin-android:${Versions.Koin}"
        const val DataStore = "androidx.datastore:datastore-preferences:1.0.0-alpha02"
        const val JavaTime = "com.android.tools:desugar_jdk_libs:${Versions.JavaTime}"
        const val Epoxy = "com.airbnb.android:epoxy:${Versions.Epoxy}"
        const val EpoxyProcessor = "com.airbnb.android:epoxy-processor:${Versions.Epoxy}"
    }

    object Gradle {
        const val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
        const val Android = "com.android.tools.build:gradle:${Versions.Android}"
        const val Navigation = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.Navigation}"
    }

    object Testing {
        const val KoinTest = "io.insert-koin:koin-test:${Versions.Koin}"
    }
}

object Versions {
    const val JavaTime = "1.1.5"
    const val Kotlin = "1.5.30"
    const val Navigation = "2.3.5"
    const val Android = "7.0.1"
    const val Koin = "3.1.2"
    const val Epoxy = "4.6.2"
}

object App {
    const val VersionName = "1.3.3"
    const val VersionCode = 17
    const val ID = "com.noto"
    const val MinSDK = 21
    const val CompileSDK = 30
    const val BuildTools = "31.0.0"
    const val TargetSDK = CompileSDK
}

object Plugins {
    const val AndroidApplication = "com.android.application"
    const val KotlinAndroid = "android"
    const val KotlinKapt = "kapt"
    const val NavigationSafeArgs = "androidx.navigation.safeargs.kotlin"
}
