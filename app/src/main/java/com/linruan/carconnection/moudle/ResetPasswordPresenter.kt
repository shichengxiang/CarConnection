package com.linruan.carconnection.moudle

import android.view.View
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/9/9 21:05
 * @version
 */
class ResetPasswordPresenter(view: ResetPasswordView?) : BasePresenter<ResetPasswordView>(view) {
    /**
     * 重置密码
     */
    fun resetPassword(account:String,password:String,repassword:String,btn:View){
        btn.isEnabled=false
        LoadingDialog.loading(mContext)
        Client.getInstance().resetPassword(account,password,repassword,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
                btn.isEnabled=true
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onResetPasswordSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"重置密码失败")
                }
            }

        })
    }
}
interface ResetPasswordView:BaseView{
    fun onResetPasswordSuccess()
}
