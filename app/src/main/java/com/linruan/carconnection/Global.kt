package com.linruan.carconnection

import android.content.Context
import android.widget.ImageView
import cn.jiguang.analytics.android.api.JAnalyticsInterface
import cn.jiguang.share.android.api.JShareInterface
import cn.jpush.android.api.JPushInterface
import com.bumptech.glide.Glide
import com.linruan.carconnection.utils.SharePreferenceUtil
import com.linruan.carconnection.utils.Util
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader

/**
 * 全局控制
 */
object Global {

    fun init(context: Context) {
        SharePreferenceUtil.init(context)
        Util.init(context)
        //设置下拉刷新加载更多
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> MaterialHeader(context) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> ClassicsFooter(context) }
        //album 初始化
        Album.initialize(AlbumConfig.newBuilder(context)
                .setAlbumLoader(object : AlbumLoader {
                    override fun load(imageView: ImageView?, albumFile: AlbumFile?) {
                        load(imageView, albumFile?.path)
                    }

                    override fun load(imageView: ImageView?, url: String?) {
                        Glide.with(context)
                                .load(url)
                                .error(R.mipmap.img_default_image)
                            .dontAnimate()
                                .into(imageView!!)
                    }
                })
                .build()
                        )
        //极光推送
        JShareInterface.init(context)
        JPushInterface.setDebugMode(BuildConfig.DEBUG)
        //极光统计
        JAnalyticsInterface.init(context)
        JAnalyticsInterface.setDebugMode(BuildConfig.DEBUG)
        JAnalyticsInterface.initCrashHandler(context)
//        JPushInterface.init(context)
        logI("/**\n" +
                "*   ┏┓　　　┏┓ \n" +
                "* ┏┛┻━━━┛┻┓ \n" +
                "* ┃　　　　　　　┃ \n" +
                "* ┃　　　━　　　┃ \n" +
                "* ┃　＞　　　＜　┃ \n" +
                "* ┃　　　　　　　┃ \n" +
                "* ┃...　⌒　...　┃ \n" +
                "* ┃　　　　　　　┃ \n" +
                "* ┗━┓　　　┏━┛ \n" +
                "*     ┃　　　┃　 \n" +
                "*     ┃　　　┃ \n" +
                "*     ┃　　　┃ \n" +
                "*     ┃　　　┃  神兽保佑 \n" +
                "*     ┃　　　┃  代码无bug　　 \n" +
                "*     ┃　　　┃ \n" +
                "*     ┃　　　┗━━━┓ \n" +
                "*     ┃　　　　　　　┣┓ \n" +
                "*     ┃　　　　　　　┏┛ \n" +
                "*     ┗┓┓┏━┳┓┏┛ \n" +
                "*       ┃┫┫　┃┫┫ \n" +
                "*       ┗┻┛　┗┻┛ \n" +
                "*/\n"
            )
//        var client = OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(5, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.SECONDS)
//                .addInterceptor(HttpLoggingInterceptor("okgo =="))
//                .build()
//        OkGo.getInstance()
//                .init(context as Application?)
//                .okHttpClient = client
    }
}
