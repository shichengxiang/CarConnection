package com.linruan.carconnection.ui.store

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.AddressListAdapter
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.entities.City
import com.linruan.carconnection.moudle.AddressListView
import com.linruan.carconnection.moudle.AddressListPresenter
import kotlinx.android.synthetic.main.activity_addressmanager.*

class AddressManagerActivity : BaseActivity(), AddressListView {
    companion object{
        const val RESULTCODE_SELECTEADDRESS=1001
    }
    private var mAdapter: AddressListAdapter? = null
    private var mPresenter: AddressListPresenter? = null
    override fun getLayout() = R.layout.activity_addressmanager

    override fun initView() {
        mPresenter = AddressListPresenter(this)
        toolbar.setTitle("收货地址")
        btnAddAddress.setOnClickListener { Router.openUI(this, EditAddressActivity::class.java) }
        rvAdddressList.layoutManager = LinearLayoutManager(this)
        mAdapter = AddressListAdapter(this)
        //        rvAdddressList.setEmptyView(emptyView)
        rvAdddressList.adapter = mAdapter
        mAdapter?.setCallBack(object : MCallBack<Address> {
            override fun call(t: Address, position: Int) {
                rvAdddressList.closeMenu()
                showConfirmDialog(t.id ?: "", position)
            }
        })
        mAdapter?.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClickListener(position: Int) {
                setResult(RESULTCODE_SELECTEADDRESS,Intent().apply {
                    var bundle=Bundle().apply {
                        putSerializable("address",mAdapter?.getData()?.get(position))
                    }
                    this.putExtras(bundle)
                })
                onBackPressed()
            }
        })
    }

    fun showConfirmDialog(id: String, position: Int) {
        var dialog = TipDialog(this)
        dialog.getTitleView()?.text = "温馨提示"
        dialog.getDescView()?.text = "确定删除此地址吗?"
        dialog.getLeftView()
                ?.apply {
                    text = "取消"
                    setOnClickListener { dialog.dismiss() }
                }
        dialog.getRightView()
                ?.apply {
                    text = "确定"
                    setOnClickListener {
                        dialog.dismiss()
                        mAdapter?.removeData(position)
                        mPresenter?.deleteAddress(id)
                    }
                }
        dialog.setCancelable(true)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.getAddressList()
    }

    override fun onGetAddressSuccess(list: ArrayList<Address>?) {
        mAdapter?.setData(list)
    }

    override fun onGetCityListSuccess(cities: ArrayList<City>, type: Int) {
    }

    override fun onAddOrModifyAddressSuccess() {
    }

    override fun onDeleteAddressSuccess() {
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}