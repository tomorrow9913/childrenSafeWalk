import org.gradle.api.JavaVersion

object Application {
    const val minSdk = 23
    const val targetSdk = 30
    const val compileSdk = 30
    const val jvmTarget = "1.8"
    const val versionCode = 1
    const val versionName = "1.0.0"

    val targetCompat = JavaVersion.VERSION_1_8
    val sourceCompat = JavaVersion.VERSION_1_8
}

object Versions {
    object Map {
        const val Map = "3.9.1"
        const val Location = "17.0.0"
    }

    object Network {
        const val Retrofit = "2.9.0"
        const val OkHttp = "4.8.1"
        const val LoggingInterceptor = "4.8.1"
    }

    object Rx {
        const val Kotlin = "3.0.0"
        const val Android = "3.0.0"
        const val Retrofit = "2.9.0"
    }

    object Essential {
        const val AppCompat = "1.2.0"
        const val Anko = "0.10.8"
        const val Kotlin = "1.4.0"
        const val Gradle = "4.0.1"
    }

    object Ktx {
        const val Core = "1.3.1"
        const val Fragment = "2.3.0"
    }

    object Di {
        const val Hilt = "2.28-alpha"
    }

    object Ui {
        const val ShapeOfView = "1.4.7"
        const val YoYo = "2.4@aar"
        const val Lottie = "3.4.2"
        const val Licenser = "2.0.0"
        const val Material = "1.2.1"
        const val Glide = "4.11.0"
        const val ConstraintLayout = "2.0.1"
    }

    object Util {
        const val TedPermission = "2.2.3"
        const val GsonConverter = "2.9.0"
        const val YoYoHelper = "2.4@aar"
        const val AndroidUtils = "4.0.2"
        const val CarshReporter = "1.1.0"
    }
}

object Dependencies {
    object Map {
        const val Map = "com.naver.maps:map-sdk:${Versions.Map.Map}"
        const val Location =
            "com.google.android.gms:play-services-location:${Versions.Map.Location}"
    }

    object Network {
        const val LoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.Network.LoggingInterceptor}"
        const val Retrofit = "com.squareup.okhttp3:okhttp:${Versions.Network.OkHttp}"
        const val OkHttp = "com.squareup.retrofit2:retrofit:${Versions.Network.Retrofit}"
    }

    object Rx {
        const val Kotlin = "io.reactivex.rxjava3:rxkotlin:${Versions.Rx.Kotlin}"
        const val Android = "io.reactivex.rxjava3:rxandroid:${Versions.Rx.Android}"
        const val Retrofit = "com.squareup.retrofit2:adapter-rxjava3:${Versions.Rx.Retrofit}"
    }

    object Essential {
        const val AppCompat = "androidx.appcompat:appcompat:${Versions.Essential.AppCompat}"
        const val Anko = "org.jetbrains.anko:anko:${Versions.Essential.Anko}"
        const val Kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Essential.Kotlin}"
    }

    object Ktx {
        const val Core = "androidx.core:core-ktx:${Versions.Ktx.Core}"
        const val Fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.Ktx.Fragment}"
    }

    object Di {
        const val Hilt = "com.google.dagger:hilt-android:${Versions.Di.Hilt}"
        const val HiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.Di.Hilt}"
    }

    object Ui {
        const val ShapeOfYou = "com.github.florent37:shapeofview:${Versions.Ui.ShapeOfView}"
        const val YoYo = "com.daimajia.androidanimations:library:${Versions.Ui.YoYo}"
        const val Lottie = "com.airbnb.android:lottie:${Versions.Ui.Lottie}"
        const val Licenser = "com.github.marcoscgdev:Licenser:${Versions.Ui.Licenser}"
        const val Material = "com.google.android.material:material:${Versions.Ui.Material}"
        const val Glide = "com.github.bumptech.glide:glide:${Versions.Ui.Glide}"
        const val ConstraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.Ui.ConstraintLayout}"
    }

    object Util {
        const val TedPermission = "gun0912.ted:tedpermission:${Versions.Util.TedPermission}"
        const val GsonConverter =
            "com.squareup.retrofit2:converter-gson:${Versions.Util.GsonConverter}"
        const val GlideCompiler = "com.github.bumptech.glide:compiler:${Versions.Ui.Glide}"
        const val YoyoHelper = "com.daimajia.easing:library:${Versions.Util.YoYoHelper}"
        const val AndroidUtils = "com.github.sungbin5304:SBT:${Versions.Util.AndroidUtils}"
        const val CrashReporter =
            "com.balsikandar.android:crashreporter:${Versions.Util.CarshReporter}"
    }
}