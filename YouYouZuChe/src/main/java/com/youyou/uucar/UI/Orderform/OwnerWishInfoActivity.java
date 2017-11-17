package com.youyou.uucar.UI.Orderform;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
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
import com.youyou.uucar.UI.Renter.RenterInfoActivity;
import com.youyou.uucar.UI.Renter.carinfo.CheckRouteActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.listen.OnClickNetworkListener;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by taurusxi on 14-8-27.
 */
public class OwnerWishInfoActivity extends BaseActivity implements ObserverListener
{


    @InjectView(R.id.start_time_tv_1)
    TextView              mStartTimeTv1;
    @InjectView(R.id.tips_linear)
    LinearLayout          mTipsLinear;
    @InjectView(R.id.order_status)
    TextView              mOrderStatus;
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
    @InjectView(R.id.start_time)
    TextView              mStartTime;
    @InjectView(R.id.end_time)
    TextView              mEndTime;
    @InjectView(R.id.view)
    View                  mView;
    @InjectView(R.id.rent_time)
    TextView              mRentTime;
    @InjectView(R.id.refuse)
    TextView              mRefuse;
    @InjectView(R.id.agree)
    TextView              mAgree;
    @InjectView(R.id.car_linear_show)
    LinearLayout          mCarLinearShow;
    @InjectView(R.id.button_linear)
    LinearLayout          mButtonLinear;
    @InjectView(R.id.main)
    LinearLayout          mMain;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private String mPreOrderDetail;
    private String mPhone;
    private String carSn;
    //    private final static int RENTER_START_WISH =1; //快速约车
//    private final static int RENTER_CONFIRMED_WISH =2; //一对一，或者 一对多
//    private int wishStatus =-1;
    private boolean isAgree = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_wish_info);
        ButterKnife.inject(this);
        ObserverManager.addObserver(ObserverManager.WISH_LIST_PUSH, this);
        mPreOrderDetail = getIntent().getStringExtra(SysConfig.PRE_ORDER_DETAIL);
