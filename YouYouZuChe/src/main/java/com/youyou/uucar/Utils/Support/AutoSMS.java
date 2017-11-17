package com.youyou.uucar.Utils.Support;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.youyou.uucar.Utils.observer.ObserverManager;

public class AutoSMS extends BroadcastReceiver {
    private String TAG = "smsreceiveandmask";

    @Override
    public void onReceive(Context context, Intent intent) {
        MLog.e(TAG, "<><><>onReceive start");
        // 第一步、获取短信的内容和发件人
        StringBuilder body = new StringBuilder();// 短信内容
        StringBuilder number = new StringBuilder();// 短信发件人
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] _pdus = (Object[]) bundle.get("pdus");
            //TODO 修正 SmsMessage[] message = new SmsMessage[_pdus.length] 报空指针
            if (_pdus==null){
                return;
            }
            SmsMessage[] message = new SmsMessage[_pdus.length];
            for (int i = 0; i < _pdus.length; i++) {
                message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
            }
            for (SmsMessage currentMessage : message) {
                body.append(currentMessage.getDisplayMessageBody());
                number.append(currentMessage.getDisplayOriginatingAddress());
            }
            String smsBody = body.toString();
            String smsNumber = number.toString();
            if (smsNumber.contains("+86")) {
                smsNumber = smsNumber.substring(3);
            }
            // 第二步:确认该短信内容是否满足过滤条件
            // boolean flags_filter = false;
            // if(smsNumber.equals("10086"))
            // {// 屏蔽10086发来的短信
            // flags_filter = true;
            // Log.v(TAG,"sms_number.equals(10086)");
            // }
            // // 第三步:取消
            // if(flags_filter)
            // {
            // this.abortBroadcast();
            // }

            if (smsBody.indexOf("友友租车") != -1) {
                ObserverManager.getObserver("Login").observer("AutoSMS", smsBody);
                //                Toast.makeText(context,"收到包含微信的短信: " + smsBody ,Toast.LENGTH_SHORT).show();
            }
        }
        Log.v(TAG, ">>>>>>>onReceive end");
    }
}