package com.linruan.carconnection.moudle

import android.view.View
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.User
import com.linruan.carconnection.entities.net.LoginResponse
import com.linruan.carconnection.logE
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/1 11:03
 * email：1328911009@qq.com
 */
class LoginPresenter(view: LoginView?) : BasePresenter<LoginView>(view) {
    fun login(account:String,group:Int,nick:String,avatar:String,openId:String){
        Client.getInstance().login(account,group,nick,avatar,openId,object :JsonCallback<LoginResponse>(LoginResponse::class.java){
            override fun onSuccess(response: Response<LoginResponse?>?) {
                val body = response?.body()
                if(body?.isSuccess()==true){
                    var isWorker=(body.data?.group_id==2)
                    UserManager.setUser(User().apply {
                        id=body.data?.id?:""
                        name = if(body.data?.name.isNullOrBlank()) "用户$account" else body.data?.name?:""
                        this.avatar=body.data?.avatar?:""
                        this.phone = account
                        token=body.data?.token?:""
                        this.isMaster=isWorker
                    })
                    mView?.onLoginSuccess(isWorker,body.data?.identify?:"0")
                }else{
                    mView?.onError(body?.msg)
                }
            }

            override fun onError(response: Response<LoginResponse?>?) {
                super.onError(response)
                logE(response?.message()?:"")
                mView?.onError("网络异常")
            }
        })
    }

    /**
     * 密码登录
     */
    fun pLogin(account:String,password:String){
        Client.getInstance().plogin(account,password,object :JsonCallback<LoginResponse>(LoginResponse::class.java){
            override fun onSuccess(response: Response<LoginResponse?>?) {
                LoadingDialog.dismiss()
                val body = response?.body()
                if(body?.isSuccess()==true){
                    var isWorker=(body.data?.group_id==2)
                    UserManager.setUser(User().apply {
                        id=body.data?.id?:""
                        name = if(body.data?.name.isNullOrBlank()) "用户$account" else body.data?.name?:""
                        this.avatar=body.data?.avatar?:""
                        this.phone = account
                        token=body.data?.token?:""
                        this.isMaster=isWorker
                    })
                    mView?.onLoginSuccess(isWorker,body.data?.identify?:"0")
                }else{
                    mView?.onError(body?.msg)
                }
            }

            override fun onError(response: Response<LoginResponse?>?) {
                super.onError(response)
                LoadingDialog.dismiss()
                logE(response?.message()?:"")
                mView?.onError("网络异常")
            }
        })
    }
    fun wxLogin(nick:String,avatar:String,openId:String){
        Client.getInstance().wxLogin(openId,object :JsonCallback<LoginResponse>(LoginResponse::class.java){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<LoginResponse?>?) {
                val body = response?.body()
                if(body?.isSuccess()==true){
                    var isWorker=(body.data?.group_id==2)
                    UserManager.setUser(User().apply {
                        id=body.data?.id?:""
                        name = body.data?.name?:""
                        this.avatar=body.data?.avatar?:""
                        phone = body.data?.account?:""
                        token=body.data?.token?:""
                        isMaster=isWorker
                    })
                    mView?.onLoginSuccess(isWorker,body.data?.identify?:"")
                }else{
                    mView?.onUnbindMobile(nick,avatar,openId)
                }
            }

            override fun onError(response: Response<LoginResponse?>?) {
                super.onError(response)
                mView?.onError("网络异常")
            }
        })
    }
}
interface LoginView:BaseView{
    fun onLoginSuccess(isIdentified:Boolean,identify:String)
    fun onUnbindMobile(nick: String,avatar:String,openId:String)
}
