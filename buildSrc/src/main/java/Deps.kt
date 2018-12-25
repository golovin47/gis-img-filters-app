import org.gradle.api.JavaVersion

object General {
    val sourceCompatibility = JavaVersion.VERSION_1_8
    val targetCompatibility = JavaVersion.VERSION_1_8
}

object Version {
    // project
    const val minSdk = 21
    const val targetSdk = 28
    const val buildToolsVersion = "28.0.2"
    const val compileSdkVersion = 28
    const val androidGradle = "3.4.0-alpha03"

    // android
    const val androidX = "1.0.0"
    const val androidKtx = "1.0.0-rc02"   //https://github.com/android/android-ktx
    const val androidLifecycle = "2.0.0-alpha1"
    const val multidex = "2.0.0"
    const val constraintLayout = "2.0.0-alpha2"

    // kotlin
    const val kotlin = "1.3.11" // https://kotlinlang.org/
    const val coroutinesAndroid = "1.0.1" // https://kotlinlang.org/

    //rxExtensions
    const val rxJava = "2.2.0"            //https://github.com/ReactiveX/RxJava
    const val rxRelay = "2.0.0"           //https://github.com/JakeWharton/RxRelay
    const val rxKotlin = "2.2.0"          //https://github.com/ReactiveX/RxKotlin
    const val rxAndroid = "2.1.0"         //https://github.com/ReactiveX/RxAndroid
    const val rxBinding = "2.1.1"         //https://github.com/JakeWharton/RxBinding
    const val rxBinding3 = "3.0.0-alpha1"         //https://github.com/JakeWharton/RxBinding
    const val rxNetwork = "2.1.0"         //https://github.com/pwittchen/ReactiveNetwork
    const val rxPermissions = "0.10.2"    //https://github.com/tbruyelle/RxPermissions

    // dependency injection
    const val dagger = "2.17" //https://github.com/google/dagger
    const val koin = "1.0.1"  //https://github.com/InsertKoinIO/koin

    // general
    const val mosby = "3.1.0" //https://github.com/sockeqwe/mosby

    // network
    const val okHttp = "3.11.0" //https://github.com/square/okhttp

    // serialization
    const val gson = "2.8.5"        //https://github.com/google/gson
    const val loganSquare = "1.3.7" //https://github.com/bluelinelabs/LoganSquare
    const val jacksonCore = "2.9.7" //https://github.com/bluelinelabs/LoganSquare

    // retrofit
    const val retrofit = "2.4.0"                     //https://github.com/square/retrofit
    const val retrofitConverterGson = "2.4.0"        //https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    const val retrofitConverterLoganSquare = "1.4.1" //https://github.com/mannodermaus/retrofit-logansquare

    // apollo
    const val apollo = "1.0.0-alpha"      // https://github.com/apollographql/apollo-android

    //persistence
    const val room = "2.0.0-rc01" //https://developer.android.com/topic/libraries/architecture/room

    //lifecycle
    const val lifecycle = "2.0.0-rc01" //https://developer.android.com/topic/libraries/architecture/lifecycle

    //navigation
    const val navigation = "1.0.0-alpha07" //https://developer.android.com/topic/libraries/architecture/navigation

    //imageLoading
    const val glide = "4.8.0"  //https://github.com/bumptech/glide
    const val picasso = "2.71828"       //https://github.com/square/picasso

    //profiling
    const val debugDb = "1.0.4"    //https://github.com/amitshekhariitbhu/Android-Debug-Database
    const val leakCanary = "1.6.2" //https://github.com/square/leakcanary

    //social auth
    const val facebookAnalytics = "[4,5)"
    const val crashlytics = "2.9.4@aar"
    const val fabricPlugin = "1.+"
    const val simpleAuth = "2.1.3"  //https://github.com/jaychang0917/SimpleAuth

    // firebase
    const val firebaseCore = "16.0.1"
    const val firebaseMessaging = "17.1.0"

    //Google
    const val googleServices = "4.0.1"
    const val googleAuth = "16.0.0"
    const val googleMaps = "16.0.0"
    const val googleLocation = "16.0.0"

