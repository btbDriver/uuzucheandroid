package com.youyou.uucar.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taurusxi on 14-8-7.
 */
public class UUReceiver extends BroadcastReceiver {


    private static List<NetworkHandler> networkChanges = new ArrayList<NetworkHandler>();

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (networkChanges.size() > 0)// 通知接口完成加载
            {
                for (NetworkHandler handler : networkChanges) {
                    handler.onNetworkChange();
                }
            }
        }
    }


    public static interface NetworkHandler {

        void onNetworkChange();

    }
}
