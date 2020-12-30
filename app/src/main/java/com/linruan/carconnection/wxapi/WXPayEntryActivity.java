package com.linruan.carconnection.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.linruan.carconnection.R;
import com.linruan.carconnection.entities.MessageEvent;
import com.linruan.carconnection.utils.PayUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI iwxapi;
    private View mIvBack;
    private ImageView mIvImage;
    private TextView mTvResult;
    private View mBtnBack;
    private View mVLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, PayUtil.APP_ID, true);
        iwxapi.handleIntent(getIntent(), this);
        setContentView(R.layout.activity_wxpayresult);
        mIvBack = findViewById(R.id.ivBack);
        mIvImage = findViewById(R.id.ivImage);
        mTvResult = findViewById(R.id.tvResult);
        mBtnBack = findViewById(R.id.btnBack);
        mVLayout = findViewById(R.id.vLayout);
        mIvBack.setOnClickListener(view -> finish());
        mBtnBack.setOnClickListener(view -> finish());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.d(new Gson().toJson(baseResp));
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            EventBus.getDefault()
//                    .post(new MessageEvent(MessageEvent.WXPAY_RESULT, baseResp.errCode + ""));
            if (baseResp.errCode == 0) {
                //成功
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.WXPAY_RESULT, baseResp.errCode + ""));
            } else if (baseResp.errCode == -2) {
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.WXPAY_RESULT, baseResp.errCode + ""));
                //用户取消
            } else {
                EventBus.getDefault()
                        .post(new MessageEvent(MessageEvent.WXPAY_RESULT, baseResp.errCode + "",baseResp.errStr));
//                Toast.makeText(this,"支付失败",Toast.LENGTH_SHORT).show();
                //错误
            }
        }
        finish();
    }
}
