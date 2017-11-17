package com.youyou.uucar.UI.Renter.carinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.view.sliding.AbSlidingPlayView;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.timessquare.CalendarPickerView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UI.Main.rent.OneToOneWaitActivity;
import com.youyou.uucar.UI.Main.rent.SelectTime;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Owner.addcar.OwnerInfoActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UI.Renter.filter.FilteredCarListActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.DisplayUtil;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.ImageView.CircleImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.ExpandableTextView;
import com.youyou.uucar.Utils.View.ObservableScrollView;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.uu.client.bean.car.common.CarCommon.CarDetailInfo;
import static com.youyou.uucar.R.drawable.car_img_def_big;

//import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterOrderConfirmActivity;
public class OldCarInfoActivity extends BaseActivity
{
    public static final String      DESCRIPTOR    = "com.umeng.share";
    public final        String      APP_ID        = "wx9abfa08f7da32b30";
    private final       SHARE_MEDIA mTestMedia    = SHARE_MEDIA.SINA;
    private final       SHARE_MEDIA TENCENT_MEDIA = SHARE_MEDIA.TENCENT;
    public              String      tag           = "OldCarInfoActivity";
    /**
     * car_sn;
     */
    public              String      sn            = "";
    public              Activity    context       = this;
    public IWXAPI api;
    public String address;
    public final int REFUSH = 1;

