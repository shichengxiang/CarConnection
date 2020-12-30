package com.linruan.carconnection.ui.master

import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.adapter.CarTypeAdapter
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.moudle.BattleSettingPresenter
import com.linruan.carconnection.moudle.BattleSettingView
import com.linruan.carconnection.toast
import kotlinx.android.synthetic.main.activity_battlesetting.*
import kotlinx.android.synthetic.main.layout_dialog_repair_scope.view.*
import kotlinx.android.synthetic.main.layout_dialog_repairtype.view.*

/**
 * author：shichengxiang on 2020/6/1 16:41
 * email：1328911009@qq.com
 */
class BattleSettingActivity : BaseActivity(), BattleSettingView {

    private var typeDialog: CommonDialog? = null
    private var scopeDialog: CommonDialog? = null
    private var mTvGoScopeDialog: TextView? = null
    private var mTvGoTypeDialog: TextView? = null
    private var mPresenter: BattleSettingPresenter? = null
    private var mCurrentScope=-1
    private var mCurrentType=""
    private var mBtnSwitch:Switch?=null
    override fun getLayout() = R.layout.activity_battlesetting

    override fun initView() {
        toolbar.setTitle("抢单设置")
        mPresenter = BattleSettingPresenter(this)
        mTvGoScopeDialog = findViewById(R.id.tvGoScopeDialog)
        mTvGoTypeDialog = findViewById(R.id.tvGoTypeDialog)
        tvGoTypeDialog?.setOnClickListener { showTypeList() }
        mTvGoScopeDialog?.setOnClickListener { showScope() }
        mBtnSwitch=findViewById(R.id.btnSwitch)
        mBtnSwitch?.isChecked = UserManager.masterStatus?.grab == 1
        mBtnSwitch?.setOnCheckedChangeListener { cb, b ->
            mPresenter?.battleSetting(if (b) 1 else 0, mCurrentScope, mCurrentType)
        }
        if (UserManager.masterStatus?.position ?: 0 > 0) {
            mTvGoScopeDialog?.text = "${UserManager.masterStatus?.position}公里"
        }
        mCurrentScope=UserManager.masterStatus?.position?:0
        mCurrentType=UserManager.masterStatus?.leixing?:""
//        tvGoTypeDialog
        if (!UserManager.masterStatus?.leixing.isNullOrBlank()) {
            mTvGoTypeDialog?.text = "${UserManager.masterStatus?.leixing}"
        }

    }

    fun showScope() {
        var types = StringBuilder()
        if (scopeDialog == null) {
            scopeDialog = object : CommonDialog(this, R.layout.layout_dialog_repair_scope) {
                override fun initView(root: View) {
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    window?.setGravity(Gravity.BOTTOM)
                    root.tvCloseDialog.setOnClickListener { dismiss() }
                    root.findViewById<TextView>(R.id.tvLine1).setOnClickListener {
                        mTvGoScopeDialog?.text = "10公里"
                        mCurrentScope=10
                        mPresenter?.battleSetting(if(mBtnSwitch?.isChecked==true) 1 else 0, 10, mCurrentType)
                        dismiss()
                    }
                    root.findViewById<TextView>(R.id.tvLine2).setOnClickListener {
                        mTvGoScopeDialog?.text = "5公里"
                        mCurrentScope=5
                        mPresenter?.battleSetting(if(mBtnSwitch?.isChecked==true) 1 else 0, 5, mCurrentType)
                        dismiss()
                    }
                    root.findViewById<TextView>(R.id.tvLine3).setOnClickListener {
                        mTvGoScopeDialog?.text = "3公里"
                        mCurrentScope=3
                        mPresenter?.battleSetting(if(mBtnSwitch?.isChecked==true) 1 else 0, 3, mCurrentType)
                        dismiss()
                    }
                }

            }
        }
        if (scopeDialog?.isShowing == false)
            scopeDialog?.show()
    }

    fun showTypeList() {
        var types = StringBuilder()
        var typeIds = StringBuffer()
        if (typeDialog == null) {
            typeDialog = object : CommonDialog(this, R.layout.layout_dialog_repairtype) {
                override fun initView(root: View) {
                    var rvTypes=root.findViewById<RecyclerView>(R.id.rvCarTypes)
                    rvTypes.layoutManager=LinearLayoutManager(this@BattleSettingActivity)
                    var adapter=CarTypeAdapter(this@BattleSettingActivity,UserManager.mCarTypes)
                    rvTypes.adapter=adapter
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    window?.setGravity(Gravity.BOTTOM)
//                    var tvLine4 = root.findViewById<CheckBox>(R.id.tvLine4)
                    UserManager.mCarTypes
                    root.btnMakeSure.setOnClickListener {
                        adapter.getData()?.forEach {
                            if(it.isChecked){
                                types.append(it.title).append(",")
                                typeIds.append(it.id).append(",")
                            }
                        }
                        if (types.isNotEmpty())
                            mTvGoTypeDialog?.text = types.toString()
                        if(typeIds.isNotBlank()){
                            mCurrentType=typeIds.substring(0,typeIds.length-1)
                        }
                        mPresenter?.battleSetting(if(mBtnSwitch?.isChecked==true) 1 else 0, mCurrentScope, mCurrentType)
                        dismiss()
                    }
                }

            }
        }
        if (typeDialog?.isShowing == false)
            typeDialog?.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        typeDialog = null
        scopeDialog = null
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
