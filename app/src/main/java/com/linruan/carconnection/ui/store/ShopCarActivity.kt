package com.linruan.carconnection.ui.store

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.ShopCarListAdapter
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.SkuBean
import com.linruan.carconnection.moudle.ShopCarPresenter
import com.linruan.carconnection.moudle.ShopCarView
import com.linruan.carconnection.utils.Util
import kotlinx.android.synthetic.main.activity_shopcar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.math.BigDecimal

class ShopCarActivity : BaseActivity(), ShopCarView {

    private var editEnable = false
    private var mAdapter: ShopCarListAdapter? = null
    private var mPresenter:ShopCarPresenter?=null

    override fun getLayout() = R.layout.activity_shopcar

    override fun initView() {
        mPresenter=ShopCarPresenter(this)
        toolbar.apply {
            tvTitle.apply {
                text = "购物车"
                setTextColor(Color.WHITE)
            }
            ivBack.setImageResource(R.drawable.ic_back_white)
            tvRight.apply {
                text = "编辑"
                setTextColor(Color.WHITE)
                setOnClickListener {
                    editEnable = !editEnable
                    if (editEnable) {
                        text = "完成"
                        btnOperate.text = "删除"
                        tvTotalAmount.visibility=View.INVISIBLE
                    } else {
                        text = "编辑"
                        btnOperate.text = "去结算"
                        tvTotalAmount.visibility=View.VISIBLE
                        tvTotalAmount.text = mAdapter?.calculatePrice().toString()
                    }
                }
            }
        }
        btnOperate.setOnClickListener {
            if (editEnable) {
                deleteGoods()
            } else {
                var data=mAdapter?.getData()
                var list= arrayListOf<Goods>()
                data?.forEach {
                    if(it.isSelected){
                        var goods=Goods().apply {
                            title=it.title
                            price=it.price
                            id=it.id
                            goods_id=it.goods_id
                            selectedSkuBean= SkuBean().apply {
                                sku=it.sku
                                skuId=it.sku_id?:""
                                price=it.price
                            }
                            num=it.num
                            sku=it.sku
                            sku_id=it.sku_id
                        }
                        list.add(goods)
                    }
                }
                if(list.size>0){
                    Router.openUIForResult(this,ConfirmGoodsOrderActivity::class.java,111,Bundle().apply {
                        putSerializable(ConfirmGoodsOrderActivity.CARTLIST,list)
                    })
                }else{
                    toast("请选择下单商品")
                }
            }
        }
        ivChooseAll.setOnClickListener {
            ivChooseAll.isSelected=!ivChooseAll.isSelected
            mAdapter?.chooseAll(ivChooseAll.isSelected)
            tvTotalAmount.text = mAdapter?.calculatePrice().toString()
        }
        rvGoods.layoutManager = LinearLayoutManager(this)
        rvGoods.addItemDecoration(object : DividerItemDecoration(this, RecyclerView.VERTICAL) {
            override fun setDrawable(drawable: Drawable) {
                var d =
                    ColorDrawable(ContextCompat.getColor(this@ShopCarActivity, R.color.line_color))
                super.setDrawable(d)
            }

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = Util.px2dp(10)
                super.getItemOffsets(outRect, view, parent, state)
            }
        })
        mAdapter = ShopCarListAdapter(this, object : ShopCarListAdapter.OnPriceChangeCallback {
            override fun onchange(total: BigDecimal,isAllSelected:Boolean) {
                tvTotalAmount.text = total.setScale(2,BigDecimal.ROUND_DOWN).toString()
                if(isAllSelected && !ivChooseAll.isSelected){
                    ivChooseAll.performClick()
                }
            }
        })
        rvGoods.adapter = mAdapter
        rvGoods.setEmptyView(emptyView)
        mPresenter?.getCartList()

    }
    private fun deleteGoods(){
        var goods=mAdapter?.getData()
        var listId= arrayListOf<String>()
        goods?.forEach {
            if(it.isSelected)
                listId.add(it.id)
        }
        mPresenter?.deleteGoodsInCart(btnOperate,*listId.toTypedArray())
    }

    override fun onError(err: String?) {
        toast(err?:"")
    }

    override fun onGetCartListSuccess(list: ArrayList<Goods>?) {
        mAdapter?.setData(list)
    }

    override fun onDeleteGoodsInCartSuccess() {
        mAdapter?.removeSelectedView()
        tvTotalAmount.text = mAdapter?.calculatePrice().toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode==111 ){
            deleteGoods()
        }
    }
}
