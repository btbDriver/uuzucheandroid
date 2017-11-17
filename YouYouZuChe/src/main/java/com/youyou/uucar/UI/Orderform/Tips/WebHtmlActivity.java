package com.youyou.uucar.UI.Orderform.Tips;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;

/**
 * Created by taurusxi on 14-9-13.
 */
public class WebHtmlActivity extends BaseActivity {
    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        Config.setActivityState(this);
        WebView view = new WebView(this);
        view.loadUrl(getIntent().getStringExtra("url"));
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MLog.e("webview ", "longclick");
                return true;
            }
        });
        setTitle("押金费用规则");
        setContentView(view);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
    }
}
