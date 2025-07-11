#############################################
# General ProGuard Configuration
#############################################

# Preserve line number info (helpful for Crashlytics stack traces)
-keepattributes SourceFile,LineNumberTable

# Optional: Remove source file names from stack traces
#-renamesourcefileattribute SourceFile

#############################################
# Jetpack Compose
#############################################
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

#############################################
# Lifecycle and ViewModel
#############################################
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

#############################################
# Navigation Compose
#############################################
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

#############################################
# Firebase SDK
#############################################
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.messaging.FirebaseMessagingService { *; }
-keep class com.google.firebase.messaging.RemoteMessage { *; }
-keep class com.google.firebase.remoteconfig.** { *; }
-keep class com.google.firebase.perf.** { *; }
-keep class com.google.firebase.appcheck.** { *; }
-keep class com.google.firebase.installations.** { *; }
-keep class com.google.firebase.firestore.** { *; }

#############################################
# Google Play Services & Maps
#############################################
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class com.google.android.libraries.places.** { *; }
-keep class com.google.maps.android.** { *; }

#############################################
# Coil Image Loading
#############################################
-keep class coil.** { *; }
-dontwarn coil.**

#############################################
# Accompanist Libraries
#############################################
-keep class com.google.accompanist.** { *; }
-dontwarn com.google.accompanist.**

#############################################
# Android Permissions
#############################################
-keep class android.Manifest$permission { *; }

#############################################
# Optional: WebView JS Interface (Uncomment if needed)
#############################################
#-keepclassmembers class com.your.package.YourJsInterface {
#    public *;
#}

#############################################
# Optional: Safe Args, if used
#############################################
#-keep class *Args { *; }

#############################################
# Testing (if needed)
#############################################
-dontwarn org.junit.**
-dontwarn androidx.test.**

#############################################
# Additional Compose optimizations
#############################################
-keep class androidx.compose.runtime.snapshots.** { *; }
-dontwarn androidx.compose.runtime.snapshots.**
