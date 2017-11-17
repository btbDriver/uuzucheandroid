package com.youyou.uucar.UI.Main.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.login.LoginInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.my.GetFriend;
import com.youyou.uucar.UI.Main.my.My;
import com.youyou.uucar.UI.Owner.addcar.AddCarBrandActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ResetPasswordPhone extends BaseActivity
{
    @InjectView(R.id.phone_root)
    RelativeLayout phone_root;
    @InjectView(R.id.phone_input)
    EditText       phone;
    @InjectView(R.id.phone_icon)
    ImageView      phoneIcon;
    @InjectView(R.id.phone_clear)
    ImageView      phoneClear;
    @InjectView(R.id.getsms)
    TextView       getsms;
    @InjectView(R.id.sms_root)
    RelativeLayout sms_root;
    @InjectView(R.id.sms_icon)
    ImageView      smsIcon;
    @InjectView(R.id.sms_input)
    EditText       sms;
    @InjectView(R.id.sms_clear)
    ImageView      smsClear;
    @InjectView(R.id.nosms)
    TextView       nosms;
    @InjectView(R.id.next)
    TextView       login;
    boolean phoneSuccess, smsSuccess;
    UserModel   model;
    UserService service;
    String  s_phone = "";
    Handler handler = new Handler();
    private TimeCount time;
    private boolean isUnusual = false;


    @OnClick(R.id.phone_clear)
    public void phoneClearClick()
    {
        phone.setText("");
    }

    @OnClick(R.id.sms_clear)
    public void smsClearClick()
    {
        sms.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.inject(this);
        isUnusual = getIntent().getBooleanExtra(SysConfig.UN_USUAL, false);
        time = new TimeCount(30000, 1000);// 构造CountDownTimer对象
        setLoginButton();
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    phone_root.setBackgroundResource(R.drawable.input_bg_focus);
                    phoneIcon.setBackgroundResource(R.drawable.register_phone_focus);
                    getsms.setBackgroundResource(R.drawable.input_right_bg_focus);
                }
                else
                {
                    phone_root.setBackgroundResource(R.drawable.input_bg_normal);
                    phoneIcon.setBackgroundResource(R.drawable.register_phone_normal);
                    getsms.setBackgroundResource(R.drawable.input_right_bg_normal);
                }
            }
        });
        sms.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    sms_root.setBackgroundResource(R.drawable.input_bg_focus);
                    smsIcon.setBackgroundResource(R.drawable.register_phone_sms_focus);
                    nosms.setBackgroundResource(R.drawable.input_right_bg_focus);
                }
                else
                {
                    sms_root.setBackgroundResource(R.drawable.input_bg_normal);
                    smsIcon.setBackgroundResource(R.drawable.register_phone_sms_normal);
                    nosms.setBackgroundResource(R.drawable.input_right_bg_normal);
                }
            }
        });
        phone.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0)
                {
                    phoneClear.setVisibility(View.VISIBLE);
                }
                else
                {
                    phoneClear.setVisibility(View.GONE);
                }
                if (s.length() == 11)
                {
                    phoneSuccess = true;
                    if (getsms.getText().toString().equals(getString(R.string.register_phone_getsms)) || getsms.getText().toString().equals("重新验证"))
                    {
                        getsms.setTextColor(getResources().getColor(R.color.c1));
                        getsms.setClickable(true);
                    }
                }
                else
                {
                    phoneSuccess = false;
                    if (getsms.getText().toString().equals(getString(R.string.register_phone_getsms)) || getsms.getText().toString().equals("重新验证"))
                    {
                        getsms.setTextColor(getResources().getColor(R.color.c8));
                        getsms.setClickable(false);
                    }
                }
                setLoginButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
        if (phone.getText().toString().length() == 11)
        {
            phoneSuccess = true;
            phoneClear.setVisibility(View.VISIBLE);
            if (getsms.getText().toString().equals(getString(R.string.register_phone_getsms)))
            {
                getsms.setTextColor(getResources().getColor(R.color.c1));
                getsms.setClickable(true);
            }
        }
        else
        {
            phoneSuccess = false;
            phoneClear.setVisibility(View.GONE);
            if (getsms.getText().toString().equals(getString(R.string.register_phone_getsms)))
            {
                getsms.setTextColor(getResources().getColor(R.color.c8));
                getsms.setClickable(false);
            }
        }
        sms.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0)
                {
                    smsClear.setVisibility(View.VISIBLE);
                }
                else
                {
                    smsClear.setVisibility(View.GONE);
                }
                if (s.length() == 4)
                {
                    smsSuccess = true;
                }
                else
                {
                    smsSuccess = false;
                }
                setLoginButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
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

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish()
        {// 计时完毕时触发
            // getsms.setText("验证");
            getsms.setText("重新验证");
            getsms.setClickable(true);
            // getsms.setTextColor(Color.parseColor("#78808A"));
            // getsms.setTextColor(Color.parseColor("#FE4E5B"));
            nosms.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {// 计时过程显示
            getsms.setClickable(false);
            getsms.setText(millisUntilFinished / 1000 + "");
            // getsms.setTextColor(Color.parseColor("#FE4E5B"));
//            if (millisUntilFinished / 1000 == 20)
//            {
//                nosms.setVisibility(View.VISIBLE);
//            }
        }
    }

    public View.OnClickListener loginClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Config.showProgressDialog(context, false, null);
            login();
        }
    };

    public UserModel getModel()
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
                model = service.getModel(UserModel.class, new String[] {s_phone});
            }
        }
        else
        {
            model = models.get(0);
        }
        return model;
    }

    private boolean saveModel()
    {
        model.phone = phone.getText().toString().trim();

        boolean flag = service.modifyModel(model);
        return flag;
    }

    final int SET_PASSWORD_REQUEST = 2;
    final int REGISTER             = 3;

    public void login()
    {
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.SmsLoginSSL_VALUE);
        networkTask.setTag("login");
        LoginInterface.SmsLoginSSL.Request.Builder request = LoginInterface.SmsLoginSSL.Request.newBuilder();
        request.setPhone(phone.getText().toString());
        request.setVerifyCode(sms.getText().toString());
        request.setScene(LoginInterface.VerifyCodeScene.FORGET_PASSWORD);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                showToast(responseData.getToastMsg());
                int startTime = (int) (System.currentTimeMillis() / 1000);
                getModel();
                saveModel();
                My.isNeedRefresh = true;
                try
                {
                    LoginInterface.SmsLoginSSL.Response response = LoginInterface.SmsLoginSSL.Response.parseFrom(responseData.getBusiData());
                    if (response.getRet() != -6)
                    {
                        showResponseCommonMsg(responseData.getResponseCommonMsg());
                    }
                    else//新用户,弹窗去取车
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("手机号" + phone.getText().toString().trim() + "还未注册");
                        builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener()
                        {
                            @Override public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent(context, Register.class);
                                intent.putExtra("phone", phone.getText().toString().trim());
                                startActivityForResult(intent, REGISTER);
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                    if (response.getRet() == 0)
                    {
                        Config.getUser(context).fromJson(response, context);
                        Config.updateUserSecurity(context, response.getUserSecurityTicket(), response.getUserId(), startTime);
                        Intent intent = new Intent(context, ResetPasswordSetPassword.class);
                        startActivityForResult(intent, SET_PASSWORD_REQUEST);
                    }
                }
                catch (InvalidProtocolBufferException e)
                {
                    e.printStackTrace();
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
                Config.dismissProgress();
            }
        });
    }

    public void setLoginButton()
    {
        if (phoneSuccess && smsSuccess)
        {
            login.setEnabled(true);
//            login.setBackgroundResource(R.drawable.register_red_button_bg);
            login.setOnClickListener(loginClick);
//            login.setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        login.setBackgroundResource(R.drawable.register_red_button_pressed_bg);
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        login.setBackgroundResource(R.drawable.register_red_button_bg);
//                    }
//                    return false;
//                }
//            });
        }
        else
        {
//            login.setBackgroundResource(R.drawable.register_red_button_nopressed_bg);
            login.setOnClickListener(null);
//            login.setOnTouchListener(null);
            login.setEnabled(false);
        }
    }

    @OnClick(R.id.getsms)
    public void getsmsClick()
    {
        String name = phone.getText().toString().trim();
        if (name.length() != 11 && !Config.btnValidatePhoneNum(name))
        {
            Toast.makeText(context, "输入的手机号无效", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Config.showProgressDialog(context, false, null);
            NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.GetSmsVerifyCodeSSL_VALUE);
            networkTask.setTag("send_sms");
            LoginInterface.GetSmsVerifyCodeSSL.Request.Builder request = LoginInterface.GetSmsVerifyCodeSSL.Request.newBuilder();
            request.setPhone(name);
            request.setScene(LoginInterface.VerifyCodeScene.FORGET_PASSWORD);
            networkTask.setBusiData(request.build().toByteArray());
            NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
            {
                @Override
                public void onSuccessResponse(UUResponseData responseData)
                {
                    try
                    {
                        if (responseData.getRet() == 0)
                        {
                            LoginInterface.GetSmsVerifyCodeSSL.Response response = LoginInterface.GetSmsVerifyCodeSSL.Response.parseFrom(responseData.getBusiData());
                            if (response.getRet() != -3)
                            {
                                showResponseCommonMsg(responseData.getResponseCommonMsg());
                            }
                            else//新用户,弹窗去取车
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("手机号" + phone.getText().toString().trim() + "还未注册");
                                builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener()
                                {
                                    @Override public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent intent = new Intent(context, Register.class);
                                        intent.putExtra("phone", phone.getText().toString().trim());
                                        startActivityForResult(intent, REGISTER);
                                    }
                                });
                                Dialog dialog = builder.create();
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                return;
                            }
                            if (response.getRet() == 0)
                            {
                                time.start();// 开始计时
                            }
                            else if (response.getRet() > 0)
                            {
                                sms.setText(response.getRet() + "");
                                time.start();// 开始计时
                            }
                        }
                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        MLog.e(tag, "getSMS error = " + e.getMessage());
                        e.printStackTrace();
                        Config.showFiledToast(context);
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

                    Config.dismissProgress();
                }
            });
        }
    }

    @OnClick(R.id.nosms)
    public void nosmsClick()
    {
//        isReadSMSGoto = true;
//        Uri smsToUri = Uri.parse("smsto://" + Config.UPLOADSMS_ADDRESS);
//        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//        String ran = "" + Config.random(1, 9999);
//        String temp = "";
//        if (ran.length() != 4) {
//            for (int i = 0; i < 4 - ran.length(); i++) {
//                temp += "0";
//            }
//            temp += ran;
//        } else {
//            temp = ran;
//        }
//        mIntent.putExtra("address", Config.UPLOADSMS_ADDRESS);
//        mIntent.putExtra("sms_body", temp + Config.UPLOADSMSBODY);
//        startActivity(mIntent);
//        sms.setText(temp);
        showProgress(false);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.GetVoiceCallVerifyCodeSSL_VALUE);
        networkTask.setTag("getVoice");
        LoginInterface.GetVoiceCallVerifyCodeSSL.Request.Builder request = LoginInterface.GetVoiceCallVerifyCodeSSL.Request.newBuilder();
        request.setPhone(phone.getText().toString());
        request.setScene(LoginInterface.VerifyCodeScene.FORGET_PASSWORD);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
