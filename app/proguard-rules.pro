# Proguard / R8 Security & Hardening Rules for Arbaeen Iraqi Translator

# Strip all debug console logs in release build
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# Preserve Room Database Entities and DAOs
-keep class com.example.data.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# Preserve Moshi & Retrofit Models
-keep class com.example.data.PhraseEntity { *; }

# Preserve Cafe Bazaar AIDL InAppBillingService interface and IPC classes
-keep class com.android.vending.billing.** { *; }
-keep interface com.android.vending.billing.IInAppBillingService { *; }
-keep class com.farsitel.bazaar.** { *; }

# Preserve In-App Billing Manager & Security Obfuscation Classes
-keep class com.example.billing.** { *; }
-keep class com.example.security.** { *; }

# Obfuscation and Security Hardening
-repackageclasses ''
-allowaccessmodification
-dontusemixedcaseclassnames
-dontpreverify

# Keep Kotlin Coroutines & Lifecycle Attributes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
