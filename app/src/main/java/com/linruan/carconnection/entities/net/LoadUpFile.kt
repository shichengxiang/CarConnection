package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/1 14:26
 * email：1328911009@qq.com
 */
class LoadUpFile:BaseResponse() {
    /**
     * error : 0
     * msg : 上传成功
     * data : {"id":"873","thumb":"http://chelianyizhong.com/public/uploads/20200601/7e1191df9058ff01b8ae986e6be12349_thumb.jpg","filepath":"http://chelianyizhong.com/public/uploads/20200601/7e1191df9058ff01b8ae986e6be12349.jpg"}
     */
    var data: DataBean? = null

    class DataBean {
        /**
         * id : 873
         * thumb : http://chelianyizhong.com/public/uploads/20200601/7e1191df9058ff01b8ae986e6be12349_thumb.jpg
         * filepath : http://chelianyizhong.com/public/uploads/20200601/7e1191df9058ff01b8ae986e6be12349.jpg
         */
        var id: String? = null
        var thumb: String? = null
        var filepath: String? = null

    }
}