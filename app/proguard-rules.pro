# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class cn.suozhi.DiBi.common.custom.LollipopWebView {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontwarn com.qiyukf.**
#-keep class com.qiyukf.** {*;}
#-assumenosideeffects class android.util.Log{
#    public static boolean isLoggable(java.lang.String,int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}

#指定代码的压缩级别
-optimizationpasses 5

#包名不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化 不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

#忽略警告
-ignorewarning

#保护注解
-keepattributes *Annotation*

#保持四大组件，Application，Fragment不混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

#保持  方法不被混淆
-keepclasseswithmembernames class * {
  native <methods>;
}
-keepclassmembers class * extends android.app.Activity {
  public void *(android.view.View);
}
#保持枚举enum类不native被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}
#保持 Parcelable（打包） 不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
  *;
}
-keep class * extends android.view.View{*;}
-keep class * extends android.app.Dialog{*;}
-keep class * implements java.io.Serializable{*;}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

#volley
#-dontwarn com.android.volley.**
#-keep class com.android.volley.**{*;}

#fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*;}

#happy-dns
-dontwarn com.qiniu.android.dns.**
-keep class com.qiniu.android.dns.**{*;}

#okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.**{*;}

-keep class okio.**{*;}

-keep class android.net.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.**{*;}

-keep class com.qiniu.android.**{*;}

-keep class android.support.annotation.**{*;}

-keep class com.squareup.wire.**{*;}

-keep class com.ant.liao.**{*;}

#腾讯
-keep class com.tencent.**{*;}

-keep class u.aly.**{*;}

#ImageLoader
-keep class com.nostra13.universalimageloader.**{*;}

#友盟
-dontwarn com.umeng.**
-keep class com.umeng.**{*;}

#pulltorefresh
-keep class com.handmark.pulltorefresh.** { *; }
-keep class android.support.v4.** { *;}
-keep public class * extends android.support.v4.**{
 public protected *;}
-keep class android.support.v7.** {*;}




-dontwarn com.baidu.**
-dontwarn com.netease.**
-dontwarn com.alibaba.**
-dontwarn com.google.**
-dontwarn com.luck.**
-dontwarn com.sun.**
-dontwarn com.yalantis.**

-dontwarn org.apache.**
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-keep interface okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
-keep interface okio.**{*;}

-dontwarn demo.**
-dontwarn cn.bingoogolapple.**
-dontwarn javax.activation.**
-dontwarn android.taobao.**

#保持自定义view不混淆
-keep class cn.suozhi.DiBi.**{*;}
