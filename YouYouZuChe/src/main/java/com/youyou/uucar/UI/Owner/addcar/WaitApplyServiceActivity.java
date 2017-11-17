package com.youyou.uucar.UI.Owner.addcar;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class WaitApplyServiceActivity extends Activity {
    public String tag = WaitApplyServiceActivity.class.getSimpleName();
    public Activity context;

    @OnClick(R.id.guest)
    public void guestClick() {
        //跳转到找车页面
        Intent intent = new Intent(context, MainActivityTab.class);
        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        WaitApplyServiceActivity.this.finish();
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        Config.setActivityState(this);
        setContentView(R.layout.wait_apply_service);
        ButterKnife.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            //Intent 跳转到 车辆管理界面
            Intent intent = new Intent(context, MainActivityTab.class);
            intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
            WaitApplyServiceActivity.this.finish();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //跳转到找车页面
        Intent intent = new Intent(context, MainActivityTab.class);
        intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
        WaitApplyServiceActivity.this.finish();

    }
}
