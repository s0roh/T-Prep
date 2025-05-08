# ================================================
# 1. Общие правила (всегда включайте)
# ================================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes InnerClasses,Signature
-keepattributes *Annotation*
-keepclassmembers class ** {
    @android.os.Parcelable *;
}

# ================================================
# 2. Kotlin и корутины
# ================================================
-keepclassmembers class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# ================================================
# 3. Kotlin Serialization
# ================================================
-keepnames class kotlinx.serialization.**
-keepclassmembers class ** implements kotlinx.serialization.Serializable {
    <fields>;
    <methods>;
}
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *;
}

# ================================================
# 4. Dagger / Hilt
# ================================================
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-keep class javax.inject.** { *; }
-keep interface javax.inject.** { *; }
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }
-keep class dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends dagger.hilt.EntryPoint
-keepclassmembers class * {
    @javax.inject.* *;
    @dagger.* *;
    @dagger.hilt.InstallIn <fields>;
    @dagger.hilt.android.lifecycle.HiltViewModel <methods>;
}

# ================================================
# 5. Room
# ================================================
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keepclassmembers class com.example.database.models.** {
    *;
}
-keep class com.example.database.models.** { *; }

# ================================================
# 6. Retrofit
# ================================================
-keepattributes RuntimeVisibleAnnotations
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class com.example.network.dto.** { *; }

# ================================================
# 7. Gson
# ================================================
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }

# ================================================
# 8. Navigation Component + SafeArgs
# ================================================
-keep class androidx.navigation.NavArgs { *; }
-keepclassmembers class * implements androidx.navigation.NavArgsLazy
-keep class androidx.navigation.** { *; }
-keep class com.example.tprep.app.navigation.** { *; }
-keepclassmembers class * implements androidx.navigation.NavController {
    public *;
}
-keepclassmembers class com.example.tprep.app.navigation.Screen {
    public static *;
}
-keep class com.example.tprep.app.navigation.Screen { *; }
-keepclassmembers class com.example.tprep.app.navigation.Screen$* { *; }

# ================================================
# 9. WorkManager
# ================================================
-keep class androidx.work.** { *; }

# ================================================
# 10. AndroidX Lifecycle
# ================================================
-keep class androidx.lifecycle.** { *; }
-keep class * implements androidx.lifecycle.DefaultLifecycleObserver

# ================================================
# 11. Compose
# ================================================
-keep class androidx.compose.ui.focus.** { *; }
-keepclassmembers class androidx.compose.ui.focus.FocusRequester {
    public <init>();
}

# ================================================
# 12. Android Image Cropper
# ================================================
-keep class com.canhub.cropper.** { *; }
-dontwarn com.canhub.cropper.**
-keep public class com.canhub.cropper.CropImageActivity

# ================================================
# 13. Parcelable
# ================================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * {
    <init>(android.os.Parcel);
}

# ================================================
# 14. Android компоненты
# ================================================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ================================================
# 15. Отладка и отчёты
# ================================================
-printusage removed.txt
-printmapping mapping.txt
-printconfiguration full-r8-config.txt

# ================================================
# 16. Coil
# ================================================
-keep class coil.** { *; }
-keep interface coil.** { *; }
-keepclassmembers class coil.** { *; }

# Для ImageRequest
-keep class coil.request.** { *; }
-keepclassmembers class coil.request.** {
    *;
}