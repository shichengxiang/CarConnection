package com.linruan.carconnection

import android.app.Activity
import android.content.Context
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.TagAliasCallback
import com.linruan.carconnection.entities.FaultPart
import com.linruan.carconnection.entities.MasterStatus
import com.linruan.carconnection.entities.User
import com.linruan.carconnection.entities.net.GetFaults
import com.linruan.carconnection.entities.net.GetRepairType
import com.linruan.carconnection.ui.user.LoginActivity
import com.linruan.carconnection.utils.SharePreferenceUtil

object UserManager {
    var currentLocation = arrayOf(0.0, 0.0) //lat  lng
    private var mUser: User? = null
    var mCarTypes: ArrayList<GetRepairType.DataBean.ListBean>? = null
    var mFaults = arrayListOf<FaultPart>()
    var isMaster = false //未登录前记录是否是师傅
    var limitTime = 3 * 60L //等待时间
    var masterStatus: MasterStatus? = null
    var cachePhone = ""
    fun getUser(): User? {
        if (mUser == null)
            mUser = SharePreferenceUtil.getUser()
        return mUser
    }

    fun setUser(user: User) {
        SharePreferenceUtil.putUser(user)
        this.mUser = user
    }

    fun setNick(nick: String) {
        this.mUser?.name = nick
        SharePreferenceUtil.putString(SharePreferenceUtil.USERNAME_KEY, nick)
    }
//
//    fun setIsMaster(isMaster: Boolean) {
//        if (this.mUser == null) {
//            this.mUser = User()
//        }
//        this.mUser?.isMaster = isMaster
//        SharePreferenceUtil.putBoolean(SharePreferenceUtil.ISMASTER_KEY, isMaster)
//    }

    /**
     * 判断是否登录并跳转登录
     */
    fun isLoginAndSkip(context: Context?): Boolean {
        if (!isLogin()) {
            Router.openUI(context, LoginActivity::class.java)
            return false
        }
        return true
    }


    fun isLogin(): Boolean {
        if (mUser == null)
            mUser = SharePreferenceUtil.getUser()
        return !mUser?.token.isNullOrBlank()
    }

    fun logout() {
        SharePreferenceUtil.putUser(null)
        mUser = null
    }

    /**
     * 登录成功后处理数据 jpush
     */
    fun handleDataWhenLoginSuccess(activity: Activity) {
        //登录成功设置别名 alias
//        var hasTag = SharePreferenceUtil.getString("alias", "")
//        var hasAlias=SharePreferenceUtil.getString("alias",UserManager.getUser()?.id?:"")
//        if (hasTag.isNullOrEmpty()) {
//            hasTag = if (UserManager.getUser()?.isMaster == true) "master" else "customer"
//        }
        if (getUser()?.isMaster == true) {
//            JPushInterface.setTags(activity, 1, setOf("xiucheshifu"))
//            JPushInterface.setTags(activity, setOf("xiucheshifu")
//            ) { code, p1, p2 ->
//                if(code!=0){
//                    logE("师傅推送tag设置失败$code")
//                }
//            }
            JPushInterface.setTags(activity, 1, setOf("xiucheshifu"))
        } else {
//            JPushInterface.setTags(activity, setOf("yonghu")){code,p1,p2->
//                if(code!=0) logE("用户推送tag设置失败$code")
//            }
            JPushInterface.setTags(activity, 1, setOf("yonghu"))
        }
        if (!getUser()?.id.isNullOrBlank()) {
            JPushInterface.setAlias(activity, getUser()?.id) { code, p1, p2 ->
                if (code != 0) logE("推送设置id失败$code")
            }
        }
    }
}
