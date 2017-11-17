package com.youyou.uucar.UI.Main.Login;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.login.LoginInterface;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ResetPasswordSetPassword extends BaseActivity
{

    public final int MINLENGTH = 6;

    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.sure)
    TextView mSure;

    public boolean isChineseChar(String str)
    {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find())
        {
            temp = true;
        }
        return temp;
    }

    @OnClick(R.id.sure)
    public void sureClick()
    {
//        if (mPassword.getText().toString().trim().length() < MINLENGTH) {
//            showToast("密码最少8位");
//            return;
//        }
        if (isChineseChar(mPassword.getText().toString()))
        {
            showToast("密码不能输入中文");
            return;
        }
        if (mPassword.getText().toString().indexOf(" ") != -1)
        {
            showToast("密码不能输入空格");
            return;
        }
        showProgress(false);
        LoginInterface.SetPasswordSSL.Request.Builder builder = LoginInterface.SetPasswordSSL.Request.newBuilder();
        builder.setPassword(mPassword.getText().toString().trim());
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.SetPasswordSSL_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("SetPassword");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                if (responseData.getRet() == 0)
                {
                    try
                    {
                        LoginInterface.SetPasswordSSL.Response response = LoginInterface.SetPasswordSSL.Response.parseFrom(responseData.getBusiData());
                        showResponseCommonMsg(responseData.getResponseCommonMsg());
                        if (response.getRet() == 0)
                        {
                            userLogonOut();
                            showToast("新密码设置成功");
                            setResult(RESULT_OK);
                            finish();

                        }
                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(VolleyError errorResponse)
            {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish()
            {
                dismissProgress();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        userLogonOut();
        finish();
    }

    UserService service;
    UserModel   model;

    private boolean saveModel()
    {
        service = new UserService(context);
        List<UserModel> models = service.getModelList(UserModel.class);
        if (models.size() == 0)
        {
            model = new UserModel();
            model.phone = "";
            boolean flag = service.insModel(model);
            if (flag)
            {
                model = service.getModel(UserModel.class, new String[] {""});
            }
        }
        else
        {
            model = models.get(0);
        }
//        model = new UserModel();
        model.phone = "";
        model.sid = "";
        model.idCardFrontPic = "";// 身份证正面照片路径
        model.idCardFrontState = "";// 身份证正面照片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态
        model.idCardBackPic = "";// 身份证正面照片路径
        model.idCardBackState = "";// 身份证正面照片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

        model.driverFrontPic = ""; //驾驶证正面图片路径
        model.driverFrontState = "";// 驾驶证正面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

        model.driverBackPic = "";//驾驶证反面图片路径
        model.driverBackState = "";// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态
        model.sign = "";
        model.userStatus = User.USER_STATUS_NEW;
        model.carStatus = User.CAR_STATUS_NOCAR;
        model.pwdcode = "";
        model.name = "";
        model.head = "";
        boolean flag = service.modifyModel(model);
        return flag;
    }

    public void userLogonOut()
    {
        saveModel();
        ObserverManager.getObserver(ObserverManager.MAINLOGOUT).observer("", "");
        Config.clearUserSecurity(context);
        getApp().quitLongConn();
        Config.isUserCancel = false;
        getApp().quitRenting();
        Config.isSppedIng = false;
        Config.isOneToOneIng = false;
        Config.speedHasAgree = false;
        Config.hasPayOrder = false;
        if (MainActivityTab.instance != null)
        {

            MainActivityTab.instance.endAnimation();
            MainActivityTab.instance.speed.name.setText("我要约车");
            MainActivityTab.instance.speed.tv_num.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_set_password);
        ButterKnife.inject(this);
        mPassword.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return true;
            }
        });
        mSure.setEnabled(false);
        mPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() >= 6)
                {
                    mSure.setEnabled(true);
                }
                else
                {
                    mSure.setEnabled(false);
                }
                if (s.length() > 20)
                {
                    String str = s.toString().substring(0, 20);
                    MLog.e("passwd", "passwd_length = " + str.length());
                    mPassword.setText(str);
                    mPassword.setSelection(str.length());
                    Toast.makeText(context, "密码不能超过20位", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            return true;
        }
        return false;
    }
}
