package com.linruan.carconnection.ui.store

import com.bumptech.glide.Glide
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.toast
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_goodsevaluate.*

/**
 * author：shichengxiang on 2020/6/4 18:19
 * email：1328911009@qq.com
 */
class GoodsEvaluateActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_goodsevaluate

    override fun initView() {
        toolbar.setTitle("商品评价")
        val bundle = intent.extras
        val goods = bundle?.getSerializable("goods") as Goods
        val orderNo = bundle?.getString("orderNo")
        val orderId = bundle?.getString("orderId")
        Glide.with(this)
            .load(goods.goods_cover)
            .error(R.mipmap.img_default_image)
            .dontAnimate()
            .into(ivProductImage)
        tvGoodsName.text = goods.goods_title ?: ""
        tvOrderNo.text = "订单编号：${orderNo}"
        btnEvaluate.setOnClickListener {
            var content = etInputEvaluate.text.toString()
            if (content.isNullOrBlank()) {
                toast("评价内容不能为空")
                return@setOnClickListener
            }
            evaluate(orderId ?: "", goods.id, content)
        }
    }

    private fun evaluate(orderId: String, goodsId: String, content: String) {
        LoadingDialog.loading(this)
        Client.getInstance()
            .evaluateGoods(orderId, goodsId, content, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body()?.isSuccess() == true) {
                        toast("商品评价成功")
                        onBackPressed()
                    } else {
                        toast(response?.body()?.msg ?: "评价出现错误了，点击重试")
                    }
                }
            })
    }
}