    @OnClick(R.id.releasecar)
    public void rentClick()
    {
        if (!Config.isNetworkConnected(this))
        {
            Config.showFiledToast(context);
            return;
        }
        if (Config.isGuest(context))
        {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        }
        else if (info != null && info.getUserId() == UserSecurityConfig.userId_ticket)
        {
            Toast.makeText(OldCarInfoActivity.this, "您已经是本车车主，无法预约本车！", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW))
        {
            Intent intent = new Intent(context, RenterRegisterIDActivity.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        }
        else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL))
        {
            Intent intent = new Intent(context, RenterRegisterVerify.class);
            startActivity(intent);
        }
        else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO))
        {
            Intent intent = new Intent(context, RenterRegisterVerifyError.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        }
        //快速约车车主响应列表页面进来的,如果再点预约就直接下单了哟
        else if (getIntent().hasExtra("speed") && getIntent().getBooleanExtra("speed", false))
        {
            showProgress(false);
            OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
            request.setPreOrderId(getIntent().getStringExtra(SysConfig.R_SN));
            request.setAgree(true);
            NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
            networkTask.setTag("ConfirmPreOrder");
            networkTask.setBusiData(request.build().toByteArray());
            NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
            {
                @Override
                public void onSuccessResponse(UUResponseData responseData)
                {
                    showToast(responseData.getToastMsg());
                    if (responseData.getRet() == 0)
                    {
                        try
                        {


                            OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());
                            boolean msgFlag = response.hasMsg();
                            String msg = "";
//                            if (msgFlag)
//                            {
//                                Config.showToast(context, response.getMsg());
//                            }


                            if (response.getRet() == 0 || response.getRet() == 1)
                            {
                                Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                                intent.putExtra(SysConfig.R_SN, response.getOrderId());
                                startActivity(intent);
                                setResult(RESULT_OK);
                                finish();
                            }
                            else if (response.getRet() == -1)
                            {
                                if (!msgFlag)
                                {
                                    Config.showFiledToast(context);
                                }
                                else
                                {
                                    showToast(response.getMsg());
                                }
                            }
                            else if (response.getRet() == -2)
                            {
                                if (!msgFlag)
                                {
                                    Config.showFiledToast(context);
                                }
                                else
                                {
                                    showToast(response.getMsg());
                                }
                            }
                            else if (response.getRet() == -3)
                            {
                                Dialog dialog;
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                if (msg == null || msg.length() == 0 || msg.equals("null"))
                                {
                                    msg = "订单冲突";
                                }
                                builder1.setMessage(msg);
                                builder1.setNegativeButton("知道了", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                                dialog = builder1.create();
                                dialog.show();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);

                            }
                            else if (response.getRet() == -4)
                            {
                                if (!msgFlag)
                                {
                                    if (msg == null || msg.length() == 0 || msg.equals("null"))
                                    {
                                        msg = "您有待支付订单";
                                    }
                                    showToast(msg);
                                }
                                else
                                {
                                    showToast(response.getMsg());
                                }
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
                    dismissProgress();
                }
            });
        }
        else
        {

            if (Config.isSppedIng)//快速约车中
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("您的快速约车请求已发送给多位车主,预约此车将中断之前的请求,是否继续预约此车");
                builder.setNegativeButton("返回", null);
                builder.setNeutralButton("预约此车", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (getIntent().hasExtra("start") && getIntent().hasExtra("end"))//如果选择过开始时间和结束时间
                        {
                            showProgress(false);
                            OrderFormInterface26.RenterStartPreOrder.Request.Builder request = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
                            request.addCarIds(sn);
                            request.setStartTime(Integer.parseInt(getIntent().getExtras().get("start").toString()));
                            request.setEndTime(Integer.parseInt(getIntent().getExtras().get("end").toString()));

                            request.setCancelLastPreOrder(true);
                            NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterStartPreOrder_VALUE);
                            task.setBusiData(request.build().toByteArray());
                            task.setTag("RenterStartPreOrder");
                            NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                            {
                                @Override
                                public void onSuccessResponse(UUResponseData responseData)
                                {
                                    if (responseData.getRet() == 0)
                                    {
                                        try
                                        {
                                            OrderFormInterface26.RenterStartPreOrder.Response response = OrderFormInterface26.RenterStartPreOrder.Response.parseFrom(responseData.getBusiData());
                                            if (response.getRet() == 0)
                                            {
                                                Config.isSppedIng = false;
//                                                context.stopService(new Intent(context, RentingService.class));
                                                getApp().quitRenting();
                                                Config.isOneToOneIng = true;
                                                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                                startActivityForResult(intent, REFUSH);
                                                setResult(RESULT_OK);
                                                finish();
                                                RentingType = RENTING_ONE;
                                                FilteredCarListActivity.isNeedRefresh = true;
                                            } /*else if (response.getRet() == -2) {
                                                rentClick();
                                            } else if (response.getRet() == -1) {
                                                showToast("失败,请重试");
                                            } else if (response.getRet() == -4) {
                                                showToast("您有未支付的订单");
                                            } else {
                                                Config.showFiledToast(context);
                                            }*/
//                                            else
//                                            {
//                                                if (response.getTipsType() == 0)//toast
//                                                {
//                                                    showToast(response.getMsg());
//                                                }
//                                                else
//                                                {
//                                                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
//                                                    msgBuilder.setMessage(response.getMsg());
//                                                    msgBuilder.setNeutralButton("知道了", null);
//                                                    msgBuilder.create().show();
//                                                }
//                                            }
                                            if (response.hasMsg() && response.getMsg().length() > 0)
                                            {
                                                if (response.getTipsType() == 0)//toast
                                                {
                                                    showToast(response.getMsg());
                                                }
                                                else
                                                {
                                                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
                                                    msgBuilder.setMessage(response.getMsg());
                                                    msgBuilder.setNeutralButton("知道了", null);
                                                    msgBuilder.create().show();
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
                                        }
                                    }
//                                    else
//                                    {
//                                        Config.showFiledToast(context);
//                                    }
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
                        else//如果没选过时间,进入时间选择页面
                        {
                            showProgress(false);
                            OrderFormInterface26.RenterCancelPreOrder.Request.Builder request = OrderFormInterface26.RenterCancelPreOrder.Request.newBuilder();
                            NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelPreOrder_VALUE);
                            task.setBusiData(request.build().toByteArray());
                            task.setTag("RenterCancelPreOrder");
                            NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                            {
                                @Override
                                public void onSuccessResponse(UUResponseData responseData)
                                {
                                    if (responseData.getRet() == 0)
                                    {
                                        try
                                        {
                                            OrderFormInterface26.RenterCancelPreOrder.Response response = OrderFormInterface26.RenterCancelPreOrder.Response.parseFrom(responseData.getBusiData());
                                            if (response.getRet() == 0)
                                            {
                                                Config.isSppedIng = false;
//                                                context.stopService(new Intent(context, RentingService.class));

                                                getApp().quitRenting();
                                                Config.isOneToOneIng = false;
                                                Intent intent = new Intent(context, SelectTime.class);
                                                intent.putExtra("displayPosition", displayPosition);
                                                Config.carDisableTime = cartime;
                                                Config.carBanTime = banTime;
                                                intent.putExtra("fromCarInfo", true);
                                                intent.putExtra(SysConfig.CAR_SN, sn);
                                                intent.putExtra("CAR_NAME", carName);
                                                intent.putExtra("BEIZHU", beizhu);
                                                startActivityForResult(intent, REFUSH);

                                            }
                                            else
                                            {
                                                showToast("取消快速预约失败");
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
                                    showToast("取消快速预约失败");

                                }

                                @Override
                                public void networkFinish()
                                {
                                    dismissProgress();
                                }
                            });


                        }

                    }
                });
                builder.create().show();
            }
            else if (RentingType == RENTING_ONE)
            {
                if (getIntent().hasExtra("from") && getIntent().getStringExtra("from").equals("onetoone"))
                {
//                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                startActivityForResult(intent, REFUSH);
                FilteredCarListActivity.isNeedRefresh = true;
            }
            else if (getIntent().hasExtra("start") && getIntent().hasExtra("end"))//如果选择过开始时间和结束时间
            {

                Intent intent = new Intent(context, SelectTime.class);
                Config.carDisableTime = cartime;
                intent.putExtra("displayPosition", displayPosition);
                Config.carBanTime = banTime;
                intent.putExtra("fromCarInfo", true);
                intent.putExtra("ban_time", info.getCarLimitUseDesc());
                String car_sn = sn;
                intent.putExtra(SysConfig.CAR_SN, car_sn);
                intent.putExtra("CAR_NAME", carName);
                intent.putExtra("BEIZHU", beizhu);
                intent.putExtra("start", getIntent().getStringExtra("start"));
                intent.putExtra("end", getIntent().getStringExtra("end"));
                startActivityForResult(intent, REFUSH);


//                showProgress(false);
//                OrderFormInterface26.RenterStartPreOrder.Request.Builder request = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
//                request.addCarIds(sn);
//                long startTime = Long.parseLong(getIntent().getStringExtra("start"));
//                long endTime = Long.parseLong(getIntent().getStringExtra("end"));
//                request.setStartTime((int) (startTime));
//                request.setEndTime((int) (endTime));
//                request.setCancelLastPreOrder(false);
//                NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterStartPreOrder_VALUE);
//                task.setBusiData(request.build().toByteArray());
//                task.setTag("RenterStartPreOrder");
//                NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
//                    @Override
//                    public void onSuccessResponse(UUResponseData responseData) {
//                        if (responseData.getRet() == 0) {
//                            try {
//                                OrderFormInterface26.RenterStartPreOrder.Response response = OrderFormInterface26.RenterStartPreOrder.Response.parseFrom(responseData.getBusiData());
//                                if (response.getRet() == 0) {
//                                    Intent intent = new Intent(context, OneToOneWaitActivity.class);
//                                    startActivityForResult(intent, REFUSH);
//                                    FilteredCarListActivity.isNeedRefresh = true;
//                                }
//                                if (response.getRet() == -2) {
//                                    Config.isSppedIng = true;
//                                    Config.isUserCancel = false;
//                                    getApp().startRenting();
//                                    rentClick();
//                                }
//                                if (responseData.getResponseCommonMsg().getMsg().length() > 0) {
////                                    if (responseData.getResponseCommonMsg().getTipsType() == 0)//toast
////                                    {
////                                        showToast(response.getMsg());
////                                    } else {
////                                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
////                                        msgBuilder.setMessage(response.getMsg());
////                                        msgBuilder.setNeutralButton("知道了", null);
////                                        msgBuilder.create().show();
////                                    }
//
//                                    showToast( responseData.getResponseCommonMsg().getMsg());
//
//
//                                }
//                            } catch (InvalidProtocolBufferException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(VolleyError errorResponse) {
//                        Config.showFiledToast(context);
//                    }
//
//                    @Override
//                    public void networkFinish() {
//                        dismissProgress();
//                    }
//                });
            }
            else if (getIntent().hasExtra(SysConfig.R_SN))
            {
                showProgress(false);
                OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
                request.setPreOrderId(getIntent().getStringExtra(SysConfig.R_SN));
                request.setAgree(true);
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
                networkTask.setTag("ConfirmPreOrder");
                networkTask.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
                {
                    @Override
                    public void onSuccessResponse(UUResponseData responseData)
                    {
                        showToast(responseData.getToastMsg());
                        if (responseData.getRet() == 0)
                        {
                            try
                            {
                                OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());
                                if (response.getRet() == 0 || response.getRet() == 1)
                                {
                                    Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                                    intent.putExtra(SysConfig.R_SN, response.getOrderId());
                                    startActivity(intent);
                                    setResult(RESULT_OK);
                                    Config.isOneToOneIng = false;
                                    Config.isSppedIng = false;
//                                    context.stopService(new Intent(context, RentingService.class));
                                    getApp().quitRenting();
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

                    }

                    @Override
                    public void networkFinish()
                    {
                        dismissProgress();
                    }
                });
            }
            else
            {
//                Config.getCoordinates(context, new LocationListener() {
//                    @Override
//                    public void locationSuccess(double lat, double lng, String addr) {
                Intent intent = new Intent(context, SelectTime.class);
                intent.putExtra("displayPosition", displayPosition);
//                        intent.putExtra("address", address);
                Config.carDisableTime = cartime;
                Config.carBanTime = banTime;
//                        intent.putExtra("lat", lat);
                intent.putExtra("fromCarInfo", true);
                intent.putExtra("ban_time", info.getCarLimitUseDesc());
//                        intent.putExtra("lng", lng);
                String car_sn = sn;
                intent.putExtra(SysConfig.CAR_SN, car_sn);
                intent.putExtra("CAR_NAME", carName);
                intent.putExtra("BEIZHU", beizhu);
                startActivityForResult(intent, REFUSH);
//                    }
//                });
            }
        }
    }

    LinearLayout       property;
    ExpandableTextView desc;
    ExpandableTextView expandableTextView;
    TextView           xinghao;
    TextView           price_hour, price_day, price_week;
    TextView       address_1;
    RelativeLayout address_1_root;
    AbSlidingPlayView mSlidingPlayView = null;
    TextView[]        weeks            = new TextView[7];
    TextView[]        days             = new TextView[7];
    TextView       name;
    //    ImageView    head;
    RatingBar      rating;
    LinearLayout   review_root;
    TextView       noreview;
    TextView       all;
    TextView       rent;
    ImageView      collect;
    LinearLayout   desc_root;
    LinearLayout   owner_root;
    RelativeLayout bottom_root;
    Dialog         shareDialog;
    // sdk controller
    private UMSocialService mController = null;

    public void reqToWx()
    {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    @InjectView(R.id.tokefu)
    TextView kefu;

    @OnClick(R.id.tokefu)
    public void kefuClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("拨打客服电话");
        builder.setMessage(Config.kefuphone);
        builder.setNegativeButton("拨打", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String inputStr = Config.kefuphone;
                if (inputStr.trim().length() != 0)
                {
                    MobclickAgent.onEvent(context, "ContactService");
                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                    startActivity(phoneIntent);
                }
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

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    @OnClick(R.id.yclc)
    public void wenhaoClick()
    {
        if (!Config.isNetworkConnected(context))
        {
            Config.showFiledToast(context);
            return;
        }
        Intent intent = new Intent(context, URLWebView.class);
        intent.putExtra("url", ServerMutualConfig.yclc);
        intent.putExtra(SysConfig.TITLE, "用车流程");
        context.startActivity(intent);
    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAllFramelayout.noDataReloading();
                getCarDetail();
            }
        });
    }

    @InjectView(R.id.jieshou)
    TextView jieshou;
    @InjectView(R.id.time)
    TextView time;

    private RelativeLayout dialogView;
    private AlertDialog    theDialog;
    CalendarPickerView calendarView;

    @InjectView(R.id.day_root)
    LinearLayout dayRoot;


    @OnClick(R.id.day_root)
    public void timeRootClick()
    {
        if (cartime == null)
        {
            return;
        }
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DATE, cartime.getShowDays());
        final Calendar lastYear = Calendar
                .getInstance();
        dialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.time_square_dialog, null);
        calendarView = (CalendarPickerView) dialogView.findViewById(R.id.calendar_view);
        calendarView.init(lastYear.getTime(), nextYear.getTime(), cartime).withSelectedDate(new Date()).displayOnly();
        TextView banText = (TextView) dialogView.findViewById(R.id.ban_text);
        banText.setText(info.getCarLimitUseDesc());
        if (theDialog != null && theDialog.isShowing())
        {
            theDialog.dismiss();
        }
        theDialog =
                new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .setNeutralButton("关闭", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                MLog.d("StartActivity", "onShow: fix the dimens!");
                calendarView.fixDialogDimens();
            }
        });
        theDialog.show();

    }

    @InjectView(R.id.ban_day)
    TextView banDay;

    @InjectView(R.id.highlight_text)
    TextView       mHighLightText;
    @InjectView(R.id.service_time_root)
    RelativeLayout service_time_root;
    @InjectView(R.id.scroll)
    ObservableScrollView mScroll;
    @InjectView(R.id.image_root)
    RelativeLayout imageRoot;
    private void setComponentsStatus(View scrollView, RelativeLayout frameLayout) {
        int scrollY = scrollView.getScrollY();
        if (frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.setTranslationY(-scrollY / 2);
        }
//        scrollView.setPadding(0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        setContentView(R.layout.activity_old_car_info);
//        share = (TextView) findViewById(R.id.share);
//        share.setOnClickListener(shareClick);
        ButterKnife.inject(this);
        initNoteDataRefush();
        reqToWx();
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageRoot.getLayoutParams();
//        if (layoutParams == null) {
//            layoutParams = new FrameLayout.LayoutParams(0, 0);
//        }
//        layoutParams.width = DisplayUtil.getScreenWidth(this);
//        layoutParams.height = DisplayUtil.getScreenWidth(this) * imageHigth / imageWidth;
//        imageRoot.setLayoutParams(layoutParams);
        mScroll.setScrollViewListener(new ObservableScrollView.OnScrollListener() {
            @Override public void onScroll(int scrollY) {
                setComponentsStatus(mScroll, imageRoot);

            }

            @Override public void onStartScroll() {
            }

            @Override public void onEndScroll() {
            }

            @Override public void onScrollChanged(View view, int x, int y, int oldx, int oldy) {

            }
        });
        setComponentsStatus(mScroll, imageRoot);
//        kefu.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        bottom_root = (RelativeLayout) findViewById(R.id.bottom_root);
        owner_root = (LinearLayout) findViewById(R.id.owner_root);
        owner_root.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Intent intent = new Intent(context, UserInfo.class);
//                intent.putExtra("type", UserInfo.TYPE_OWNER);
//                intent.putExtra("car_sn", sn);
//                startActivity(intent);
            }
        });
        desc_root = (LinearLayout) findViewById(R.id.desc_root);
        collect = (ImageView) findViewById(R.id.collect);
        collect.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (info != null)
                {
                    if (!info.getIfCollected())
                    {
                        if (Config.isGuest(context))
                        {
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivityForResult(intent, SysConfig.COLLET_TO_PHONE);
                            return;
                        }
                        showProgress(false);
                        UserInterface.CollectCar.Request.Builder builder = UserInterface.CollectCar.Request.newBuilder();
                        builder.setCarId(info.getCarId());
                        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.CollectCar_VALUE);
                        task.setBusiData(builder.build().toByteArray());
                        task.setTag("CollectCar");
                        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                        {
                            @Override
                            public void onSuccessResponse(UUResponseData responseData)
                            {
                                showToast(responseData.getToastMsg());
                                if (responseData.getRet() == 0)
                                {
                                    try
                                    {
                                        UserInterface.CollectCar.Response response = UserInterface.CollectCar.Response.parseFrom(responseData.getBusiData());
                                        if (response.getRet() == 0)
                                        {
                                            showToast("收藏成功");
                                            MobclickAgent.onEvent(context, "Collect");
                                            CarDetailInfo.Builder carDetailInfoBuilder = CarDetailInfo.newBuilder(info);
                                            carDetailInfoBuilder.setIfCollected(true);
                                            info = carDetailInfoBuilder.build();
                                            collect.setBackgroundResource(R.drawable.new_car_info_collect);
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
                                dismissProgress();
                            }
                        });
                    }
                    else
                    {

                        showProgress(false);
                        UserInterface.CancelCollectCar.Request.Builder builder = UserInterface.CancelCollectCar.Request.newBuilder();
                        builder.setCarId(info.getCarId());
                        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.CancelCollectCar_VALUE);
                        task.setBusiData(builder.build().toByteArray());
                        task.setTag("CancelCollectCar");
                        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                        {
                            @Override
                            public void onSuccessResponse(UUResponseData responseData)
                            {
                                showToast(responseData.getToastMsg());
                                if (responseData.getRet() == 0)
                                {
                                    try
                                    {
                                        UserInterface.CancelCollectCar.Response response = UserInterface.CancelCollectCar.Response.parseFrom(responseData.getBusiData());
                                        if (response.getRet() == 0)
                                        {

                                            showToast("已取消收藏");
                                            MobclickAgent.onEvent(context, "CancelCollect");
                                            CarDetailInfo.Builder carDetailInfoBuilder = CarDetailInfo.newBuilder(info);
                                            carDetailInfoBuilder.setIfCollected(false);
                                            info = carDetailInfoBuilder.build();
                                            collect.setBackgroundResource(R.drawable.new_car_info_collect_no);
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
                                dismissProgress();
                            }
                        });
                    }
                }
            }
        });
