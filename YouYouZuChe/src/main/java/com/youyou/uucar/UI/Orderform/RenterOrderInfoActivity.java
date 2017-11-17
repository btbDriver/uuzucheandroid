package com.youyou.uucar.UI.Orderform;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterOrderReviewActivity;
import com.youyou.uucar.UI.Main.MyStrokeFragment.SelectPayActivity;
import com.youyou.uucar.UI.Main.MyStrokeFragment.UseCouponActivity;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by taurusxi on 14-8-26.
 */
public class RenterOrderInfoActivity extends BaseActivity
{


    @InjectView(R.id.start_time_tv_1)
    TextView              mStartTimeTv1;
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
    BaseNetworkImageView  mCarDetails;
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
    @InjectView(R.id.plan_time)
    TextView              mPlanTime;
    @InjectView(R.id.linear_1)
    LinearLayout          mLinear1;
    @InjectView(R.id.start_time_2)
    TextView              mStartTime2;
    @InjectView(R.id.end_time_2)
    TextView              mEndTime2;
    @InjectView(R.id.actual_time_2)
    TextView              mActualTime2;
    @InjectView(R.id.view_1)
    View                  mView1;
    @InjectView(R.id.actual_tv)
    TextView              mActualTv;
    @InjectView(R.id.chaoshi)
    TextView              mChaoshi;
    @InjectView(R.id.chaoshi_time)
    TextView              mChaoshiTime;
    @InjectView(R.id.linear_2)
    LinearLayout          mLinear2;
    @InjectView(R.id.coupon_question)
    BaseNetworkImageView  mCouponQuestion;
    @InjectView(R.id.coupon)
    TextView              mCoupon;
    @InjectView(R.id.coupon_linear)
    RelativeLayout        mCouponLinear;
    @InjectView(R.id.question)
    BaseNetworkImageView  mQuestion;
    @InjectView(R.id.price)
    TextView              mPrice;
    @InjectView(R.id.check)
    CheckBox              mCheck;
    @InjectView(R.id.bjmp_choose_linear)
    LinearLayout          mBjmpChooseLinear;
    @InjectView(R.id.need_money)
    TextView              mNeedMoney;
    @InjectView(R.id.click)
    BaseNetworkImageView  mClick;
    @InjectView(R.id.money_1)
    LinearLayout          mMoney1;
    @InjectView(R.id.rent_fee)
    TextView              mRentFee;
    @InjectView(R.id.actual_insurance_question_1)
    BaseNetworkImageView  mActualInsuranceQuestion1;
    @InjectView(R.id.rent_insurance)
    TextView              mRentInsurance;
    @InjectView(R.id.bjmp_question)
    BaseNetworkImageView  mBjmpQuestion;
    @InjectView(R.id.rent_bjmp)
    TextView              mRentBjmp;
    @InjectView(R.id.bjmp_linear)
    LinearLayout          mBjmpLinear;
    @InjectView(R.id.coupon_question_2)
    BaseNetworkImageView  mCouponQuestion2;
    @InjectView(R.id.rent_coupon)
    TextView              mRentCoupon;
    @InjectView(R.id.rent_coupon_linear)
    LinearLayout          mRentCouponLinear;
    @InjectView(R.id.deposit_question)
    BaseNetworkImageView  mDepositQuestion;
    @InjectView(R.id.deposit_fee)
    TextView              mDepositFee;
    @InjectView(R.id.all_fee)
    TextView              mAllFee;
    @InjectView(R.id.sign_1)
    TextView              mSign1;
    @InjectView(R.id.account_balance)
    TextView              mAccountBalance;
    @InjectView(R.id.sign_2)
    TextView              mSign2;
    @InjectView(R.id.need_prepaid)
    TextView              mNeedPrepaid;
    @InjectView(R.id.pay_linear)
    LinearLayout          mPayLinear;
    @InjectView(R.id.need_money_show)
    LinearLayout          mNeedMoneyShow;
    @InjectView(R.id.money)
    LinearLayout          mMoney;
    @InjectView(R.id.actual_money_tv)
    TextView              mActualMoneyTv;
    @InjectView(R.id.actual_click)
    BaseNetworkImageView  mActualClick;
    @InjectView(R.id.actual_money)
    LinearLayout          mActualMoney;
    @InjectView(R.id.actual_rent_fee)
    TextView              mActualRentFee;
    @InjectView(R.id.actual_insurance_question)
    BaseNetworkImageView  mActualInsuranceQuestion;
    @InjectView(R.id.actual_rent_insurance)
    TextView              mActualRentInsurance;
    @InjectView(R.id.actual_bjmp_question)
    BaseNetworkImageView  mActualBjmpQuestion;
    @InjectView(R.id.actual_rent_bjmp)
    TextView              mActualRentBjmp;
    @InjectView(R.id.actual_bjmp_linear)
    LinearLayout          mActualBjmpLinear;
    @InjectView(R.id.coupon_questionshiji)
    BaseNetworkImageView  mCouponQuestionshiji;
    @InjectView(R.id.rent_coupon_shiji)
    TextView              mRentCouponShiji;
    @InjectView(R.id.rent_coupon_shiji_linear)
    LinearLayout          mRentCouponShijiLinear;
    @InjectView(R.id.actual_deposit_fee)
    TextView              mActualDepositFee;
    @InjectView(R.id.violations)
    TextView              mViolations;
    @InjectView(R.id.total_fee)
    TextView              mTotalFee;
    @InjectView(R.id.need_actual_money_show)
    LinearLayout          mNeedActualMoneyShow;
    @InjectView(R.id.actual_money_linear)
    LinearLayout          mActualMoneyLinear;
    @InjectView(R.id.cancle_money_tv)
    TextView              mCancleMoneyTv;
    @InjectView(R.id.cancle_click)
    BaseNetworkImageView  mCancleClick;
    @InjectView(R.id.cancle_money)
    LinearLayout          mCancleMoney;
    @InjectView(R.id.cancle_fee)
    TextView              mCancleFee;
    @InjectView(R.id.cancle_all_fee)
    TextView              mCancleAllFee;
    @InjectView(R.id.cancle_money_show)
    LinearLayout          mCancleMoneyShow;
    @InjectView(R.id.cancle_money_linear)
    LinearLayout          mCancleMoneyLinear;
    @InjectView(R.id.order_cancle)
    TextView              mOrderCancle;
    @InjectView(R.id.order_question)
    BaseNetworkImageView  mOrderQuestion;
    @InjectView(R.id.order_cancle_linear)
    LinearLayout          mOrderCancleLinear;
    @InjectView(R.id.ratingbar_owner)
    RatingBar             mRatingbarOwner;
    @InjectView(R.id.owner_evaluate)
    TextView              mOwnerEvaluate;
    @InjectView(R.id.owner_linear)
    LinearLayout          mOwnerLinear;
    @InjectView(R.id.kefu_content)
    TextView              mKefuContent;
    @InjectView(R.id.kefu_linear)
    LinearLayout          mKefuLinear;
    @InjectView(R.id.save)
    TextView              mSave;
    @InjectView(R.id.need_attention_linear)
    LinearLayout          mNeedAttentionLinear;
    @InjectView(R.id.button)
    TextView              mButton;
    @InjectView(R.id.button_linear)
    LinearLayout          mButtonLinear;
    @InjectView(R.id.main_linear)
    LinearLayout          mMainLinear;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    @InjectView(R.id.time_out_fee_linear)
    LinearLayout          mTimeOutFeeLinear;
    @InjectView(R.id.violations_linear)
    LinearLayout          mViolationsLinear;
    private String orderId;
    boolean isShow       = false;
    boolean isActualShow = false;
    boolean isCancleShow = false;
    private final static int SURE_ORDER_AND_PAY   = 1;
    private final static int OVER_TRIP            = 2;
    private final static int EVALUATION_OWNER     = 3;
    private final static int ORDER_CANCLE_REQUEST = 1001;
    private OrderFormCommon.RenterCostItems chooseRenterCostItems, noChooseRenterCostItems;
    private final static int COUPON            = 10001;
    private final static int RENTER_EVALUATION = 10002;
    private int renterStatus;
    Dialog dialog;
    private View   actionBarView;
    //    private Button mMapTv;
    private String mPhone;
    private String carSN = null;
    private String sessionKey;
    boolean isCancleOrder = true;
    List<String> conponIds;
    private float availableBlance;
    private float allBlance;
    private float needBlance;

