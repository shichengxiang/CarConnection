package com.linruan.carconnection

interface MCallBack<T> {
    fun call(t:T,position:Int)
}
interface AdapterListener{
    fun  call(tag:Int,position: Int,vararg params:String)
}
