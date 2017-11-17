package com.youyou.uucar.Utils.Network.listen;

import android.view.View;
import android.widget.Toast;

import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

/**
 * Created by taurusxi on 14-9-17.
 */
public abstract class OnClickNetworkListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        boolean isNetworkOk = Config.isNetworkConnected(Config.currentContext);

        if (isNetworkOk) {
            onNetworkClick(v);
        } else {
            Toast.makeText(Config.currentContext, SysConfig.NETWORK_FAIL, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void onNetworkClick(View v);
}
