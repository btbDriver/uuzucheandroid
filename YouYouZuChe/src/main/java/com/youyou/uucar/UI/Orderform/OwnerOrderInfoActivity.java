package com.youyou.uucar.UI.Orderform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Common.Car.CarInfoAndLocationActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Owner.addcar.CarRealTimeStatusInfoActivity;
import com.youyou.uucar.UI.Owner.addcar.OwnerEvaluationRenterActivity;
import com.youyou.uucar.UI.Renter.RenterInfoActivity;
import com.youyou.uucar.UI.Renter.carinfo.CheckRouteActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.animation.AnimationUtils;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by taurusxi on 14-8-27.
 */
public class OwnerOrderInfoActivity extends BaseActivity implements ObserverListener {


    @InjectView(R.id.start_time_tv_1)
    TextView mStartTimeTv1;

    @InjectView(R.id.tips_linear)
    LinearLayout          mTipsLinear;
    @InjectView(R.id.status)
    TextView              mStatus;
    @InjectView(R.id.car_img)
    BaseNetworkImageView  mCarImg;
    @InjectView(R.id.price_day)
    TextView              mPriceDay;
    @InjectView(R.id.brand)
    TextView              mBrand;
    @InjectView(R.id.car_details)
    ImageView             mCarDetails;
    @InjectView(R.id.plate_number)
    TextView              mPlateNumber;
    @InjectView(R.id.start_time_root)
    RelativeLayout        mStartTimeRoot;
    @InjectView(R.id.rsn)
    TextView              mRsn;
    @InjectView(R.id.end_time_root)
    RelativeLayout        mEndTimeRoot;
    @InjectView(R.id.car_linear)
    FrameLayout           mCarLinear;
    @InjectView(R.id.start_time_1)
    TextView              mStartTime1;
    @InjectView(R.id.end_time_1)
    TextView              mEndTime1;
    @InjectView(R.id.view_1)
    View                  mView1;
    @InjectView(R.id.plan_1)
    TextView              mPlan1;
    @InjectView(R.id.linear_1)
    LinearLayout          mLinear1;
    @InjectView(R.id.start_time_2)
    TextView              mStartTime2;
    @InjectView(R.id.end_time_2)
    TextView              mEndTime2;
    @InjectView(R.id.actual_time_2)
    TextView              mActualTime2;
    @InjectView(R.id.view)
    View                  mView;
    @InjectView(R.id.actual_tv)
    TextView              mActualTv;
    @InjectView(R.id.chaoshi)
    TextView              mChaoshi;
    @InjectView(R.id.chaoshi_time)
    TextView              mChaoshiTime;
    @InjectView(R.id.linear_2)
    LinearLayout          mLinear2;
    @InjectView(R.id.income_tv)
    TextView              mIncomeTv;
    @InjectView(R.id.click)
    ImageView             mClick;
    @InjectView(R.id.income_linear)
    LinearLayout          mIncomeLinear;
    @InjectView(R.id.rent_fee)
    TextView              mRentFee;
    @InjectView(R.id.rent_fee_linear)
    LinearLayout          mRentFeeLinear;
    @InjectView(R.id.weiyuejin_income)
    TextView              mWeiyuejinIncome;
    @InjectView(R.id.weiyuejin_linear)
    LinearLayout          mWeiyuejinLinear;
    @InjectView(R.id.time_out_income)
    TextView              mTimeOutIncome;
    @InjectView(R.id.time_out_linear)
    LinearLayout          mTimeOutLinear;
    @InjectView(R.id.rent_coupon)
    TextView              mRentCoupon;
    @InjectView(R.id.income_linear_show)
    LinearLayout          mIncomeLinearShow;
    @InjectView(R.id.money)
    LinearLayout          mMoney;
    @InjectView(R.id.attentionNote)
    TextView              mAttentionNote;
    @InjectView(R.id.safe_linear)
    LinearLayout          mSafeLinear;
    @InjectView(R.id.renter_star)
    RatingBar             mRenterStar;
    @InjectView(R.id.renter_evaluate)
    TextView              mRenterEvaluate;
    @InjectView(R.id.receive_linear)
    LinearLayout          mReceiveLinear;
    @InjectView(R.id.ratingbar_owner)
    RatingBar             mRatingbarOwner;
    @InjectView(R.id.owner_evaluate)
    TextView              mOwnerEvaluate;
    @InjectView(R.id.owner_linear)
    LinearLayout          mOwnerLinear;
    @InjectView(R.id.not_get_car)
    TextView              mNotGetCar;
    @InjectView(R.id.has_get_car)
    TextView              mHasGetCar;
    @InjectView(R.id.car_linear_show)
    LinearLayout          mCarLinearShow;
    @InjectView(R.id.evaluate_renter)
    TextView              mEvaluateRenter;
    @InjectView(R.id.button_linear)
    LinearLayout          mButtonLinear;
    @InjectView(R.id.main_linear)
    LinearLayout          mMainLinear;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private String mOrderId;
    private String mRenterPhone;
    private View   actionBarView;
    //    private Button mMapTv;
    private String mPhone;
    private String carSn;
    Dialog dialog;
    boolean isShow = false;
    private final static int CAR_INFO_AND_LOCATION = 1;
    private final static int RENTER                = 2;
    private              int choose                = -1;
    boolean isShowRenter = false;
    @InjectView(R.id.renter_info_root)
    RelativeLayout renterInfoRoot;
    @InjectView(R.id.renter_name)
    TextView       tv_renterName;
    @InjectView(R.id.rating)
    RatingBar      rating;
    @InjectView(R.id.new_renter)
    TextView       newRenter;
    @InjectView(R.id.rent_times)
    TextView       rentTimes;
    @InjectView(R.id.address_root)
    RelativeLayout addressRoot;
    @InjectView(R.id.address)
    TextView       address;
    UuCommon.LatlngPosition position;

