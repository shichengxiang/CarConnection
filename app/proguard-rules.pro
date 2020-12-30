# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 本地代码混淆
#-dontshrink 关闭压缩
-optimizationpasses 5
-keep class com.linruan.carconnection.view.*{*;}
-keep class com.linruan.carconnection.entities.*{*;}
-keep class com.linruan.carconnection.entities.net.*{*;}
-keepclasseswithmembernames class * { # 保持native方法不被混淆
    native <methods>;
}
-keep class * implements Android.os.Parcelable { # 保持Parcelable不被混淆
    public static final Android.os.Parcelable$Creator *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# gson
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# 支付宝 微信


# 高德地图
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
-keep   class com.amap.api.services.**{*;}
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
# MOB
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**
#EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# 极光推送
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
-keep public class com.sina.** {
    *;
}
-keep class android.support.** { *; }
-keep class androidx.** { *; }
-keep class com.google.android.** { *; }

#
#Rxjava RxAndroid
-dontwarn rx.*
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQuene*Field*{
long producerIndex;
long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
rx.internal.util.atomic.LinkedQueueNode producerNode;
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
# recyclerview-swipe
-keepclasseswithmembers class android.support.v7.widget.RecyclerView$ViewHolder {
   public final View *;
}
-dontwarn com.yanzhenjie.recyclerview.swipe.**
-keep class com.yanzhenjie.recyclerview.swipe.** {*;}
# 微信混淆
-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}
