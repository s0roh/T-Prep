-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes InnerClasses,Signature
-keepattributes *Annotation*
-keepclassmembers class ** {
    @android.os.Parcelable *;
}

-keepclassmembers class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

-keepnames class kotlinx.serialization.**
-keepclassmembers class ** implements kotlinx.serialization.Serializable {
    <fields>;
    <methods>;
}
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *;
}

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

-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keepclassmembers class com.example.database.models.** {
    *;
}
-keep class com.example.database.models.** { *; }

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
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }

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

-keep class androidx.work.** { *; }

-keep class androidx.lifecycle.** { *; }
-keep class * implements androidx.lifecycle.DefaultLifecycleObserver

-keep class androidx.compose.ui.focus.** { *; }
-keepclassmembers class androidx.compose.ui.focus.FocusRequester {
    public <init>();
}

-keep class com.canhub.cropper.** { *; }
-dontwarn com.canhub.cropper.**
-keep public class com.canhub.cropper.CropImageActivity

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * {
    <init>(android.os.Parcel);
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-printusage removed.txt
-printmapping mapping.txt
-printconfiguration full-r8-config.txt

-keep class coil.** { *; }
-keep interface coil.** { *; }
-keepclassmembers class coil.** { *; }

-keep class coil.request.** { *; }
-keepclassmembers class coil.request.** {
    *;
}