    //    private int[] couponArray;
    @Override
    protected void  onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_verify_info);
        ButterKnife.inject(this);
        orderId = getIntent().getStringExtra(SysConfig.R_SN);
        getOrderData(orderId);
//        setActionBar();
        initNoteDataRefush();
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
                    getOrderData(orderId);
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    boolean isShowOwner = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {


        return isShowOwner;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_desc, menu);
        menu.findItem(R.id.action_save).setTitle("联系车主");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (id == R.id.action_save)
        {
            tellOwner();
        }
        return super.onOptionsItemSelected(item);
    }


    private void tellOwner()
    {
        if (mPhone != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage(mPhone);
            View view = getLayoutInflater().inflate(R.layout.tell_dialog, null);
            TextView phone = (TextView) view.findViewById(R.id.phone);
            phone.setText(mPhone);
            builder.setView(view);
            builder.setNegativeButton("拨打", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
//                    String inputStr;
//                    inputStr = mPhone;
                    if (mPhone.trim().length() != 0)
                    {
                        MobclickAgent.onEvent(context, "ContactOwner");
//                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
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
    }

    private void iniListen()
    {
        mMoney1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isShow)
                {
                    mNeedMoneyShow.setVisibility(View.GONE);
                    AnimationUtils.rotateIndicatingArrow180(mClick, false);
                }
                else
                {
                    AnimationUtils.rotateIndicatingArrow180(mClick, true);
                    mNeedMoneyShow.setVisibility(View.VISIBLE);
                }
                isShow = !isShow;
            }
        });
        mCancleMoney.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isCancleShow)
                {
                    mCancleMoneyShow.setVisibility(View.GONE);
                    AnimationUtils.rotateIndicatingArrow180(mCancleClick, false);
                }
                else
                {
                    AnimationUtils.rotateIndicatingArrow180(mCancleClick, true);
                    mCancleMoneyShow.setVisibility(View.VISIBLE);
                }
                isCancleShow = !isCancleShow;
            }
        });

        mActualMoney.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isActualShow)
                {
                    mNeedActualMoneyShow.setVisibility(View.GONE);
                    AnimationUtils.rotateIndicatingArrow180(mActualClick, false);
                }
                else
                {
                    mNeedActualMoneyShow.setVisibility(View.VISIBLE);
                    AnimationUtils.rotateIndicatingArrow180(mActualClick, true);
                }
                isActualShow = !isActualShow;
            }
        });

        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {


                if (isChecked)
                {
                    if (chooseRenterCostItems != null)
                    {
                        mRentFee.setText(String.format("%.2f", chooseRenterCostItems.getCarRentAmount()) + "元");
                        mRentInsurance.setText(String.format("%.2f", chooseRenterCostItems.getInsuranceCost()) + "元");
                        mBjmpLinear.setVisibility(View.VISIBLE);
                        mRentBjmp.setText(String.format("%.2f", chooseRenterCostItems.getNonDeductibleCost()) + "元");
                        mAllFee.setText(String.format("%.2f", chooseRenterCostItems.getTotalAdvancePayment()) + "元");
                        mNeedPrepaid.setText(String.format("%.2f", chooseRenterCostItems.getRechargeAmount()) + "元");
                        needBlance = chooseRenterCostItems.getRechargeAmount();
                        allBlance = chooseRenterCostItems.getTotalAdvancePayment();
                        mNeedMoney.setText("共需要预付款" + String.format("%.2f", chooseRenterCostItems.getTotalAdvancePayment()) + "元，还需支付" + String.format("%.2f", chooseRenterCostItems.getRechargeAmount()) + "元");
                    }
                }
                else
                {
                    if (noChooseRenterCostItems != null)
                    {
                        mRentFee.setText(String.format("%.2f", noChooseRenterCostItems.getCarRentAmount()) + "元");
                        mRentInsurance.setText(String.format("%.2f", noChooseRenterCostItems.getInsuranceCost()) + "元");
                        mBjmpLinear.setVisibility(View.GONE);
                        mRentBjmp.setText(String.format("%.2f", noChooseRenterCostItems.getNonDeductibleCost()) + "元");
                        mAllFee.setText(String.format("%.2f", noChooseRenterCostItems.getTotalAdvancePayment()) + "元");
                        mNeedPrepaid.setText(String.format("%.2f", noChooseRenterCostItems.getRechargeAmount()) + "元");
                        needBlance = noChooseRenterCostItems.getRechargeAmount();
                        allBlance = noChooseRenterCostItems.getTotalAdvancePayment();
                        mNeedMoney.setText("共需要预付款" + String.format("%.2f", noChooseRenterCostItems.getTotalAdvancePayment()) + "元，还需支付" + String.format("%.2f", noChooseRenterCostItems.getRechargeAmount()) + "元");
                    }
                }
                float needPay = 0f;
                if (isChecked)
                {
                    if (chooseRenterCostItems != null)
                    {
                        needPay = chooseRenterCostItems.getRechargeAmount();
                    }
                }
                else
                {
                    if (noChooseRenterCostItems != null)
                    {
                        needPay = noChooseRenterCostItems.getRechargeAmount();
                    }

                }
                if (needPay == 0f)
                {
                    mPayLinear.setVisibility(View.GONE);
                }
                else
                {
                    mPayLinear.setVisibility(View.VISIBLE);
                }
                float bjmpPay = 0f;
                if (isChecked)
                {
                    if (chooseRenterCostItems != null)
                    {
                        bjmpPay = chooseRenterCostItems.getNonDeductibleCost();
                    }
                }
                else
                {
                    if (noChooseRenterCostItems != null)
                    {
                        bjmpPay = noChooseRenterCostItems.getNonDeductibleCost();
                    }
                }
                if (bjmpPay == 0f)
                {
                    mBjmpLinear.setVisibility(View.GONE);
                }
                else
                {
                    mBjmpLinear.setVisibility(View.VISIBLE);
                }
            }
        });
        mBjmpChooseLinear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCheck.performClick();
            }
        });
        if (isCanUseCoupon)
        {
            mCouponLinear.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    Intent intent = new Intent();
                    intent.setClass(context, UseCouponActivity.class);
                    intent.putExtra(SysConfig.R_SN, orderId);
                    intent.putStringArrayListExtra(SysConfig.COUPON_ID, (ArrayList<String>) conponIds);
                    RenterOrderInfoActivity.this.startActivityForResult(intent, COUPON);
                }
            });
        }
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                operateData(renterStatus);
            }
        });

        mOrderQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(context, OrderCancleActivity.class);
                intent.putExtra("url", ServerMutualConfig.renter_cancel_order_url);
                intent.putExtra("cancle", isCancleOrder);
                intent.putExtra(SysConfig.R_SN, orderId);
                RenterOrderInfoActivity.this.startActivityForResult(intent, ORDER_CANCLE_REQUEST);
            }
        });

        mCarLinear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (carSN != null)
                {
                    Intent intent = new Intent();
                    intent.setClass(context, OldCarInfoActivity.class);

                    if (passedMsg != null && !passedMsg.equals(""))
                    {
                        intent.putExtra("passedMsg", passedMsg);
                    }
                    intent.putExtra(SysConfig.CAR_SN, carSN);
                    intent.putExtra(SysConfig.NEED_SUBMIT, false);
                    context.startActivity(intent);
                }
            }
        });

        mActualInsuranceQuestion1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.insurance_certificate + orderId);
