package com.youyou.uucar.UI.Main.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.AbStrUtil;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.login.LoginInterface;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Main.my.money.MoneyManager;
import com.youyou.uucar.UI.Owner.addcar.AddCarBrandActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.BitmapUtils;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUParams;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class My extends Fragment
{

    public final         int         COLLECTREQUEST = 900;
    public static final  String      TAG            = My.class.getSimpleName();
    private static final String      SCHEME         = "settings";
    private static final String      AUTHORITY      = "My";
    public static final  Uri         URI            = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();
    public static final  String      DESCRIPTOR     = "com.umeng.share";
    public final         String      APP_ID         = "wx9abfa08f7da32b30";
    private final        SHARE_MEDIA mTestMedia     = SHARE_MEDIA.SINA;
    private final        SHARE_MEDIA TENCENT_MEDIA  = SHARE_MEDIA.TENCENT;
    public               String      tag            = "More";
    public  Activity context;
    private Uri      photoUri;
    private String   picPath;
    private String   bigPicPath;
    Bitmap bitmap;
    public static boolean              isNeedRefresh = false;
    public        View.OnClickListener noLoginClick  = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
//            Intent intent = new Intent(context, RegisterPhone.class);
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("tomain", true);
            startActivity(intent);
            isChangePhone = true;
        }
    };
    public IWXAPI api;
    public View.OnTouchListener LayoutTouch = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            // if(event.getAction() == MotionEvent.ACTION_DOWN)
            // {
            // v.setBackgroundColor(Color.WHITE);
            // ((RelativeLayout)v).findViewById(R.id.arrow).setBackgroundResource(R.drawable.set_arrow_pressed);
            // }
            // if(event.getAction() == MotionEvent.ACTION_UP)
            // {
            // v.setBackgroundColor(Color.parseColor("#00ffffff"));
            // ((RelativeLayout)v).findViewById(R.id.arrow).setBackgroundResource(R.drawable.set_arrow);
            // }
            return false;
        }
    };
    public String               shareText   = "";
    TextView login;
    TextView chezhu, zuke, name;
    RelativeLayout money, share, feedback;
    com.youyou.uucar.Utils.ImageView.CircleImageView head;
    //    RelativeLayout selectcity;
    boolean                                          isChangePhone;
    public View.OnClickListener loginClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (SysConfig.ISMONKEY && SysConfig.DEVELOP_MODE)
            {
                return;
            }
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("tomain", true);
            startActivityForResult(intent, 10000);
            isChangePhone = true;
        }
    };
    Dialog dialog;
    public View.OnClickListener
                                selectImageClick  = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (dialog != null && dialog.isShowing())
            {
                Config.deleteFile(Config.ImageFile + "temp_image.jpg");
                // Intent intent = new Intent();
                // intent.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                // startActivityForResult(intent,Config.IMAGE_IMAGE_RESULT);
                // 从相册中去获取
                try
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, Config.IMAGE_IMAGE_RESULT);
                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(context, "没有找到照片", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }
    };
    public View.OnClickListener selectCameraClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (dialog != null && dialog.isShowing())
            {
//                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Config.imageUri);
//                startActivityForResult(i, Config.IMAGE_CAMERA_RESULT);
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED))
                {
                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                    bigPicPath = SysConfig.SD_IMAGE_PATH + "temp.jpg";
                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bigPicPath)));
                    startActivityForResult(getImageByCamera, Config.IMAGE_CAMERA_RESULT);
                }
                else
                {
                    Toast.makeText(context, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        }
    };
    public View.OnClickListener headClick         = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!Config.isGuest(context))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = inflater.inflate(R.layout.image_dialog, null);
                builder.setView(convertView);
                TextView image = (TextView) convertView.findViewById(R.id.image);
                TextView camera = (TextView) convertView.findViewById(R.id.camera);
                image.setOnClickListener(selectImageClick);
                camera.setOnClickListener(selectCameraClick);
                dialog = builder.create();
                dialog.show();
            }
        }
    };
    Dialog timePickDialog;
    public View.OnClickListener layoutClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.collect:
                    Intent intent = new Intent(context, CollectList.class);
                    startActivityForResult(intent, COLLECTREQUEST);
                    break;
                case R.id.about:
                    startActivity(new Intent(context, About.class));
                    break;
                case R.id.coupon:
                    startActivity(new Intent(context, Coupon.class));
                    break;
