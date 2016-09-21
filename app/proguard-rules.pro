# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/rocket/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#Using for retrofit & gson
#-keep class com.google.gson.** { *; }
#-keep class com.google.inject.** { *; }
#-keep class retrofit.** { *; }
#-keep class okio.** { *; }
#-dontwarn java.nio.file.**
#-dontwarn org.codehaus.mojo.animal_sniffer.**
#-keep class com.squareup.okhttp.internal.huc.** { *; }
#-keep class com.google.gson.stream.** { *; }
#-keepclassmembernames interface * {
#    @retrofit.http.* <methods>;
#}
#
#-keep class com.simpledeveloper.averse.pojos.Poets.** { *; }
#-keep class com.simpledeveloper.averse.pojos.Poem.** { *; }
#-keep interface retrofit.** { *;}
#-keep interface com.squareup.** { *; }
#-dontwarn retrofit2.**
