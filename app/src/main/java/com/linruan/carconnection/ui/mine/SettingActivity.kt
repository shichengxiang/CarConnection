package com.linruan.carconnection.ui.mine

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.linruan.carconnection.*
import com.linruan.carconnection.entities.net.GetAppVersion
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.ui.WebViewActivity
import com.linruan.carconnection.ui.master.PersonalInfoActivity
import com.linruan.carconnection.ui.master.StoreSettingActivity
import com.linruan.carconnection.ui.store.AddressManagerActivity
import com.linruan.carconnection.ui.user.LoginActivity
import com.linruan.carconnection.ui.user.ResetPasswordActivity
import com.linruan.carconnection.utils.update.UpdateManager
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_repaircarorder.toolbar
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_setting

    override fun initView() {
        toolbar.setTitle(getString(R.string.setting))
        btnLogOut.setOnClickListener {
            UserManager.logout()
            Router.openUI(this, LoginActivity::class.java)
            ActivityUtils.finishToActivity(MainActivity::class.java, true)
        }
        rlNickName.setOnClickListener {
            Router.openUIForResult(
                this,
                EditNickActivity::class.java,
                111
            )
        }
        rlAppIntro.setOnClickListener {
            WebViewActivity.openWebView(this,Constant.URL_APP_INTRO,"App介绍")
        }
        rlAddressManager.setOnClickListener {
            Router.openUI(
                this,
                AddressManagerActivity::class.java
            )
        }
        llMasterInfo.setOnClickListener { Router.openUI(this, PersonalInfoActivity::class.java) }
        llMasterShopSetting.setOnClickListener {
            Router.openUI(
                this,
                StoreSettingActivity::class.java
            )
        }
        llResetPassword.setOnClickListener {
            Router.openUI(this,ResetPasswordActivity::class.java)
        }
        llCheckUpdate.setOnClickListener {
            checkVersion()
        }
        llMasterIdea.setOnClickListener { Router.openUI(this, FeedBackActivity::class.java) }
        llMasterPrivacy.setOnClickListener {
            WebViewActivity.openWebView(this,Constant.URL_PRIVACY,"隐私协议")
        }
        initLayout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == 101) {
            var name = data?.getStringExtra("nick")
            tvNickName.text = name ?: ""
            UserManager.setNick(name ?: "")
        }
    }

    /**
     * 根据角色修改布局
     */
    private fun initLayout() {
        if (UserManager.getUser()?.isMaster == true) {
            llMasterInfo.visibility = View.VISIBLE
            llMasterShopSetting.visibility = View.VISIBLE
            llMasterIdea.visibility = View.VISIBLE
//            llMasterPrivacy.visibility = View.VISIBLE
            llCustomerNick.visibility=View.GONE
        } else {
            llMasterInfo.visibility = View.GONE
            llMasterShopSetting.visibility = View.GONE
            llMasterIdea.visibility = View.GONE
//            llMasterPrivacy.visibility = View.GONE
        }
    }

    /**
     * 版本检测
     */
    private fun checkVersion() {
        Client.getInstance().checkAppVersion(object : JsonCallback<GetAppVersion>() {
            override fun onSuccess(response: Response<GetAppVersion?>?) {
                if (response?.body().isSuccess()) {
                    var res = response?.body()?.data
                    UpdateManager.build(this@SettingActivity).checkVersion(
                        res?.version_title ?: "",
                        res?.version ?: 0,
//                        3,
                        res?.down_url2 ?: "",
                        res?.app_intro ?: "",
                        "${res?.down_filesize?:1}M",
                        false, object : UpdateManager.OnUpdateListener {
                            override fun noNewVersion() {
                                toast("已经是最新版本了")
                            }
                        }
                    )
                }
            }
        })
    }
}
