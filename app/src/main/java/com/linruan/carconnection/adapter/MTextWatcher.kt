package com.linruan.carconnection.adapter

import android.text.Editable
import android.text.TextWatcher

/**
 * author：shichengxiang on 2020/5/30 11:16
 * email：1328911009@qq.com
 */
open class MTextWatcher:TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}