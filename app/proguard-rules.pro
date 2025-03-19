# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep generic type parameters
-keepattributes Signature

# Keep all Material Design components
-keep class com.google.android.material.** { *; }

# Keep coroutines from being stripped
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }

-keep public class * implements java.lang.reflect.Type

# Remove all Log.d(), Log.v(), and Log.i() calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}