package com.youyou.uucar.UI.Main.my.money;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;

public class MoneyManager extends BaseActivity {
    public String tag = "MoneyManager";
    public String card_id = "";
    Activity context;
    public View.OnClickListener getMoneyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (Float.parseFloat(yue.getText().toString().trim()) < 0) {
//                Toast.makeText(context, "您的余额不足1元,无法提现", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (card_num.getText().toString().indexOf("绑定") != -1) {
                Toast.makeText(context, "请先添加银行卡", Toast.LENGTH_SHORT).show();
                return;
            }
            if (money < 50) {
                showToast("账户余额满50才可以提现");
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = context.getLayoutInflater().inflate(R.layout.getmoney, null);
            final EditText price = (EditText) view.findViewById(R.id.price);
            price.setSingleLine(true);
            price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().contains(".")) {
                        if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                            s = s.toString().subSequence(0,
                                    s.toString().indexOf(".") + 3);
                            price.setText(s);
                            price.setSelection(s.length());
                        }
                    }
                    if (s.toString().trim().substring(0).equals(".")) {
                        s = "0" + s;
                        price.setText(s);
                        price.setSelection(2);
                    }

                    if (s.toString().startsWith("0")
                            && s.toString().trim().length() > 1) {
                        if (!s.toString().substring(1, 2).equals(".")) {
                            price.setText(s.subSequence(0, 1));
                            price.setSelection(1);
                            return;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            final EditText password = (EditText) view.findViewById(R.id.password);
            password.setSingleLine(true);
            builder.setView(view);
            builder.setNegativeButton("提现", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (price.getText().toString().trim().length() == 0) {
                        dialog.dismiss();
                        showToast("请输入提现金额");
                        return;
                    }

                    if (password.getText().toString().trim().length() == 0) {
                        dialog.dismiss();
                        showToast("请输入提现密码");
                        return;
                    }
                    if (!Config.getUser(context).carStatus.equals(User.CAR_STATUS_OWNER) && Float.parseFloat(price.getText().toString().trim()) < 50) {
                        Toast.makeText(context, "提现金额最少50元", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if (Float.parseFloat(price.getText().toString().trim()) > Float.parseFloat(yue.getText().toString().trim())) {
//                        Toast.makeText(context, "提现金额不能大于余额", Toast.LENGTH_LONG).show();
//                        return;
//                    }
                    Config.showProgressDialog(context, false, null);
                    UserInterface.UserApplyCash.Request.Builder request = UserInterface.UserApplyCash.Request.newBuilder();
                    request.setAmount(Float.parseFloat(price.getText().toString().trim()));
                    request.setAccountPwd(password.getText().toString().trim());
                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UserApplyCash_VALUE);
                    networkTask.setBusiData(request.build().toByteArray());
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                        @Override
                        public void onSuccessResponse(UUResponseData uuResponseData) {
                            Config.showToast(context, uuResponseData.getToastMsg());
                            if (uuResponseData.getRet() == 0) {
                                try {
                                    showToast(uuResponseData.getResponseCommonMsg().getMsg());
                                    UserInterface.UserApplyCash.Response response = UserInterface.UserApplyCash.Response.parseFrom(uuResponseData.getBusiData());
                                    if (response.getRet() == 0) {
                                        getInfo();
//                                        showToast("提现成功,请等待客服处理");
                                    }
//                                    else if (response.getRet() == -1) {
//                                        showToast("提现失败，请重试");
//                                    } else if (response.getRet() == -2) {
//                                        showToast("请输入正确的提现密码");
//                                    }
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                    Config.showFiledToast(context);
                                }
                            } else {
                                Config.showFiledToast(context);
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
    };
    public View.OnClickListener addCardClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (card_num.getText().toString().indexOf("绑定") != -1) {
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
                                    int banksCount = response.getBanksCount();
                                    if (banksCount > 0) {
                                        Config.bankList = response.getBanksList();
                                    }
                                    Intent intent = new Intent(context, AddCard.class);
                                    startActivity(intent);
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
            } else {
                startActivity(new Intent(context, MyCard.class));
            }
        }
    };
    public View.OnClickListener layoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.jiaoyi:
                    intent.setClass(context, MoneyDetail.class);
                    intent.putExtra("mode", "jiaoyi");
                    startActivity(intent);
                    break;
                // case R.id.yushou:
                // intent.setClass(context,MoneyDetail.class);
                // intent.putExtra("mode","yushou");
                // startActivity(intent);
                // break;
            }
        }
    };
    LinearLayout jiaoyi/* , yushou */;
    TextView top_money;
    //    TextView yue, card_num;
    TextView card_num;
    TextView mFreezTv, mWithdrawalTv;
    LinearLayout bank_card;
    TextView getmoney;
    ImageView freezImage, withdrawalImage;

//    RelativeLayout invite;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        setContentView(R.layout.money_manager_activity);
//        invite = (RelativeLayout) findViewById(R.id.invite);
        // invite.setOnClickListener(inviteClick);
//        invite.setVisibility(View.GONE);
        bank_card = (LinearLayout) findViewById(R.id.bank_card);
        bank_card.setOnClickListener(addCardClick);
        top_money = (TextView) findViewById(R.id.top_money);
        mFreezTv = (TextView) findViewById(R.id.freez_money_tv);
        mWithdrawalTv = (TextView) findViewById(R.id.withdrawal_money_tv);
//        yue = (TextView) findViewById(R.id.yue);
        card_num = (TextView) findViewById(R.id.card_num);
        card_num.setOnClickListener(addCardClick);
        jiaoyi = (LinearLayout) findViewById(R.id.jiaoyi);
        // yushou = (RelativeLayout)findViewById(R.id.yushou);
        jiaoyi.setOnClickListener(layoutClick);
        getmoney = (TextView) findViewById(R.id.getmoney);
        freezImage = (ImageView) findViewById(R.id.freez_icon);
        withdrawalImage = (ImageView) findViewById(R.id.withdrawal_icon);

        getmoney.setOnClickListener(getMoneyClick);

        freezImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MoneyManager.this, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.withdraw_deposit);
                intent.putExtra("title", "提现规则");
                MoneyManager.this.startActivity(intent);

            }
        });
        withdrawalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MoneyManager.this, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.frosting);
                intent.putExtra("title", "冻结规则");
                MoneyManager.this.startActivity(intent);

            }
        });


        // yushou.setOnClickListener(layoutClick);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        getInfo();
    }

    float money = 0;

    public void getInfo() {
        Config.showProgressDialog(context, false, null);

        UserInterface.MyAccountBalanceV2.Request.Builder request = UserInterface.MyAccountBalanceV2.Request.newBuilder();
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.MyAccountBalanceV2_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData uuResponseData) {
                Config.showToast(context, uuResponseData.getToastMsg());
                if (uuResponseData.getRet() == 0) {
                    try {
                        UserInterface.MyAccountBalanceV2.Response response = UserInterface.MyAccountBalanceV2.Response.parseFrom(uuResponseData.getBusiData());
                        if (response.getRet() == 0) {
                            if (response.hasAvailableBalance()) {
                                top_money.setText(Config.fmtMicrometer(String.format("%.2f", response.getAvailableBalance())));
                                money = response.getAvailableBalance();
                            }

                            if (response.hasFrozenAmount()) {
                                mWithdrawalTv.setText("： " + Config.fmtMicrometer(String.format("%.2f", response.getFrozenAmount())) + "元");
                            }

                            if (response.hasApplyCashAmount()) {
                                mFreezTv.setText("： " + Config.fmtMicrometer(String.format("%.2f", response.getApplyCashAmount())) + "元");
                            }

                            if (response.hasCardNum()) {
                                card_num.setText(response.getCardNum());
                            } else {
                                card_num.setText("绑定银行卡");
                            }
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
