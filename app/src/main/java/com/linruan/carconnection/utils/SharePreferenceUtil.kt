package com.linruan.carconnection.utils

import android.content.Context
import android.content.SharedPreferences
import com.linruan.carconnection.entities.User

object SharePreferenceUtil {
    private val SPTAG = "linruan"
    val USERNAME_KEY = "userName" //用户名
    val USERID_KEY = "userid" //用户id
    val USERAVATAR_KEY = "useravatar" //用户头像
    val PHONE_KEY = "phone" //电话
    val ISMASTER_KEY = "isMaster"
    val TOKEN_KEY = "token" //昵称

    private var instance: SharePreferenceUtil? = null
    private var sp: SharedPreferences? = null
    private lateinit var mApplication: Context
    fun init(context: Context) {
        mApplication = context
        sp = context?.getSharedPreferences(SPTAG, Context.MODE_PRIVATE)
    }

    fun putUser(user: User?) {
        if (user == null) {
            sp?.edit()
                    ?.remove(USERNAME_KEY)
                    ?.remove(ISMASTER_KEY)
                    ?.remove(PHONE_KEY)
                    ?.remove(TOKEN_KEY)
                    ?.commit()
            return
        }
        sp?.edit()
                ?.apply {
                    putString(USERNAME_KEY, user.name)
                    commit()
                    putBoolean(ISMASTER_KEY, user.isMaster)
                    commit()
                    putString(PHONE_KEY, user.phone)
                    commit()
                    putString(TOKEN_KEY, user.token)
                    commit()
                    putString(USERID_KEY, user.id)
                    commit()
                    putString(USERAVATAR_KEY, user.avatar)
                    commit()
                }
    }

    fun getUser(): User? {
        if (sp == null)
            sp = mApplication.getSharedPreferences(SPTAG, Context.MODE_PRIVATE)
        val userName = sp?.getString(USERNAME_KEY, "")
        val phone = sp?.getString(PHONE_KEY, "")
        val isMaster = sp?.getBoolean(ISMASTER_KEY, false)
        val token = sp?.getString(TOKEN_KEY, "")
        val id=sp?.getString(USERID_KEY,"")
        val avatar=sp?.getString(USERAVATAR_KEY,"")
        return if (token.isNullOrEmpty()) {
            null
        } else {
            User().also {
                it.name = userName ?: "用户$phone"
                it.phone = phone?:""
                it.isMaster = isMaster ?: false
                it.token = token
                it.id=id?:""
                it.avatar=avatar?:""
            }
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        sp?.edit()
                ?.putBoolean(key, value)
                ?.apply()
    }

    fun putString(key: String, value: String) {
        sp?.edit()
                ?.putString(key, value)
                ?.apply()
    }

    fun getString(key: String, default: String): String {
        return sp?.getString(key, default) ?: ""
    }
    fun getBoolean(key:String,default:Boolean):Boolean{
        return sp?.getBoolean(key,default)?:default
    }

}
