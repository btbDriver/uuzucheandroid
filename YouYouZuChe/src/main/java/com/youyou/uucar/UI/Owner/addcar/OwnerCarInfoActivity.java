package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.view.sliding.AbSlidingPlayView;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.timessquare.CalendarPickerView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.rent.OneToOneWaitActivity;
import com.youyou.uucar.UI.Main.rent.SelectTime;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Orderform.Tips.WebHtmlActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.ImageView.CircleImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
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

public class OwnerCarInfoActivity extends BaseActivity
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

    private boolean  isOkForRelease = false;
    public  Activity context        = this;
    public String address;
//    public LinearLayout yclcLinear;

    @OnClick(R.id.releasecar)
    public void rentClick()
    {
        if (!isOkForRelease)
        {
            showToast("需要完成车辆信息，才可以发布车辆");
            return;
        }
        if (Config.isGuest(context))
        {
            Intent intent = new Intent(context, NoPasswordLogin.class);
            intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
            startActivityForResult(intent, 165);
        }
        else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW))
        {
            Intent intent = new Intent(context, RenterRegisterIDActivity.class);
            startActivity(intent);
        }
        else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL))
        {
            Intent intent = new Intent(context, MainActivityTab.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_VERIFY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO))
        {
            Intent intent = new Intent(context, MainActivityTab.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_VERIFY_ERROR);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
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
                            request.setStartTime((int) (getIntent().getLongExtra("start", 0) / 1000));
                            request.setEndTime((int) (getIntent().getLongExtra("end", 0) / 1000));
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
                                            if (response.getRet() == 0)
                                            {
                                                Config.isSppedIng = false;
//                                                context.stopService(new Intent(context, RentingService.class));
                                                getApp().quitRenting();
                                                Config.isOneToOneIng = true;
                                                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                                startActivity(intent);
                                                RentingType = RENTING_ONE;
                                            } /*else if (response.getRet() == -2) {
                                                rentClick();
                                            } else if (response.getRet() == -1) {
                                                showToast("失败,请重试");
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
                                                intent.putExtra(SysConfig.CAR_SN, sn);
                                                intent.putExtra("CAR_NAME", carName);
                                                intent.putExtra("BEIZHU", info.getCarLimitUseDesc());
                                                startActivity(intent);

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

//                            request.
                    }
                });
                builder.create().show();
            }
            else if (RentingType == RENTING_ONE)
            {
                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                startActivity(intent);
            }
            else if (getIntent().hasExtra("start") && getIntent().hasExtra("end"))//如果选择过开始时间和结束时间
            {
                showProgress(false);
                OrderFormInterface26.RenterStartPreOrder.Request.Builder request = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
                request.addCarIds(sn);
                request.setStartTime((int) (getIntent().getLongExtra("start", 0) / 1000));
                request.setEndTime((int) (getIntent().getLongExtra("end", 0) / 1000));
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
                                    Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                    startActivity(intent);
                                }/* else if (response.getRet() == -2) {
                                    Config.isSppedIng = true;
                                    Config.isUserCancel = false;
                                    startService(new Intent(context, RentingService.class));
                                    rentClick();
                                } else if (response.getRet() == -1) {
                                    showToast("失败,请重试");
                                }*/
//                                else
//                                {
//                                    if (response.getTipsType() == 0)//toast
//                                    {
//                                        showToast(response.getMsg());
//                                    }
//                                    else
//                                    {
//                                        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
//                                        msgBuilder.setMessage(response.getMsg());
//                                        msgBuilder.setNeutralButton("知道了", null);
//                                        msgBuilder.create().show();
//                                    }
//                                }
                                if (response.hasMsg() && response.getMsg().length() > 0)
                                {
//                            showToast(responseData.getToastMsg());
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
                showProgress(false);
                Config.getCoordinates(context, new LocationListener()
                {
                    @Override
                    public void locationSuccess(double lat, double lng, String addr)
                    {
                        Intent intent = new Intent(context, SelectTime.class);
                        intent.putExtra("address", address);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        intent.putExtra("ban_time", info.getCarLimitUseDesc());
                        intent.putExtra("BEIZHU", info.getCarLimitUseDesc());
                        intent.putExtra("CAR_NAME", carName);
                        String car_sn = sn;
                        intent.putExtra(SysConfig.CAR_SN, car_sn);
                        startActivityForResult(intent, 615);
                    }
                });
            }
        }
    }

    ;
    LinearLayout       property;
    ExpandableTextView desc;
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

    private RelativeLayout dialogView;
    private AlertDialog    theDialog;
    CalendarPickerView calendarView;

    CarCommon.CarSelectRentTime banTime;
    CarCommon.CarSelectRentTime cartime;

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
        Intent intent = new Intent(context, WebHtmlActivity.class);
        intent.putExtra("url", ServerMutualConfig.yclc);
        intent.putExtra(SysConfig.TITLE, "用车流程");
        context.startActivity(intent);

    }

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
        kefu.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        isOkForRelease = getIntent().getBooleanExtra("sure", false);
        bottom_root = (RelativeLayout) findViewById(R.id.bottom_root);
        owner_root = (LinearLayout) findViewById(R.id.owner_root);
