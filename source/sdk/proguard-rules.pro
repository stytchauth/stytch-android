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

-keep public interface com.stytch.sdk.** {
    public *;
}

-keep public class com.stytch.sdk.**$* {
    *;
}

-keep public class com.stytch.sdk.b2b.* {
    public *;
}

-keep public class com.stytch.sdk.common.* {
    public *;
}

-keep public class com.stytch.sdk.consumer.* {
    public *;
}

-keep public class com.stytch.sdk.ui.* {
    public *;
}

-keep public class com.stytch.sdk.**.models.* {
    public *;
}


# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
-dontwarn java.lang.invoke.StringConcatFactory