    @OnClick(R.id.renter_info_root)
    public void renterInfoClick()
    {
        Intent intent = new Intent(context, RenterInfoActivity.class);
        intent.putExtra(SysConfig.S_ID, renterUserId);
        startActivity(intent);
    }

    @OnClick(R.id.address_root)
    public void addressClick()
    {
        Intent intent = new Intent();
        intent.putExtra("end_lat", position.getLat());
        intent.putExtra("end_lng", position.getLng());
        intent.setClass(context, CheckRouteActivity.class);
        startActivity(intent);
    }

    public int renterUserId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_order_info);
        ButterKnife.inject(this);
        ObserverManager.addObserver(ObserverManager.OWNERORDERDETAIL, this);
        mOrderId = getIntent().getStringExtra(SysConfig.R_SN);
//        mOrderId = "111";
        getOrderData(mOrderId);
        initListener();
//        setActionBar();
        initNoteDataRefush();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return isShowRenter;
    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Config.isNetworkConnected(context))
                {
                    getOrderData(mOrderId);
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

//    private void setActionBar() {
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBarView = getLayoutInflater().inflate(R.layout.action_bar_order, null,
//                false);
//        TextView titleTv = (TextView) actionBarView.findViewById(R.id.title);
//        titleTv.setText("车主订单详情");
//        mMapTv = (Button) actionBarView.findViewById(R.id.map);
//        mMapTv.setText("联系租客");
//        mMapTv.setEnabled(true);
//        mMapTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tellRenter();
//            }
//        });
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setCustomView(actionBarView);
//    }

    private void tellRenter() {
        if (mPhone != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = getLayoutInflater().inflate(R.layout.tell_dialog, null);
            TextView phone = (TextView) view.findViewById(R.id.phone);
            phone.setText(mPhone);
//            builder.setMessage(mPhone);
            builder.setView(view);
            builder.setNegativeButton("拨打", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    String inputStr;
//                    inputStr = mPhone;
                    if (mPhone.trim().length() != 0) {
                        MobclickAgent.onEvent(context, "ContactOwner");
//                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                        startActivity(phoneIntent);
                    }
                }
            });
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }


    private void initListener() {
        mIncomeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    mIncomeLinearShow.setVisibility(View.GONE);
                    AnimationUtils.rotateIndicatingArrow(mClick, false);
                } else {
                    AnimationUtils.rotateIndicatingArrow(mClick, true);
                    mIncomeLinearShow.setVisibility(View.VISIBLE);
                }
                isShow = !isShow;
            }
        });
        mEvaluateRenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (choose) {
                    case RENTER:
                        Intent intent = new Intent();
                        intent.setClass(context, OwnerEvaluationRenterActivity.class);
                        intent.putExtra(SysConfig.R_SN, mOrderId);
                        context.startActivityForResult(intent, 165);
                        break;
                    case CAR_INFO_AND_LOCATION:
                        Intent location = new Intent();
                        location.setClass(context, CarRealTimeStatusInfoActivity.class);
                        location.putExtra(SysConfig.CAR_SN, carSn);
                        location.putExtra(SysConfig.R_SN, mOrderId);
                        context.startActivity(location);
                        break;

                }

            }
        });
        mNotGetCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("确认未收到车吗？若未收到，客服将介入处理！");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("未收到车", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dia, int whichButton) {
                        if (Config.isNetworkConnected(OwnerOrderInfoActivity.this)) {
                            Config.showProgressDialog(context, true, null);
                            NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerConfirmIfGetCar_VALUE);
                            OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.Builder request = OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.newBuilder();
                            request.setOrderId(mOrderId);
                            request.setIsGetCar(false);
                            network.setBusiData(request.build().toByteArray());
                            NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                @Override
                                public void onSuccessResponse(UUResponseData response) {
                                    if (response.getRet() == 0) {

                                        Config.showToast(context, response.getToastMsg());

                                        try {
                                            OrderFormInterface26.CarOwnerConfirmIfGetCar.Response data = OrderFormInterface26.CarOwnerConfirmIfGetCar.Response.parseFrom(response.getBusiData());
                                            if (data.getRet() == 0) {
                                                //未收车成功
                                                MLog.e("TAG", "未收车成功");
                                                getOrderData(mOrderId);
                                                MainActivityTab.instance.order.needRefush = true;

                                                MainActivityTab.instance.order.currentRefush = true;
                                                MainActivityTab.instance.order.cancelRefush = true;
                                                MainActivityTab.instance.order.finishRefush = true;

                                            } else if (data.getRet() == -1) {
                                                //未收车失败
                                                MLog.e("TAG", "未收车失败");
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
                        } else {
                            Toast.makeText(OwnerOrderInfoActivity.this, SysConfig.NETWORK_FAIL, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

        });

        mHasGetCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("确认已经收到车了吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        dialog.cancel();

                    }
                }).setPositiveButton("已收到车", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dia, int whichButton) {
                        if (Config.isNetworkConnected(OwnerOrderInfoActivity.this)) {
                            Config.showProgressDialog(context, true, null);
                            NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerConfirmIfGetCar_VALUE);
                            OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.Builder request = OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.newBuilder();
                            request.setOrderId(mOrderId);
                            request.setIsGetCar(true);
                            network.setBusiData(request.build().toByteArray());
                            NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                @Override
                                public void onSuccessResponse(UUResponseData response) {
                                    if (response.getRet() == 0) {
                                        Config.showToast(context, response.getToastMsg());
                                        try {
                                            OrderFormInterface26.CarOwnerConfirmIfGetCar.Response data = OrderFormInterface26.CarOwnerConfirmIfGetCar.Response.parseFrom(response.getBusiData());
                                            if (data.getRet() == 0) {
                                                //确认收车
                                                getOrderData(mOrderId);
                                            } else if (data.getRet() == -1) {
                                                //确认收车失败
                                                MLog.e("TAG", "确认收车失败");
                                                getOrderData(mOrderId);
                                                Config.showFiledToast(context);
                                            } else {
                                                Config.showFiledToast(context);
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
                        } else {
                            Toast.makeText(OwnerOrderInfoActivity.this, SysConfig.NETWORK_FAIL, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });


        mCarLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (carSn != null) {
                    Intent intent = new Intent();
                    intent.setClass(context, CarInfoAndLocationActivity.class);
                    intent.putExtra(SysConfig.R_SN, mOrderId);
                    intent.putExtra(SysConfig.CAR_SN, carSn);
                    context.startActivity(intent);
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_desc, menu);
        menu.findItem(R.id.action_save).setTitle("联系租客");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            tellRenter();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getOrderData(final String orderId) {
        NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerQueryOrderDetail_VALUE);
        OrderFormInterface26.CarOwnerQueryOrderDetail.Request.Builder request = OrderFormInterface26.CarOwnerQueryOrderDetail.Request.newBuilder();
        request.setOrderId(orderId);
        network.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData response) {

                if (response.getRet() == 0) {

                    mAllFramelayout.makeProgreeDismiss();
                    try {
                        OrderFormInterface26.CarOwnerQueryOrderDetail.Response data = OrderFormInterface26.CarOwnerQueryOrderDetail.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0) {
                            OrderFormCommon.CarOwnerOrderFormInfo orderInfo = data.getOrderInfo();
                            CarCommon.CarBriefInfo carBriefInfo = orderInfo.getCarBriefInfo();// 车辆信息
                            carSn = carBriefInfo.getCarId();
                            mBrand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                            renterUserId = orderInfo.getRenter().getUserId();
                            mPlateNumber.setText(carBriefInfo.getLicensePlate());
                            mRsn.setText("订单号：" + orderInfo.getOrderId());
                            if (carBriefInfo.hasPricePerDay()) {
                                float pricePerDay = carBriefInfo.getPricePerDay(); // 汽车价格，按天算
                                String result = "";
                                if ((pricePerDay + "").endsWith(".0")) {
                                    result = ((int) pricePerDay) + "";
                                } else {
                                    result = pricePerDay + "";
                                }

                                mPriceDay.setText(result + "元/天");
                            }

                            UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), mCarImg, R.drawable.list_car_img_def);
                            OrderFormCommon.OrderFormPropertys orderFormPropertys = orderInfo.getOrderFormPropertys();// 订单相关属性
                            int planToStartTime = orderFormPropertys.getPlanToStartTime();// 订单计划开始时间戳，单位秒
                            int planToEndTime = orderFormPropertys.getPlanToEndTime(); // 订单计划结束时间戳，单位秒
                            mPlan1.setText(orderFormPropertys.getOrderDuration());// 订单时长，根据订单计划开始、结束计算得到的
                            mStartTime1.setText(Config.getFormatTime(planToStartTime));
                            mStartTime2.setText(Config.getFormatTime(planToStartTime));
                            mEndTime1.setText(Config.getFormatTime(planToEndTime));
                            mEndTime2.setText(Config.getFormatTime(planToEndTime));
//                            mStartTimeTv1.setText(orderInfo.get);
                            String renterEndTripTimeStr = "";
                            String renterShortEndTripTimeStr = "";
                            if (orderFormPropertys.hasRenterEndTripTime()) {
                                int renterEndTripTime = orderFormPropertys.getRenterEndTripTime();// 租客还车（结束行程）时间戳，单位秒
                                renterEndTripTimeStr = Config.getFormatTime(renterEndTripTime);
                                renterShortEndTripTimeStr = Config.getShortFormatTime(renterEndTripTime);
                                mActualTime2.setText(renterEndTripTimeStr);
                            }
                            if (orderFormPropertys.hasActualOrderDuration()) {
                                String actualOrderDuration = orderFormPropertys.getActualOrderDuration();// 实际租时
                                mActualTv.setText(actualOrderDuration);
                            }

                            if (orderFormPropertys.hasRenterOvertime()) {
                                String renterOvertime = orderFormPropertys.getRenterOvertime();// 超时时间
                                mChaoshiTime.setText(renterOvertime);
                            }
                            OrderFormCommon.OrderFormStatus status = orderInfo.getStatus();// 订单状态
                            String renterName = "";
                            if (orderInfo.hasRenter()) {
                                UserCommon.UserBriefInfo renter = orderInfo.getRenter(); // 租客信息
                                String lastName = renter.getLastName();
                                int gender = renter.getGender();
                                if (gender == 2) {
                                    renterName = lastName + "女士";
                                } else {
                                    renterName = lastName + "先生";
                                }
                            }

                            tv_renterName.setText(renterName);
                            if (orderInfo.hasIsNewMember() && orderInfo.getIsNewMember() == 0)//新会员
                            {
                                rating.setVisibility(View.GONE);
                                newRenter.setVisibility(View.VISIBLE);
                                int drivinAge = orderInfo.getDrivingAge();
                                String rentTime = "";
                                if (drivinAge < 12)
                                {
                                    rentTime = "驾龄" + drivinAge + "月";
                                }
                                else
                                {
                                    rentTime = "驾龄" + (drivinAge / 12) + "年";
                                }
                                rentTimes.setText(rentTime);
                            }
                            else
                            {
                                newRenter.setVisibility(View.GONE);
                                rating.setVisibility(View.VISIBLE);
                                rating.setRating(orderInfo.getRenter().getStars());
                                rentTimes.setText("租车" + orderInfo.getRenter().getRentCount() + "次");
                            }
                            if (orderInfo.hasPosition())
                            {
                                position = orderInfo.getPosition();
                            }
                            if (orderInfo.getCarBriefInfo().hasDisplayPosition() && orderInfo.getCarBriefInfo().getDisplayPosition() == 0)
                            {
                                addressRoot.setVisibility(View.VISIBLE);
                                if (orderInfo.hasPositionDesc())
                                {
                                    address.setText(orderInfo.getPositionDesc());
                                }
                            }
                            else
                            {
                                addressRoot.setVisibility(View.GONE);
                            }


                            switch (status.getNumber()) {
                                case OrderFormCommon.OrderFormStatus.WAIT_RENTER_PAY_VALUE:   // 等待租客支付
                                    mStatus.setText("订单状态：等待" + renterName + "支付");
                                    mClick.setVisibility(View.VISIBLE);
                                    mMoney.setEnabled(true);
                                    mMoney.performClick();
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mLinear2.setVisibility(View.GONE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_PAY_TIMEOUT_VALUE:  // 租客付款超时
                                    mStatus.setText("订单状态：超时未付款");
                                    mClick.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mMoney.setEnabled(true);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mLinear2.setVisibility(View.GONE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_CANCEL_BEFORE_PAY_VALUE:// 租客付款前取消订单
                                    mStatus.setText("订单状态：" + renterName + "已取消订单");
                                    mMoney.setEnabled(false);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mIncomeTv.setText("0");
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mClick.setVisibility(View.INVISIBLE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_CONFIRM_ORDER_WAIT_PAY_VALUE:   // 等待租客支付
                                    mStatus.setText("订单状态：" + renterName + "确认订单，但是还没有付款");
                                    mClick.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mMoney.setEnabled(true);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.WAIT_RENTER_TAKE_CAR_VALUE: // 租客待取车
                                    mStatus.setText("订单状态：等待" + renterName + "取车");
                                    mClick.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mLinear2.setVisibility(View.GONE);
                                    mMoney.setEnabled(true);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_CANCEL_AFTER_PAY_VALUE:// 租客付款后取消订单，将会产生扣费项目
                                    mStatus.setText("订单状态：" + renterName + "已取消订单");
                                    mClick.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mMoney.setEnabled(true);
                                    mLinear2.setVisibility(View.GONE);
                                    mWeiyuejinLinear.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.GONE);
                                    mCarLinearShow.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mIncomeLinear.performClick();
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_ON_TRIP_VALUE: // 租客正在用车
                                    mStatus.setText("订单状态：" + renterName + "用车中");
                                    mClick.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    mCarLinearShow.setVisibility(View.GONE);
                                    mEvaluateRenter.setVisibility(View.VISIBLE);
                                    choose = CAR_INFO_AND_LOCATION;
                                    mEvaluateRenter.setText("查看车辆实时位置");
                                    mMoney.setEnabled(true);

                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_USE_CAR_TIMEOUT_VALUE:// 租客用车已超时
                                    mStatus.setText("订单状态：" + renterName + "用车已超时");
                                    mClick.setVisibility(View.VISIBLE);
                                    mMoney.setEnabled(true);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.VISIBLE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_END_TRIP_VALUE:// 租客结束行程（表示租客已还车）
                                    mStatus.setText("订单状态：" + renterName + "已经还车，待确认");
                                    mClick.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.VISIBLE);
                                    mStartTimeTv1.setText(renterName + "已在" + renterShortEndTripTimeStr + "还车，若有异议，请在" + orderInfo.getHoursToContactCompany() + "小时内联系客服");
                                    mLinear1.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mMoney.setEnabled(true);
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    mCarLinearShow.setVisibility(View.VISIBLE);
                                    mEvaluateRenter.setVisibility(View.GONE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.VISIBLE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.CAR_OWNER_NOT_GET_BACK_CAR_VALUE:// 车主确认未收到车

                                    mStatus.setText("订单状态：未收到车");
                                    mClick.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mMoney.setEnabled(true);
                                    mButtonLinear.setVisibility(View.GONE);
                                    mCarLinearShow.setVisibility(View.VISIBLE);
                                    mEvaluateRenter.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.VISIBLE);
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mReceiveLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);

                                    break;
                                case OrderFormCommon.OrderFormStatus.COMPANY_DEAL_WITH_VALUE: // 客服已介入
                                    mStatus.setText("订单状态：客服已介入");
                                    mLinear1.setVisibility(View.GONE);
                                    mMoney.setEnabled(true);
                                    mClick.setVisibility(View.VISIBLE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    mTimeOutLinear.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.GONE);
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    mCarLinearShow.setVisibility(View.GONE);
                                    mEvaluateRenter.setVisibility(View.VISIBLE);
                                    choose = CAR_INFO_AND_LOCATION;
                                    mEvaluateRenter.setText("查看车辆实时位置");
                                    break;
                                case OrderFormCommon.OrderFormStatus.CAR_OWNER_GET_CAR_AND_FINISHED_VALUE:// 车主确认已收到车（订单已完成）

                                    if (orderInfo.hasCarOwnerComment()) {
                                        mStatus.setText("订单状态：" + renterName + "已经完成租车，您已评价");
                                        mButtonLinear.setVisibility(View.GONE);
                                        mOwnerLinear.setVisibility(View.VISIBLE);
                                    } else {
                                        mStatus.setText("订单状态：" + renterName + "已经完成租车，您未评价");
                                        mButtonLinear.setVisibility(View.VISIBLE);
                                        mCarLinearShow.setVisibility(View.GONE);
                                        mEvaluateRenter.setVisibility(View.VISIBLE);
                                        mEvaluateRenter.setText("评价租客");
                                        choose = RENTER;
                                        mOwnerLinear.setVisibility(View.GONE);
                                    }
                                    if (orderInfo.hasRenterComment()) {

                                        mReceiveLinear.setVisibility(View.VISIBLE);

                                    } else {
                                        mReceiveLinear.setVisibility(View.GONE);
                                    }
                                    mMoney.setEnabled(true);
                                    mRentFeeLinear.setVisibility(View.VISIBLE);
                                    mWeiyuejinLinear.setVisibility(View.GONE);
                                    mClick.setVisibility(View.VISIBLE);
                                    mIncomeLinearShow.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.GONE);
                                    mTimeOutLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mSafeLinear.setVisibility(View.GONE);
                                    break;
                            }
                            if (orderInfo.hasCarRentIncome()) {
                                float carRentIncome = orderInfo.getCarRentIncome();  // 汽车租金
                                mRentFee.setText(String.format("%.2f", carRentIncome) + "元");
                            }
                            if (orderInfo.hasRenterUserCarOvertimeIncome()) {
                                float renterUserCarOvertimeIncome = orderInfo.getRenterUserCarOvertimeIncome();  // 租客用车超时的租金收入
                                mTimeOutIncome.setText(String.format("%.2f", renterUserCarOvertimeIncome) + "元");
                                mTimeOutLinear.setVisibility(View.VISIBLE);
                            } else {
                                mTimeOutLinear.setVisibility(View.GONE);
                            }
                            if (orderInfo.hasTotalIncome()) {
                                float totalIncome = orderInfo.getTotalIncome();//
                                mRentCoupon.setText(String.format("%.2f", totalIncome) + "元");
                                mIncomeTv.setText(String.format("%.2f", totalIncome));
                            }

                            if (orderInfo.hasRenterBreakContractIncome()) {
                                float renterBreakContractIncome = orderInfo.getRenterBreakContractIncome();//
                                mWeiyuejinIncome.setText(String.format("%.2f", renterBreakContractIncome) + "元");
                            }

                            if (orderInfo.hasRenterComment()) {
                                OrderFormCommon.OrderComment renterComment = orderInfo.getRenterComment(); // 租客评价

                                if (renterComment.getContent() != null && !(renterComment.getContent().equals(""))) {

                                    mRenterEvaluate.setText(renterComment.getContent());
                                    mRenterStar.setRating(renterComment.getStars());

                                } else {

                                    mReceiveLinear.setVisibility(View.GONE);

                                }


                            } else {
                                mReceiveLinear.setVisibility(View.GONE);
                            }
                            if (orderInfo.hasCarOwnerComment()) {
                                OrderFormCommon.OrderComment carOwnerComment = orderInfo.getCarOwnerComment();// 车主评价

                                if (carOwnerComment.getContent() != null && !(carOwnerComment.getContent().equals(""))) {
                                    mOwnerEvaluate.setText(carOwnerComment.getContent());
                                    mRatingbarOwner.setRating(carOwnerComment.getStars());
                                } else {
                                    mOwnerLinear.setVisibility(View.GONE);
                                }
                            } else {
                                mOwnerLinear.setVisibility(View.GONE);


                            }

                            if (orderInfo.hasRenterPhone()) {
                                String renterPhone = orderInfo.getRenterPhone();// 租客手机联系号码
                                mPhone = renterPhone;
                                isShowRenter = true;
                            } else {
                                isShowRenter = false;
                                mPhone = null;
                            }
                            invalidateOptionsMenu();
                            if (orderInfo.hasAttentionNotes()) {
                                String attentionNotes = orderInfo.getAttentionNotes();// 注意事项
                                if (attentionNotes != null && !attentionNotes.trim().equals("")) {
                                    mSafeLinear.setVisibility(View.VISIBLE);
                                    mAttentionNote.setText(attentionNotes);
                                } else {
                                    mSafeLinear.setVisibility(View.GONE);
                                }
                            } else {
                                mSafeLinear.setVisibility(View.GONE);
                            }

                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {

            }
        });

    }

    @Override
    public void observer(String from, Object obj) {
//        Toast.makeText(context, "push__orderId:" + obj, Toast.LENGTH_LONG).show();
        if (from != null) {
            if (from.equals("push")) {

                if (obj.equals(mOrderId)) {
                    getOrderData(mOrderId);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (getIntent() != null && getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("money")) {
            finish();
        } else {
            Intent intent = new Intent(OwnerOrderInfoActivity.this, MainActivityTab.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_STROKE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
//            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        }
    }
}
