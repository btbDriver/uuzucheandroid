package com.youyou.uucar.Utils.Network;

import android.app.Activity;

import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Support.Config;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by taurusxi on 14-8-6.
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final AtomicInteger seq = new AtomicInteger(100);

    public static final AtomicBoolean isShowDialog = new AtomicBoolean(false);

    public static void executeNetwork(NetworkTask networkTask, HttpResponse.NetWorkResponse baseResponse) {

        final Activity activity = Config.currentContext;
//        if (Config.isNetworkConnected(activity)){
        HttpNetwork httpNetwork = new VolleyNetworkHelper();
        switch (networkTask.getMethod()) {
            case NetworkTask.Method.POST:
                httpNetwork.doPost(seq.incrementAndGet(), networkTask, baseResponse);
                break;
            default:
                break;

        }
//        }else {
//            Config.showFiledToast(activity);
//        }


    }


    public static void cancleNetworkRequest(Object tag) {

        UUAppCar.getRequestQueue().cancelAll(tag);
    }

}