//        if (SysConfig.DEVELOP_MODE) {
//            mPreOrderDetail = "11122";
//        }
        initView();
        initListen();

        initNoteDataRefush();
        getData(mPreOrderDetail);

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
                    getData(mPreOrderDetail);
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    protected void initListen()
    {
        mRefuse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String gender = "";
                if (renter.getGender() == 2)
                {
                    gender = "女士";
                }
                else
                {
                    gender = "先生";
                }

                builder.setMessage("真的要拒绝" + renter.getLastName() + gender + "的用车请求吗？您将会少赚" + String.format("%.2f", carOwnerRentIncome) + "元。");
                builder.setNegativeButton("再想想", null);
                builder.setNeutralButton("拒绝用车", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        refuseAndAgree(false);
                    }
                });
                builder.create().show();


            }
        });

        mAgree.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                refuseAndAgree(true);
            }
        });

        mCarLinear.setOnClickListener(new OnClickNetworkListener()
        {
            @Override
            public void onNetworkClick(View v)
            {
                if (carSn != null)
                {
                    Intent intent = new Intent();
                    intent.setClass(context, CarInfoAndLocationActivity.class);
                    intent.putExtra(SysConfig.R_SN, mPreOrderDetail);
                    intent.putExtra(SysConfig.CAR_SN, carSn);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void refuseAndAgree(final boolean flag)
    {
        showProgress(true);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
        OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
        request.setPreOrderId(mPreOrderDetail);
        request.setAgree(flag);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData response)
            {
                if (response.getRet() == 0)
                {
                    Config.showToast(context, response.getToastMsg());

                    try
                    {
                        OrderFormInterface26.ConfirmPreOrder.Response data = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(response.getBusiData());
                        String msg = data.getMsg();
                        boolean msgFlag = data.hasMsg();
                        if (msgFlag)
                        {
                            Config.showToast(context, data.getMsg());
                        }
                        if (data.getRet() == 0)
                        {
                            if (flag)
                            {
                                //快速约车，等待 租客端确认
                                MLog.e("TAG", "快速约车，等待 租客端确认");

                            }
                            else
                            {
                                //表示 拒绝意向单 成功
                            }
                            getData(mPreOrderDetail);
                            MainActivityTab.instance.order.needRefush = true;
                            MainActivityTab.instance.order.currentRefush = true;
                            MainActivityTab.instance.order.cancelRefush = true;
                            MainActivityTab.instance.order.finishRefush = true;

                        }
                        else if (data.getRet() == -1)
                        {
                            //接口调用失败
                            if (!msgFlag)
                            {
                                Config.showFiledToast(context);
                            }
//                            Toast.makeText(context, "连接失败，请重试！", Toast.LENGTH_SHORT).show();

                        }
                        else if (data.getRet() == 1)
                        {
                            //一对一租车，或者一对多租车，直接转换为订单
                            if (data.hasOrderId())
                            {
                                MainActivityTab.instance.order.needRefush = true;
                                MainActivityTab.instance.order.currentRefush = true;
                                MainActivityTab.instance.order.cancelRefush = true;
                                MainActivityTab.instance.order.finishRefush = true;
                                Intent intent = new Intent();
                                intent.setClass(context, OwnerOrderInfoActivity.class);
                                intent.putExtra(SysConfig.R_SN, data.getOrderId());
                                OwnerWishInfoActivity.this.finish();
                                context.startActivity(intent);

                            }

                        }
                        else if (data.getRet() == -2)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            if (msg == null || msg.length() == 0 || msg.equals("null"))
                            {
                                msg = "您响应慢了，租客已经选了其它车辆";
                            }

                            builder.setMessage(msg);
                            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    getData(mPreOrderDetail);
                                }
                            });
                            builder.create().show();

                        }
                        else if (data.getRet() == -3)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            if (msg == null || msg.length() == 0 || msg.equals("null"))
                            {
                                msg = "您的爱车与此订单时间有冲突,不能同意";
                            }

                            builder.setMessage(msg);
                            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    getData(mPreOrderDetail);
                                }
                            });
                            builder.create().show();

                        }
                        else if (data.getRet() == -4)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("抱歉，由于您" + TIME_COUNT + "分钟没有选择合适的车辆租用，系统已自动取消您的预约。");

                            if (msg == null || msg.length() == 0 || msg.equals("null"))
                            {
                                msg = "租客有待支付订单,暂时不能同意";
                            }
                            builder.setMessage(msg);
                            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                }
                            });
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
                Config.dismissProgress();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }


    private void initView()
    {

    }

    UserCommon.UserBriefInfo renter;
    float                    carOwnerRentIncome;

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

    public void getData(final String preOrderDetail)
    {

        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryPreOrderDetail_VALUE);
        OrderFormInterface26.QueryPreOrderDetail.Request.Builder request = OrderFormInterface26.QueryPreOrderDetail.Request.newBuilder();
        request.setPreOrderId(preOrderDetail);
        networkTask.setBusiData(request.build().toByteArray());

        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData data)
            {
                if (data.getRet() == 0)
                {
                    Config.showToast(context, data.getToastMsg());

                    try
                    {
                        OrderFormInterface26.QueryPreOrderDetail.Response response = OrderFormInterface26.QueryPreOrderDetail.Response.parseFrom(data.getBusiData());
                        if (response.getRet() == 0)
                        {
                            OrderFormCommon.PreOrderFormInfo preOrderDetailInfo = response.getPreOrderDetailInfo();
                            OrderFormCommon.OrderFormPropertys orderFormPropertys = preOrderDetailInfo.getOrderFormPropertys();
                            renterUserId = preOrderDetailInfo.getRenter().getUserId();
                            int planToStartTime = orderFormPropertys.getPlanToStartTime();
                            int planToEndTime = orderFormPropertys.getPlanToEndTime();
                            String orderDuration = orderFormPropertys.getOrderDuration();
                            mRentTime.setText(orderDuration);
                            mStartTime.setText(Config.getFormatTime(planToStartTime + ""));
                            mEndTime.setText(Config.getFormatTime(planToEndTime + ""));
                            CarCommon.CarBriefInfo carBriefInfo = preOrderDetailInfo.getCarBriefInfo();
                            carSn = carBriefInfo.getCarId();
                            if (carBriefInfo.hasThumbImg())
                            {
                                UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), mCarImg, R.drawable.list_car_img_def);
                            }
                            if (carBriefInfo.hasPricePerDay())
                            {
                                mPriceDay.setText(((int) carBriefInfo.getPricePerDay()) + "元/天");
                            }
                            mPlateNumber.setText(carBriefInfo.getLicensePlate());
                            mBrand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
//                            mRsn.setText("订单号：" + preOrderDetailInfo.getPreOrderId());
                            mRsn.setText("订单号：暂无");
                            renter = preOrderDetailInfo.getRenter();
                            String renterLastName = renter.getLastName();
                            String renterName = renterLastName + (renter.getGender() == 1 ? "先生" : "女士");
                            OrderFormCommon.PreOrderFormStatus status = preOrderDetailInfo.getStatus();
                            int renterStartPreOrderTime = preOrderDetailInfo.getRenterStartPreOrderTime();
                            int carOwnerConfirmTime = preOrderDetailInfo.getCarOwnerTimeLimitToConfirm();  //分钟
                            carOwnerRentIncome = preOrderDetailInfo.getCarOwnerRentIncome();

                            tv_renterName.setText(renterName);
                            if (preOrderDetailInfo.hasIsNewMember() && preOrderDetailInfo.getIsNewMember() == 0)//新会员
                            {
                                rating.setVisibility(View.GONE);
                                newRenter.setVisibility(View.VISIBLE);
                                int drivinAge = preOrderDetailInfo.getDrivingAge();
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
                                rating.setRating(preOrderDetailInfo.getRenter().getStars());
                                rentTimes.setText("租车" + preOrderDetailInfo.getRenter().getRentCount() + "次");
                            }
                            if (preOrderDetailInfo.hasPosition())
                            {
                                position = preOrderDetailInfo.getPosition();
                            }
                            if (preOrderDetailInfo.getCarBriefInfo().hasDisplayPosition() && preOrderDetailInfo.getCarBriefInfo().getDisplayPosition() == 0)
                            {
                                addressRoot.setVisibility(View.VISIBLE);
                                if (preOrderDetailInfo.hasPositionDesc())
                                {
                                    address.setText(preOrderDetailInfo.getPositionDesc());
                                }
                            }
                            else
                            {
                                addressRoot.setVisibility(View.GONE);
                            }

                            if (status == OrderFormCommon.PreOrderFormStatus.RENTER_CONFIRMED)
                            {
                                //
                                mOrderStatus.setText("订单状态：" + renterName + "想租您的车，等待您同意");
                                mTipsLinear.setVisibility(View.VISIBLE);
                                mButtonLinear.setVisibility(View.VISIBLE);
                                mRefuse.setText("拒绝");
                                mAgree.setText("同意用车");
//                                wishStatus = RENTER_CONFIRMED_WISH;接受订单可收入
                                mStartTimeTv1.setText(renterName + Config.getShortFormatTime(renterStartPreOrderTime) + "发出预约请求,请" + carOwnerConfirmTime + "分钟内处理，接受订单可收入" + carOwnerRentIncome + "元");
                            }
                            else if (status == OrderFormCommon.PreOrderFormStatus.RENTER_START)
                            {
                                // 租客发起意向单请求（快速约车）
                                mTipsLinear.setVisibility(View.VISIBLE);
                                mButtonLinear.setVisibility(View.VISIBLE);
                                mOrderStatus.setText("订单状态：" + renterName + "想租一辆车，等待您同意");
                                mRefuse.setText("拒绝");
                                mAgree.setText("抢单");
//                                wishStatus = RENTER_START_WISH;抢单后可收入
                                mStartTimeTv1.setText(renterName + Config.getShortFormatTime(renterStartPreOrderTime) + "发出预约请求,请" + carOwnerConfirmTime + "分钟内处理，抢单后可收入" + carOwnerRentIncome + "元");

                            }
                            else if (status == OrderFormCommon.PreOrderFormStatus.CAR_OWNER_CONFIRMED)
                            {
                                // 车主侧已确认意向单
                                mOrderStatus.setText("订单状态：您已抢单，等待" + renterName + "选车");
                                mTipsLinear.setVisibility(View.GONE);
                                mButtonLinear.setVisibility(View.GONE);

                            }
                            else if (status == OrderFormCommon.PreOrderFormStatus.CAR_OWNER_REFUSEED)
                            {
                                // 车主拒绝
                                mOrderStatus.setText("订单状态：您已拒绝");
                                mTipsLinear.setVisibility(View.GONE);
                                mButtonLinear.setVisibility(View.GONE);
                            }
                            else if (status == OrderFormCommon.PreOrderFormStatus.RENTER_REFUSEED)
                            {
                                // 租客拒绝
                                mOrderStatus.setText("订单状态：租客已经拒绝");
                                mButtonLinear.setVisibility(View.GONE);
                                mTipsLinear.setVisibility(View.GONE);
                            }
                            else if (status == OrderFormCommon.PreOrderFormStatus.FINISHED)
                            {
                                // 结束
                                mOrderStatus.setText("订单状态：意向单结束");
                                mButtonLinear.setVisibility(View.GONE);
                                mTipsLinear.setVisibility(View.GONE);
                            }

                            mAllFramelayout.makeProgreeDismiss();
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
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish()
            {
                Config.dismissProgress();
            }
        });
    }

    @Override
    public void observer(String from, Object obj)
    {
        if (from != null)
        {
            if (from.equals("push"))
            {
                getData(mPreOrderDetail);
            }
        }
    }
}