//                case R.id.select_city:
//                    startActivityForResult(new Intent(context, SelectOpenCity.class), 10);
//                    break;
                case R.id.rule:
                    startActivity(new Intent(context, RuleSelect.class));
                    break;
                case R.id.money:
                    startActivity(new Intent(context, MoneyManager.class));
                    break;
                case R.id.feedback:
                    /*FeedbackAgent agent = new FeedbackAgent(context);
                    agent.startFeedbackActivity();*/
                    //跳转用户反馈界面
                    startActivity(new Intent(context, Feedback.class));
                    break;
                case R.id.share:
                    startActivity(new Intent(context, GetFriend.class));
                    break;
            }
        }
    };

    private long selectedTime = 0;
    UserService service;
    UserModel   model;
    // sdk controller
    private UMSocialService mController = null;

    public void reqToWx()
    {
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        api.registerApp(APP_ID);
    }

    View view;
    @InjectView(R.id.top_login_root)
    RelativeLayout topLoginRoot;
    @InjectView(R.id.no_login_root)
    RelativeLayout topNoLoginRoot;
    @InjectView(R.id.coupon)
    RelativeLayout coupon;
    @InjectView(R.id.collect)
    RelativeLayout collect;
    @InjectView(R.id.coupon_num)
    TextView       couponNum;
//    @InjectView(R.id.switch_check)
//    CheckBox ceshiCheckBox;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = getActivity();
        view = inflater.inflate(R.layout.activity_my, null);
        ButterKnife.inject(this, view);
        reqToWx();
//        rule = (RelativeLayout) view.findViewById(R.id.rule);
        money = (RelativeLayout) view.findViewById(R.id.money);
        share = (RelativeLayout) view.findViewById(R.id.share);
        feedback = (RelativeLayout) view.findViewById(R.id.feedback);
        head = (com.youyou.uucar.Utils.ImageView.CircleImageView) view.findViewById(R.id.head);
        name = (TextView) view.findViewById(R.id.name);
        login = (TextView) view.findViewById(R.id.login);
        login.setOnClickListener(loginClick);
        chezhu = (TextView) view.findViewById(R.id.chezhu);
        zuke = (TextView) view.findViewById(R.id.zuke);
//        rule.setOnTouchListener(LayoutTouch);
        share.setOnTouchListener(LayoutTouch);
        feedback.setOnTouchListener(LayoutTouch);
        money.setOnTouchListener(LayoutTouch);
        money.setOnClickListener(layoutClick);

        feedback.setOnClickListener(layoutClick);
        share.setOnClickListener(layoutClick);
        collect.setOnClickListener(layoutClick);
        view.findViewById(R.id.about).setOnClickListener(layoutClick);
//        rule.setOnClickListener(layoutClick);
        head.setOnClickListener(headClick);
