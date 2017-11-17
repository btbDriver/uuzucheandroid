package com.youyou.uucar.UI.Main.MyStrokeFragment.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeCancelFragment;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeCurrentFragment;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeFinishFragment;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterOrderReviewActivity;
import com.youyou.uucar.UI.Main.fragment.BasicFragment;
import com.youyou.uucar.UI.Orderform.OwnerOrderInfoActivity;
import com.youyou.uucar.UI.Orderform.OwnerWishInfoActivity;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Orderform.H5Activity;
import com.youyou.uucar.UI.Owner.addcar.CarRealTimeStatusInfoActivity;
import com.youyou.uucar.UI.Owner.addcar.OwnerEvaluationRenterActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.H5Constant;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Network.listen.OnClickNetworkListener;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by liuchao on 2015/3/18.
 */
public class MyStrokeAdapter extends BaseAdapter {
    public String tag = "MyStrokeAdapter";
    private BaseActivity context = null;
    private List<OrderFormCommon.TripOrderCard> data = null;
    private BasicFragment mFragment = null;

    private final static int RENTER_REVIEW = 11002;
    private final static int OWNER_REVIEW = 11012;
    private final static int LOCATION_REVIEW = 11112;

    public MyStrokeAdapter(BaseActivity context, List<OrderFormCommon.TripOrderCard> data, BasicFragment mFragment) {
        this.context = context;
        this.data = data;
        this.mFragment = mFragment;
    }

