package com.linruan.carconnection.ui.mine

import android.content.Intent
import android.graphics.Color
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.toast
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_editnick.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*


/**
 * author：Administrator on 2020/5/29 14:58
 * email：1328911009@qq.com
 */
class EditNickActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_editnick

    override fun initView() {
        toolbar.tvRight.apply {
            text = "保存"
            setBackgroundResource(R.drawable.bg_0ed4d4_radius22)
            setTextColor(Color.WHITE)
            setOnClickListener {
                var name = etInputNick.text.toString()
                if (name.isNullOrBlank()) {
                    return@setOnClickListener
                }
                updateNick(name)
            }
        }
        var name = UserManager.getUser()?.name
        if (!name.isNullOrBlank())
            etInputNick.setText(name)

    }

    private fun updateNick(nick: String) {
        LoadingDialog.loading(this)
        Client.getInstance()
                .updateUserInfo("", nick, "", object : JsonCallback<BaseResponse>() {
                    override fun onFinish() {
                        LoadingDialog.dismiss()
                    }
                    override fun onSuccess(response: Response<BaseResponse?>?) {
                        if (response?.body()
                                    ?.isSuccess() == true) {
                            setResult(101, Intent().apply {
                                putExtra("nick", nick)
                            })
                            finish()
                        } else {
                            toast(response?.body()?.msg ?: "昵称更新失败")
                        }
                    }
                })
    }
}