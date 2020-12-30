package com.linruan.carconnection.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.linruan.carconnection.entities.MessageEvent;
import com.linruan.carconnection.entities.net.GetWXToken;
import com.linruan.carconnection.entities.net.GetWxUserInfo;
import com.linruan.carconnection.net.JsonCallback;
import com.linruan.carconnection.utils.PayUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import cn.jiguang.share.wechat.WeChatHandleActivity;

/**
 * author：shichengxiang on 2020/6/13 15:03
 * email：1328911009@qq.com
 */
public class WXEntryActivity extends WeChatHandleActivity implements IWXAPIEventHandler {
    private IWXAPI iwxapi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, PayUtil.APP_ID, true);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("===", new Gson().toJson(baseReq));
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.i("===", new Gson().toJson(baseResp));
        if(baseResp.getType()==1){
            //登录
//            switch (baseResp.errCode) {
//                case BaseResp.ErrCode.ERR_OK: {
//                    String code = ((SendAuth.Resp) baseResp).code;
//                    //获取accesstoken
//                    getAccessToken(code);
//                }
//                case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                case BaseResp.ErrCode.ERR_USER_CANCEL: {
//                    break;
//                }
//            }
            //授权
        }else {
            //分享 ==2
//            switch (baseResp.errCode) {
//                case BaseResp.ErrCode.ERR_OK: {
//                }
//                case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                case BaseResp.ErrCode.ERR_USER_CANCEL: {
//                    break;
//                }
//            }
        }
        finish();
    }

    private void getAccessToken(String code) {
//        LoadingDialog.INSTANCE.loading(this);
        OkGo.<GetWXToken>get(String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                PayUtil.APP_ID,
                PayUtil.APP_SECRET,
                code))
                .execute(new JsonCallback<GetWXToken>() {
                    @Override
                    public void onSuccess(@org.jetbrains.annotations.Nullable Response<GetWXToken> response) {
                            getUserInfo(response.body().getAccess_token(), response.body().getOpenid());
                    }
                });
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(String accessToken, String openId) {
        OkGo.<GetWxUserInfo>get(String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&connect_redirect=1", accessToken, openId))
                .execute(new JsonCallback<GetWxUserInfo>() {
                    @Override
                    public void onSuccess(@org.jetbrains.annotations.Nullable Response<GetWxUserInfo> response) {
                        String nick = response.body().getNickname();
                        String headImgUrl = response.body().getHeadimgurl();
                        EventBus.getDefault()
                                .post(new MessageEvent(MessageEvent.WEIXIN_LOGIN, nick, headImgUrl,openId));
                    }
                });
    }
}
