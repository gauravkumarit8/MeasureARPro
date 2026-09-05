# MeasureAR Pro — release ProGuard/R8 rules.
# Keep rules here are deliberately conservative (broad "keep class X.**") rather
# than tightly scoped, because these libraries use reflection/JNI internally
# and over-aggressive shrinking manifests as runtime crashes, not build errors —
# much harder to debug than a slightly larger APK. Tighten later with real
# crash data from Play Console once the app has traffic.

# ARCore + Filament (via SceneView) use JNI extensively; native code calls back
# into these classes by name, which R8 can't see as a "used" reference.
-keep class com.google.ar.core.** { *; }
-keep class com.google.android.filament.** { *; }
-keep class io.github.sceneview.** { *; }
-dontwarn com.google.ar.core.**
-dontwarn com.google.android.filament.**

# Room generates implementation classes at compile time (via KSP) that R8
# sometimes can't trace back to their @Database/@Entity annotations correctly
# in aggressive shrink passes.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Play Billing Library's AIDL-generated classes are looked up by the Play
# Store app via reflection at runtime, not by our own code.
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.api.**

# AdMob / Play Services Ads + UMP consent SDK.
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.ump.** { *; }
-dontwarn com.google.android.gms.ads.**

# zxing QR encode/decode.
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# Kotlin coroutines/Flow internals occasionally get flagged by aggressive R8
# passes when generic type information is stripped.
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
