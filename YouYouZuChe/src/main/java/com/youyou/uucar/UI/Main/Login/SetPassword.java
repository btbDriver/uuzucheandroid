package com.youyou.uucar.UI.Main.Login;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.login.LoginInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SetPassword extends BaseActivity {

    public final int MINLENGTH = 6;

    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.sure)
    TextView mSure;

    public boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    @OnClick(R.id.sure)
    public void sureClick() {
//        if (mPassword.getText().toString().trim().length() < MINLENGTH) {
//            showToast("密码最少8位");
//            return;
//        }
        if (isChineseChar(mPassword.getText().toString())) {
            showToast("密码不能输入中文");
            return;
        }
        if (mPassword.getText().toString().indexOf(" ") != -1) {
            showToast("密码不能输入空格");
            return;
        }
        showProgress(false);
        LoginInterface.SetPasswordSSL.Request.Builder builder = LoginInterface.SetPasswordSSL.Request.newBuilder();
        builder.setPassword(mPassword.getText().toString().trim());
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.SetPasswordSSL_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("SetPassword");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        LoginInterface.SetPasswordSSL.Response response = LoginInterface.SetPasswordSSL.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            showToast("设置成功");
                            setResult(RESULT_OK);
                            finish();
                        } else if (response.getRet() == -1) {
                            showToast("设置失败");

                        } else if (response.getRet() == 2) {
                            showToast(response.getMsg());
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
                dismissProgress();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_set_password);
        ButterKnife.inject(this);
        mPassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mSure.setEnabled(false);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    mSure.setEnabled(true);
                } else {
                    mSure.setEnabled(false);
                }
                if (s.length() > 20) {
                    String str = s.toString().substring(0, 20);
                    MLog.e("passwd", "passwd_length = " + str.length());
                    mPassword.setText(str);
                    mPassword.setSelection(str.length());
                    Toast.makeText(context, "密码不能超过20位", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return false;
    }
}
