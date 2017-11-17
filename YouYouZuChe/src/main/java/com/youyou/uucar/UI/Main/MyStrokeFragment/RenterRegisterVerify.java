package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.ab.http.AbHttpUtil;
//import com.ab.http.AbRequestParams;
//import com.youyou.uucar.Utils.Support.MyAbStringHttpResponseListener;

public class RenterRegisterVerify extends BaseActivity {
    public static final String TAG = RenterRegisterVerify.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "RenterRegisterVerify";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();
    public String tag = RenterRegisterVerify.class.getSimpleName();
    public Activity context;
    public ObserverListener listener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {

//


        }
    };

    UserModel model;
    UserService service;

    public UserModel getModel() {

        service = new UserService(context);
        List<UserModel> models = service.getModelList(UserModel.class);
        if (models.size() == 0) {
            model = new UserModel();
            model.phone = "";
            boolean flag = service.insModel(model);
            if (flag) {
                model = service.getModel(UserModel.class, new String[]{""});
            }
        } else {
            model = models.get(0);
        }
        return model;
    }

    private boolean saveModel() {
        boolean flag = service.modifyModel(model);
        return flag;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home || id == 0) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.contact_service)
    public void serviceClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("拨打客服电话");
        builder.setMessage(Config.kefuphone);
        builder.setNegativeButton("拨打", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputStr = Config.kefuphone;
                if (inputStr.trim().length() != 0) {
                    MobclickAgent.onEvent(context, "ContactService");
                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                    startActivity(phoneIntent);
                }
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        builder.create().show();
    }

    @OnClick(R.id.guest)
    public void guestClick() {
        Intent intent = new Intent(context, MainActivityTab.class);
        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        setContentView(R.layout.activity_renter_register_verify);
        ButterKnife.inject(this);
        findViewById(R.id.step_1).setSelected(true);
        findViewById(R.id.step_1_line).setSelected(true);
        findViewById(R.id.step_2).setSelected(true);
        findViewById(R.id.step_1_text).setSelected(true);
        findViewById(R.id.step_2_text).setSelected(true);
        findViewById(R.id.step_2_line).setSelected(true);
        findViewById(R.id.step_3).setSelected(true);
        findViewById(R.id.step_3).setEnabled(false);
        ObserverManager.addObserver("renter_register_verify", listener);
    }


}
