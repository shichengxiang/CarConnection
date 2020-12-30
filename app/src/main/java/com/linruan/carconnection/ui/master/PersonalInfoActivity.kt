package com.linruan.carconnection.ui.master

import android.content.Intent
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.ui.mine.EditNickActivity
import kotlinx.android.synthetic.main.activity_personalinfo.*

/**
 * author：shichengxiang on 2020/6/3 10:51
 * email：1328911009@qq.com
 */
class PersonalInfoActivity:BaseActivity() {
    override fun getLayout()= R.layout.activity_personalinfo

    override fun initView() {
        toolbar.setTitle("个人信息")
        tvGoEditNick.setOnClickListener { Router.openUIForResult(this,EditNickActivity::class.java,101) }
        tvGoEditNick.text = UserManager.getUser()?.name?:""
        rlGoAuthPage.setOnClickListener { Router.openUI(this,MasterAuthActivity::class.java) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            var name = data?.getStringExtra("nick")
            tvGoEditNick.text = name ?: ""
            UserManager.setNick(name ?: "")
        }
    }
}