//        selectcity = (RelativeLayout) view.findViewById(R.id.select_city);
//        city = (TextView) view.findViewById(R.id.city);
//        selectcity.setOnClickListener(layoutClick);
        coupon.setOnClickListener(layoutClick);
        mController = UMServiceFactory.getUMSocialService(DESCRIPTOR, RequestType.SOCIAL);
        // 添加新浪和QQ空间的SSO授权支持
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        ObserverManager.addObserver(ObserverManager.MYLOGOUT, logoutListener);
        ObserverManager.addObserver(ObserverManager.COUPONNUM, couponNumListener);
        ObserverManager.addObserver(ObserverManager.TICKET_LOGOUT, ticketLogoutListener);
        if (MainActivityTab.couponNum == 0)
        {
            couponNum.setVisibility(View.GONE);
        }
        else
        {
            couponNum.setVisibility(View.VISIBLE);
            couponNum.setText(MainActivityTab.couponNum + "张");
        }
        MLog.e(tag, "head:" + Config.getUser(context).head);
        UUAppCar.getInstance().display(Config.getUser(context).head, head, R.drawable.user_default);

        return view;
    }

    ObserverListener couponNumListener    = new ObserverListener()
    {
        @Override
        public void observer(String from, Object obj)
        {
            if (MainActivityTab.couponNum == 0)
            {
                couponNum.setVisibility(View.GONE);
            }
            else
            {
                couponNum.setVisibility(View.VISIBLE);
                couponNum.setText(MainActivityTab.couponNum + "张");
            }
            setTopInfo();

        }
    };
    ObserverListener logoutListener       = new ObserverListener()
    {
        @Override
        public void observer(String from, Object obj)
        {
            if (SysConfig.ISMONKEY && SysConfig.DEVELOP_MODE)
            {
                return;
            }

            if (SysConfig.ISMONKEY)
            {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("确认要退出吗？");
            builder.setNegativeButton("确认", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Config.showProgressDialog(context, false, null);
                    LoginInterface.LogoutSSL.Request.Builder request = LoginInterface.LogoutSSL.Request.newBuilder();
                    request.setTime((int) (System.currentTimeMillis() / 1000));
                    NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.LogoutSSL_VALUE);
                    task.setTag("LogoutSSL");
                    task.setBusiData(request.build().toByteArray());
                    NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                    {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData)
                        {
                            userLogonOut();
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
            });
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            });
            builder.create().show();
        }
    };
    ObserverListener ticketLogoutListener = new ObserverListener()
    {
        @Override
        public void observer(String from, Object obj)
        {
            userLogonOut();
        }
    };

    public void userLogonOut()
    {
        head.setImageResource(R.drawable.user_default);
        saveModel();
        ObserverManager.getObserver(ObserverManager.MAINLOGOUT).observer("", "");
        setTopInfo();
        Config.clearUserSecurity(context);
//        Intent closeIntent = new Intent(My.this.getActivity(), UUService.class);
//        closeIntent.putExtra(SysConfig.LONG_CONNECT, SysConfig.CLOSE_LONG_CONNECT);
//        getActivity().startService(closeIntent);
        ((BaseActivity) (getActivity())).getApp().quitLongConn();
        couponNum.setText("");
        Config.isUserCancel = false;
//        context.stopService(new Intent(context, RentingService.class));
        ((BaseActivity) getActivity()).getApp().quitRenting();
        Config.isSppedIng = false;
        //是否正在一对一或者一对多约车
        Config.isOneToOneIng = false;
        //快速约车并且已经有人同意了
        Config.speedHasAgree = false;
        /**
         * 是否有待支付订单
         */
        Config.hasPayOrder = false;
        if (MainActivityTab.instance != null)
        {
            MainActivityTab.instance.endAnimation();
            MainActivityTab.instance.speed.name.setText("我要约车");
            MainActivityTab.instance.speed.tv_num.setVisibility(View.GONE);
            MainActivityTab.instance.setPublicSetTabNum();
        }

    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto()
    {
        try
        {
            File mCurrentPhotoFile = new File(new File(Config.ImageFile), "temp_image.jpg");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, Config.IMAGE_CAMERA_RESULT);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "未找到系统相机程序", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检测该包名所对应的应用是否存在
     *
     * @param packageName
     * @return
     */
    public boolean checkPackage(String packageName)
    {
        if (packageName == null || "".equals(packageName))
        {
            return false;
        }
        try
        {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    /**
     * 分享给还友的图片
     *
     * @param file
     */
    private void shareToFriend(File file)
    {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        // intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
        startActivity(intent);
    }

    /**
     * 分享到朋友圈图片
     *
     * @param file
     */
    private void shareToTimeLine(File file)
    {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        // intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
        startActivity(intent);
    }

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

    private boolean saveAvatarModel(UserModel userModel)
    {
        service = new UserService(context);
        boolean flag = service.modifyModel(userModel);
        return flag;
    }

    private void copyShareQRCodeToSDCard()
    {
        try
        {
            InputStream is = context.getAssets().open("erweima.png");
            String path = Config.ImageFile + "share.png";
            FileOutputStream os = new FileOutputStream(path);
            byte[] buffer = new byte[1024];
            int count = 0;
            // 将静态数据库文件拷贝到目的地
            while ((count = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, count);
            }
            is.close();
            os.close();
        }
        catch (IOException e)
        {
            //
            e.printStackTrace();
        }
    }

    public void onPause()
    {
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
        setTopInfo();
//        SharedPreferences citysp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
//        city.setText(citysp.getString("city", "北京"));
        MLog.e(tag, "user  = " + Config.getUser(context).userStatus);
    }

    public void setTopInfo()
    {
        if (Config.getUser(context).sid.length() > 0)
        {
            money.setOnClickListener(layoutClick);
            feedback.setOnClickListener(layoutClick);
            share.setOnClickListener(layoutClick);
            collect.setOnClickListener(layoutClick);
            coupon.setOnClickListener(layoutClick);
            topLoginRoot.setVisibility(View.VISIBLE);
            topNoLoginRoot.setVisibility(View.GONE);
            name.setText(Config.getUser(context).name);
            MLog.e(tag, "head:" + Config.getUser(context).head);
            if (isNeedRefresh)
            {
                UUAppCar.getInstance().display(Config.getUser(context).head, head, R.drawable.user_default);
                isNeedRefresh = false;
            }
            Drawable img_off;
            MLog.e(TAG, "userStatus = " + Config.getUser(context).userStatus + "  carStatus = " + Config.getUser(context).carStatus);
            if (this.isAdded())
            {
                switch (Integer.parseInt(Config.getUser(context).userStatus))
                {
                    case 1:
                        zuke.setText("租客身份未认证");
                        name.setText(Config.getUser(context).phone);
                        img_off = getResources().getDrawable(R.drawable.und);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        zuke.setCompoundDrawables(null, null, img_off, null);
                        zuke.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(context, RenterRegisterIDActivity.class);
                                startActivityForResult(intent, 154);
                            }
                        });
                        break;
                    case 2:
                        zuke.setText("租客身份认证中");
                        name.setText("审核中");
                        img_off = getResources().getDrawable(R.drawable.ing);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        zuke.setCompoundDrawables(null, null, img_off, null);
                        zuke.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                startActivity(new Intent(context, RenterRegisterVerify.class));
                            }
                        });
                        break;
                    case 3:
                        zuke.setText("租客身份已认证");
                        name.setText(Config.getUser(context).name);
                        img_off = getResources().getDrawable(R.drawable.done);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        zuke.setCompoundDrawables(null, null, img_off, null);
                        zuke.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Config.showToast(context, "租客身份已审核成功");
                            }
                        });
                        break;
                    case 4:
                        zuke.setText("租客身份认证失败");
                        name.setText("审核中");
                        img_off = getResources().getDrawable(R.drawable.und);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        zuke.setCompoundDrawables(null, null, img_off, null);
                        zuke.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                startActivity(new Intent(context, RenterRegisterVerifyError.class));
                            }
                        });
                        break;
                }
                switch (Integer.parseInt(Config.getUser(context).carStatus))
                {
                    case 1:
                        chezhu.setText("车主身份未认证");
                        img_off = getResources().getDrawable(R.drawable.und);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        chezhu.setCompoundDrawables(null, null, img_off, null);
                        chezhu.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), AddCarBrandActivity.class);
                                getActivity().startActivityForResult(intent, SysConfig.ADD_CAR);
