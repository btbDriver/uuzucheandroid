package com.youyou.uucar.UI.Main.my.money;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyCard extends BaseActivity {
    public Activity context;
    public String tag = "MyCard";
    public String card_id;

    private void deleteBank() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("解除银行卡");
        final EditText edit = new EditText(context);
        edit.setHint("请输入提现密码 ");
        builder.setView(edit);
        builder.setNegativeButton("解除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Config.showProgressDialog(context, false, null);
                UserInterface.DelBankCard.Request.Builder request = UserInterface.DelBankCard.Request.newBuilder();
                request.setCardId(Integer.valueOf(card_id));
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.DelBankCard_VALUE);
                networkTask.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                    @Override
                    public void onSuccessResponse(UUResponseData uuResponseData) {
                        Config.showToast(context, uuResponseData.getToastMsg());
                        if (uuResponseData.getRet() == 0) {
                            try {
                                UserInterface.DelBankCard.Response response = UserInterface.DelBankCard.Response.parseFrom(uuResponseData.getBusiData());
                                if (response.getRet() == 0) {
                                    finish();
                                } else {
                                    showToast("解除银行卡失败");
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError errorResponse) {
                        Config.showFiledToast(context);
                    }

                    @Override
                    public void networkFinish() {
                        Config.dismissProgress();
                    }
                });
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();

    }

    TextView card_num;
    TextView bank, time;
    ArrayList<AddCard.Bank> data = new ArrayList<AddCard.Bank>();

    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        Config.setActivityState(this);
        setContentView(R.layout.mycard);
        card_num = (TextView) findViewById(R.id.card_num);
        bank = (TextView) findViewById(R.id.card_bank);
        time = (TextView) findViewById(R.id.time);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_desc, menu);
        MenuItem item = menu.findItem(R.id.action_save);
        item.setTitle("解除绑定");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_save) {

            deleteBank();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getInfo();
    }

    public void getInfo() {
        Config.showProgressDialog(context, false, null);
        UserInterface.MyBankCards.Request.Builder request = UserInterface.MyBankCards.Request.newBuilder();
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.MyBankCards_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData uuResponseData) {
                Config.showToast(context, uuResponseData.getToastMsg());
                if (uuResponseData.getRet() == 0) {
                    try {
                        UserInterface.MyBankCards.Response response = UserInterface.MyBankCards.Response.parseFrom(uuResponseData.getBusiData());
                        if (response.getRet() == 0) {
                            card_num.setText(response.getCardNum());
                            card_id = response.getCardId() + "";
                            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                            time.setText(formatter2.format(new Date(response.getAddTime() * 1000L)));
                            bank.setText(response.getBankName());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
            }
        });
    }
}
