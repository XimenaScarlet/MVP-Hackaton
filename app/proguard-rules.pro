# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\scarl\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class androidx.hilt.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }

# Model classes (Firestore)
-keepclassmembers class com.example.univapp.data.** {
    @com.google.firebase.firestore.PropertyName *;
    @com.google.firebase.firestore.IgnoreExtraProperties *;
    @com.google.firebase.firestore.ServerTimestamp *;
    @com.google.firebase.firestore.Exclude *;
    <fields>;
    <methods>;
}

# Poiji / Excel
-keep class com.poiji.** { *; }
-keep class org.dhatim.fastexcel.** { *; }

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Keep Retrofit (if added later)
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**