//                                MainActivityTab.instance.gotoOwner();
                            }
                        });
                        break;
                    case 2:
                        chezhu.setText("车主身份认证中");
                        img_off = getResources().getDrawable(R.drawable.ing);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        chezhu.setCompoundDrawables(null, null, img_off, null);
                        chezhu.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                MainActivityTab.instance.gotoOwner();
                            }
                        });
                        break;
                    case 4:
                        chezhu.setText("车主身份认证失败");
                        img_off = getResources().getDrawable(R.drawable.und);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        chezhu.setCompoundDrawables(null, null, img_off, null);
                        chezhu.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                MainActivityTab.instance.gotoOwner();
                            }
                        });
                        break;
                    case 3:
                        chezhu.setText("车主身份已认证");
                        img_off = getResources().getDrawable(R.drawable.done);
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        chezhu.setCompoundDrawables(null, null, img_off, null);
                        chezhu.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Config.showToast(context, "车主身份已审核成功");
                            }
                        });
                        break;
                }
            }
            ObserverManager.getObserver(ObserverManager.MAINLOGIN).observer("", "");
        }
        else
        {
            money.setOnClickListener(noLoginClick);
            share.setOnClickListener(noLoginClick);
            coupon.setOnClickListener(noLoginClick);
            collect.setOnClickListener(noLoginClick);
            topLoginRoot.setVisibility(View.GONE);
            topNoLoginRoot.setVisibility(View.VISIBLE);
            couponNum.setText("");
            ObserverManager.getObserver(ObserverManager.MAINLOGIN).observer("", "");
        }
    }