//                showToast(responseData.getToastMsg());
                if (responseData.getRet() == 0)
                {
                    showResponseCommonMsg(responseData.getResponseCommonMsg());
                    try
                    {
                        LoginInterface.GetVoiceCallVerifyCodeSSL.Response response = LoginInterface.GetVoiceCallVerifyCodeSSL.Response.parseFrom(responseData.getBusiData());

                        if (response.getRet() != -3)
                        {
                            showResponseCommonMsg(responseData.getResponseCommonMsg());
                        }
                        else//新用户,弹窗去取车
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("手机号" + phone.getText().toString().trim() + "还未注册");
                            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener()
                            {
                                @Override public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent intent = new Intent(context, Register.class);
                                    intent.putExtra("phone", phone.getText().toString().trim());
                                    startActivityForResult(intent, REGISTER);
                                }
                            });
                            Dialog dialog = builder.create();
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            return;
                        }


                        if (response.getRet() == 0)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("友友租车将给您拨打自动语音电话阅读验证码，请注意接听。来电号码" + response.getVoiceCallNumber());
                            builder.setNeutralButton("我知道了", null);
                            builder.create().show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SET_PASSWORD_REQUEST || requestCode == REGISTER)
            {
                Intent intent = new Intent();
                intent.putExtra("phone", phone.getText().toString().trim());
                if (requestCode == REGISTER)
                {
                    intent.putExtra("register", true);
                }
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }
}
