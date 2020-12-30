package com.linruan.carconnection.ui.mine

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.LoadUpImageAdapter
import com.linruan.carconnection.adapter.MTextWatcher
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.adapter.TextListAdapter
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.GetComplainType
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.toast
import com.lzy.okgo.model.Response
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_complain.*

/**
 * author：Administrator on 2020/5/30 10:41
 * email：1328911009@qq.com
 */
class ComplainActivity : BaseActivity() {

    private var chooseDialog: Dialog? = null
    private var tvType: TextView? = null
    private val MAXCOUNT = 6
    private var mComplainTypes:ArrayList<GetComplainType.ComplainType>?= arrayListOf()
    override fun getLayout() = R.layout.activity_complain

    override fun initView() {
        toolbar.setTitle("投诉")
        tvType = findViewById(R.id.tvComplainType)
        //投诉
        etInputComplain.addTextChangedListener(object : MTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvInputCount.text = "${s?.length ?: 0}/200"
            }
        })
        //选择
        rlChooseType.setOnClickListener { showPop() }
        btnCommit.setOnClickListener {
            toast("提交")
        }
        getComplainType()
    }

    /**
     * 获取投诉类型
     */
    private fun getComplainType(){
        Client.getInstance().getComplainTye(object :JsonCallback<GetComplainType>(){
            override fun onSuccess(response: Response<GetComplainType?>?) {
                if(response?.body()?.isSuccess()==true){
                        mComplainTypes=response?.body()?.data?.list
                }else{
                    toast(response?.body()?.msg?:"获取类别失败")
                }
            }
        })
    }

    fun showPop() {
        if (chooseDialog == null){
            var types= arrayListOf<String>()
            mComplainTypes?.forEach {
                types.add(it.title?:"")
            }
            chooseDialog = object : CommonDialog(this, R.layout.layout_dialog_list) {
                override fun initView(root: View) {
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    var rv = root.findViewById<RecyclerView>(R.id.rvPopList)
                    var btnCancel = root.findViewById<View>(R.id.btnCancel)
                    rv.layoutManager = LinearLayoutManager(this@ComplainActivity)
                    rv.adapter = TextListAdapter(
                        this@ComplainActivity,
                        types,
                        object : MCallBack<String> {
                            override fun call(t: String, p: Int) {
                                dismiss()
                                tvType?.setText(t)
                            }
                        })
                    btnCancel.setOnClickListener { dismiss() }

                }
            }
        }
        chooseDialog?.show()
        chooseDialog?.window?.setGravity(Gravity.BOTTOM)
    }
}