//
        rent = (TextView) findViewById(R.id.releasecar);
        all = (TextView) findViewById(R.id.all);
        noreview = (TextView) findViewById(R.id.noreview);
        review_root = (LinearLayout) findViewById(R.id.review_root);
        name = (TextView) findViewById(R.id.name);
//        head = (ImageView) findViewById(R.id.user_head);
        rating = (RatingBar) findViewById(R.id.rating);
        price_hour = (TextView) findViewById(R.id.price_hour);
        price_day = (TextView) findViewById(R.id.price_day);
        price_week = (TextView) findViewById(R.id.price_week);
        desc = (ExpandableTextView) findViewById(R.id.expand_text_view);
        expandableTextView = (ExpandableTextView) findViewById(R.id.expand_text_view);

//        xinghao = (TextView) findViewById(R.id.xinghao);
        sn = getIntent().getStringExtra(SysConfig.CAR_SN);
        property = (LinearLayout) findViewById(R.id.property);
        address_1 = (TextView) findViewById(R.id.address_1);
        address_1_root = (RelativeLayout) findViewById(R.id.address_1_root);
        address_1_root.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                if (info != null)
                {
                    if (info.hasPosition())
                    {
                        intent.putExtra("end_lat", info.getPosition().getLat());
                        intent.putExtra("end_lng", info.getPosition().getLng());
                    }
                    if (info.hasTransmissionType())
                    {

                        if (info.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO))
                        {
                            intent.putExtra("type", "auto");
                        }
                        else
                        {
                            intent.putExtra("type", "hand");
                        }
                    }
                }
                intent.setClass(context, CheckRouteActivity.class);
                startActivity(intent);
            }
        });
        mSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);
        mSlidingPlayView.setPageLineHorizontalGravity(Gravity.CENTER);
        mSlidingPlayView.displayImage = BitmapFactory.decodeResource(getResources(), R.drawable.play_img_dis);
        mSlidingPlayView.hideImage = BitmapFactory.decodeResource(getResources(), R.drawable.play_img_nodis);
        weeks[0] = (TextView) findViewById(R.id.week1);
        weeks[1] = (TextView) findViewById(R.id.week2);
        weeks[2] = (TextView) findViewById(R.id.week3);
        weeks[3] = (TextView) findViewById(R.id.week4);
        weeks[4] = (TextView) findViewById(R.id.week5);
        weeks[5] = (TextView) findViewById(R.id.week6);
        weeks[6] = (TextView) findViewById(R.id.week7);
        days[0] = (TextView) findViewById(R.id.day1);
        days[1] = (TextView) findViewById(R.id.day2);
        days[2] = (TextView) findViewById(R.id.day3);
        days[3] = (TextView) findViewById(R.id.day4);
        days[4] = (TextView) findViewById(R.id.day5);
        days[5] = (TextView) findViewById(R.id.day6);
        days[6] = (TextView) findViewById(R.id.day7);
        if (getIntent().getBooleanExtra("hide", false))
        {
            bottom_root.setVisibility(View.GONE);
        }
        getCarDetail();
        mController = UMServiceFactory.getUMSocialService(DESCRIPTOR, RequestType.SOCIAL);
        // 添加新浪和QQ空间的SSO授权支持
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.car_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items)
    {
        if (items.getItemId() == android.R.id.home || items.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (items.getItemId() == R.id.action_share)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = context.getLayoutInflater().inflate(R.layout.share_pop, null);
            LinearLayout weixin = (LinearLayout) view.findViewById(R.id.wx);
            LinearLayout friend = (LinearLayout) view.findViewById(R.id.friend);
            LinearLayout weibo = (LinearLayout) view.findViewById(R.id.wb);
            LinearLayout tx_weibo = (LinearLayout) view.findViewById(R.id.txwb);
            tx_weibo.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mController.setShareContent(info.getShareWord());
                    mController.setShareImage(new UMImage(context, R.drawable.get_friend_icon));
                    mController.postShare(context, TENCENT_MEDIA, new SocializeListeners.SnsPostListener()
                    {
                        @Override
                        public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2)
                        {
                            Toast.makeText(context, "分享完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onStart()
                        {
                            Toast.makeText(context, "开始分享", Toast.LENGTH_SHORT).show();
                        }
                    });
                    shareDialog.dismiss();
                }
            });
            weixin.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if (Config.isAvilible(context, "com.tencent.mm"))
                    {

                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = info.getShareWord().substring(info.getShareWord().indexOf("http:"));
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = "友友私家车共享平台";
                        msg.description = info.getShareWord().substring(0, info.getShareWord().indexOf("http:"));
                        msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        api.sendReq(req);
                    }
                    shareDialog.dismiss();
                }
            });
            friend.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if (Config.isAvilible(context, "com.tencent.mm"))
                    {

                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = info.getShareWord().substring(info.getShareWord().indexOf("http:"));
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = info.getShareWord().substring(0, info.getShareWord().indexOf("http:"));
                        msg.description = info.getShareWord().substring(0, info.getShareWord().indexOf("http:"));
                        msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        api.sendReq(req);
                    }

                    shareDialog.dismiss();
                }
            });
            weibo.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mController.setShareContent(info.getShareWord());
                    mController.setShareImage(new UMImage(context, R.drawable.get_friend_icon));
                    mController.postShare(context, mTestMedia, new SocializeListeners.SnsPostListener()
                    {
                        @Override
                        public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2)
                        {
                            Toast.makeText(context, "分享完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onStart()
                        {
                            Toast.makeText(context, "开始分享", Toast.LENGTH_SHORT).show();
                        }
                    });
                    shareDialog.dismiss();
                }
            });
            TextView cancel = (TextView) view.findViewById(R.id.cancel);
            cancel.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    shareDialog.dismiss();
                }
            });
            builder.setView(view);

            shareDialog = builder.create();
            shareDialog.show();
        }
        return super.onOptionsItemSelected(items);
    }

    CarDetailInfo info;
    public final int RENTING_FAST = 2;
    public final int RENTING_ONE  = 1;
    public final int NORENTING    = 0;
    public       int RentingType  = NORENTING;

    @Override
    public void onResume()
    {
        super.onResume();
//        if (RentingType == RENTING_ONE)//如果正在点对点约车
//        {
//            rent.setText("约车中");
//        } else {
//            rent.setText(R.string.book_car);
//        }

//        getCarDetail();
    }


    CarCommon.CarSelectRentTime banTime;
    CarCommon.CarSelectRentTime cartime;

    private String carName;
    private String beizhu;

    @InjectView(R.id.rent_time_text)
    TextView       rentTimeText;
    @InjectView(R.id.service_time)
    TextView       serviceTime;
    @InjectView(R.id.miss_root)
    RelativeLayout missRoot;
    @InjectView(R.id.miss_tip)
    TextView       miss_tip;

    //进入时间选择的时候是否选择取车地址// 0 = 是, 1 = 否
    int displayPosition = 0;

    public void getCarDetail()
    {

        CarInterface.GetCarDetailInfo.Request.Builder builder = CarInterface.GetCarDetailInfo.Request.newBuilder();
        builder.setCarId(sn);
        if (getIntent().hasExtra("passedMsg"))
        {
            builder.setPassedMsg(getIntent().getStringExtra("passedMsg"));
        }
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.GetCarDetailInfo_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("GetCarDetailInfo");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                if (responseData.getRet() == 0)
                {
                    try
                    {
                        CarInterface.GetCarDetailInfo.Response response = CarInterface.GetCarDetailInfo.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0)
                        {
                            info = response.getCarDetailInfo();
                            displayPosition = info.getDisplayPosition();
                            final int carUserId = info.getUserId();
                            UuCommon.LatlngPosition latlng = info.getPosition();
                            String dessc = "暂无";

                            if (info.hasColoredCarOpDesc())
                            {
                                dessc = "";
                                if (info.getColoredCarOpDesc().hasText())
                                {
                                    mHighLightText.setVisibility(View.VISIBLE);
                                    mHighLightText.setText(info.getColoredCarOpDesc().getText());
                                }
                                else
                                {
                                    mHighLightText.setVisibility(View.GONE);
                                }
                                if (info.getColoredCarOpDesc().hasTextHexColor())
                                {
                                    mHighLightText.setTextColor(Color.parseColor(info.getColoredCarOpDesc().getTextHexColor()));
                                }
                            }
                            else
                            {
                                mHighLightText.setVisibility(View.GONE);
                            }
                            if (info.getCarDesc().trim().length() != 0)
                            {
                                dessc = info.getCarDesc().trim();
                            }
                            desc.setText(dessc);

                            if (info.hasCarCanntRentDesc())
                            {
                                missRoot.setVisibility(View.VISIBLE);
                                miss_tip.setVisibility(View.VISIBLE);
                                miss_tip.setText(info.getCarCanntRentDesc());
                                findViewById(R.id.bottom_root).setVisibility(View.GONE);
                            }
                            else
                            {
                                missRoot.setVisibility(View.GONE);
                                findViewById(R.id.bottom_root).setVisibility(View.VISIBLE);
                            }
                            if (info.hasCarStatus())
                            {
                                if (info.getCarStatus().equals(CarCommon.CarStatus.SUSPEND_RENT))
                                {
                                    findViewById(R.id.bottom_root).setVisibility(View.GONE);
                                }
                            }
                            if (info.hasCarOwnerSetRentLimitDesc())
                            {
                                rentTimeText.setText(info.getCarOwnerSetRentLimitDesc());
                            }
                            else
                            {
                                rentTimeText.setVisibility(View.GONE);
                            }
                            if (info.hasOpDesc1())
                            {
                                if (info.getOpDesc1().hasText())
                                {
                                    serviceTime.setVisibility(View.VISIBLE);
                                    service_time_root.setVisibility(View.VISIBLE);
                                    serviceTime.setText(info.getOpDesc1().getText());
                                }
                                else
                                {
                                    serviceTime.setVisibility(View.GONE);
                                    service_time_root.setVisibility(View.GONE);
                                }
                                if (info.getOpDesc1().hasTextHexColor())
                                {
                                    serviceTime.setTextColor(Color.parseColor(info.getOpDesc1().getTextHexColor()));
                                }
                            }
                            else
                            {
                                serviceTime.setVisibility(View.GONE);
                                service_time_root.setVisibility(View.GONE);
                            }
                            carName = info.getBrand() + info.getCarModel();
                            setTitle(carName);
                            price_hour.setText((int) info.getPriceByHour() + "");
                            price_day.setText("￥" + (int) info.getPriceByDay() + "");
                            price_week.setText((int) info.getPriceByWeek() + "");
                            address_1.setText(info.getAddress());
                            if (info.hasColoredAddress())
                            {
                                if (info.getColoredAddress().hasText())
                                {
                                    address_1.setText(info.getColoredAddress().getText());
                                }
                                if (info.getColoredAddress().hasTextHexColor())
                                {
                                    address_1.setTextColor(Color.parseColor(info.getColoredAddress().getTextHexColor()));
                                }
                            }
                            name.setText(info.getCarOwnerName());
                            rating.setRating(info.getCarOwnerStars());

                            List<CarCommon.CarImg> carimgs = info.getCarImgsList();
                            mSlidingPlayView.removeAllViews();
                            for (int i = 0; i < carimgs.size(); i++)
                            {
                                BaseNetworkImageView img = new BaseNetworkImageView(context);
                                String imgUrl = info.getCarImgUrlPrefix() + carimgs.get(i).getImgThumb();
                                UUAppCar.getInstance().display(imgUrl, img, car_img_def_big);
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                mSlidingPlayView.addView(img);
                            }
                            RentingType = info.getRentingType();
                            if (RentingType == RENTING_ONE)//如果正在点对点约车
                            {
                                rent.setText("约车中");
                            }
                            else if (info.getUserId() == UserSecurityConfig.userId_ticket)
                            {
                                rent.setText("您是车主");
                                rent.setEnabled(false);
                            }
                            else if (RentingType == RENTING_FAST)
                            {
                                rent.setText("租这辆车");
                            }
                            else
                            {
                                rent.setText(R.string.book_car);
                            }
                            List<CarCommon.CarProperties> propertys = info.getCarPropertiesList();

//                            if (info.getAcceptOrderPercent() == 0 && info.getAverageResponseTimes() == 0)
//                            {
//                                jieshou.setText("新车主,爱车尚未出租过");
//                                time.setVisibility(View.GONE);
//                            }
//                            else
//                            {
//
//                                jieshou.setText("订单接受率:" + info.getAcceptOrderPercent() + "%");
//                                String times = "";
//                                if (info.getAverageResponseTimes() < 60)
//                                {
//                                    times = info.getAverageResponseTimes() + "秒";
//                                }
//                                else
//                                {
//                                    times = info.getAverageResponseTimes() / 60 + "分" + info.getAverageResponseTimes() % 60 + "秒";
//                                }
//                                time.setText("平均响应时间:" + times);
//                            }
                            if (info.hasAcceptOrderPercentTxt())
                            {
                                if (info.getAcceptOrderPercentTxt().hasTextHexColor())
                                {
                                    jieshou.setTextColor(Color.parseColor(info.getAcceptOrderPercentTxt().getTextHexColor()));
                                }
                                if (info.getAcceptOrderPercentTxt().hasText())
                                {
                                    jieshou.setText(info.getAcceptOrderPercentTxt().getText());
                                }
                            }
                            else
                            {
                                jieshou.setVisibility(View.INVISIBLE);
                            }
                            if (info.hasAverageResponseTimesTxt())
                            {
                                if (info.getAverageResponseTimesTxt().hasTextHexColor())
                                {
                                    time.setTextColor(Color.parseColor(info.getAverageResponseTimesTxt().getTextHexColor()));
                                }
                                if (info.getAverageResponseTimesTxt().hasText())
                                {
                                    time.setText(info.getAverageResponseTimesTxt().getText());
                                }
                            }


                            property.removeAllViews();
                            View view = getLayoutInflater().inflate(R.layout.new_car_info_property, null);
                            RelativeLayout line = (RelativeLayout) (view.findViewById(R.id.root));
                            for (int i = 0; i < propertys.size(); i++)
                            {
                                CarCommon.CarProperties carProperties = propertys.get(i);
                                if (i % 2 == 0)
                                {
                                    view = getLayoutInflater().inflate(R.layout.new_car_info_property, null);
                                    line = (RelativeLayout) (view.findViewById(R.id.root));
                                    property.addView(view);
                                }
                                TextView text = null;
                                BaseNetworkImageView img;
                                if (i % 2 == 0)
                                {
                                    text = (TextView) line.getChildAt(1);
                                    img = (BaseNetworkImageView) line.getChildAt(0);
                                }
                                else
                                {
                                    text = (TextView) line.getChildAt(3);
                                    img = (BaseNetworkImageView) line.getChildAt(2);
                                }
                                text.setText(carProperties.getDescStr());
                                if (carProperties.getImgName().indexOf("http") != -1)
                                {
                                    UUAppCar.getInstance().display(carProperties.getImgName(), img, R.drawable.car_property_mileage);
                                }
                                else
                                {
                                    img.setBackgroundResource(getResources().getIdentifier(carProperties.getImgName(), "drawable", getPackageName()));
                                }
                            }
                            cartime = info.getCarDisableTime();
                            banTime = info.getCarSelectRentTime();
//                            long current = cartime.getStartTime() * 1000;

                            beizhu = info.getCarLimitUseDesc();
                            banDay.setText(beizhu);
                            long current = System.currentTimeMillis();
                            for (int i = 0; i < days.length; i++)
                            {
                                weeks[i].setText(getWeekOfDate(current, i));
                                days[i].setText(MonthAdd(current, i));
                            }

                            weeks[0].setText("今天");
                            for (int i = 0; i < cartime.getUnavailableTimeCount(); i++)
                            {

                                if (cartime.getUnavailableTimeList().get(i).getUnavailableDay() > 0 && cartime.getUnavailableTimeList().get(i).getUnavailableDay() <= 7)
                                {

                                    if (cartime.getUnavailableTimeList().get(i).getType().equals(CarCommon.CarUnavailableTimeType.AM))
                                    {
                                        days[cartime.getUnavailableTimeList().get(i).getUnavailableDay() - 1].setBackgroundResource(R.drawable.up_half_ban_day);
                                    }
                                    else if (cartime.getUnavailableTimeList().get(i).getType().equals(CarCommon.CarUnavailableTimeType.PM))
                                    {
                                        days[cartime.getUnavailableTimeList().get(i).getUnavailableDay() - 1].setBackgroundResource(R.drawable.down_half_ban_day);
                                    }
                                    else
                                    {
                                        days[cartime.getUnavailableTimeList().get(i).getUnavailableDay() - 1].setBackgroundResource(R.drawable.new_car_info_dont);
                                        days[cartime.getUnavailableTimeList().get(i).getUnavailableDay() - 1].setTextColor(getResources().getColor(R.color.c11));
                                    }
                                }
                            }
                            if (info.getIfCollected())
                            {
                                collect.setBackgroundResource(R.drawable.new_car_info_collect);
                            }
                            else
                            {
                                collect.setBackgroundResource(R.drawable.new_car_info_collect_no);
                            }

                            List<CarDetailInfo.RenterAppraisal> reviews = info.getAppraisalListList();
                            review_root.removeAllViews();
                            for (int i = 0; i < reviews.size(); i++)
                            {
                                View root = (View) context.getLayoutInflater().inflate(R.layout.new_car_info_review_item, null);
                                TextView name = (TextView) root.findViewById(R.id.name);
                                CircleImageView head = (CircleImageView) root.findViewById(R.id.user_head);
                                TextView desc = (TextView) root.findViewById(R.id.note);
                                name.setText(reviews.get(i).getName());
                                UUAppCar.getInstance().display(reviews.get(i).getAvatar(), head, R.drawable.user_info_small_head_def);
                                desc.setText(reviews.get(i).getAppraisalContent());
                                TextView time = (TextView) root.findViewById(R.id.time);
                                SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日");
                                time.setText(planformatter.format(((long) reviews.get(i).getOccurTime()) * 1000));
                                review_root.addView(root);
                            }
                            if (info.hasAppraisalHasMore() && info.getAppraisalHasMore())
                            {
                                all.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                all.setVisibility(View.GONE);
                            }
                            if (reviews.isEmpty())
                            {
                                review_root.setVisibility(View.GONE);
                                noreview.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                all.setOnClickListener(new OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent intent = new Intent(context, OwnerInfoActivity.class);
                                        intent.putExtra(SysConfig.CAR_SN, sn);
                                        intent.putExtra(SysConfig.S_ID, carUserId);
                                        startActivity(intent);
                                    }
                                });
                            }

                        }
                        else
                        {
                            mAllFramelayout.makeProgreeNoData();
                        }
                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }

            @Override
            public void onError(VolleyError errorResponse)
            {
                MLog.e(tag, "getCarInfo error = " + errorResponse.getMessage() + "    " + errorResponse.getLocalizedMessage());
                Config.showFiledToast(context);
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish()
            {
                dismissProgress();
                mAllFramelayout.makeProgreeDismiss();
            }
        });
    }

    // 当前时间是星期几
    public String getWeekOfDate(long current, int day)
    {
        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar cal = Calendar.getInstance();
        Date curDate = new Date(current + (day * 1000 * 60 * 60 * 24));
        cal.setTime(curDate);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
        {
            w = 0;
        }
        return weekDays[w];
    }

    public String MonthAdd(long current, int day)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
//        Calendar calendar = Calendar.getInstance();
//        Date curDate = new Date(current + (day * 1000 * 60 * 60 * 24));
//        calendar.setTime(curDate);
//        calendar.add(calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        return sdf.format(calendar.getTime()); // 这个时间就是日期往后推一天的结果
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {

            switch (requestCode)
            {
                case REFUSH:
                    getCarDetail();
                    break;
                case SysConfig.COLLET_TO_PHONE:
                    getCarDetail();
                    break;
            }
        }
    }
}
