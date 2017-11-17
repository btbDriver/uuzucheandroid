package com.youyou.uucar.UI.Main.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    public final int SET_PASSWORD_REQUEST = 2;
    public final int RESET_PASSWORD_REQUEST = 3;
    public final int NO_PASSWORD_LOGIN = 4;
    public final int REGISTER = 5;
    public static final String RENTER_REGISTER = "renter";
    public static final String OWNER_REGISTER = "owner";
    public static final String GETFRIEND = "getfriend";
    @InjectView(R.id.phone_icon)
    ImageView phoneIcon;
    @InjectView(R.id.phone_input)
    EditText phone;
    @InjectView(R.id.phone_clear)
    ImageView mPhoneClear;
    @InjectView(R.id.phone_root)
    RelativeLayout phone_root;
    @InjectView(R.id.password_icon)
    ImageView mPasswordIcon;
    @InjectView(R.id.password_input)
    EditText mPasswordInput;
    @InjectView(R.id.password_clear)
    ImageView mPasswordClear;
    @InjectView(R.id.password_root)
    RelativeLayout mPasswordRoot;
    @InjectView(R.id.login)
    TextView mLogin;
    @InjectView(R.id.register)
    TextView mRegister;
    @InjectView(R.id.no_password)
    TextView mNoPassword;

    @OnClick(R.id.phone_clear)
    public void phoneClearClick() {
        phone.setText("");
    }

    @OnClick(R.id.password_clear)
    public void smsClearClick() {
        mPasswordInput.setText("");
    }

    @OnClick(R.id.no_password)
    public void noPasswordClick() {
        Intent intent = new Intent(context, NoPasswordLogin.class);
        startActivityForResult(intent, NO_PASSWORD_LOGIN);
    }

    @OnClick(R.id.register)
    public void registerClick() {
        Intent intent = new Intent(context, Register.class);
        startActivityForResult(intent, REGISTER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phone_root.setBackgroundResource(R.drawable.input_bg_focus);
                    phoneIcon.setBackgroundResource(R.drawable.register_phone_focus);
                } else {
                    phone_root.setBackgroundResource(R.drawable.input_bg_normal);
                    phoneIcon.setBackgroundResource(R.drawable.register_phone_normal);
                }
            }
        });
        mPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mPasswordRoot.setBackgroundResource(R.drawable.input_bg_focus);
                    mPasswordIcon.setBackgroundResource(R.drawable.password_icon_select);
                    mPasswordInput.setText("");
                } else {
                    mPasswordRoot.setBackgroundResource(R.drawable.input_bg_normal);
                    mPasswordIcon.setBackgroundResource(R.drawable.password_icon_normal);
                }
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mPhoneClear.setVisibility(View.VISIBLE);
                } else {
                    mPhoneClear.setVisibility(View.GONE);
                }
                if (s.length() == 11) {
                    phoneSuccess = true;
                } else {
                    phoneSuccess = false;
                }
                setLoginButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mPasswordClear.setVisibility(View.GONE);
                }
                if (s.length() >= 6) {
                    passwordSuccess = true;
                } else {
                    passwordSuccess = false;
                }
                setLoginButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    boolean phoneSuccess, passwordSuccess;

    public void setLoginButton() {
        if (phoneSuccess && passwordSuccess) {
            mLogin.setEnabled(true);
            mLogin.setOnClickListener(loginClick);
        } else {
            mLogin.setOnClickListener(null);
            mLogin.setEnabled(false);
        }
    }

    public View.OnClickListener
            loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Config.showProgressDialog(context, false, null);
            login();
        }
    };
    UserModel model;
    UserService service;
    String s_phone = "";

    public UserModel getModel() {

        service = new UserService(context);
        List<UserModel> models = service.getModelList(UserModel.class);
        if (models.size() == 0) {
            model = new UserModel();
            model.phone = "";
            boolean flag = service.insModel(model);
            if (flag) {
                model = service.getModel(UserModel.class, new String[]{s_phone});
            }
        } else {
            model = models.get(0);
        }
        return model;
    }

    private boolean saveModel() {
        model.phone = phone.getText().toString().trim();

        boolean flag = service.modifyModel(model);
        return flag;
    }

    public void login() {
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AccountLoginSSL_VALUE);
        networkTask.setTag("AccountLoginSSL_VALUE");
        LoginInterface.AccountLoginSSL.Request.Builder request = LoginInterface.AccountLoginSSL.Request.newBuilder();
        request.setAccountId(phone.getText().toString());
        request.setPassword(mPasswordInput.getText().toString());

        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                showToast(responseData.getToastMsg());
                int startTime = (int) (System.currentTimeMillis() / 1000);
                getModel();
                saveModel();
                My.isNeedRefresh = true;
                try {
                    LoginInterface.AccountLoginSSL.Response response = LoginInterface.AccountLoginSSL.Response.parseFrom(responseData.getBusiData());
                    showResponseCommonMsg(responseData.getResponseCommonMsg());
                    if (response.getRet() == 0) {
                        LoginInterface.LoginResponse loginResponse = response.getLoginResponse();
                        Config.getUser(context).fromPasswordJson(loginResponse, context);
                        UserCommon.UserStatus userStatus = loginResponse.getUserStatus();
                        if (userStatus.getHasPreOrdering()) {
                            switch (userStatus.getPreOrderType()) {
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
                        } else {
                            Config.isSppedIng = false;
                            Config.isOneToOneIng = false;
                        }
                        if (userStatus.getWaitPayOrderIdCount() > 0) {
                            Config.hasPayOrder = true;
                            Config.waitPayOrderId = userStatus.getWaitPayOrderId(0);
                        } else {
                            Config.hasPayOrder = false;
                        }
                        if (Config.isSppedIng || Config.isOneToOneIng) {

                            Config.isUserCancel = false;
//                            startService(new Intent(context, RentingService.class));
                            getApp().startRenting();
                            if (MainActivityTab.instance != null) {
                                MainActivityTab.instance.speed.name.setText("约车中...");
                            }
                        } else if (Config.hasPayOrder) {
                            if (MainActivityTab.instance != null) {
                                MainActivityTab.instance.speed.name.setText("约车成功");
                            }

                        } else {
                            if (MainActivityTab.instance != null) {
                                MainActivityTab.instance.speed.name.setText("我要约车");
                            }
                        }
                        Config.updateUserSecurity(context, loginResponse.getUserSecurityTicket(), loginResponse.getUserId(), startTime);
                        getApp().startLongConn();


                        if (MainActivityTab.instance != null) {
                            MainActivityTab.instance.order.needRefush = true;

                            MainActivityTab.instance.order.currentRefush = true;
                            MainActivityTab.instance.order.cancelRefush = true;
                            MainActivityTab.instance.order.finishRefush = true;
                        }
                        if (MainActivityTab.instance != null) {
                            MainActivityTab.instance.owner.needRefush = true;
                        }
//                        if (!loginResponse.getHasSetPassword())
//                        {
//                            MLog.e(tag, "intent = " + getIntent().getStringExtra("goto"));
//                            Intent intent = new Intent(context, SetPassword.class);
//                            startActivityForResult(intent, SET_PASSWORD_REQUEST);
//                        }
//                        else
                        {
                            if (getIntent() != null && getIntent().hasExtra("goto")) {
                                if (getIntent().hasExtra("back")) {
                                    Intent intent = new Intent();
                                    intent.putExtra("goto", getIntent().getStringExtra("back"));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    if (getIntent().getStringExtra("goto").equals(RENTER_REGISTER)) {
                                        if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
                                            startActivity(new Intent(context, RenterRegisterIDActivity.class));
                                            finish();
                                        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO) || Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
                                            Intent intent = new Intent();
                                            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_VERIFY);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent();
                                            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    } else if (getIntent().getStringExtra("goto").equals(GETFRIEND)) {
                                        startActivity(new Intent(context, GetFriend.class));
                                        finish();

                                    } else if (getIntent().getStringExtra("goto").equals(OWNER_REGISTER)) {
                                        if (Config.getUser(context).carStatus.equals(User.CAR_STATUS_NOCAR)) {
                                            Intent intent = new Intent(context, AddCarBrandActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (Config.getUser(context).carStatus.equals(User.CAR_STATUS_XINGSHIZHENG) || Config.getUser(context).carStatus.equals(User.CAR_STATUS_OWNER_NO)) {
                                            Intent intent = new Intent();
                                            intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent();
                                            intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }

                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra("goto", getIntent().getStringExtra("goto"));
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }

                            } else {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }

                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reset_password) {
            startActivityForResult(new Intent(context, ResetPasswordPhone.class), RESET_PASSWORD_REQUEST);
            return false;
        }
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loginResult() {

        if (getIntent() != null && getIntent().hasExtra("goto")) {
            if (getIntent().hasExtra("back")) {
                Intent intent = new Intent();
                intent.putExtra("goto", getIntent().getStringExtra("back"));
                setResult(RESULT_OK, intent);
                finish();
            } else {
                if (getIntent().getStringExtra("goto").equals(RENTER_REGISTER)) {
                    if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
                        startActivity(new Intent(context, RenterRegisterIDActivity.class));
                        finish();
                    } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO) || Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
                        Intent intent = new Intent();
                        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_VERIFY);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else if (getIntent().getStringExtra("goto").equals(GETFRIEND)) {
                    startActivity(new Intent(context, GetFriend.class));
                    finish();

                } else if (getIntent().getStringExtra("goto").equals(OWNER_REGISTER)) {
                    if (Config.getUser(context).carStatus.equals(User.CAR_STATUS_NOCAR)) {
                        Intent intent = new Intent();
                        intent.putExtra("goto", MainActivityTab.GOTO_OWNER_ADDCAR);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (Config.getUser(context).carStatus.equals(User.CAR_STATUS_XINGSHIZHENG) || Config.getUser(context).carStatus.equals(User.CAR_STATUS_OWNER_NO)) {
                        Intent intent = new Intent();
                        intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("goto", getIntent().getStringExtra("goto"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESET_PASSWORD_REQUEST) {
                if (data != null && data.hasExtra("phone")) {
                    phone.setText(data.getStringExtra("phone"));
                    mPasswordInput.setFocusable(true);
                    mPasswordInput.setFocusableInTouchMode(true);
                    mPasswordInput.requestFocus();
                    mPasswordInput.requestFocusFromTouch();
                    mPasswordInput.setText("");
                    if (data.getBooleanExtra("register", false)) {
                        loginResult();
                    }
                }
            } else if (requestCode == REGISTER) {
                if (data != null && data.hasExtra("phone"))//注册的时候 是老用户走这个逻辑
                {
                    phone.setText(data.getStringExtra("phone"));
                    mPasswordInput.setFocusable(true);
                    mPasswordInput.setFocusableInTouchMode(true);
                    mPasswordInput.requestFocus();
                    mPasswordInput.requestFocusFromTouch();
                    mPasswordInput.setText("");
                } else if (data != null && data.hasExtra("setPassword") && data.getBooleanExtra("setPassword", false))//设置密码去
                {
                    Intent intent = new Intent(context, SetPassword.class);
                    startActivityForResult(intent, SET_PASSWORD_REQUEST);
                } else {
                    loginResult();
                }
            } else if (requestCode == SET_PASSWORD_REQUEST || requestCode == NO_PASSWORD_LOGIN) {
                if (getIntent() != null && getIntent().hasExtra("goto")) {
                    if (getIntent().hasExtra("back")) {
                        Intent intent = new Intent();
                        intent.putExtra("goto", getIntent().getStringExtra("back"));
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        if (getIntent().getStringExtra("goto").equals(RENTER_REGISTER)) {
                            if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
                                startActivity(new Intent(context, RenterRegisterIDActivity.class));
                                finish();
                            } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO) || Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
                                Intent intent = new Intent();
                                intent.putExtra("goto", MainActivityTab.GOTO_RENTER_VERIFY);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } else if (getIntent().getStringExtra("goto").equals(GETFRIEND)) {
                            startActivity(new Intent(context, GetFriend.class));
                            finish();

                        } else if (getIntent().getStringExtra("goto").equals(OWNER_REGISTER)) {
                            if (Config.getUser(context).carStatus.equals(User.CAR_STATUS_NOCAR)) {
                                Intent intent = new Intent();
                                intent.putExtra("goto", MainActivityTab.GOTO_OWNER_ADDCAR);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (Config.getUser(context).carStatus.equals(User.CAR_STATUS_XINGSHIZHENG) || Config.getUser(context).carStatus.equals(User.CAR_STATUS_OWNER_NO)) {
                                Intent intent = new Intent();
                                intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("goto", getIntent().getStringExtra("goto"));
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }
}
