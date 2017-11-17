package com.youyou.uucar.UI.Main.my.money;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;

import java.util.ArrayList;
import java.util.List;

public class AddCard extends BaseActivity {
    public String tag = "AddCard";
    public Activity context;
    TextView name, bank;
    EditText card_num, password, city, password1;
    int bankindex = 1;

    List<UserInterface.MyBankCards.Bank> bankList;
    ArrayList<Bank> data = new ArrayList<Bank>();

    public static void fillBankNumSpeace(final EditText mAddCardNumEdt) {// 银行卡补齐 空格
        mAddCardNumEdt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (s.length() == 4) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(5);
                    }
                    if (s.length() == 9) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(10);
                    }
                    if (s.length() == 14) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(15);
                    }
                    if (s.length() == 19) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(20);
                    }
                } else if (count == 0) {
                    if (s.length() == 4) {
                        mAddCardNumEdt.setText(s.subSequence(0, s.length() - 1));
                        mAddCardNumEdt.setSelection(3);
                    }
                    if (s.length() == 9) {
                        mAddCardNumEdt.setText(s.subSequence(0, s.length() - 1));
                        mAddCardNumEdt.setSelection(8);
                    }
                    if (s.length() == 14) {
                        mAddCardNumEdt.setText(s.subSequence(0, s.length() - 1));
                        mAddCardNumEdt.setSelection(13);
                    }
                    if (s.length() == 19) {
                        mAddCardNumEdt.setText(s.subSequence(0, s.length() - 1));
                        mAddCardNumEdt.setSelection(18);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        Config.setActivityState(this);
        setContentView(R.layout.addcard);
        bankList = Config.bankList;

        if (bankList != null && bankList.size() > 0) {
            data.clear();
            for (UserInterface.MyBankCards.Bank bankItem : bankList) {
                Bank item = new Bank();
                item.id = bankItem.getBankId();
                item.name = bankItem.getBankName();
                data.add(item);
            }
        }
//        try {
//            json = new JSONObject(getIntent().getStringExtra("json"));
//            data.clear();
//            for (int i = 0; i < json.getJSONObject("content").getJSONArray("bank").length(); i++) {
//                JSONObject bank = json.getJSONObject("content").getJSONArray("bank").getJSONObject(i);
//                Bank item = new Bank();
//                item.id = bank.getInt("bank_id");
//                item.name = bank.getString("bank_name");
//                data.add(item);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        name = (TextView) findViewById(R.id.name);
        name.setText(Config.getUser(context).name);
        bank = (TextView) findViewById(R.id.bank);
        bank.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankSelect();
            }
        });
        card_num = (EditText) findViewById(R.id.card_num);
        fillBankNumSpeace(card_num);
        password = (EditText) findViewById(R.id.password);
        password1 = (EditText) findViewById(R.id.password_1);
        city = (EditText) findViewById(R.id.city);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        } else if (item.getItemId() == R.id.add_card) {

            String cityStr = city.getText().toString();
            String passwdStr = password.getText().toString();
            String passwdRepeateStr = password1.getText().toString();
            if (card_num.getText().toString().trim().length() < 14) {
                Toast.makeText(context, "请填写正确的银行卡号!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if ((bank.getText().toString() + "").trim().equals("")) {
                Toast.makeText(context, "请选择银行!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (cityStr == null || cityStr.trim().equals("")) {
                Toast.makeText(context, "请填写城市!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (passwdStr == null || passwdStr.trim().length() != 6) {
                Toast.makeText(context, "请输入6位数字的提现密码!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (passwdRepeateStr == null || passwdRepeateStr.trim().equals("")) {
                Toast.makeText(context, "请再次输入提现密码!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!passwdRepeateStr.equals(passwdStr)) {
                Toast.makeText(context, "两次密码输入不一致，请重新输入!", Toast.LENGTH_SHORT).show();
                return false;
            }

            Builder builder = new Builder(context);
            builder.setMessage("您要绑定的储蓄卡信息为：\n账户名：" + name.getText().toString() + "\n卡号：" + card_num.getText().toString() + "\n银行：" + bank.getText().toString() + "\n开户城市：" + city.getText().toString());
            builder.setNegativeButton("重新填写", null);
            builder.setNeutralButton("确认绑定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    {
                        Config.showProgressDialog(context, false, null);
                        UserInterface.AddBankCard.Request.Builder request = UserInterface.AddBankCard.Request.newBuilder();
                        request.setBankId(selectBankId);
                        request.setCardNum(card_num.getText().toString().trim().replace(" ", ""));
                        request.setBankAccountCity(city.getText().toString().trim());
                        request.setApplyCashPassword(password.getText().toString().trim());
                        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AddBankCard_VALUE);
                        networkTask.setBusiData(request.build().toByteArray());
                        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                            @Override
                            public void onSuccessResponse(UUResponseData response) {
                                Config.showToast(context, response.getToastMsg());
                                if (response.getRet() == 0) {
                                    try {
                                        UserInterface.AddBankCard.Response data = UserInterface.AddBankCard.Response.parseFrom(response.getBusiData());
                                        if (data.getRet() == 0) {
                                            showToast("添加成功");
                                            finish();
                                        } else {
                                            showToast("添加失败");
                                        }
                                    } catch (InvalidProtocolBufferException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onError(VolleyError errorResponse) {

                            }

                            @Override
                            public void networkFinish() {
                                Config.dismissProgress();
                            }
                        });
                    }
                }
            });
            builder.create().show();
        }
        return true;
    }

    public int selectBankId = 0;

    public void showBankSelect() {
        Builder builder = new Builder(context);
        builder.setTitle("选择银行");
        String banks[] = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            banks[i] = data.get(i).name;
        }
        builder.setSingleChoiceItems(banks, bankindex - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bankindex = which + 1;
                selectBankId = data.get(which).id;
                bank.setText(data.get(which).name);
            }
        });
        builder.create().show();
    }

    public class Bank {
        public int id = 0;
        public String name = "";
    }
}