    // tests
    const val jUnit = "4.12"
    const val androidTestRunner = "1.0.0-rc01"
    const val espresso = "3.1.0-alpha4"
    const val mockito = "2.22.0"
    const val mockitoAndroid = "2.22.0"
    const val mockitoKotlin = "2.0.0-RC1"
}

object Deps {

    // Android
    const val androidCore = "androidx.core:core:${Version.androidX}"
    const val androidAnnotation = "androidx.annotation:annotation:${Version.androidX}"
    const val androidLifecycle = "androidx.lifecycle:lifecycle-runtime:${Version.androidLifecycle}"
    const val appCompat = "androidx.appcompat:appcompat:${Version.androidX}"
    const val material = "com.google.android.material:material:${Version.androidX}"
    const val palette = "androidx.palette:palette:${Version.androidX}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Version.constraintLayout}"
    const val cardview = "androidx.cardview:cardview:${Version.androidX}"
    const val multidex = "androidx.multidex:multidex:${Version.multidex}"
    const val animatedVector = "androidx.vectordrawable:vectordrawable-animated:${Version.androidX}"

    const val androidKtx = "androidx.core:core-ktx:${Version.androidKtx}"
    const val paletteKtx = "androidx.palette:palette-ktx:${Version.androidKtx}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Version.androidKtx}"
    const val collectionKtx = "androidx.collection:collection-ktx:${Version.androidKtx}"

