package com.youyou.uucar.UI.Main.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NoPasswordLogin extends BaseActivity
{
    public final        int    SET_PASSWORD_REQUEST = 2;
    public static final String RENTER_REGISTER      = "renter";
    public static final String OWNER_REGISTER       = "owner";
    public static final String GETFRIEND            = "getfriend";
    public              String tag                  = "RegisterPhone";
    public Activity context;
    public OnClickListener loginClick = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Config.showProgressDialog(context, false, null);
            login();
        }
    };
    ObserverListener readSMSListener = new ObserverListener()
    {
        @Override
        public void observer(String from, Object obj)
        {
            // Log.e(tag,"login Observer from = " + from + "  obj =" + obj);
            // 05-18 14:08:37.394: E/Login(8239): login Observer from = AutoSMS obj =(9007)是友友租车验证码，欢迎加入中国租车社区，友邻友车，尽在友友！【友友租车】
            String string_sms = obj.toString();
            if (string_sms.indexOf("(") != -1 && string_sms.indexOf(")") != -1)
            {
                String pass = string_sms.substring(string_sms.indexOf("(") + 1, string_sms.indexOf(")"));
                sms.setText(pass);
                Config.showProgressDialog(context, false, null);
                login();
            }
        }
    };
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
    @InjectView(R.id.login)
    TextView       login;
    boolean phoneSuccess, smsSuccess;
    UserModel   model;
    UserService service;
    String  s_phone       = "";
    Handler handler       = new Handler();
    /**
     * 点击没收到后,变成true.变true后onresume的时候睡一会直接调用注册
     */
    boolean isReadSMSGoto = false;
    private TimeCount time;
    private boolean isUnusual = false;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ObserverManager.removeObserver("Login");
    }

