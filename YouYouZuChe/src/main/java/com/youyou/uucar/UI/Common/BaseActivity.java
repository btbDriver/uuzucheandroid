package com.youyou.uucar.UI.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.head.HeaderCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.DB.Model.OpenCityModel;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.rent.OneToOneWaitActivity;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

/**
 * Created by taurusxi on 14-8-26.
 */
public abstract class BaseActivity extends FragmentActivity {

    public String tag = BaseActivity.this.getClass().getSimpleName();
    public Activity context;

    public UUAppCar getApp() {
        return (UUAppCar) getApplication();
    }


    public void ShowToast(String msg) {
//        Config.showToast(context, msg);
        if (msg != null && !msg.trim().equals("")) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void showProgress(boolean canCancled) {
        Config.showProgressDialog(context, canCancled, null);
    }

    public void showProgress(boolean canCancled, final Config.ProgressCancelListener listener) {
        Config.showProgressDialog(context, canCancled, listener);
    }

    public void showToast(String text) {
        if (text != null && !text.trim().equals("")) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(String msg, int time) {
        if (msg != null && !msg.trim().equals("")) {
            Toast.makeText(this, msg, time).show();
        }
    }

    public void showResponseCommonMsg(HeaderCommon.ResponseCommonMsg msg) {
        if (msg.hasShowType()) {
            if (msg.getShowType().equals(HeaderCommon.ResponseCommonMsgShowType.TOAST)) {

                showToast(msg.getMsg(), 0);
            } else if (msg.getShowType().equals(HeaderCommon.ResponseCommonMsgShowType.WINDOW)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(msg.getMsg());
                builder.setNegativeButton(msg.getButtonsList().get(0).getButtonText(), null);
                Dialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        } else {
            showToast(msg.getMsg(), 0);
        }
    }

    public void dismissProgress() {
        Config.dismissProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Config.currentContext = this;
        getName();
        if (!getName().equals("com.youyou.uucar.UI.Main.MainActivityTab"))//首页不用统计stat
        {
            MobclickAgent.onPageStart(getName());
        }
        MobclickAgent.onResume(this);
        TestinAgent.onResume(this);
//        if (!Config.isGuest(this))
//        {
        Config.getUserSecurity(this);
//        }
        if (Config.openCity == null || Config.openCity.size() == 0) {
            OpenCityModel.getInstance(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        TestinAgent.onStop(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!getName().equals("com.youyou.uucar.UI.Main.MainActivityTab"))//首页不用统计stat
        {
            MobclickAgent.onPageEnd(getName());
        }
        MobclickAgent.onPause(this);
    }

    Dialog dialog;

    protected void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        Config.setActivityState(this);
        ObserverManager.addObserver(getName(), new ObserverListener() {
                                        @Override
                                        public void observer(final String from, final Object obj) {
                                            runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run() {

                                                    if (from.equals("showDialog")) {
                                                        final OrderFormInterface26.NewOrderCreatedPush push = (OrderFormInterface26.NewOrderCreatedPush) obj;
                                                        UuCommon.TipsMsg msg = push.getMsg();
                                                        if (dialog != null) {
                                                            dialog.dismiss();
                                                            dialog = null;
                                                        }
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals("")) {
                                                            builder.setMessage(msg.getToastMsg());
                                                        }
                                                        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (Config.currentContext.getClass().getName().equals(OneToOneWaitActivity.class.getName()))//如果当前在一对一约车页面去行程页,如果不是,就不动
                                                                {
                                                                    Intent intent = new Intent(Config.currentContext, MainActivityTab.class);
                                                                    intent.putExtra("goto", MainActivityTab.GOTO_RENTER_STROKE);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                    Config.currentContext.startActivity(intent);
                                                                }
                                                            }
                                                        });
                                                        builder.setNeutralButton("查看并支付", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(Config.currentContext, RenterOrderInfoActivity.class);
                                                                intent.putExtra(SysConfig.R_SN, push.getOrderId());
                                                                Config.currentContext.startActivity(intent);
                                                            }
                                                        });
                                                        dialog = builder.create();
                                                        dialog.setCancelable(true);
                                                        dialog.setCanceledOnTouchOutside(false);
                                                        dialog.show();
                                                    }
                                                }
                                            });
                                        }
                }

        );
    }

    public String getName() {
        MLog.e("BaseActivity", "name = " + BaseActivity.this.getClass().getName());
        return BaseActivity.this.getClass().getName();
    }

    public void initNetOperation(Method method) {
        method.method();
    }

    public static interface Method {
        void method();
    }

}
