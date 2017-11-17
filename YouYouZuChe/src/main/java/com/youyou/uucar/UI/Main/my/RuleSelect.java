package com.youyou.uucar.UI.Main.my;

import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.R;
import com.youyou.uucar.Utils.Support.Config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class RuleSelect extends Activity {
    Activity context;
    public String tag = "RuleSelect";
    RelativeLayout rule1, rule2, rule3, rule4;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.rule_select);
        rule1 = (RelativeLayout) findViewById(R.id.rule1);
        rule2 = (RelativeLayout) findViewById(R.id.rule2);
        rule3 = (RelativeLayout) findViewById(R.id.rule3);
        rule4 = (RelativeLayout) findViewById(R.id.rule4);
        rule1.setOnClickListener(ruleClick);
        rule2.setOnClickListener(ruleClick);
        rule3.setOnClickListener(ruleClick);
        rule4.setOnClickListener(ruleClick);
    }

    public OnClickListener ruleClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.rule1:
                    MobclickAgent.onEvent(context, "CheckRule");
                    intent.putExtra("url", ServerMutualConfig.ruleurl);
                    intent.setClass(context, URLWebView.class);
                    startActivity(intent);
                    break;
                case R.id.rule2:
                    MobclickAgent.onEvent(context, "CheckOwnerService");
                    intent.putExtra("url", ServerMutualConfig.owner_protocol);
                    intent.setClass(context, URLWebView.class);
                    startActivity(intent);
                    break;
                case R.id.rule3:
                    MobclickAgent.onEvent(context, "CheckService");
                    intent.putExtra("url", ServerMutualConfig.reg);
                    intent.setClass(context, URLWebView.class);
                    startActivity(intent);
                    break;
                case R.id.rule4:
                    MobclickAgent.onEvent(context, "CheckService");
                    intent.putExtra("url", ServerMutualConfig.baoxian);
                    intent.setClass(context, URLWebView.class);
                    startActivity(intent);
                    break;
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }
}
