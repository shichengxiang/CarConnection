package com.linruan.carconnection.ui.store

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.SelectCityAdapter
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.entities.City
import com.linruan.carconnection.moudle.AddressListPresenter
import com.linruan.carconnection.moudle.AddressListView
import com.linruan.carconnection.toast
import kotlinx.android.synthetic.main.activity_editaddress.*

class EditAddressActivity : BaseActivity(), AddressListView {
    companion object {
        val ADDRESSKEY = "address"
    }

    private var mModifyAddress: Address? = null //需要修改地址的id
    private var mPresenter: AddressListPresenter? = null
    private var mProvince = City() //省
    private var mCity = City() //市
    private var mAreaId = City() //区
    private var mTvCountry: TextView? = null
    private var mProvinceList = ArrayList<City>()

    override fun getLayout() = R.layout.activity_editaddress

    override fun initView() {
        mPresenter = AddressListPresenter(this)
        toolbar.setTitle("编辑收货地址")
        handleIntent(intent)
        mTvCountry = tvCountry
        mPresenter?.getCity(0, 0)
        mAreaAdapter = SelectCityAdapter(this@EditAddressActivity, object : MCallBack<City> {
            override fun call(t: City, type: Int) {
                when (type) {
                    0 -> {
                        mProvince = t
                        mTvCountry?.text = "${mProvince.name} "
                        mPresenter?.getCity(t.id, 1)
                    }
                    1 -> {
                        mCity = t
                        mTvCountry?.text = "${mProvince.name} ${mCity.name} "
                        mPresenter?.getCity(t.id, 2)
                    }
                    2 -> {
                        mAreaId = t
                        mTvCountry?.text = "${mProvince.name} ${mCity.name} ${t.name}"
                        cityDialog?.dismiss()
                    }
                }
            }
        })
        mTvCountry?.setOnClickListener { showCityDialog() }
        btnCommit?.setOnClickListener { checkAndCommitAddress() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        if (intent == null)
            return
        mModifyAddress = intent.extras?.get(ADDRESSKEY) as Address?
        if (mModifyAddress != null) {
            etPerson.setText(mModifyAddress?.name ?: "")
            etMobile.setText(mModifyAddress?.phone ?: "")
            tvCountry.setText(mModifyAddress?.getCountry() ?: "")
            etDetailAddress.setText(mModifyAddress?.address ?: "")
        }
    }

    private var mAreaAdapter: SelectCityAdapter? = null
    private var cityDialog: CityDialog? = null
    private fun showCityDialog() {
        if (cityDialog == null) {
            cityDialog = CityDialog(this, R.layout.layout_dialog_pickcity)
            cityDialog?.setAdapter(mAreaAdapter)
        }
        if (cityDialog != null && !cityDialog!!.isShowing) {
            mAreaAdapter?.setData(mProvinceList, 0)
            cityDialog?.show()
        }
    }

    fun checkAndCommitAddress() {
        var person = etPerson.text.toString()
        if (person.isNullOrBlank()) {
            toast("请填写收货人")
            return
        }
        var mobile = etMobile.text.toString()
        if (mobile.isNullOrBlank()) {
            toast("请填写手机号")
            return
        }
        var country = tvCountry.text.toString()
        if (country.isNullOrBlank()) {
            toast("请选择城市")
            return
        }
        var area = etDetailAddress.text.toString()
        if (area.isNullOrBlank()) {
            toast("请填写详细地址")
            return
        }
        mPresenter?.addAddress(mModifyAddress?.id ?: "", person, mobile, mProvince.id.toString(), mCity.id.toString(), mAreaId.id.toString(), area)
    }

    override fun onGetAddressSuccess(list: ArrayList<Address>?) {
    }

    override fun onGetCityListSuccess(cities: ArrayList<City>, type: Int) {
        if (type == 0) {
            mProvinceList = cities
        }
        mAreaAdapter?.setData(cities, type)
    }

    override fun onAddOrModifyAddressSuccess() {
        onBackPressed()
    }

    override fun onDeleteAddressSuccess() {
    }

    open class CityDialog(context: Context, layout: Int, style: Int = R.style.TipDialog) : CommonDialog(context, layout, style) {
        private var rvAreas: RecyclerView? = null
        override fun initView(root: View) {
            window?.setGravity(Gravity.BOTTOM)
            window?.setWindowAnimations(R.style.BottomDialogAnim)
            root.findViewById<View>(R.id.tvClose)
                    .setOnClickListener { dismiss() }
            rvAreas = root.findViewById<RecyclerView>(R.id.rvProvinces)
            rvAreas?.layoutManager = LinearLayoutManager(context)
        }

        fun setAdapter(areaAdapter: SelectCityAdapter?) {
            rvAreas?.adapter = areaAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
        mAreaAdapter = null
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}