//                intent.putExtra("url", ServerMutualConfig.coupon);
                intent.putExtra(SysConfig.TITLE, "保险凭证规则");
                context.startActivity(intent);
            }
        });

        mCouponQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.coupon);
                intent.putExtra(SysConfig.TITLE, "优惠券规则");
                context.startActivity(intent);
            }
        });
        mCouponQuestion2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.coupon);
                intent.putExtra(SysConfig.TITLE, "优惠券规则");
                context.startActivity(intent);
            }
        });
        mCouponQuestionshiji.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.coupon);
                intent.putExtra(SysConfig.TITLE, "优惠券规则");
                context.startActivity(intent);
            }
        });

        mActualInsuranceQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.insurance_certificate + orderId);
                intent.putExtra(SysConfig.TITLE, "保险凭证规则");
                context.startActivity(intent);
            }
        });
        mBjmpQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.bjmpurl);
                intent.putExtra(SysConfig.TITLE, "不计免赔");
                context.startActivity(intent);
            }
        });

        mQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(context, URLWebView.class);
                intent.putExtra("title", "不计免赔");
                intent.putExtra("url", ServerMutualConfig.bjmpurl);
                context.startActivity(intent);
            }
        });
        mActualBjmpQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(context, URLWebView.class);
                intent.putExtra("title", "不计免赔");
                intent.putExtra("url", ServerMutualConfig.bjmpurl);
                context.startActivity(intent);
            }
        });
        mDepositQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(context, URLWebView.class);
                intent.putExtra(SysConfig.TITLE, "押金费用");
                intent.putExtra("url", ServerMutualConfig.renter_yajin);
                context.startActivity(intent);
            }
        });
    }

    private void operateData(int renterStatus)
    {

        switch (renterStatus)
        {
            case SURE_ORDER_AND_PAY:
                sureAndPay();
                break;
            case OVER_TRIP:
                overTrip();
                break;
            case EVALUATION_OWNER:
                evaluationOwner();
                break;
            default:
                break;
        }
    }

    private void evaluationOwner()
    {
        Intent intent = new Intent();
        intent.setClass(context, RenterOrderReviewActivity.class);
        intent.putExtra(SysConfig.R_SN, orderId);
        RenterOrderInfoActivity.this.startActivityForResult(intent, RENTER_EVALUATION);
    }

    private void overTrip()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认已还车，结束行程吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dia, int which)
            {
                dialog.cancel();

            }
        }).setPositiveButton("结束行程", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dia, int whichButton)
            {
                Config.showProgressDialog(context, true, null);
                NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.FinishTrip_VALUE);
                OrderFormInterface.FinishTrip.Request.Builder request = OrderFormInterface.FinishTrip.Request.newBuilder();
                request.setOrderId(orderId);
                network.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>()
                {
                    @Override
                    public void onSuccessResponse(UUResponseData response)
                    {
                        if (response.getRet() == 0)
                        {
                            Config.showToast(context, response.getToastMsg());
                            try
                            {
                                OrderFormInterface.FinishTrip.Response data = OrderFormInterface.FinishTrip.Response.parseFrom(response.getBusiData());
                                if (data.getRet() == 0)
                                {
                                    //结束行程成功
                                    getOrderData(orderId);
                                    MainActivityTab.instance.order.needRefush = true;
                                    MainActivityTab.instance.order.currentRefush = true;
                                    MainActivityTab.instance.order.cancelRefush = true;
                                    MainActivityTab.instance.order.finishRefush = true;
                                    Config.showToast(context, "您已确认还车");

                                    Intent intent = new Intent();
                                    intent.setClass(context, RenterOrderReviewActivity.class);
                                    intent.putExtra(SysConfig.R_SN, orderId);
                                    context.startActivityForResult(intent, 10002);
                                }
                                else if (data.getRet() == -1)
                                {
                                    //结束行程失败
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
        });
        dialog = builder.create();
        dialog.show();
    }

    boolean isSelectCouponDialog = false;

    private void sureAndPay()
    {

        //这张订单可以使用优惠券,并且可以使用的优惠券数量大于0,并且还没使用过优惠券
        if (orderInfo.getCanSelectCoupon() == 1 && orderInfo.getIfHasTicket() > 0 && orderInfo.getCouponAmount() == 0 && !isSelectCouponDialog)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("您还有优惠券未使用,不要错过优惠良机哦!");
            builder.setNegativeButton("选择优惠券", new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {

                    Intent intent = new Intent();
                    intent.setClass(context, UseCouponActivity.class);
                    intent.putExtra(SysConfig.R_SN, orderId);
                    intent.putStringArrayListExtra(SysConfig.COUPON_ID, (ArrayList<String>) conponIds);
                    RenterOrderInfoActivity.this.startActivityForResult(intent, COUPON);
                }
            });
            builder.setNeutralButton("不使用优惠券", new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    isSelectCouponDialog = true;
                    sureAndPay();
                }
            });
            Dialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return;
        }
        if (needBlance == 0)//余额>=总价钱
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("将从您的帐户余额中冻结" + String.format("%.2f", allBlance) + "元用于此次租车预付款");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dia, int which)
                {
                    dialog.cancel();
                    isSelectCouponDialog = false;

                }
            }).setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dia, int whichButton)
                {
                    pay();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
        else if (needBlance != allBlance && needBlance < allBlance)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("将从您的帐户余额中冻结" + String.format("%.2f", allBlance - needBlance) + "元用于此次租车预付款，您还需支付" + String.format("%.2f", needBlance) + "元");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dia, int which)
                {
                    dialog.cancel();
                    isSelectCouponDialog = false;
                }
            }).setPositiveButton("继续支付", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dia, int whichButton)
                {
                    pay();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
        else
        {
            pay();
        }
    }

    private void pay()
    {
        Config.showProgressDialog(context, true, null);
        NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.ConfirmToPayNew_VALUE);
        OrderFormInterface.ConfirmToPayNew.Request.Builder request = OrderFormInterface.ConfirmToPayNew.Request.newBuilder();
        request.setIsNonDeductibleChecked(mCheck.isChecked());
        request.setOrderId(orderId);