    // Kotlin
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlin}"

    // Coroutines
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutinesAndroid}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutinesAndroid}"

    // RxExtension
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Version.rxJava}"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:${Version.rxRelay}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Version.rxKotlin}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Version.rxAndroid}"

    const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Version.rxBinding3}"
    const val rxBindingCore = "com.jakewharton.rxbinding3:rxbinding-core:${Version.rxBinding3}"
    const val rxBindingAppCompat = "com.jakewharton.rxbinding3:rxbinding-appcompat:${Version.rxBinding3}"
    const val rxBindingDrawer = "com.jakewharton.rxbinding3:rxbinding-drawerlayout:${Version.rxBinding3}"
    const val rxBindingLeanBack = "com.jakewharton.rxbinding3:rxbinding-leanback:${Version.rxBinding3}"
    const val rxBindingRecyclerView = "com.jakewharton.rxbinding3:rxbinding-recyclerview:${Version.rxBinding3}"
    const val rxBindingSlidingPanel = "com.jakewharton.rxbinding3:rxbinding-slidingpanelayout:${Version.rxBinding3}"
    const val rxBindingSwipeRefresh = "com.jakewharton.rxbinding3:rxbinding-swiperefreshlayout:${Version.rxBinding3}"
    const val rxBindingViewPager = "com.jakewharton.rxbinding3:rxbinding-viewpager:${Version.rxBinding3}"
    const val rxBindingDesign = "com.jakewharton.rxbinding3:rxbinding-material:${Version.rxBinding3}"

    const val rxNetwork = "com.github.pwittchen:reactivenetwork-rx2:${Version.rxNetwork}"
    const val rxPermissions = "com.github.tbruyelle:rxpermissions:${Version.rxPermissions}"
    const val rxBindingKotlin = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Version.rxBinding}"
    const val rxBindingDesignKotlin = "com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Version.rxBinding}"
    const val rxBindingAppCompatV4Kotlin = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Version.rxBinding}"
    const val rxBindingAppCompatV7Kotlin = "com.jakewharton.rxbinding2:rxbinding-appcompat-v7:${Version.rxBinding}"
    const val rxBindingRecyclerViewV7Kotlin = "com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Version.rxBinding}"

    // Dependency Injection
    const val inject = "javax.inject:javax.inject:1"
    const val dagger = "com.google.dagger:dagger:${Version.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Version.dagger}"
    const val koinCore = "org.koin:koin-core:${Version.koin}"
    const val koinAndroid = "org.koin:koin-android:${Version.koin}"
    const val koinCoreScope = "org.koin:koin-androidx-scope:${Version.koin}"
    const val koinAndroidViewModel = "org.koin:koin-androidx-viewmodel:${Version.koin}"
    const val koinTests = "org.koin:koin-test:${Version.koin}"

    // General
    const val mosby = "com.hannesdorfmann.mosby3:mvi:${Version.mosby}"

    // Network
    const val okHttp = "com.squareup.okhttp3:okhttp:${Version.okHttp}"
    const val okHttpInteceptor = "com.squareup.okhttp3:logging-interceptor:${Version.okHttp}"

    // Retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofit}"
    const val retrofitRxJavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Version.retrofit}"
    const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Version.retrofitConverterGson}"
    const val retrofitConverterLoganSquare = "com.github.aurae.retrofit2:converter-logansquare:${Version.retrofitConverterLoganSquare}"

    // Apollo
    const val apollo = "com.apollographql.apollo:apollo-runtime:${Version.apollo}"
    const val apolloAndroidSupport = "com.apollographql.apollo:apollo-android-support:${Version.apollo}"
    const val apolloRxJavaSupport = "com.apollographql.apollo:apollo-rx2-support:${Version.apollo}"

    // Serialization
    const val gson = "com.google.code.gson:gson:${Version.gson}"
    const val loganSquare = "com.bluelinelabs:logansquare:${Version.loganSquare}"
    const val loganSquareCompiler = "com.bluelinelabs:logansquare-compiler:${Version.loganSquare}"
    const val jacksonCore = "com.fasterxml.jackson.core:jackson-core:${Version.loganSquare}"

    // Persistence
    const val room = "androidx.room:room-runtime:${Version.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Version.room}"
    const val rxRoom = "androidx.room:room-rxjava2:${Version.room}"

    // Lifecycle
    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Version.lifecycle}"

    // Navigation
    const val navigationFragment = "android.arch.navigation:navigation-fragment-ktx:${Version.navigation}"
    const val navigationUI = "android.arch.navigation:navigation-ui-ktx:${Version.navigation}"

    // ImageLoading
    const val glide = "com.github.bumptech.glide:glide:${Version.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Version.glide}"
    const val glideOkHttp3 = "com.github.bumptech.glide:okhttp3-integration:${Version.glide}"
    const val picasso = "com.squareup.picasso:picasso:${Version.picasso}"

    // Profiling
    const val debugDb = "com.amitshekhar.android:debug-db:${Version.debugDb}"
    const val leakCanaryDebug = "com.squareup.leakcanary:leakcanary-android:${Version.leakCanary}"
    const val leakCanaryRelease = "com.squareup.leakcanary:leakcanary-android-no-op:${Version.leakCanary}"
    const val leakCanaryFragment = "com.squareup.leakcanary:leakcanary-support-fragment:${Version.leakCanary}"

    // Social auth
    const val googleAuth = "com.google.android.gms:play-services-auth:${Version.googleAuth}"
    const val socialAuth = "com.jaychang:simpleauth:${Version.simpleAuth}"
    const val socialAuthFacebook = "com.jaychang:simpleauth-facebook:${Version.simpleAuth}"
    const val socialAuthGoogle = "com.jaychang:simpleauth-google:${Version.simpleAuth}"
    const val socialAuthInstagram = "com.jaychang:simpleauth-instagram:${Version.simpleAuth}"
    const val socialAuthTwitter = "com.jaychang:simpleauth-twitter:${Version.simpleAuth}"

    // Facebook
    const val facebookAnalytics = "com.facebook.android:facebook-android-sdk:${Version.facebookAnalytics}"

    // Fabric
    const val fabricPlugin = "io.fabric.tools:gradle:${Version.fabricPlugin}"
    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Version.crashlytics}"

    // Firebase
    const val firebaseCore = "com.google.firebase:firebase-core:${Version.firebaseCore}"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging:${Version.firebaseMessaging}"

    //Maps
    const val googleMaps = "com.google.android.gms:play-services-maps:${Version.googleMaps}"
    const val googleLocation = "com.google.android.gms:play-services-location:${Version.googleLocation}"

    // Tests
    const val jUnit = "junit:junit:${Version.jUnit}"
    const val mockito = "org.mockito:mockito-core:${Version.mockito}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Version.mockitoKotlin}"
    const val mockitoAndroid = "org.mockito:mockito-android:${Version.mockitoAndroid}"
    const val mockitoInline = "org.mockito:mockito-inline:+"
    const val androidTestRunner = "androidx.test:runner:${Version.androidTestRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Version.espresso}"
}
