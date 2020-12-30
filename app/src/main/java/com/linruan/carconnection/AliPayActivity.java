package com.linruan.carconnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.linruan.carconnection.entities.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AliPayActivity extends AppCompatActivity {
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    LogUtils.e(new Gson().toJson(msg.obj));
                    PayResult result = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = result.result;
                    String resultStatus = result.resultStatus;
                    EventBus.getDefault()
                            .post(new MessageEvent(MessageEvent.ALIPAY_RESULT, resultStatus, resultInfo));
                    break;
                case SDK_AUTH_FLAG:
                    break;
                default:
                    break;
            }
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String orderInfo = getIntent().getStringExtra("orderInfo");
        pay(orderInfo);
    }

    private void pay(String orderInfo) {
        final Runnable runnable = () -> {
            PayTask task = new PayTask(AliPayActivity.this);
            Map<String, String> result = task.payV2(orderInfo, true);
            LogUtils.e(result.toString());
            Message msg = mHandler.obtainMessage();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };
        new Thread(runnable).start();
    }
//    @Override
//    public int getLayout() {
//        return 0;
//    }
//
//    @Override
//    public void initView() {
//
//    }

    /**
     * 支付宝支付
     */
    public static void startPay(Activity activity, String orderInfo) {
        Intent intent = new Intent(activity, AliPayActivity.class);
        intent.putExtra("orderInfo", orderInfo);
        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }


    public class PayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public PayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }

            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }
}