//    @InjectView(R.id.code_input)
//    EditText       codeInput;
//    @InjectView(R.id.code_icon)
//    ImageView      codeIcon;
//    @InjectView(R.id.code_root)
//    RelativeLayout codeRoot;

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.register_phone);
        ButterKnife.inject(this);
        isUnusual = getIntent().getBooleanExtra(SysConfig.UN_USUAL, false);
        ObserverManager.addObserver("Login", readSMSListener);
        time = new TimeCount(30000, 1000);// 构造CountDownTimer对象
        model = getModel();
        phone.setText(model.phone);
        if (!model.phone.equals(""))
        {
            getsms.setTextColor(getResources().getColor(R.color.c1));
            getsms.setClickable(true);
            phoneSuccess = true;
        }
        else
        {
            getsms.setClickable(false);
        }

        setLoginButton();
        phone.setOnFocusChangeListener(new OnFocusChangeListener()
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
        sms.setOnFocusChangeListener(new OnFocusChangeListener()
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
//        codeInput.setOnFocusChangeListener(new OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus)
//            {
//                if (hasFocus)
//                {
//                    codeRoot.setBackgroundResource(R.drawable.input_bg_focus);
//                    codeIcon.setBackgroundResource(R.drawable.code_icon_focus);
//                }
//                else
//                {
//                    codeRoot.setBackgroundResource(R.drawable.input_bg_normal);
//                    codeIcon.setBackgroundResource(R.drawable.code_icon);
//                }
//            }
//        });
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if (isUnusual)
        {
            Config.clearUserSecurity(context);
            Config.clearUser(context);
            Intent intent = new Intent(context, MainActivityTab.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        else
        {
            MainActivityTab.openMenu = false;
            finish();
        }
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
            request.setScene(LoginInterface.VerifyCodeScene.SMS_LOGIN);
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
                            if (response.getRet() == 0)
                            {
                                time.start();// 开始计时
                                showToast("请等待验证码短信");
                            }
                            else if (response.getRet() > 0)
                            {
                                sms.setText(response.getRet() + "");
                                time.start();// 开始计时

                            }
                            else if (response.getRet() == -2)
                            {
                                showToast("您填写的手机号不正确");
                            }
                            else
                            {

                                Config.showFiledToast(context);
                            }
                        }
                        else
                        {

                            Config.showFiledToast(context);
                        }
                    }
                    catch (InvalidProtocolBufferException e)
                    {
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

        String name = phone.getText().toString().trim();
        if (name.length() != 11 && !Config.btnValidatePhoneNum(name))
        {
            Toast.makeText(context, "输入的手机号无效", Toast.LENGTH_SHORT).show();
        }
        else
        {

            showProgress(false);
            NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.GetVoiceCallVerifyCodeSSL_VALUE);
            networkTask.setTag("getVoice");
            LoginInterface.GetVoiceCallVerifyCodeSSL.Request.Builder request = LoginInterface.GetVoiceCallVerifyCodeSSL.Request.newBuilder();
            request.setPhone(phone.getText().toString());
            request.setScene(LoginInterface.VerifyCodeScene.SMS_LOGIN);
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

    }

    public void onResume()
    {
        super.onResume();
        if (isReadSMSGoto)
        {
            isReadSMSGoto = false;
            Config.showProgressDialog(context, false, null);
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000 * 5);
                        handler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                login();
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    public String readChannel()
    {
        ApplicationInfo info;
        try
        {
            info = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData.getString("UMENG_CHANNEL");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void login()
    {
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.SmsLoginSSL_VALUE);
        networkTask.setTag("login");
        LoginInterface.SmsLoginSSL.Request.Builder request = LoginInterface.SmsLoginSSL.Request.newBuilder();
        request.setPhone(phone.getText().toString());
        request.setScene(LoginInterface.VerifyCodeScene.SMS_LOGIN);
        request.setVerifyCode(sms.getText().toString());
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
                    showResponseCommonMsg(responseData.getResponseCommonMsg());
                    if (response.getRet() == 0)
                    {
                        Config.getUser(context).fromJson(response, context);
                        UserCommon.UserStatus userStatus = response.getUserStatus();
                        if (userStatus.getHasPreOrdering())
                        {
                            switch (userStatus.getPreOrderType())
                            {
                                case 1:
                                    Config.isSppedIng = true;
                                    break;
                                case 2:
                                    Config.isOneToOneIng = true;
                                    break;
                                case 3:
                                    Config.isSppedIng = true;
                                    Config.speedHasAgree = true;
                                    break;
                            }
                        }
                        else
                        {
                            Config.isSppedIng = false;
                            Config.isOneToOneIng = false;
                        }
                        if (userStatus.getWaitPayOrderIdCount() > 0)
                        {
                            Config.hasPayOrder = true;
                            Config.waitPayOrderId = userStatus.getWaitPayOrderId(0);
                        }
                        else
                        {
                            Config.hasPayOrder = false;
                        }
                        if (Config.isSppedIng || Config.isOneToOneIng)
                        {

                            Config.isUserCancel = false;
//                            startService(new Intent(context, RentingService.class));
                            getApp().startRenting();
                            if (MainActivityTab.instance != null)
                            {
                                MainActivityTab.instance.speed.name.setText("约车中...");
                            }
                        }
                        else if (Config.hasPayOrder)
                        {
                            if (MainActivityTab.instance != null)
                            {
                                MainActivityTab.instance.speed.name.setText("约车成功");
                            }

                        }
                        else
                        {
                            if (MainActivityTab.instance != null)
                            {
                                MainActivityTab.instance.speed.name.setText("我要约车");
                            }
                        }
                        Config.updateUserSecurity(context, response.getUserSecurityTicket(), response.getUserId(), startTime);
//                        Intent longConnectIntent = new Intent(RegisterPhone.this, UUService.class);
//                        longConnectIntent.putExtra(SysConfig.LONG_CONNECT, SysConfig.START_LONG_CONNECT);
//                        startService(longConnectIntent);
                        getApp().startLongConn();
                        if (MainActivityTab.instance != null)
                        {
                            MainActivityTab.instance.order.needRefush = true;

                            MainActivityTab.instance.order.currentRefush = true;
                            MainActivityTab.instance.order.cancelRefush = true;
                            MainActivityTab.instance.order.finishRefush = true;
                        }
                        if (MainActivityTab.instance != null)
                        {
                            MainActivityTab.instance.owner.needRefush = true;
                        }
                        if (!response.getHasSetPassword())
                        {
                            Intent intent = new Intent(context, SetPassword.class);
                            startActivityForResult(intent, SET_PASSWORD_REQUEST);
                        }
                        else
                        {
                            setResult(RESULT_OK);
                            finish();
                        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SET_PASSWORD_REQUEST)
            {
//                if (getIntent() != null && getIntent().hasExtra("goto"))
//                {
                setResult(RESULT_OK);
                finish();
//                }
            }
        }
    }
}