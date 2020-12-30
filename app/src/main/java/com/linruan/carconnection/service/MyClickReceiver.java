package com.linruan.carconnection.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.linruan.carconnection.Constant;
import com.linruan.carconnection.UserManager;
import com.linruan.carconnection.entities.PushMessage;
import com.linruan.carconnection.ui.MainActivity;
import com.linruan.carconnection.ui.master.MasterRepairDetailActivity;
import com.linruan.carconnection.ui.mine.MessageActivity;
import com.linruan.carconnection.ui.mine.RePairCarOrderActivity;
import com.linruan.carconnection.ui.mine.RepairCarOrderDetailActivity;
import com.linruan.carconnection.ui.store.GoodsOrderDetailActivity;
import com.linruan.carconnection.ui.store.RefundedDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jiguang.share.android.utils.Logger;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyClickReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG";
    public static final String ACTION = "com.linruan.clicknotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PushMessage msg = (PushMessage) intent.getSerializableExtra("data");
            if(msg==null){
            	return;
			}
            LogUtils.i(new Gson().toJson(msg));
            String orderId=msg.getId();
            if (msg.getType() == Constant.INSTANCE.getNOTIFICATION_TYPE_BATTLED()) {
            	//新订单 或者已经被接单
				if(UserManager.INSTANCE.getUser()!=null && UserManager.INSTANCE.getUser().isMaster()){
					Intent i = new Intent(context, MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}else {
					Intent i = new Intent(context, RepairCarOrderDetailActivity.class);
					i.putExtra(RepairCarOrderDetailActivity.REPAIRID_KEY,orderId);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
            }else if (msg.getType() == Constant.INSTANCE.getNOTIFICATION_TYPE_REPAIRSUCCESS() || msg.getType() == Constant.INSTANCE.getNOTIFICATION_TYPE_REPAIRFAIL()) {
//			    //维修成功或失败 默认开启首页不管了
				Intent i = new Intent(context, MasterRepairDetailActivity.class);
				i.putExtra(RepairCarOrderDetailActivity.REPAIRID_KEY,orderId);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
            }else if(msg.getType()==Constant.INSTANCE.getNOTIFICATION_TYPE_SENDEDGOODS()){
			    //已发货通知 调整订单详情
				Intent i = new Intent(context, GoodsOrderDetailActivity.class);
				i.putExtra(GoodsOrderDetailActivity.ORDERIDKEY,orderId);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
            }else if(msg.getType()==Constant.INSTANCE.getNOTIFICATION_TYPE_RETURNGOODS()){
//			    //退货通知
				Intent i = new Intent(context, RefundedDetailActivity.class);
				i.putExtra(RefundedDetailActivity.ORDERID_KEY,orderId);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
            }else if(msg.getType()==Constant.INSTANCE.getNOTIFICATION_TYPE_TALKMESSAGE()){
			    //贴吧通知
				Intent i = new Intent(context, MessageActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
            }else if(msg.getType()==Constant.INSTANCE.getNOTIFICATION_TYPE_MASTERISRECEIVED()){
//			    //师傅到达通知
				Intent i = new Intent(context, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
            }else if(msg.getType()==Constant.INSTANCE.getNOTIFICATION_TYPE_PUSHSERVICE()){
//			    //后台推送
				Intent i = new Intent(context, MessageActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
            }else if(msg.getType()==Constant.INSTANCE.getNOTIFICATION_TYPE_REPAIRORDER_WAITCONFIRM() || msg.getType()==Constant.INSTANCE.getNOTIFICATON_TYPE_CANCELORDER()){
				//等待用户维修确认通知 、取消订单
				Intent confirmIntent=new Intent(context, RepairCarOrderDetailActivity.class);
				confirmIntent.putExtra(RepairCarOrderDetailActivity.REPAIRID_KEY,orderId);
				confirmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(confirmIntent);
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isDestory) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivit);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//		}
    }
}
