package com.linruan.carconnection.ui

import android.os.CountDownTimer
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.amap.api.location.AMapLocation
import com.bumptech.glide.Glide
import com.linruan.carconnection.*
import com.linruan.carconnection.entities.net.GetGuideResponse
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.user.LoginActivity
import com.linruan.carconnection.utils.MapUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.CountedCompleter

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/5/13 16:09
 * @version
 */
class SplashActivity : BaseActivity() {


    private var countDownTimer: CountDownTimer? = null
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun initView() {
        rlSecondPage.postDelayed({
            var anim = AnimationUtils.loadAnimation(this, R.anim.anim_adert_fromright_enter)
            rlSecondPage.visibility = View.VISIBLE
            rlSecondPage.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    rlFirstPage.visibility = View.GONE
                    countDownTimer?.start()
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }, 1000)
        tvSkip.setOnClickListener {
            skip()
        }
        countDownTimer = object : CountDownTimer(3 * 1000, 1000) {
            override fun onFinish() {
                skip()
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }
        //启动页获取经纬度
        MapUtil.getInfoLocation(this, object : MCallBack<AMapLocation> {
            override fun call(t: AMapLocation, position: Int) {
                logI("获取当前维度经度： ${t.latitude}  ${t.longitude}")
                UserManager.currentLocation = arrayOf(t.latitude, t.longitude)
            }
        })
        getAdvert()//获取广告
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    fun skip() {
        countDownTimer?.cancel()
        Router.openUI(this, MainActivity::class.java)//全部默认首页
//        if (UserManager.isLogin()) {
//            Router.openUI(this, MainActivity::class.java)
//        } else {
//            Router.openUI(this, LoginActivity::class.java)
//        }
        finish()
    }

    /**
     * 获取引导页广告
     */
    private fun getAdvert() {
        Client.getInstance().guideAdvert(object : JsonCallback<GetGuideResponse>() {
            override fun onSuccess(response: Response<GetGuideResponse?>?) {
                if (response?.body().isSuccess()) {
                    val list = response?.body()?.data?.list
                    if (!list.isNullOrEmpty()) {
                        if (!this@SplashActivity.isDestroyed)
                            Glide.with(this@SplashActivity).load(
                                list[0].cover ?: ""
                            ).dontAnimate().into(iv_advertising)
                    }
                }
            }
        })
    }
}