//        yclcLinear = (LinearLayout) findViewById(R.id.yclc_linear);
//        yclcLinear.setVisibility(View.GONE);
        findViewById(R.id.yclc).setVisibility(View.GONE);

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
        collect.setVisibility(View.GONE);
        rent = (TextView) findViewById(R.id.releasecar);
        rent.setText("发布车辆");
        rent.setOnClickListener(rentClick);
        rent.setEnabled(isOkForRelease);
        all = (TextView) findViewById(R.id.all);
        noreview = (TextView) findViewById(R.id.noreview);
        review_root = (LinearLayout) findViewById(R.id.review_root);
        name = (TextView) findViewById(R.id.name);
//        head = (ImageView) findViewById(R.id.user_head);
        rating = (RatingBar) findViewById(R.id.rating);
        price_hour = (TextView) findViewById(R.id.price_hour);
        price_day = (TextView) findViewById(R.id.price_day);
        price_week = (TextView) findViewById(R.id.price_week);
        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setVisibility(View.GONE);
        desc = (ExpandableTextView) findViewById(R.id.expand_text_view);
//        xinghao = (TextView) findViewById(R.id.xinghao);
        sn = getIntent().getStringExtra(SysConfig.CAR_SN);
        property = (LinearLayout) findViewById(R.id.property);
        address_1 = (TextView) findViewById(R.id.address_1);
        address_1_root = (RelativeLayout) findViewById(R.id.address_1_root);
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
        findViewById(R.id.time).setVisibility(View.GONE);
        TextView jieshou = (TextView) findViewById(R.id.jieshou);
        jieshou.setText("新车主,爱车尚未出租过。");
    }


    public OnClickListener rentClick = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            if (Config.isGuest(context))
            {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                startActivityForResult(intent, 165);
            }
            else
            {

                Config.showProgressDialog(context, false, null);
                CarInterface.AddNewCarFinish.Request.Builder builder = CarInterface.AddNewCarFinish.Request.newBuilder();
                builder.setCarId(sn);
                NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.AddNewCarFinish_VALUE);
                task.setBusiData(builder.build().toByteArray());
                task.setTag("AddNewCarFinish");
                NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                {
                    @Override
                    public void onSuccessResponse(UUResponseData responseData)
                    {
                        if (responseData.getRet() == 0)
                        {
                            try
                            {

                                CarInterface.AddNewCarFinish.Response response = CarInterface.AddNewCarFinish.Response.parseFrom(responseData.getBusiData());
                                if (response.getRet() == 0)
                                {
                                    Intent intent = new Intent();
                                    intent.setClass(context, FinishAddCarActivity.class);
                                    context.startActivity(intent);
                                    OwnerCarInfoActivity.this.finish();
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
                        MLog.e(tag, "getCarInfo error = " + errorResponse.getMessage() + "    " + errorResponse.getLocalizedMessage());
                        Config.showFiledToast(context);
                        finish();
                    }

                    @Override
                    public void networkFinish()
                    {
                        dismissProgress();
                    }
                });
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem items)
    {
        if (items.getItemId() == android.R.id.home || items.getItemId() == 0)
        {
            onBackPressed();
            return false;
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
        getCarDetail();
    }

    private String carName;

    @InjectView(R.id.service_time)
    TextView serviceTime;

    @InjectView(R.id.service_time_root)
    RelativeLayout service_time_root;
    @InjectView(R.id.miss_root)
    RelativeLayout missRoot;
    @InjectView(R.id.miss_tip)
    TextView       miss_tip;

    public void getCarDetail()
    {

        CarInterface.GetCarDetailInfo.Request.Builder builder = CarInterface.GetCarDetailInfo.Request.newBuilder();
        builder.setCarId(sn);
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

                            final int carUserId = info.getUserId();

                            UuCommon.LatlngPosition latlng = info.getPosition();
                            String dessc = "暂无";
                            if (info.getCarDesc().trim().length() != 0)
                            {
                                dessc = info.getCarDesc();
                            }
                            desc.setText(dessc);


                            if (getIntent().hasExtra("isRelease") && getIntent().getBooleanExtra("isRelease", false))
                            {
                                missRoot.setVisibility(View.GONE);
                                findViewById(R.id.bottom_root).setVisibility(View.VISIBLE);
                            }
                            else
                            {
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
                            }

                            carName = info.getBrand() + info.getCarModel();
                            setTitle(carName);
                            price_hour.setText((int) info.getPriceByHour() + "");
                            price_day.setText("￥" + (int) info.getPriceByDay() + "");
                            price_week.setText((int) info.getPriceByWeek() + "");
                            address_1.setText(info.getAddress());
                            name.setText(info.getCarOwnerName());
                            rating.setRating(info.getCarOwnerStars());
                            List<CarCommon.CarImg> carimgs = info.getCarImgsList();


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
                            mSlidingPlayView.removeAllViews();
                            for (int i = 0; i < carimgs.size(); i++)
                            {
                                BaseNetworkImageView img = new BaseNetworkImageView(context);
                                String imgUrl = info.getCarImgUrlPrefix() + carimgs.get(i).getImgThumb();
                                UUAppCar.getInstance().display(imgUrl, img, car_img_def_big);
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                mSlidingPlayView.addView(img);
                            }
                            List<CarCommon.CarProperties> propertys = info.getCarPropertiesList();
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
//                            banDay.setText(info.getCarLimitUseDesc());
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
//        calendar.add(calendar.MONTH, day);// 把日期往后增加一天.整数往后推,负数往前移动
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        return sdf.format(calendar.getTime()); // 这个时间就是日期往后推一天的结果
    }
}