//        if (conponIds != null && conponIds.size() > 0) {
//            for (int i = 0; i < couponList.size(); i++) {
//                request.setCouponIds(i, couponList.get(i));
//            }
//        }
        if (conponIds != null && conponIds.size() > 0)
        {
            request.addAllCouponIds(conponIds);
        }
        request.setSessionKey(sessionKey);
        network.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData response)
            {
                if (response.getRet() == 0)
                {
                    Config.showToast(context, response.getToastMsg());
                    try
                    {
                        OrderFormInterface.ConfirmToPayNew.Response data = OrderFormInterface.ConfirmToPayNew.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0)
                        {
                            //支付成功
                            getOrderData(orderId);
                        }
                        else if (data.getRet() == -1)
                        {
                            //支付失败
                        }
                        else if (data.getRet() == -2)
                        {
                            //需要充值
                            Intent intent = new Intent();
                            Config.payList = new ArrayList<OrderFormInterface.ConfirmToPayNew.PayTypeInfo>();
                            float needRechargeAmount = data.getNeedRechargeAmount(); //需要充值的数额
                            int payTypeInfoListCount = data.getPayTypeInfoListCount();
                            if (payTypeInfoListCount > 0)
                            {
                                Config.payList = data.getPayTypeInfoListList();// 可用的支付方式
                            }
                            intent.setClass(context, SelectPayActivity.class);
                            intent.putExtra(SysConfig.R_SN, orderId);
                            intent.putExtra(SysConfig.NEED_RECHANGE_AMOUNT, needRechargeAmount);
                            RenterOrderInfoActivity.this.startActivityForResult(intent, SysConfig.RENTER_PAY);
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


    boolean isCanUseCoupon = true;
    public String passedMsg = "";
    OrderFormCommon.RenterOrderFormInfo orderInfo;

    public void getOrderData(final String orderId)
    {

        NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.RenterQueryOrderDetail_VALUE);
        OrderFormInterface26.RenterQueryOrderDetail.Request.Builder request = OrderFormInterface26.RenterQueryOrderDetail.Request.newBuilder();
        request.setOrderId(orderId);
        if (conponIds != null && conponIds.size() > 0)
        {
            request.addAllCouponIds(conponIds);
        }
        network.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>()
        {

            @Override
            public void onSuccessResponse(UUResponseData response)
            {
                if (response.getRet() == 0)
                {
                    try
                    {
                        OrderFormInterface26.RenterQueryOrderDetail.Response data = OrderFormInterface26.RenterQueryOrderDetail.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0)
                        {

                            mAllFramelayout.makeProgreeDismiss();
                            orderInfo = data.getOrderInfo();
                            mRsn.setText("订单号：" + orderInfo.getOrderId());
                            OrderFormCommon.OrderFormPropertys orderFormPropertys = orderInfo.getOrderFormPropertys();
//                            String carId = orderFormPropertys.getCarId();  // 车辆id
//                            int carOwnerUsrId = orderFormPropertys.getCarOwnerUsrId();  // 车主用户id
//                            int renterUsrId = orderFormPropertys.getRenterUsrId(); // 租客用户id
                            if (orderInfo.hasSessionKey())
                            {
                                sessionKey = orderInfo.getSessionKey();
                            }
                            int planToStartTime = orderFormPropertys.getPlanToStartTime(); // 订单计划开始时间戳，单位秒
                            mStartTime.setText(Config.getFormatTime(planToStartTime));
                            mStartTime2.setText(Config.getFormatTime(planToStartTime));
                            int planToEndTime = orderFormPropertys.getPlanToEndTime();// 订单计划结束时间戳，单位秒
                            mEndTime.setText(Config.getFormatTime(planToEndTime));
                            mEndTime2.setText(Config.getFormatTime(planToEndTime));
                            String orderDuration = orderFormPropertys.getOrderDuration(); // 订单时长，根据订单计划开始、结束计算得到的
                            mPlanTime.setText(orderDuration);
                            int timesToEnd = orderFormPropertys.getTimesToEnd();// 距离订单结束的时间，单位秒，该值等于 planToEndTime - 当前时间，也就是说，如果订单还没超时，则该值是正数，如果已超时，则是负数
                            int orderFormCreateTime = orderFormPropertys.getOrderFormCreateTime();// 订单建立时间戳，单位秒，如果是一对一、一对多约车，则是车主同意意向单的时间；如果是快速约车，则是租客选定车的时间
                            mStartTimeTv1.setText("车主" + Config.getShortFormatTime(orderFormCreateTime) + "分同意您的预约");
                            if (orderFormPropertys.hasRenterEndTripTime())
                            {
                                int renterEndTripTime = orderFormPropertys.getRenterEndTripTime(); // 租客还车（结束行程）时间戳，单位秒
                                mActualTime2.setText(Config.getFormatTime(renterEndTripTime));
                            }
                            int renterTimeLimitToPay = orderInfo.getRenterTimeLimitToPay();//租客需要在多少时间内完成支付，单位分钟

                            mStartTimeTv1.setText("租约已在" + Config.getShortFormatTime(orderFormCreateTime) + "达成，请在" + renterTimeLimitToPay + "分钟内支付");
                            if (orderFormPropertys.hasActualOrderDuration())
                            {
                                String actualOrderDuration = orderFormPropertys.getActualOrderDuration();// 实际租时
                                mActualTv.setText(actualOrderDuration);
                            }
                            if (orderFormPropertys.hasRenterOvertime())
                            {
                                String renterOvertime = orderFormPropertys.getRenterOvertime();// 超时时间
                                mChaoshiTime.setText(renterOvertime);
                            }
                            CarCommon.CarBriefInfo carBriefInfo = orderInfo.getCarBriefInfo();// 车辆信息

                            if (carBriefInfo.hasPassedMsg() && carBriefInfo.getPassedMsg() != null && !carBriefInfo.getPassedMsg().equals(""))
                            {
                                passedMsg = carBriefInfo.getPassedMsg();
                            }
                            String carId1 = carBriefInfo.getCarId();// 车辆唯一标示id
                            carSN = carId1;
                            String licensePlate = carBriefInfo.getLicensePlate();// 汽车牌照
                            mPlateNumber.setText(licensePlate);
                            String brand = carBriefInfo.getBrand();// 汽车品牌
                            String carModel = carBriefInfo.getCarModel();// 汽车型号
                            mBrand.setText(brand + carModel);
                            if (orderInfo.getCouponIdsCount() > 0)
                            {
                                conponIds = orderInfo.getCouponIdsList();
                            }
                            CarCommon.CarTransmissionType transmissionType = carBriefInfo.getTransmissionType();// 变速箱类型
                            if (carBriefInfo.hasPricePerDay())
                            {
                                float pricePerDay = carBriefInfo.getPricePerDay(); // 汽车价格，按天算
                                String result = "";
                                if ((pricePerDay + "").endsWith(".0"))
                                {
                                    result = ((int) pricePerDay) + "";
                                }
                                else
                                {
                                    result = pricePerDay + "";
                                }
                                mPriceDay.setText(result + "元/天");
                            }
                            if (carBriefInfo.hasThumbImg())
                            {
                                String thumbImg = carBriefInfo.getThumbImg(); // 车辆缩略图
                                UUAppCar.getInstance().display(thumbImg, mCarImg, R.drawable.list_car_img_def);
                            }
                            boolean isNonDeductible = orderInfo.getIsNonDeductible();  // true:选中不计免赔；false：未选中
                            mCheck.setChecked(isNonDeductible);
//                            float totalAdvancePayment = orderInfo.getTotalAdvancePayment();// 总需预付款数
                            float couponAmount = orderInfo.getCouponAmount();// 优惠券金额数


                            if (orderInfo.getCanSelectCoupon() == 0)
                            {
                                isCanUseCoupon = false;
                                mCoupon.setText("不可与活动同时使用");
                            }
                            else
                            {
                                isCanUseCoupon = true;
                                mCoupon.setText(String.format("%.2f", couponAmount) + "元");
                                if (couponAmount == 0)
                                {
                                    mCoupon.setText(orderInfo.getIfHasTicket() + "张可用");
                                }
                            }
                            float nonDeductiblePerDay = orderInfo.getNonDeductiblePerDay();// 不计免赔每天费用
                            mPrice.setText(String.format("%.2f", nonDeductiblePerDay) + "元/天");
                            float accountBalance = orderInfo.getAccountBalance(); // 租客账户余额
                            mAccountBalance.setText(String.format("%.2f", accountBalance) + "元");
//                            float rechargeAmount = orderInfo.getRechargeAmount();// 租客需充值数
//                            mNeedPrepaid.setText(rechargeAmount + "元");
//                            mNeedMoney.setText("共需要预款" + totalAdvancePayment + "元，需充值" + rechargeAmount + "元");
                            float depositCost = orderInfo.getDepositCost();// 押金费用
                            mDepositFee.setText(String.format("%.2f", depositCost) + "元");

                            if (orderInfo.hasPreCostNonDeductibleChecked())
                            {

                                chooseRenterCostItems = orderInfo.getPreCostNonDeductibleChecked(); // 不计免赔选中时租客需付款项;
//                                OrderFormCommon.RenterCostItems preCostNonDeductibleChecked = orderInfo.getPreCostNonDeductibleChecked(); // 不计免赔选中时租客需付款项
//                                float carRentAmount = preCostNonDeductibleChecked.getCarRentAmount();// 汽车租金
//                                mRentFee.setText(carRentAmount + "元");
//                                float insuranceCost = preCostNonDeductibleChecked.getInsuranceCost();// 保险费用
//                                float nonDeductibleCost = preCostNonDeductibleChecked.getNonDeductibleCost(); // 不计免赔费用
//                                float renterUserCarOvertimeCost = preCostNonDeductibleChecked.getRenterUserCarOvertimeCost(); // 租客用车超时产生的费用
//                                float renterTrafficViolationCost = preCostNonDeductibleChecked.getRenterTrafficViolationCost();// 租客交通违章产生的费用
//                                float renterBreakContractCost = preCostNonDeductibleChecked.getRenterBreakContractCost();// 租客违约产生的费用（订单支付后，租客取消订单产生的违约费用）
//                                float actualCost = preCostNonDeductibleChecked.getActualCost();// 租客实际支出
                            }
                            if (orderInfo.hasPreCostNonDeductibleNotChecked())
                            {
                                noChooseRenterCostItems = orderInfo.getPreCostNonDeductibleNotChecked();/// 不计免赔未选中时租客需付款项
                            }
                            if (isNonDeductible)
                            {
                                mRentFee.setText(String.format("%.2f", chooseRenterCostItems.getCarRentAmount()) + "元");
                                mRentInsurance.setText(String.format("%.2f", chooseRenterCostItems.getInsuranceCost()) + "元");
                                mBjmpLinear.setVisibility(View.VISIBLE);
                                mRentBjmp.setText(String.format("%.2f", chooseRenterCostItems.getNonDeductibleCost()) + "元");
                                mAllFee.setText(String.format("%.2f", chooseRenterCostItems.getTotalAdvancePayment()) + "元");
                                mNeedPrepaid.setText(String.format("%.2f", chooseRenterCostItems.getRechargeAmount()) + "元");
                                needBlance = chooseRenterCostItems.getRechargeAmount();
                                allBlance = chooseRenterCostItems.getTotalAdvancePayment();
                                mNeedMoney.setText("共需要预付款" + String.format("%.2f", chooseRenterCostItems.getTotalAdvancePayment()) + "元，还需支付" + String.format("%.2f", chooseRenterCostItems.getRechargeAmount()) + "元");
                            }
                            else
                            {
//                                mRentFee.setText(noChooseRenterCostItems.getCarRentAmount() + "元");
//                                mRentInsurance.setText(noChooseRenterCostItems.getInsuranceCost() + "元");
//                                mBjmpLinear.setVisibility(View.GONE);
//                                mRentBjmp.setText(noChooseRenterCostItems.getNonDeductibleCost() + "元");
//                                mAllFee.setText(noChooseRenterCostItems.getTotalAdvancePayment() + "元");
//                                mNeedPrepaid.setText(noChooseRenterCostItems.getRechargeAmount() + "元");
                                mRentFee.setText(String.format("%.2f", noChooseRenterCostItems.getCarRentAmount()) + "元");
                                mRentInsurance.setText(String.format("%.2f", noChooseRenterCostItems.getInsuranceCost()) + "元");
                                mBjmpLinear.setVisibility(View.GONE);
                                mRentBjmp.setText(String.format("%.2f", noChooseRenterCostItems.getNonDeductibleCost()) + "元");
                                mAllFee.setText(String.format("%.2f", noChooseRenterCostItems.getTotalAdvancePayment()) + "元");
                                mNeedPrepaid.setText(String.format("%.2f", noChooseRenterCostItems.getRechargeAmount()) + "元");
                                needBlance = noChooseRenterCostItems.getRechargeAmount();
                                allBlance = noChooseRenterCostItems.getTotalAdvancePayment();
                                mNeedMoney.setText("共需要预付款" + String.format("%.2f", noChooseRenterCostItems.getTotalAdvancePayment()) + "元，还需支付" + String.format("%.2f", noChooseRenterCostItems.getRechargeAmount()) + "元");
                            }


                            if (orderInfo.hasRenterComment())
                            {
                                OrderFormCommon.OrderComment renterComment = orderInfo.getRenterComment();  // 租客评价
                                int fromUserId = renterComment.getFromUserId();// 谁评论的
                                int toUserId = renterComment.getToUserId(); // 谁被评论
                                float stars = renterComment.getStars();
                                mRatingbarOwner.setRating(stars);
                                String content = renterComment.getContent();// 评价内容
                                mOwnerEvaluate.setText(content);
                            }
                            if (orderInfo.hasCustomerServiceNote())
                            {
                                String customerServiceNote = orderInfo.getCustomerServiceNote();   // 客服补充

                                if (customerServiceNote != null && !customerServiceNote.trim().equals(""))
                                {
                                    mKefuContent.setText(customerServiceNote);
                                }
                                else
                                {
                                    mKefuLinear.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                mKefuLinear.setVisibility(View.GONE);
                            }
                            if (orderInfo.hasCarOwnerPhone())
                            {
                                String carOwnerPhone = orderInfo.getCarOwnerPhone(); // 车主手机联系号码
                                mPhone = carOwnerPhone;
                                isShowOwner = true;
//                                mMapTv.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                mPhone = null;
                                isShowOwner = false;
//                                mMapTv.setVisibility(View.INVISIBLE);
                            }
                            invalidateOptionsMenu();
                            OrderFormCommon.OrderFormStatus status = orderInfo.getStatus();   // 订单状态
                            boolean isHasComment = orderInfo.hasRenterComment();
                            OrderFormCommon.OrderComment renterComment = orderInfo.getRenterComment();

                            String payTypeStr = "";
                            if (orderInfo.hasUserChargeType())
                            {
                                OrderFormCommon.UserChargeType userChargeType = orderInfo.getUserChargeType();
                                if (userChargeType.getNumber() == OrderFormCommon.UserChargeType.ALIPAY_VALUE)
                                {
                                    payTypeStr = "支付宝支付";
                                }
                                else if (userChargeType.getNumber() == OrderFormCommon.UserChargeType.UPAY_VALUE)
                                {
                                    payTypeStr = "U付支付";
                                }
                                else if (userChargeType.getNumber() == OrderFormCommon.UserChargeType.WECHAT_VALUE)
                                {
                                    payTypeStr = "微信支付";
                                }
                            }
                            String totalAdvancePaymentFloat = "";
                            if (orderInfo.getIsNonDeductible())
                            {
                                totalAdvancePaymentFloat = String.format("%.2f", chooseRenterCostItems.getTotalAdvancePayment());
                            }
                            else
                            {
                                totalAdvancePaymentFloat = String.format("%.2f", noChooseRenterCostItems.getTotalAdvancePayment());
                            }
                            int color3 = getResources().getColor(R.color.c3);
                            int color8 = getResources().getColor(R.color.c8);
                            //TODO 颜色
                            if (status.getNumber() == OrderFormCommon.OrderFormStatus.WAIT_RENTER_PAY_VALUE)
                            {
                                mSign2.setTextColor(color3);
                                mNeedPrepaid.setTextColor(color3);
                            }
                            else
                            {
                                mSign2.setTextColor(color8);
                                mNeedPrepaid.setTextColor(color8);
                            }

                            switch (status.getNumber())
                            {
                                case OrderFormCommon.OrderFormStatus.WAIT_RENTER_PAY_VALUE:   // 等待租客支付
                                    mStatus.setText("订单状态：待确认未支付");
                                    mTipsLinear.setVisibility(View.VISIBLE);
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mCouponLinear.setVisibility(View.VISIBLE);
                                    mBjmpChooseLinear.setVisibility(View.VISIBLE);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.VISIBLE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.GONE);
                                    mButton.setText("确认并支付");
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    renterStatus = SURE_ORDER_AND_PAY;
                                    mSign1.setText("可用余额");
                                    mSign2.setText("还需支付");
                                    mPayLinear.setVisibility(View.VISIBLE);
                                    isCancleOrder = true;
                                    mCouponLinear.setEnabled(true);
                                    mBjmpChooseLinear.setEnabled(true);
                                    mCheck.setEnabled(true);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_PAY_TIMEOUT_VALUE:  // 租客付款超时
                                    mStatus.setText("订单状态：您在" + renterTimeLimitToPay + "分钟内未付款，订单已取消");
                                    mSign1.setText("可用余额");
                                    mSign2.setText("还需支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mPayLinear.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.VISIBLE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                    isCancleOrder = false;
                                    mButtonLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_CANCEL_BEFORE_PAY_VALUE:// 租客付款前取消订单
                                    mStatus.setText("订单状态：您已取消订单");
                                    mSign1.setText("可用余额");
                                    mSign2.setText("还需支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mPayLinear.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    isCancleOrder = false;
                                    mOrderCancleLinear.setVisibility(View.VISIBLE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    break;

                                case OrderFormCommon.OrderFormStatus.WAIT_RENTER_TAKE_CAR_VALUE: // 租客待取车
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mSign1.setText("余额支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mSign2.setText(payTypeStr);
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mStatus.setText("订单状态：待取车");
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    isCancleOrder = true;
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.VISIBLE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_CANCEL_AFTER_PAY_VALUE:// 租客付款后取消订单，将会产生扣费项目
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mStatus.setText("订单状态：您已取消订单");
                                    mTipsLinear.setVisibility(View.GONE);
                                    mCancleMoneyLinear.setVisibility(View.VISIBLE);
                                    mLinear2.setVisibility(View.GONE);
                                    mSign1.setText("可用余额");
                                    mSign2.setText("还需支付");
                                    mPayLinear.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    isCancleOrder = false;
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                    mButtonLinear.setVisibility(View.GONE);
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_CONFIRM_ORDER_WAIT_PAY_VALUE:// 租客确认订单，但是还没付款
                                    mStatus.setText("订单状态：已确认未支付");
                                    mSign1.setText("可用余额");
                                    mSign2.setText("还需支付");
                                    mPayLinear.setVisibility(View.VISIBLE);
                                    mTipsLinear.setVisibility(View.VISIBLE);
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    isCancleOrder = true;
                                    mCouponLinear.setVisibility(View.VISIBLE);
                                    mCouponLinear.setEnabled(false);
                                    if (couponAmount == 0)
                                    {
                                        mCoupon.setText("未使用");
                                    }
                                    if (!isNonDeductible)
                                    {
                                        mPrice.setText("未购买");
                                    }
                                    else
                                    {
                                        mPrice.setText("已购买");
                                    }
                                    findViewById(R.id.coupon_arrow).setVisibility(View.GONE);
                                    mCheck.setEnabled(false);
                                    mCheck.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.VISIBLE);
                                    mBjmpChooseLinear.setEnabled(false);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.VISIBLE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.GONE);
                                    mButton.setText("立即支付");
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    renterStatus = SURE_ORDER_AND_PAY;
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_ON_TRIP_VALUE: // 租客正在用车
                                    mTipsLinear.setVisibility(View.GONE);
                                    mSign1.setText("余额支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mSign2.setText("还需支付");
                                    mPayLinear.setVisibility(View.VISIBLE);
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mStatus.setText("订单状态：用车中");
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    isCancleOrder = true;
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                    mButton.setText("结束行程");
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    renterStatus = OVER_TRIP;
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_USE_CAR_TIMEOUT_VALUE:// 租客用车已超时
                                    mTipsLinear.setVisibility(View.GONE);
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mStatus.setText("订单状态：用车已超时");
                                    mSign1.setText("余额支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mSign2.setText(payTypeStr);
                                    mLinear2.setVisibility(View.GONE);
                                    mLinear1.setVisibility(View.VISIBLE);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mActualMoneyLinear.setVisibility(View.GONE);
                                    isCancleOrder = true;
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mOwnerLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                    mButton.setText("结束行程");
                                    mButtonLinear.setVisibility(View.VISIBLE);
                                    renterStatus = OVER_TRIP;
                                    break;
                                case OrderFormCommon.OrderFormStatus.RENTER_END_TRIP_VALUE:// 租客结束行程（表示租客已还车）
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.GONE);
                                    mSign1.setText("余额支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mSign2.setText(payTypeStr);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    isCancleOrder = true;
                                    mActualMoneyLinear.setVisibility(View.VISIBLE);
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.GONE);

                                    if (isHasComment)
                                    {
                                        mStatus.setText("订单状态：已完成已评价");
                                        mOwnerLinear.setVisibility(View.VISIBLE);
                                        mButtonLinear.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        mStatus.setText("订单状态：已完成未评价");
                                        mOwnerLinear.setVisibility(View.GONE);
                                        mButton.setText("评价车主");
                                        mButtonLinear.setVisibility(View.VISIBLE);
                                        renterStatus = EVALUATION_OWNER;
                                    }
                                    break;
                                case OrderFormCommon.OrderFormStatus.CAR_OWNER_NOT_GET_BACK_CAR_VALUE:// 车主确认未收到车
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mStatus.setText("订单状态：车主未收到车");
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.GONE);
                                    mSign1.setText("余额支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mSign2.setText(payTypeStr);
                                    mCouponLinear.setVisibility(View.GONE);
                                    isCancleOrder = true;
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.GONE);
//                                    if (isHasComment){
//                                        mStatus.setText("订单状态：已完成已评价");
//
//                                    }else {
//                                        mStatus.setText("订单状态：已完成未评价");
//                                    }
                                    break;
                                case OrderFormCommon.OrderFormStatus.COMPANY_DEAL_WITH_VALUE: // 客服已介入
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mStatus.setText("订单状态：客服已介入");
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.GONE);
                                    mSign1.setText("余额支付");
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mSign2.setText(payTypeStr);
                                    mCouponLinear.setVisibility(View.GONE);
                                    isCancleOrder = true;
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.GONE);
                                    if (isHasComment)
                                    {
                                        mOwnerLinear.setVisibility(View.VISIBLE);
                                        mButtonLinear.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        mOwnerLinear.setVisibility(View.GONE);
                                        mButton.setText("评价车主");
                                        mButtonLinear.setVisibility(View.VISIBLE);
                                        renterStatus = EVALUATION_OWNER;
                                    }
//                                    if (isHasComment){
//                                        mStatus.setText("订单状态：已完成已评价");
//
//                                    }else {
//                                        mStatus.setText("订单状态：已完成未评价");
//                                    }
                                    break;
                                case OrderFormCommon.OrderFormStatus.CAR_OWNER_GET_CAR_AND_FINISHED_VALUE:// 车主确认已收到车（订单已完成）
                                    mNeedMoney.setText("预付款" + totalAdvancePaymentFloat + "元");
                                    mTipsLinear.setVisibility(View.GONE);
                                    mLinear2.setVisibility(View.VISIBLE);
                                    mLinear1.setVisibility(View.GONE);
                                    mCancleMoneyLinear.setVisibility(View.GONE);
                                    mCouponLinear.setVisibility(View.GONE);
                                    mSign1.setText("余额支付");

                                    mSign2.setText(payTypeStr);
                                    mBjmpChooseLinear.setVisibility(View.GONE);
                                    isCancleOrder = true;
                                    mActualMoneyLinear.setVisibility(View.VISIBLE);
                                    mOrderCancleLinear.setVisibility(View.GONE);
                                    mNeedAttentionLinear.setVisibility(View.GONE);
                                    if (isHasComment)
                                    {
                                        mStatus.setText("订单状态：已完成已评价");
                                        mOwnerLinear.setVisibility(View.VISIBLE);
                                        mButtonLinear.setVisibility(View.GONE);

                                    }
                                    else
                                    {
                                        mStatus.setText("订单状态：已完成未评价");
                                        mOwnerLinear.setVisibility(View.GONE);
                                        mButton.setText("评价车主");
                                        mButtonLinear.setVisibility(View.VISIBLE);
                                        renterStatus = EVALUATION_OWNER;
                                    }
                                    break;
                            }
                            String nNeedPreStr = mNeedPrepaid.getText() + "";
                            if (nNeedPreStr.equals("0.00元"))
                            {
                                mPayLinear.setVisibility(View.GONE);
                            }
                            else
                            {
                                mPayLinear.setVisibility(View.VISIBLE);
                            }

                            mRentCoupon.setText(String.format("%.2f", couponAmount) + "元");
                            if (couponAmount == 0f)
                            {
                                mRentCouponLinear.setVisibility(View.GONE);
                            }
                            else
                            {
                                mRentCouponLinear.setVisibility(View.VISIBLE);
                            }
                            mRentCouponShiji.setText(String.format("%.2f", couponAmount) + "元");
                            if (couponAmount == 0f)
                            {
                                mRentCouponShijiLinear.setVisibility(View.GONE);
                            }
                            else
                            {
                                mRentCouponShijiLinear.setVisibility(View.VISIBLE);
                            }
                            float bjmpPay = 0f;
                            if (isNonDeductible)
                            {
                                if (chooseRenterCostItems != null)
                                {
                                    bjmpPay = chooseRenterCostItems.getNonDeductibleCost();
                                }

                            }
                            else
                            {
                                if (noChooseRenterCostItems != null)
                                {
                                    bjmpPay = noChooseRenterCostItems.getNonDeductibleCost();
                                }
                            }
                            if (bjmpPay == 0f)
                            {
                                mBjmpLinear.setVisibility(View.GONE);
                            }
                            else
                            {
                                mBjmpLinear.setVisibility(View.VISIBLE);
                            }


                            if (orderInfo.hasAttentionNotes())
                            {
                                String attentionNotes = orderInfo.getAttentionNotes();// 注意事项
                                if (attentionNotes != null && !attentionNotes.trim().equals(""))
                                {
                                    mSave.setText(attentionNotes);
                                    mNeedAttentionLinear.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    mNeedAttentionLinear.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                mNeedAttentionLinear.setVisibility(View.GONE);
                            }


                            if (orderInfo.hasFinallyCost())
                            {
                                OrderFormCommon.RenterCostItems finallyCost = orderInfo.getFinallyCost();/// // 租客最终消费
                                float carRentAmount = finallyCost.getCarRentAmount();// 汽车租金
                                mActualRentFee.setText(String.format("%.2f", carRentAmount) + "元");
                                float insuranceCost = finallyCost.getInsuranceCost();// 保险费用
                                mActualRentInsurance.setText(String.format("%.2f", insuranceCost) + "元");
                                float nonDeductibleCost = finallyCost.getNonDeductibleCost(); // 不计免赔费用
                                mActualRentBjmp.setText(String.format("%.2f", nonDeductibleCost) + "元");
                                float renterUserCarOvertimeCost = finallyCost.getRenterUserCarOvertimeCost(); // 租客用车超时产生的费用
                                mActualDepositFee.setText(String.format("%.2f", renterUserCarOvertimeCost) + "元");
                                float renterTrafficViolationCost = finallyCost.getRenterTrafficViolationCost();// 租客交通违章产生的费用
                                mViolations.setText(String.format("%.2f", renterTrafficViolationCost) + "元");
                                float renterBreakContractCost = finallyCost.getRenterBreakContractCost();// 租客违约产生的费用（订单支付后，租客取消订单产生的违约费用）
                                float actualCost = finallyCost.getActualCost();// 租客实际支出
                                mTotalFee.setText(String.format("%.2f", actualCost) + "元");
                                mActualMoneyTv.setText("实际消费" + String.format("%.2f", actualCost) + "元");
                                mCancleMoneyTv.setText("实际消费" + String.format("%.2f", actualCost) + "元");
                                mCancleFee.setText(String.format("%.2f", renterBreakContractCost) + "元");
                                mCancleAllFee.setText(String.format("%.2f", actualCost) + "元");

                                if (nonDeductibleCost == 0f)
                                {
                                    mActualBjmpLinear.setVisibility(View.GONE);
                                }
                                else
                                {
                                    mActualBjmpLinear.setVisibility(View.VISIBLE);
                                }

                                if (renterUserCarOvertimeCost == 0f)
                                {
                                    mTimeOutFeeLinear.setVisibility(View.GONE);
                                }
                                else
                                {
                                    mTimeOutFeeLinear.setVisibility(View.VISIBLE);
                                }

                                if (renterTrafficViolationCost == 0f)
                                {
                                    mViolationsLinear.setVisibility(View.GONE);
                                }
                                else
                                {
                                    mViolationsLinear.setVisibility(View.VISIBLE);
                                }

                            }

                            iniListen();
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
                MLog.e("TAG", "VolleyError:" + errorResponse.toString());
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish()
            {
                MLog.e("TAG", "networkFinish:");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {

            if (requestCode == COUPON)
            {
                conponIds = data.getStringArrayListExtra(SysConfig.COUPON_ID);
//                data.getStr
            }

            getOrderData(orderId);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (getIntent() != null && getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("money"))
        {
            finish();
        }
        else
        {

            Intent intent = new Intent(RenterOrderInfoActivity.this, MainActivityTab.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_STROKE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    }


}