    //    RENTER_START = 1; // 租客发起意向单请求（快速约车）
    //    RENTER_CONFIRMED = 2; // 租客侧已确认意向单（一对一、一对多约车）
    //    CAR_OWNER_CONFIRMED = 3; // 车主侧已确认意向单
    //    CAR_OWNER_REFUSEED = 4; // 车主拒绝
    //    RENTER_REFUSEED = 5; // 租客拒绝
    //    FINISHED = 6; // 结束
    //        public MyStrokeAdapter(Activity context)
    //        {
    //        }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderFormCommon.TripOrderCard model = (OrderFormCommon.TripOrderCard) getItem(position);
        final UserCommon.UserBriefInfo renter = model.getRenter();
        CarCommon.CarBriefInfo carBriefInfo = model.getCarBriefInfo();
        OrderFormCommon.OrderFormPropertys propertys = model.getOrderFormPropertys();
        String gender = "";
        if (renter.getGender() == 2) {
            gender = "女士";
        } else {
            gender = "先生";
        }
        if (model.getOrderType() == 0) {
            convertView = context.getLayoutInflater().inflate(R.layout.stroke_intent_item, null);
            RelativeLayout bottomRoot = (RelativeLayout) convertView.findViewById(R.id.bottom_root);
            TextView tip = (TextView) convertView.findViewById(R.id.tip);
            OrderFormCommon.PreOrderFormStatus status = model.getPreOrderStatus();
            TextView brand = (TextView) convertView.findViewById(R.id.brand);
            TextView tv_status = (TextView) convertView.findViewById(R.id.status);
            tv_status.setVisibility(View.GONE);
            TextView refuse = (TextView) convertView.findViewById(R.id.refuse);
            TextView agree = (TextView) convertView.findViewById(R.id.agree);

            //租客发起意向单-车主
            if (status.equals(OrderFormCommon.PreOrderFormStatus.RENTER_START)) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OwnerWishInfoActivity.class);
                        intent.putExtra(SysConfig.PRE_ORDER_DETAIL, model.getOrderId());
                        context.startActivity(intent);
                    }
                });
                long time = propertys.getOrderFormCreateTime() * 1000L;
                SimpleDateFormat tipformatter = new SimpleDateFormat("HH:mm");
                String dateString = tipformatter.format(time);
                String tip_string = renter.getLastName() + gender + dateString + "发出预约请求,请" + model.getCarOwnerTimeLimitToConfirm() + "分钟内处理";
                SpannableStringBuilder style = new SpannableStringBuilder(tip_string);
                style.setSpan(new ForegroundColorSpan(Color.parseColor("#f08300")), tip_string.indexOf("求,请") + 3, tip_string.indexOf("分钟"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                tip.setText(style);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(renter.getLastName() + gender);
                RatingBar star = (RatingBar) convertView.findViewById(R.id.star);
                star.setRating(renter.getStars());
                TextView rent_times = (TextView) convertView.findViewById(R.id.rent_times);
                rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                TextView start_time = (TextView) convertView.findViewById(R.id.start_time);
                TextView end_time = (TextView) convertView.findViewById(R.id.end_time);
                SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日 HH:mm");
                String startDateString = planformatter.format((propertys.getPlanToStartTime() * 1000L));
                String endDateString = planformatter.format((propertys.getPlanToEndTime() * 1000L));
                start_time.setText(startDateString);
                end_time.setText(endDateString);
                bottomRoot.setVisibility(View.VISIBLE);
                brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                final String finalGender = gender;
                final String finalGender1 = gender;
                refuse.setOnClickListener(new OnClickNetworkListener() {

                    @Override
                    public void onNetworkClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("真的要拒绝" + renter.getLastName() + finalGender + "的用车请求吗？您将会少赚" + String.format("%.2f", model.getCarOwnerIncome()) + "元。");
                        builder.setNegativeButton("再想想", null);
                        builder.setNeutralButton("拒绝用车", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Config.showProgressDialog(context, false, null);
                                OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
                                request.setPreOrderId(model.getOrderId());
                                request.setAgree(false);
                                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
                                networkTask.setTag("ConfirmPreOrder");
                                networkTask.setBusiData(request.build().toByteArray());
                                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {

                                    @Override
                                    public void onSuccessResponse(UUResponseData responseData) {
                                        if (responseData.getRet() == 0) {
                                            try {


                                                OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());

                                                String msg = "";
                                                if (response.getMsg().length() > 0) {
                                                    msg = response.getMsg();
                                                }
                                                if (response.getRet() == 0 || response.getRet() == 1) {
                                                    data.remove(position);
                                                    adapterNotifyDataSetChanged();
                                                } else if (response.getRet() == -1) {
                                                    msg = "失败请重试";
                                                    if (response.getMsg().length() > 0) {
                                                        msg = response.getMsg();
                                                    }


                                                    Config.showToast(context, msg);
                                                } else if (response.getRet() == -2) {
                                                    data.remove(position);
                                                    adapterNotifyDataSetChanged();

                                                    msg = "您响应慢了,租客已经选了其他车辆";
                                                    if (response.getMsg().length() > 0) {
                                                        msg = response.getMsg();
                                                    }
                                                    Config.showToast(context, msg);
                                                } else if (response.getRet() == -3) {
                                                    data.remove(position);
                                                    adapterNotifyDataSetChanged();

                                                    msg = "与该用户在进行中的订单有时间冲突，不能预约该车辆";
                                                    if (response.getMsg().length() > 0) {
                                                        msg = response.getMsg();
                                                    }
                                                    Config.showToast(context, msg);
                                                } else if (response.getRet() == -4) {
                                                    data.remove(position);
                                                    adapterNotifyDataSetChanged();

                                                    msg = "有未支付的订单，不能预约";
                                                    if (response.getMsg().length() > 0) {
                                                        msg = response.getMsg();
                                                    }
                                                    Config.showToast(context, msg);
                                                }

                                            } catch (InvalidProtocolBufferException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError errorResponse) {
                                        Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                                    }

                                    @Override
                                    public void networkFinish() {
                                        Config.dismissProgress();

                                    }
                                });
                            }
                        });
                        builder.create().show();
                    }
                });
                agree.setText("立即抢单");
                agree.setOnClickListener(new OnClickNetworkListener() {
                    @Override
                    public void onNetworkClick(View v) {
                        Config.showProgressDialog(context, false, null);
                        OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
                        request.setPreOrderId(model.getOrderId());
                        request.setAgree(true);
                        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
                        networkTask.setTag("ConfirmPreOrder");
                        networkTask.setBusiData(request.build().toByteArray());
                        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                            @Override
                            public void onSuccessResponse(UUResponseData responseData) {

                                if (responseData.getRet() == 0) {
                                    try {
                                        context.showToast(responseData.getToastMsg());
                                        ((BaseActivity) mFragment.getActivity()).showToast(responseData.getToastMsg());
                                        OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());
                                        // 0：成功；-1：失败；1：意向单已成功转为订单；-2：意向单已经失效；-3：与该用户在进行中的订单有时间冲突，不能预约该车辆；-4：有未支付的订单，不能预约
                                        String msg = "";
                                        if (response.getRet() == 0 || response.getRet() == 1) {
                                            goToFresh();

                                            msg = "抢单成功，等待租客选车。";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);
                                        } else if (response.getRet() == -1) {
                                            Config.showToast(context, "失败请重试");
                                        } else if (response.getRet() == -2) {
                                            data.remove(position);
                                            adapterNotifyDataSetChanged();


                                            msg = "您响应慢了," + renter.getLastName() + finalGender1 + "已经选了其他车辆";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);

                                        } else if (response.getRet() == -3) {
                                            data.remove(position);
                                            adapterNotifyDataSetChanged();

                                            msg = "您的爱车与此订单时间有冲突,不能同意";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);

                                        } else if (response.getRet() == -4) {
                                            data.remove(position);
                                            adapterNotifyDataSetChanged();
                                            msg = "租客有未支付订单,不能同意";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);
                                        } else {
                                            data.remove(position);
                                            adapterNotifyDataSetChanged();

                                            msg = "您响应慢了," + renter.getLastName() + finalGender1 + "已经选了其他车辆";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);
                                        }


                                    } catch (InvalidProtocolBufferException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onError(VolleyError errorResponse) {
                                Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                            }

                            @Override
                            public void networkFinish() {
                                Config.dismissProgress();
                            }
                        });
                    }
                });

            }
            //租客同意-意向单-租客
            else if (status.equals(OrderFormCommon.PreOrderFormStatus.RENTER_CONFIRMED)) {
//                    tip.setText("这个状态是不会有的,呵呵呵" +"    RENTER_CONFIRMED");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OwnerWishInfoActivity.class);
                        intent.putExtra(SysConfig.PRE_ORDER_DETAIL, model.getOrderId());
                        context.startActivity(intent);
                    }
                });
                long time = propertys.getOrderFormCreateTime() * 1000L;
                SimpleDateFormat tipformatter = new SimpleDateFormat("HH:mm");
                String dateString = tipformatter.format(time);
                String tip_string = renter.getLastName() + gender + dateString + "发出预约请求,请" + model.getCarOwnerTimeLimitToConfirm() + "分钟内处理";
                SpannableStringBuilder style = new SpannableStringBuilder(tip_string);
                style.setSpan(new ForegroundColorSpan(Color.parseColor("#f08300")), tip_string.indexOf("求,请") + 3, tip_string.indexOf("分钟"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                tip.setText(style);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(renter.getLastName() + gender);
                RatingBar star = (RatingBar) convertView.findViewById(R.id.star);
                star.setRating(renter.getStars());
                TextView rent_times = (TextView) convertView.findViewById(R.id.rent_times);
                rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                TextView start_time = (TextView) convertView.findViewById(R.id.start_time);
                TextView end_time = (TextView) convertView.findViewById(R.id.end_time);
                SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日 HH:mm");
                String startDateString = planformatter.format((propertys.getPlanToStartTime() * 1000L));
                String endDateString = planformatter.format((propertys.getPlanToEndTime() * 1000L));
                start_time.setText(startDateString);
                end_time.setText(endDateString);
                bottomRoot.setVisibility(View.VISIBLE);
                brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                final String finalGender = gender;
                refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("真的要拒绝" + renter.getLastName() + finalGender + "的用车请求吗？您将会少赚" + String.format("%.2f", model.getCarOwnerIncome()) + "元。");
                        builder.setNegativeButton("再想想", null);
                        builder.setNeutralButton("拒绝用车", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Config.showProgressDialog(context, false, null);
                                OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
                                request.setPreOrderId(model.getOrderId());
                                request.setAgree(false);
                                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
                                networkTask.setTag("ConfirmPreOrder");
                                networkTask.setBusiData(request.build().toByteArray());
                                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {

                                    @Override
                                    public void onSuccessResponse(UUResponseData responseData) {
                                        if (responseData.getRet() == 0) {
                                            try {

                                                ((BaseActivity) mFragment.getActivity()).showToast(responseData.getToastMsg());
                                                OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());
                                                String msg = "";
                                                if (response.getRet() == 0) {
                                                    data.remove(position);
                                                    adapterNotifyDataSetChanged();
                                                } else if (response.getRet() == -1) {

                                                    msg = "失败请重试";
                                                    if (response.getMsg().length() > 0) {
                                                        msg = response.getMsg();
                                                    }
                                                    Config.showToast(context, msg);
                                                } else if (response.getRet() == -2) {
                                                    data.remove(position);
                                                    adapterNotifyDataSetChanged();
                                                    msg = "您响应慢了,租客已经选了其他车辆";
                                                    if (response.getMsg().length() > 0) {
                                                        msg = response.getMsg();
                                                    }
                                                    Config.showToast(context, msg);
                                                }

                                            } catch (InvalidProtocolBufferException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError errorResponse) {
                                        Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                                    }

                                    @Override
                                    public void networkFinish() {
                                        Config.dismissProgress();

                                    }
                                });
                            }
                        });
                        builder.create().show();
                    }
                });
                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Config.showProgressDialog(context, false, null);
                        OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
                        request.setPreOrderId(model.getOrderId());
                        request.setAgree(true);
                        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
                        networkTask.setTag("ConfirmPreOrder");
                        networkTask.setBusiData(request.build().toByteArray());
                        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                            @Override
                            public void onSuccessResponse(UUResponseData responseData) {
                                if (responseData.getRet() == 0) {
                                    try {
                                        ((BaseActivity) mFragment.getActivity()).showResponseCommonMsg(responseData.getResponseCommonMsg());
                                        OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());
                                        String msg = "";
                                        if (response.getRet() == 0 || response.getRet() == 1) {
                                            goToFresh();

                                            msg = "您已同意王先生的用车请求，等待对方付款";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);
                                        } else if (response.getRet() == -1) {
                                            msg = "失败请重试";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);
                                        } else if (response.getRet() == -2) {
                                            data.remove(position);
                                            adapterNotifyDataSetChanged();
                                            msg = "您响应慢了,租客已经选了其他车辆";
                                            if (response.getMsg().length() > 0) {
                                                msg = response.getMsg();
                                            }
                                            Config.showToast(context, msg);
                                        }

                                    } catch (InvalidProtocolBufferException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onError(VolleyError errorResponse) {
                                Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                            }

                            @Override
                            public void networkFinish() {
                                Config.dismissProgress();
                            }
                        });
                    }
                });
            }
            //车主同意意向单-车主
            else if (status.equals(OrderFormCommon.PreOrderFormStatus.CAR_OWNER_CONFIRMED)) {

                convertView.setOnClickListener(new OnClickNetworkListener() {
                    @Override
                    public void onNetworkClick(View v) {
                        Intent intent = new Intent(context, OwnerWishInfoActivity.class);
                        intent.putExtra(SysConfig.PRE_ORDER_DETAIL, model.getOrderId());
                        context.startActivity(intent);
                    }
                });
                tip.setText("已抢单,等待" + renter.getLastName() + gender + "选车");
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(renter.getLastName() + gender);
                RatingBar star = (RatingBar) convertView.findViewById(R.id.star);
                star.setRating(renter.getStars());
                TextView rent_times = (TextView) convertView.findViewById(R.id.rent_times);
                rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                TextView start_time = (TextView) convertView.findViewById(R.id.start_time);
                TextView end_time = (TextView) convertView.findViewById(R.id.end_time);
                SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日 HH:mm");
                String startDateString = planformatter.format((propertys.getPlanToStartTime() * 1000L));
                String endDateString = planformatter.format((propertys.getPlanToEndTime() * 1000L));
                start_time.setText(startDateString);
                end_time.setText(endDateString);
                brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                bottomRoot.setVisibility(View.GONE);
            }
            //车主拒绝
            else if (status.equals(OrderFormCommon.PreOrderFormStatus.CAR_OWNER_REFUSEED)) {
                tip.setText("点了拒绝之后,这条Card会消失的,不会出现的呵呵呵呵" + "   CAR_OWNER_REFUSEED");
            }
            //租客拒绝-车主看到的
            else if (status.equals(OrderFormCommon.PreOrderFormStatus.CAR_OWNER_REFUSEED)) {
                tip.setText("这个状态是不会有的,呵呵呵" + "   CAR_OWNER_REFUSEED");
            }
        }
        //订单
        else {
            OrderFormCommon.OrderFormStatus status = model.getOrderStatus();
            //车主
            if (propertys.getCarOwnerUsrId() == UserSecurityConfig.userId_ticket) {

                convertView = context.getLayoutInflater().inflate(R.layout.stroke_intent_item, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OwnerOrderInfoActivity.class);
                        intent.putExtra(SysConfig.R_SN, model.getOrderId());
                        //Intent intent = new Intent(context, RenterOrderInfoH5Activity.class);
                        //intent.putExtra("weburl", model.getOrderDetailH5Url());
                        //intent.putExtra("userType", H5Constant.USER_TYPE_OWNER);
                        context.startActivity(intent);
                    }
                });
                TextView tip = (TextView) convertView.findViewById(R.id.tip);
                tip.setVisibility(View.VISIBLE);
                TextView name = (TextView) convertView.findViewById(R.id.name);
                TextView rent_times = (TextView) convertView.findViewById(R.id.rent_times);
                TextView start_time = (TextView) convertView.findViewById(R.id.start_time);
                TextView end_time = (TextView) convertView.findViewById(R.id.end_time);
                TextView brand = (TextView) convertView.findViewById(R.id.brand);
                RelativeLayout bottomRoot = (RelativeLayout) convertView.findViewById(R.id.bottom_root);
                TextView tv_status = (TextView) convertView.findViewById(R.id.status);
                RatingBar star = (RatingBar) convertView.findViewById(R.id.star);
                SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日 HH:mm");
                String startDateString = planformatter.format((propertys.getPlanToStartTime() * 1000L));
                String endDateString = planformatter.format((propertys.getPlanToEndTime() * 1000L));
                SimpleDateFormat tipformatter = new SimpleDateFormat("HH:mm");
                SimpleDateFormat tipformatter2 = new SimpleDateFormat("mm");

                //等待租客支付
                if (status.equals(OrderFormCommon.OrderFormStatus.WAIT_RENTER_PAY) || status.equals(OrderFormCommon.OrderFormStatus.RENTER_CONFIRM_ORDER_WAIT_PAY)) {
                    tip.setText("租约已在" + tipformatter.format((model.getOrderFormPropertys().getOrderFormCreateTime() * 1000L)) + "达成,对方将在" + model.getRenterTimeLimitToPay() + "分钟内支付");
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("待支付");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
                    bottomRoot.setVisibility(View.GONE);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客超时未付款
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_PAY_TIMEOUT)) {
                    tip.setText("对方超时未付款");
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已取消");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c8));
                    bottomRoot.setVisibility(View.GONE);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客付款前取消订单
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_CANCEL_BEFORE_PAY)) {
                    tip.setText("租客已取消订单");
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已取消");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c8));
                    bottomRoot.setVisibility(View.GONE);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客待取车
                else if (status.equals(OrderFormCommon.OrderFormStatus.WAIT_RENTER_TAKE_CAR)) {

                    tip.setText("等待对方取车");
                    tip.setVisibility(View.GONE);
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("待取车");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
                    bottomRoot.setVisibility(View.GONE);
                }
                //租客付款后取消订单，将会产生扣费项目
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_CANCEL_AFTER_PAY)) {
                    tip.setText("租客已取消订单,获得" + String.format("%.2f", model.getCarOwnerIncome()) + "元赔偿");
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已取消");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c8));
                    bottomRoot.setVisibility(View.GONE);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客正在用车
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_ON_TRIP)) {
                    tip.setText("对方正在用车");
                    tip.setVisibility(View.GONE);
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("用车中");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
                    bottomRoot.setVisibility(View.VISIBLE);

                    TextView refuse = (TextView) convertView.findViewById(R.id.refuse);
                    refuse.setVisibility(View.GONE);
                    TextView agree = (TextView) convertView.findViewById(R.id.agree);
                    agree.setText("查看车辆实时位置");
                    agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(context, CarRealTimeStatusInfoActivity.class);
                            intent.putExtra(SysConfig.CAR_SN, model.getCarBriefInfo().getCarId());
                            context.startActivityForResult(intent, LOCATION_REVIEW);
                        }
                    });
                }
                //租客用车已超时
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_USE_CAR_TIMEOUT)) {
                    tip.setText("对方用车已超时");
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("用车中");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
//                        bottomRoot.setVisibility(View.GONE);
                    bottomRoot.setVisibility(View.VISIBLE);
                    TextView refuse = (TextView) convertView.findViewById(R.id.refuse);
                    refuse.setVisibility(View.GONE);
                    TextView agree = (TextView) convertView.findViewById(R.id.agree);
                    agree.setText("查看车辆实时位置");
                    agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent();
                            intent.setClass(context, CarRealTimeStatusInfoActivity.class);
                            intent.putExtra(SysConfig.CAR_SN, model.getCarBriefInfo().getCarId());
                            context.startActivityForResult(intent, LOCATION_REVIEW);
                        }
                    });
                }
                //租客结束行程（表示租客已还车）
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_END_TRIP)) {
                    //车主
                    long time = model.getRenterEndTripTime() * 1000L;
                    String dateString = tipformatter.format(new Date(time));
                    tip.setText(renter.getLastName() + gender + "已在" + dateString + "还车,若有异常请在24小时内反馈");
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已还车");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c10));
                    bottomRoot.setVisibility(View.VISIBLE);
                    //未收到车
                    TextView refuse = (TextView) convertView.findViewById(R.id.refuse);
                    refuse.setText("未收到车");
                    //已收到车
                    TextView agree = (TextView) convertView.findViewById(R.id.agree);
                    agree.setText("已收到车");

                    refuse.setOnClickListener(new OnClickNetworkListener() {
                        @Override
                        public void onNetworkClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("确认未收到车吗？若未收到，客服将介入处理");
                            builder.setNegativeButton("取消", null);
                            builder.setNeutralButton("未收到车", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Config.showProgressDialog(context, false, null);
                                    OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.Builder request = OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.newBuilder();
                                    request.setOrderId(model.getOrderId());
                                    request.setIsGetCar(false);
                                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerConfirmIfGetCar_VALUE);
                                    networkTask.setTag("CarOwnerConfirmIfGetCar");
                                    networkTask.setBusiData(request.build().toByteArray());
                                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData) {
                                            if (responseData.getRet() == 0) {
                                                try {

                                                    ((BaseActivity) mFragment.getActivity()).showToast(responseData.getToastMsg());
                                                    OrderFormInterface26.CarOwnerConfirmIfGetCar.Response response = OrderFormInterface26.CarOwnerConfirmIfGetCar.Response.parseFrom(responseData.getBusiData());
                                                    if (response.getRet() == 0) {
                                                        goToFresh();

                                                    } else {
                                                        Config.showFiledToast(context);
                                                    }

                                                } catch (InvalidProtocolBufferException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Config.showFiledToast(context);
                                            }
                                        }

                                        @Override
                                        public void onError(VolleyError errorResponse) {
                                            Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                                        }

                                        @Override
                                        public void networkFinish() {
                                            Config.dismissProgress();
                                        }
                                    });
                                }
                            });
                            builder.create().show();
                        }

                    });
                    agree.setOnClickListener(new OnClickNetworkListener() {
                        @Override
                        public void onNetworkClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("确认已经收到车了吗？");
                            builder.setNegativeButton("取消", null);
                            builder.setNeutralButton("已收到车", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Config.showProgressDialog(context, false, null);
                                    OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.Builder request = OrderFormInterface26.CarOwnerConfirmIfGetCar.Request.newBuilder();
                                    request.setOrderId(model.getOrderId());
                                    request.setIsGetCar(true);
                                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerConfirmIfGetCar_VALUE);
                                    networkTask.setTag("CarOwnerConfirmIfGetCar");
                                    networkTask.setBusiData(request.build().toByteArray());
                                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData) {
                                            if (responseData.getRet() == 0) {
                                                try {
                                                    ((BaseActivity) mFragment.getActivity()).showToast(responseData.getToastMsg());
                                                    OrderFormInterface26.CarOwnerConfirmIfGetCar.Response response = OrderFormInterface26.CarOwnerConfirmIfGetCar.Response.parseFrom(responseData.getBusiData());
                                                    if (response.getRet() == 0) {
                                                        goToFresh();

                                                    } else {
                                                        Config.showFiledToast(context);
                                                    }

                                                } catch (InvalidProtocolBufferException e) {
                                                    e.printStackTrace();
                                                    Config.showFiledToast(context);
                                                }
                                            } else {

                                                Config.showFiledToast(context);
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
                            });
                            builder.create().show();
                        }
                    });

                }
                //客服已介入
                else if (status.equals(OrderFormCommon.OrderFormStatus.COMPANY_DEAL_WITH) || status.equals(OrderFormCommon.OrderFormStatus.CAR_OWNER_NOT_GET_BACK_CAR)) {
                    tip.setText("客服已介入");
                    long time = model.getRenterEndTripTime() * 1000L;
                    String dateString = tipformatter.format(time);
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("未收车");
                    tv_status.setTextColor(mFragment.getResources().getColor(R.color.c10));
                    if (model.getIsCarOwnerCommnet()) {
                        bottomRoot.setVisibility(View.GONE);
                    } else {
                        bottomRoot.setVisibility(View.VISIBLE);
                        TextView refuse = (TextView) convertView.findViewById(R.id.refuse);
                        refuse.setVisibility(View.GONE);
                        TextView agree = (TextView) convertView.findViewById(R.id.agree);
                        agree.setText("查看车辆实时位置");
                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent();
                                intent.setClass(context, CarRealTimeStatusInfoActivity.class);
                                intent.putExtra(SysConfig.CAR_SN, model.getCarBriefInfo().getCarId());
                                context.startActivityForResult(intent, LOCATION_REVIEW);
                            }
                        });
                    }
                }
                //车主确认已收到车订单已结束（行程已完成）
                else if (status.equals(OrderFormCommon.OrderFormStatus.CAR_OWNER_GET_CAR_AND_FINISHED)) {

                    long time = model.getRenterEndTripTime() * 1000L;
                    String dateString = tipformatter.format(time);
                    name.setText(renter.getLastName() + gender);
                    star.setRating(renter.getStars());
                    rent_times.setText("租车次数 ：" + renter.getRentCount() + "次");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setTextColor(context.getResources().getColor(R.color.c10));
                    if (model.getIsCarOwnerCommnet()) {
                        tip.setText("订单已完成,已评价");
                        tv_status.setText("已完成");
                        bottomRoot.setVisibility(View.GONE);
                    } else {
                        tip.setText("订单已完成,未评价");
                        tv_status.setText("未评价");
                        bottomRoot.setVisibility(View.VISIBLE);
                        TextView refuse = (TextView) convertView.findViewById(R.id.refuse);
                        refuse.setVisibility(View.GONE);
                        TextView agree = (TextView) convertView.findViewById(R.id.agree);
                        agree.setText("评价" + renter.getLastName() + gender);
                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

//                                    Intent intent = new Intent();
//                                    intent.setClass(context, RenterOrderReviewActivity.class);
//                                    intent.putExtra(SysConfig.R_SN, model.getOrderId());
//                                    context.startActivityForResult(intent, RENTER_REVIEW);


                                Intent intent = new Intent();
                                intent.setClass(context, OwnerEvaluationRenterActivity.class);
                                intent.putExtra(SysConfig.R_SN, model.getOrderId());
                                context.startActivityForResult(intent, RENTER_REVIEW);
                            }
                        });

                    }
                }
            }
            //租客
            else if (propertys.getRenterUsrId() == UserSecurityConfig.userId_ticket) {
                convertView = context.getLayoutInflater().inflate(R.layout.stroke_over_item, null);

                //进入订单详情页面html5
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                        //intent.putExtra(SysConfig.R_SN, model.getOrderId());
                        Intent intent = new Intent(context, H5Activity.class);
                        intent.putExtra("weburl", model.getOrderDetailH5Url());
                        intent.putExtra("userType", H5Constant.USER_TYPE_RENTER);
                        context.startActivity(intent);
                    }
                });
                RelativeLayout overRoot = (RelativeLayout) convertView.findViewById(R.id.over_root);
                long time = propertys.getOrderFormCreateTime() * 1000L;
                SimpleDateFormat tipformatter = new SimpleDateFormat("HH:mm");
                String dateString = tipformatter.format(time);
                SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日 HH:mm");
                String startDateString = planformatter.format((propertys.getPlanToStartTime() * 1000L));
                String endDateString = planformatter.format((propertys.getPlanToEndTime() * 1000L));
                TextView tip = (TextView) convertView.findViewById(R.id.tip);
                TextView start_time = (TextView) convertView.findViewById(R.id.start_time);
                TextView end_time = (TextView) convertView.findViewById(R.id.end_time);
                TextView brand = (TextView) convertView.findViewById(R.id.brand);
                TextView tv_status = (TextView) convertView.findViewById(R.id.status);
                BaseNetworkImageView carImg = (BaseNetworkImageView) convertView.findViewById(R.id.car_img);
                //等待租客支付
                if (status.equals(OrderFormCommon.OrderFormStatus.WAIT_RENTER_PAY) || status.equals(OrderFormCommon.OrderFormStatus.RENTER_CONFIRM_ORDER_WAIT_PAY)) {

                    String tip2s = "租约已在" + dateString + "达成,请" + model.getRenterTimeLimitToPay() + "分钟内完成支付";
                    SpannableStringBuilder style = new SpannableStringBuilder(tip2s);
                    style.setSpan(new ForegroundColorSpan(Color.parseColor("#f08300")), tip2s.indexOf("成,请") + 3, tip2s.indexOf("分钟"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tip.setText(style);
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("待支付");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
//                        overRoot.setVisibility(View.GONE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);
                    overRoot.setVisibility(View.VISIBLE);
                    TextView over = (TextView) convertView.findViewById(R.id.over);
                    over.setText("立即支付");
                    over.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                            intent.putExtra(SysConfig.R_SN, model.getOrderId());
                            context.startActivity(intent);
                        }
                    });

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客超时未付款
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_PAY_TIMEOUT)) {
                    String tip2s = "您在" + model.getRenterTimeLimitToPay() + "分钟内未付款";
                    SpannableStringBuilder style = new SpannableStringBuilder(tip2s);
                    style.setSpan(new ForegroundColorSpan(Color.parseColor("#f08300")), tip2s.indexOf("在") + 1, tip2s.indexOf("分钟"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tip.setText(style);
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已取消");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c8));
                    overRoot.setVisibility(View.GONE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客付款前取消订单
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_CANCEL_BEFORE_PAY)) {
                    tip.setText("您已取消订单");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已取消");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c8));
                    overRoot.setVisibility(View.GONE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客待取车
                else if (status.equals(OrderFormCommon.OrderFormStatus.WAIT_RENTER_TAKE_CAR)) {
                    tip.setText("待取车");
                    tip.setVisibility(View.GONE);
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("待取车");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
                    overRoot.setVisibility(View.GONE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);
                }
                //租客付款后取消订单，将会产生扣费项目
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_CANCEL_AFTER_PAY)) {
                    tip.setText("您已取消订单,违约损失" + String.format("%.2f", model.getRenterCost()) + "元");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("已取消");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c8));
                    overRoot.setVisibility(View.GONE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);

                    //更新显示文案（从服务器端得到）
                    updateText(model, tip, tv_status);
                }
                //租客正在用车
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_ON_TRIP)) {
                    tip.setText("正在用车");
                    tip.setVisibility(View.GONE);
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("用车中");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
                    overRoot.setVisibility(View.VISIBLE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);
                    TextView over = (TextView) convertView.findViewById(R.id.over);
                    over.setText("结束行程");
                    over.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("确认已经还车,结束行程吗？");
                            builder.setNegativeButton("取消", null);
                            builder.setNeutralButton("结束行程", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Config.showProgressDialog(context, false, null);
                                    OrderFormInterface.FinishTrip.Request.Builder request = OrderFormInterface.FinishTrip.Request.newBuilder();
                                    request.setOrderId(model.getOrderId());
                                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.FinishTrip_VALUE);
                                    networkTask.setTag("FinishTrip");
                                    networkTask.setBusiData(request.build().toByteArray());
                                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData) {
                                            if (responseData.getRet() == 0) {
                                                try {
                                                    ((BaseActivity) mFragment.getActivity()).showToast(responseData.getToastMsg());
                                                    OrderFormInterface26.CarOwnerConfirmIfGetCar.Response response = OrderFormInterface26.CarOwnerConfirmIfGetCar.Response.parseFrom(responseData.getBusiData());
                                                    if (response.getRet() == 0) {

                                                        goToFresh();

                                                        Config.showToast(context, "您已确认还车");
                                                        ObserverManager.getObserver("已完成").observer("", "show");
//                                                            ObserverManager.getObserver("gotoFinish").observer("", "");
                                                        //跳转到评价页面
                                                        Intent intent = new Intent();
                                                        intent.setClass(context, RenterOrderReviewActivity.class);
                                                        intent.putExtra(SysConfig.R_SN, model.getOrderId());
                                                        context.startActivityForResult(intent, 10002);
                                                    }

                                                } catch (InvalidProtocolBufferException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(VolleyError errorResponse) {
                                            Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                                        }

                                        @Override
                                        public void networkFinish() {
                                            Config.dismissProgress();
                                        }
                                    });
                                }
                            });
                            builder.create().show();
                        }
                    });
                }
                //租客用车已超时
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_USE_CAR_TIMEOUT)) {
                    tip.setText("您用车已超时,请尽快还车");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel() + " " + carBriefInfo.getLicensePlate());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setText("用车中");
                    tv_status.setTextColor(context.getResources().getColor(R.color.c3));
                    overRoot.setVisibility(View.VISIBLE);
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);
                    TextView over = (TextView) convertView.findViewById(R.id.over);
                    over.setText("结束行程");
                    overRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("确认已经还车,结束行程吗？");
                            builder.setNegativeButton("取消", null);
                            builder.setNeutralButton("结束行程", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Config.showProgressDialog(context, false, null);
                                    OrderFormInterface.FinishTrip.Request.Builder request = OrderFormInterface.FinishTrip.Request.newBuilder();
                                    request.setOrderId(model.getOrderId());
                                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.FinishTrip_VALUE);
                                    networkTask.setTag("FinishTrip");
                                    networkTask.setBusiData(request.build().toByteArray());
                                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData) {
                                            if (responseData.getRet() == 0) {
                                                try {
                                                    ((BaseActivity) mFragment.getActivity()).showToast(responseData.getToastMsg());
                                                    OrderFormInterface26.CarOwnerConfirmIfGetCar.Response response = OrderFormInterface26.CarOwnerConfirmIfGetCar.Response.parseFrom(responseData.getBusiData());
                                                    if (response.getRet() == 0) {
                                                        goToFresh();

                                                        Config.showToast(context, "您已确认还车");
//                                                            ObserverManager.getObserver("gotoFinish").observer("", "");

                                                        Intent intent = new Intent();
                                                        intent.setClass(context, RenterOrderReviewActivity.class);
                                                        intent.putExtra(SysConfig.R_SN, model.getOrderId());
                                                        context.startActivityForResult(intent, 10002);
                                                    }

                                                } catch (InvalidProtocolBufferException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(VolleyError errorResponse) {
                                            Config.showErrorDialog(mFragment.getActivity(), errorResponse.getLocalizedMessage() + "____" + errorResponse.getMessage());
                                        }

                                        @Override
                                        public void networkFinish() {
                                            Config.dismissProgress();
                                        }
                                    });
                                }
                            });
                            builder.create().show();
                        }
                    });
                }
                //租客结束行程（表示租客已还车）
                else if (status.equals(OrderFormCommon.OrderFormStatus.RENTER_END_TRIP) || status.equals(OrderFormCommon.OrderFormStatus.CAR_OWNER_GET_CAR_AND_FINISHED) || status.equals(OrderFormCommon.OrderFormStatus.CAR_OWNER_NOT_GET_BACK_CAR)) {
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setTextColor(context.getResources().getColor(R.color.c10));
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);

                    if (model.getIsRenterCommnet()) {
                        tip.setText("订单已完成 ,已评价");
                        tv_status.setText("已完成");
                        overRoot.setVisibility(View.GONE);
                    } else {
                        tip.setText("订单已完成 ,未评价");
                        tv_status.setText("未评价");
                        overRoot.setVisibility(View.VISIBLE);
                        TextView over = (TextView) convertView.findViewById(R.id.over);
                        over.setText("立即评价");
                        over.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

//                                    Intent intent = new Intent();
//                                    intent.setClass(context, OwnerEvaluationRenterActivity.class);
//                                    intent.putExtra(SysConfig.R_SN, model.getOrderId());
//                                    context.startActivityForResult(intent, RENTER_REVIEW);


                                Intent intent = new Intent();
                                intent.setClass(context, RenterOrderReviewActivity.class);
                                intent.putExtra(SysConfig.R_SN, model.getOrderId());
                                context.startActivityForResult(intent, 10002);
                            }
                        });

                    }
                }
                //客服已介入
                else if (status.equals(OrderFormCommon.OrderFormStatus.COMPANY_DEAL_WITH)) {
                    tip.setText("客服已介入");
                    start_time.setText(startDateString);
                    end_time.setText(endDateString);
                    brand.setText(carBriefInfo.getBrand() + carBriefInfo.getCarModel());
                    tv_status.setVisibility(View.VISIBLE);
                    tv_status.setTextColor(mFragment.getResources().getColor(R.color.c10));
                    UUAppCar.getInstance().display(carBriefInfo.getThumbImg(), carImg, R.drawable.list_car_img_def);

                    if (model.getIsRenterCommnet()) {
                        overRoot.setVisibility(View.GONE);
                        tv_status.setText("已评价");
                    } else {
                        overRoot.setVisibility(View.VISIBLE);
                        TextView over = (TextView) convertView.findViewById(R.id.over);
                        tv_status.setText("未评价");
                        over.setText("立即评价");
                        over.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent();
                                intent.setClass(context, RenterOrderReviewActivity.class);
                                intent.putExtra(SysConfig.R_SN, model.getOrderId());
                                context.startActivityForResult(intent, 10002);
//                                    Intent intent = new Intent();
//                                    intent.setClass(context, OwnerEvaluationRenterActivity.class);
//                                    intent.putExtra(SysConfig.R_SN, model.getOrderId());
//                                    context.startActivityForResult(intent, RENTER_REVIEW);
                            }
                        });

                    }
                }
                //车主确认已收到车订单已结束（行程已完成）
                else if (status.equals(OrderFormCommon.OrderFormStatus.CAR_OWNER_GET_CAR_AND_FINISHED)) {

                }
            } else {

            }

        }
        return convertView;
    }

    /**
     * desc:刷新下一页
     */
    public void goToFresh() {
        if (mFragment instanceof MyStrokeCurrentFragment) {
            MyStrokeCurrentFragment currentFragment = (MyStrokeCurrentFragment)mFragment;
            currentFragment.mSwiperefreshlayout.setRefreshing(true);
            currentFragment.page = 1;
            currentFragment.requestModel.pageRequest.setPageNo(currentFragment.page);
            currentFragment.getData();
        } else if (mFragment instanceof  MyStrokeCancelFragment) {
            MyStrokeCancelFragment cancelFragment = (MyStrokeCancelFragment)mFragment;
            cancelFragment.mSwiperefreshlayout.setRefreshing(true);
            cancelFragment.page = 1;
            cancelFragment.requestModel.pageRequest.setPageNo(cancelFragment.page);
            cancelFragment.getData();
        } else if (mFragment instanceof MyStrokeFinishFragment) {
            MyStrokeFinishFragment finishFragment = (MyStrokeFinishFragment)mFragment;
            finishFragment.mSwiperefreshlayout.setRefreshing(true);
            finishFragment.page = 1;
            finishFragment.requestModel.pageRequest.setPageNo(finishFragment.page);
            finishFragment.getData();
        }
    }

    /**
     * desc:更新adapter数据
     */
    public void adapterNotifyDataSetChanged() {
        if (this.mFragment instanceof MyStrokeCurrentFragment) {
            ((MyStrokeCurrentFragment) this.mFragment).adapter.notifyDataSetChanged();
        } else if (this.mFragment instanceof MyStrokeFinishFragment) {
            ((MyStrokeFinishFragment) this.mFragment).adapter.notifyDataSetChanged();
        } else if (this.mFragment instanceof MyStrokeCancelFragment) {
            ((MyStrokeCancelFragment) this.mFragment).adapter.notifyDataSetChanged();
        }
    }

    /**
     * desc:更新服务器端返回的文案
     * @param model
     * @param tip
     * @param tv_status
     */
    public void updateText(OrderFormCommon.TripOrderCard model, TextView tip, TextView tv_status) {
        //订单描述运控文案
        if (model.getOrderDiscrp() != null) {
            tip.setText(model.getOrderDiscrp());
        }
        //订单状态文案
        if (model.getOrderStatusDiscrp() != null) {
            tv_status.setText(model.getOrderStatusDiscrp().getText());
            if (model.getOrderStatusDiscrp().getTextHexColor() != null &&
                    model.getOrderStatusDiscrp().getTextHexColor().length() == 7) {
                tv_status.setTextColor(Color.parseColor(model.getOrderStatusDiscrp().getTextHexColor()));
            }
        }
    }
}
