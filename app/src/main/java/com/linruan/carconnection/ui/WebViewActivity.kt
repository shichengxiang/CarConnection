package com.linruan.carconnection.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.webkit.*
import com.blankj.utilcode.util.LogUtils
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import kotlinx.android.synthetic.main.activity_webview.*

/**
 * author：shichengxiang on 2020/6/10 17:05
 * email：1328911009@qq.com
 */
class WebViewActivity : BaseActivity() {
    private var currentUrl = ""
    override fun getLayout() = R.layout.activity_webview

    override fun initView() {
        if (intent.hasExtra("url"))
            currentUrl = intent.getStringExtra("url")
        var title = intent.getStringExtra("title")
        initWebview(mWebView)
        mWebView.loadUrl(currentUrl)
        toolbar.setTitle(title)

    }

    private fun initWebview(webView: WebView) {
        webView.settings.apply {
            setSupportZoom(false) //缩放
            useWideViewPort = true //调整图片尺寸到webview大小
            loadWithOverviewMode = true //setLoadWithOverviewMode
            loadsImagesAutomatically = true //自动加载图片
            javaScriptEnabled = true //js脚本
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//关闭webview中缓存
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                LogUtils.e("url")
                webView.loadUrl(url)
                return true
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
//                LogUtils.e(newProgress)
                if (newProgress < 100) {
                    progressBar.progress = newProgress
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                LogUtils.e(title)
                super.onReceivedTitle(view, title)
                //设置标题
            }
        }


    }

    companion object {
        @JvmStatic
        fun openWebView(context: Context?, url: String, title: String?) {
            var intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("title", title)
            context?.startActivity(intent)
        }
    }
}
