package com.linruan.carconnection.ui

import com.linruan.carconnection.BaseActivity
import pub.devrel.easypermissions.EasyPermissions

class Test : BaseActivity() {
    var permissions = arrayOf("1", "2")
    fun test() {
        EasyPermissions.requestPermissions(this, "权限申请", 1001, *permissions)
    }

    override fun getLayout(): Int {
        return 0
    }

    override fun initView() {}
}