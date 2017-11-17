package com.youyou.uucar.UI.Welcome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.timessquare.CalendarPickerView;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.login.LoginInterface;
import com.youyou.uucar.DB.Model.OpenCityModel;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StartActivity extends Activity {
    public String tag = "StartActivity";
    Activity context;
    LinearLayout view;
    public final static int START = 1;
    public final static int MAIN_TAB = 2;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestinAgent.init(this, "ed500b09032a0cd63473293e925e689e");
        MobclickAgent.openActivityDurationTrack(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.start);
        OpenCityModel.getInstance(this);
        UUAppCar.getInstance().addActivity(this);
//        getActionBar().hide();
        context = this;
        Config.currentContext = this;
        File foder = new File(Config.HeadFile);
        File foder2 = new File(Config.ImageFile);
        File foder3 = new File(Config.HeadFile);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        if (!foder2.exists()) {
            foder2.mkdirs();
        }
        if (!foder3.exists()) {
            foder3.mkdirs();
        }
        ImageView img = (ImageView) findViewById(R.id.img);
        if (getChannel().equals("baidu")) {
            img.setImageResource(R.drawable.logo_baidu);
        } else if (getChannel().equals("m91")) {
            img.setImageResource(R.drawable.logo_m91);
        } else if (getChannel().equals("hiapk")) {
            img.setImageResource(R.drawable.logo_hiapk);
        } else if (getChannel().equals("m360")) {
            img.setImageResource(R.drawable.logo_m360);
        } else if (getChannel().equals("wandoujia")) {
            img.setImageResource(R.drawable.wandoujia);
        } else if (getChannel().equals("huawei")) {
            img.setImageResource(R.drawable.huawei);
        }
        MobclickAgent.updateOnlineConfig(context);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START:
                        Intent intent = new Intent(context, GuestStart.class);
                        intent.putExtra("goto", getIntent().getStringExtra("goto"));
                        startActivity(intent);
                        finish();
                        break;
                    case MAIN_TAB:
                        Intent intent1 = new Intent(context, MainActivityTab.class);
                        intent1.putExtra("goto", getIntent().getStringExtra("goto"));
                        if (getIntent().hasExtra("goto") && getIntent().getStringExtra("goto") != null && getIntent().getStringExtra("goto").equals(MainActivityTab.GOTO_OPERATE_POP))//如果是活动弹窗
                        {
                            intent1.putExtra("canClose", getIntent().getBooleanExtra("canClose", false));
                            intent1.putExtra("wording", getIntent().getStringExtra("wording"));
                            intent1.putExtra("actionUrl", getIntent().getStringExtra("actionUrl"));
                            intent1.putExtra("imgUrl", getIntent().getStringExtra("imgUrl"));
                        }
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent1);
                        break;

                }
            }
        };

        Config.getUserSecurity(this);
        ((UUAppCar) getApplication()).startLongConn();
        getCity();
    }


    UserModel model;
    UserService service;

    /**
     * activity MetaData读取
     */
    private String getChannel() {
        ApplicationInfo appInfo;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

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

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void onStop() {
        super.onStop();
        TestinAgent.onStop(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        TestinAgent.onResume(this);
    }

    public void anonymousLoginSSL() {

        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AnonymousLoginSSL_VALUE);
        networkTask.setTag("updateTicket");
        LoginInterface.AnonymousLoginSSL.Request.Builder request = LoginInterface.AnonymousLoginSSL.Request.newBuilder();
        networkTask.setBusiData(request.build().toByteArray());

        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    int startTime = (int) (System.currentTimeMillis() / 1000);
                    try {
                        LoginInterface.AnonymousLoginSSL.Response response = LoginInterface.AnonymousLoginSSL.Response.parseFrom(responseData.getBusiData());
                        MLog.e(tag, "ret = " + response.getRet());
                        Config.updateUserSecurity(Config.currentContext, response.getUserSecurityTicket(), response.getUserId(), startTime);
                        UserSecurityConfig.isAnonymousLogin = true;

                        SharedPreferences sp = context.getSharedPreferences("guide", Context.MODE_PRIVATE);
                        String pkName = getPackageName();
                        try {
                            if (!sp.getString("version", "0").equals(getPackageManager().getPackageInfo(pkName, 0).versionName)) {
                                mHandler.sendEmptyMessageDelayed(START, 2000);
                            } else {
                                mHandler.sendEmptyMessageDelayed(MAIN_TAB, 2000);
                            }
                        } catch (Exception ee) {

                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {

                MLog.e("UserSecurityConfig", "anonymousLoginSSL error = " + errorResponse);
            }

            @Override
            public void networkFinish() {
                UserSecurityConfig.isAnonymousLogin = false;
            }
        });


    }

    public void getCity() {
        if (!Config.isNetworkConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(SysConfig.NETWORK_FAIL);
            builder.setNegativeButton("重试", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getCity();
                }
            });
            builder.create().show();
        } else {
            if (Config.isGuest(context)) {
                anonymousLoginSSL();
            } else {
                SharedPreferences sp = context.getSharedPreferences("guide", Context.MODE_PRIVATE);
                String pkName = getPackageName();
                try {
                    if (!sp.getString("version", "0").equals(getPackageManager().getPackageInfo(pkName, 0).versionName)) {
                        mHandler.sendEmptyMessageDelayed(START, 2000);
                    } else {
                        mHandler.sendEmptyMessageDelayed(MAIN_TAB, 2000);
                    }
                } catch (Exception ee) {

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, resultCode + " umpResultMessage:" + data.getStringExtra("umpResultMessage") + "\n umpResultCode:" + data.getStringExtra("umpResultCode") + "\n orderId:" + data.getStringExtra("orderId"), Toast.LENGTH_LONG).show();
        MLog.e(tag, "onacti  = " + resultCode + " umpResultMessage:" + data.getStringExtra("umpResultMessage") + "\n umpResultCode:" + data.getStringExtra("umpResultCode") + "\n orderId:" + data.getStringExtra("orderId"));
    }


}