//    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//    {
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override public void onPrepareOptionsMenu(Menu menu)
//    {
//        super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override public boolean onOptionsItemSelected(MenuItem item)
//    {
//        return super.onOptionsItemSelected(item);
//    }

    private String getRealPathFromURI(Uri contentUri)
    {
        String path = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return path;
        }
    }


    public static boolean saveImage(Bitmap photo, String spath)
    {
        try
        {
            File mPhotoFile = new File(spath);
            if (!mPhotoFile.exists())
            {
                mPhotoFile.createNewFile();
            }
            photo = BitmapUtils.getInSampleAvatarByBitmap(photo);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false)
            );
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        MLog.e(tag, "requestCode = " + requestCode);
        MLog.e(tag, "resultCode = " + resultCode);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == Config.IMAGE_IMAGE_RESULT)
            {
                try
                {
                    ContentResolver resolver = getActivity().getContentResolver();
                    if (intent == null)
                    {
                        Toast.makeText(context, "选择图片文件出错", Toast.LENGTH_LONG).show();
                        return;
                    }
                    photoUri = intent.getData();
                    if (photoUri == null)
                    {
                        Toast.makeText(context, "选择图片文件出错", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String photoStr = getRealPathFromURI(photoUri);
                    if (photoStr == null)
                    {

                        if (photoUri.toString().contains("media"))
                        {
                            Bitmap bitmapTemp = null;

                            bitmapTemp = MediaStore.Images.Media.getBitmap(resolver, photoUri);

                            picPath = SysConfig.SD_IMAGE_PATH + "avatar.jpg";
                            saveImage(bitmapTemp, picPath);
                            bitmap = BitmapUtils.getInSampleBitmap(picPath, 80, 80);

                            if (bitmapTemp != null)
                            {
                                bitmapTemp.recycle();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "选择图片文件格式出错", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    else
                    {
                        if (photoStr.endsWith(".png") || photoStr.endsWith(".PNG") || photoStr.endsWith(".jpg")
                                || photoStr.endsWith(".JPG") || photoStr.endsWith(".jpeg") || photoStr.endsWith(".JPEG"))
                        {
                            Bitmap bitmapTemp = MediaStore.Images.Media.getBitmap(resolver, photoUri);
                            picPath = SysConfig.SD_IMAGE_PATH + "avatar.jpg";
                            saveImage(bitmapTemp, picPath);
//                bitmapTemp.recycle();
                            bitmap = BitmapUtils.getInSampleBitmap(picPath, 80, 80);

                            if (bitmapTemp != null)
                            {
                                bitmapTemp.recycle();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "选择图片文件格式出错", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                postPicByVolley(picPath);
            }
            else if (requestCode == COLLECTREQUEST)
            {
                MainActivityTab.instance.gotoFindCar();
            }
            else if (requestCode == Config.IMAGE_CAMERA_RESULT)
            {
                Bitmap bitmapTemp = null;

                if (bigPicPath != null)
                {
                    bitmapTemp = BitmapUtils.getInSampleBitmapByFile(bigPicPath);
                }
                picPath = SysConfig.SD_IMAGE_PATH + "avatar.jpg";
                saveImage(bitmapTemp, picPath);
                if (bitmapTemp != null)
                {
                    bitmapTemp.recycle();
                }
                try
                {
                    bitmap = BitmapUtils.getInSampleBitmap(picPath, 80, 80);

                }
                catch (Exception e)
                {
                    MLog.e(tag, "error = " + e.getMessage());
                }
                postPicByVolley(picPath);

            }
            else if (requestCode == 165)
            {

            }
            else
            {

                setTopInfo();
            }
        }
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public String getPath(Uri uri)
    {
        if (AbStrUtil.isEmpty(uri.getAuthority()))
        {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 发送微博
     *
     * @param file
     */
    public void ShareNeedIntent(Context context, File file)
    {
        ComponentName comp = new ComponentName("com.sina.weibo", "com.sina.weibo.EditActivity");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(comp);
        context.startActivity(intent);
    }


    private void postPicByVolley(final String pic)
    {
        if (Config.isNetworkConnected(getActivity()))
        {
            Config.showProgressDialog(context, false, null);
            UUParams params = new UUParams();
            params.put("avatar", new File(pic));
            NetworkTask networkTask = new NetworkTask(params);
            networkTask.setTag("TAG");
            networkTask.setHttpUrl(NetworkTask.PHOTO_UPLOAD_HTTP + "?public=1");
            NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<JSONObject>()
            {
                @Override
                public void onSuccessResponse(JSONObject json)
                {

                    MLog.e(TAG, "json:" + json.toString());
                    try
                    {
                        int ret = json.getInt("ret");
                        if (ret == 0)
                        {
                            String responseData = json.getString("sessionKey");
                            if (responseData != null)
                            {
                                if (responseData.equals("-1"))
                                {
                                    Config.dismissProgress();
                                    //表示 照片上传失败
                                    Config.showToast(context, "照片上传失败，请重新上传！");
                                }
                                else
                                {
                                    NetworkTask photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadUserAvatar_VALUE);
                                    UserInterface.UploadUserAvatar.Request.Builder idCard = UserInterface.UploadUserAvatar.Request.newBuilder();
                                    idCard.setIdCode(responseData);

                                    photoTask.setBusiData(idCard.build().toByteArray());
                                    photoTask.setTag("UploadUserAvatar");
                                    NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>()
                                    {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData)
                                        {

                                            if (responseData.getRet() == 0)
                                            {
                                                try
                                                {
                                                    UserInterface.UploadUserAvatar.Response response = UserInterface.UploadUserAvatar.Response.parseFrom(responseData.getBusiData());
                                                    if (response.getRet() == 0)
                                                    {
                                                        head.setImageBitmap(bitmap);
                                                        Toast.makeText(getActivity(), "头像上传成功！", Toast.LENGTH_SHORT).show();
                                                        if (response.getAvatar() != null && !response.getAvatar().equals(""))
                                                        {
                                                            UserModel userModel = getModel();
                                                            userModel.head = response.getAvatar();
                                                            saveAvatarModel(userModel);
//                                                        UUAppCar.getInstance().display(response.getAvatar(), head, R.drawable.user_default);
                                                        }
                                                    }
                                                    else if (response.getRet() == -2)
                                                    {
                                                        Toast.makeText(getActivity(), "头像失败，请重新上传！", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if (response.getRet() == -1)
                                                    {
                                                        Toast.makeText(getActivity(), "头像上传失败！", Toast.LENGTH_SHORT).show();
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

                                        }

                                        @Override
                                        public void networkFinish()
                                        {
                                            Config.dismissProgress();
                                        }
                                    });
                                }
                            }
                        }
                        else
                        {
                            Config.dismissProgress();
                            //表示 照片上传失败
                            Config.showToast(context, "照片上传失败，请重新上传！");

                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError errorResponse)
                {
                    Config.dismissProgress();
                }

                @Override
                public void networkFinish()
                {

                }
            });
        }
        else
        {
            Toast.makeText(getActivity(), SysConfig.NETWORK_PHOTO_FAIL, Toast.LENGTH_SHORT).show();
        }
    }


    public UserModel getModel()
    {
        UserModel userModel = null;
        service = new UserService(getActivity());
        List<UserModel> models = service.getModelList(UserModel.class);
        if (models.size() == 0)
        {
//            model = new UserModel();
//            model.phone = "";
//            boolean flag = service.insModel(model);
//            if (flag) {
//                model = service.getModel(UserModel.class, new String[]{s_phone});
//            }
        }
        else
        {
            userModel = models.get(0);
        }
        return userModel;
    }

}
