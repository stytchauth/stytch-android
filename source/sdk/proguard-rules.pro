# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep,allowoptimization public interface com.stytch.sdk.**, com.stytch.sdk.**.*$* {
    public *;
}


-keep,allowoptimization public enum com.stytch.sdk.** {
    public *;
}

-keep,allowoptimization public class
    com.stytch.sdk.**.*Response*,
    com.stytch.sdk.b2b.*,
    com.stytch.sdk.common.*,
    com.stytch.sdk.consumer.*,
    com.stytch.sdk.ui.b2b.*,
    com.stytch.sdk.ui.b2c.* {
    public *;
}

-keep,allowoptimization @com.stytch.sdk.common.annotations.JacocoExcludeGenerated public class com.stytch.sdk.** {
    public *;
}

-keep,allowoptimization public class com.stytch.sdk.**.*$Companion* {
    public *;
}

-keep,allowoptimization,allowobfuscation,allowshrinking public class
    !com.stytch.sdk.**.*Impl*,
    !com.stytch.sdk.**.*Request*,
    !com.stytch.sdk.**.*JsonAdapter* {
    public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable


# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
-dontwarn java.lang.invoke.StringConcatFactory
