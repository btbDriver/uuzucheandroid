package com.youyou.uucar.UI.Main.my;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class About extends BaseActivity {

    @InjectView(R.id.icon)
    ImageView mIcon;
    @InjectView(R.id.name)
    TextView mName;
    @InjectView(R.id.phone_icon)
    ImageView mPhoneIcon;
    @InjectView(R.id.phone_text)
    TextView mPhoneText;
    @InjectView(R.id.phone)
    RelativeLayout mPhone;

    private long firstTime = 0;
    private Timer delayTimer;
    private Handler handler;
    private TimerTask task;
    private long interval = 500;
    private int count = 0;
    Toast toast;

    @OnClick(R.id.phone)
    public void phoneClick() {
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
//
//    // 延迟时间是连击的时间间隔有效范围
//    private void delay() {
//        if (task != null)
//            task.cancel();
//
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                // message.what = 0;
//                handler.sendMessage(message);
//            }
//        };
//        delayTimer = new Timer();
//        delayTimer.schedule(task, interval);
//    }

    @InjectView(R.id.get_version)
    RelativeLayout mGetVersion;
    @InjectView(R.id.line)
    View mLine;
    @InjectView(R.id.rule)
    RelativeLayout mRule;

    //强制更新dialog
    public AlertDialog forceUpdateDialog;
    //更新更新内容
    public TextView umeng_update_content;
    //立即更新按钮
    public Button umeng_update_id_ok;

    @OnClick(R.id.get_version)
    public void getVersionClick() {
//        UpdateConfig.setDebug(false);
//        UmengUpdateAgent.setDefault();
//        UmengUpdateAgent.setUpdateAutoPopup(true);
//        UmengUpdateAgent.update(getApplicationContext());
//        UmengUpdateAgent.setUpdateOnlyWifi(false);


        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    //判断是否有新版本需要更新
                    case UpdateStatus.Yes: // has update
                        try {
                            //在线读取更新参数
                            String value = MobclickAgent.getConfigParams(About.this, "FORCE_UPDATE_MIXVERSION");
                            if (value != null && !value.trim().equals("")) {
                                int versionCode = Config.changeVersionNameToCode(value);
                                if (versionCode != 0) {
                                    String localVersionName = getVersionName();
                                    int localVersionCode = Config.changeVersionNameToCode(localVersionName);
                                    //判断当前版本号于友盟中的最低版本号，若当前版本号小于最低版本号，则强制更新，否则非强制更新
                                    if (localVersionCode <= versionCode) {
                                        UmengUpdateAgent.setUpdateAutoPopup(false);

                                        LayoutInflater inflater = LayoutInflater.from(About.this);
                                        View view = inflater.inflate(R.layout.umeng_update_dialog_force, null);
                                        umeng_update_content = (TextView) view.findViewById(R.id.umeng_update_content);
                                        umeng_update_id_ok = (Button) view.findViewById(R.id.umeng_update_id_ok);

                                        final UpdateResponse updateInfos = updateInfo;
                                        umeng_update_id_ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                UmengUpdateAgent.startDownload(About.this, updateInfos);
                                            }
                                        });

                                        StringBuffer content = new StringBuffer("最新版本：" + updateInfo.version + "\n\n");
                                        content.append("更新内容\n" + updateInfo.updateLog + "\n");
                                        umeng_update_content.setText(content.toString());
                                        forceUpdateDialog = new AlertDialog.Builder(About.this).create();
                                        forceUpdateDialog.setCancelable(false);
                                        forceUpdateDialog.setView(view);
                                        forceUpdateDialog.show();
                                    } else {
                                        UmengUpdateAgent.setUpdateAutoPopup(true);
                                        UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                                    }
                                } else {
                                    UmengUpdateAgent.setUpdateAutoPopup(true);
                                    UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                                }
                            } else {
                                UmengUpdateAgent.setUpdateAutoPopup(true);
                                UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case UpdateStatus.No: // has no update
                        showToast("您当前使用的友友租车已是最新版本");
                        break;
                }
            }
        });
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.forceUpdate(context);
    }

    @OnClick(R.id.rule)
    public void ruleClick() {
        startActivity(new Intent(context, RuleSelect.class));
    }

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        try {
            mName.setText("友友租车" + getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPhoneText.setText(Html.fromHtml("<u>4000-772-110</u>"));

        toast = new Toast(context);

        //读取在线参数
        MobclickAgent.updateOnlineConfig(this);

        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SysConfig.DEVELOP_MODE) {


                    long secondTime = System.currentTimeMillis();
                    // 判断每次点击的事件间隔是否符合连击的有效范围
                    // 不符合时，有可能是连击的开始，否则就仅仅是单击

                    if (secondTime - firstTime <= interval) {
                        ++count;
                        toast.cancel();
                        toast.makeText(context, "还差" + (1 - count) + "下切换环境.快快快,时间不够了", Toast.LENGTH_SHORT).show();
                    } else {
                        count = 1;
                    }
                    // 延迟，用于判断用户的点击操作是否结束
//                    delay();
                    firstTime = secondTime;
                    if (count == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setMessage("我们不用叫爸爸就能切换");
                        builder.setNeutralButton("为您提供另一种选择:学狗叫", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final SharedPreferences networkInfo = context.getSharedPreferences("network", 0);
                                if (NetworkTask.BASEURL.equals("115.28.82.160")) {
                                    NetworkTask.BASEURL = "42.96.249.15";          //测试环境

                                    networkInfo.edit().putBoolean("check", true).apply();
                                } else {
                                    NetworkTask.BASEURL = "115.28.82.160";//正式环境
                                    networkInfo.edit().putBoolean("check", false).apply();
                                }
                                if (NetworkTask.BASEURL.equals("42.96.249.15")) {
//                                    Toast.makeText(context, "正式环境：" + NetworkTask.BASEURL, Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                    builder2.setMessage("你现在是正式环境");
                                    builder2.setNegativeButton("汪汪汪", null);
                                    builder2.create().show();
                                } else if (NetworkTask.BASEURL.equals("115.28.82.160")) {
//                                    Toast.makeText(context, "测试环境：" + NetworkTask.BASEURL, Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                    builder2.setMessage("你现在是测试环境");
                                    builder2.setNegativeButton("汪汪汪", null);
                                    builder2.create().show();
                                }
                            }
                        });
                        builder.setNegativeButton("任性的要叫爸爸", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final SharedPreferences networkInfo = context.getSharedPreferences("network", 0);
                                if (NetworkTask.BASEURL.equals("115.28.82.160")) {
                                    NetworkTask.BASEURL = "42.96.249.15";          //测试环境

                                    networkInfo.edit().putBoolean("check", true).apply();
                                } else {
                                    NetworkTask.BASEURL = "115.28.82.160";//正式环境
                                    networkInfo.edit().putBoolean("check", false).apply();
                                }
                                if (NetworkTask.BASEURL.equals("42.96.249.15")) {
//                                    Toast.makeText(context, "正式环境：" + NetworkTask.BASEURL, Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                    builder2.setMessage("你现在是正式环境");
                                    builder2.setNegativeButton("谢谢爸爸", null);
                                    builder2.create().show();
                                } else if (NetworkTask.BASEURL.equals("115.28.82.160")) {
//                                    Toast.makeText(context, "测试环境：" + NetworkTask.BASEURL, Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                    builder2.setMessage("你现在是测试环境");
                                    builder2.setNegativeButton("谢谢爸爸", null);
                                    builder2.create().show();
                                }
                            }
                        });
                        dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }

                }
            }
        });
    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }
}
