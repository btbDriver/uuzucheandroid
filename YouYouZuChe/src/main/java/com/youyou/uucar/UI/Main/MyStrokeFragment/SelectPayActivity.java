package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umpay.quickpay.UmpPayInfoBean;
import com.umpay.quickpay.UmpayQuickPay;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.pay.MD5;
import com.youyou.uucar.pay.Result;
import com.youyou.uucar.pay.Rsa;
import com.youyou.uucar.pay.SignUtils;
import com.youyou.uucar.pay.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import evn.EnvConstants;
import pay.utils.BaseHelper;
import pay.utils.Constants;
import pay.utils.Md5Algorithm;
import pay.utils.MobileSecurePayer;
import pay.utils.PayOrder;

/**
 * Created by 16515_000 on 2014/7/17.
 */
public class SelectPayActivity extends BaseActivity
{
    //    APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final  String APP_ID       = "wx9abfa08f7da32b30";
    /**
     * 商家向财付通申请的商家id
     */
    public static final  String PARTNER_ID   = "1218677801";
    private static final int    SDK_PAY_FLAG = 1;
    private static final int    RQF_LOGIN    = 2;

    private boolean needRefresh = false;
    Dialog citydialog;
    Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            Result result = new Result((String) msg.obj);
            switch (msg.what)
            {
                case SDK_PAY_FLAG:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = context.getLayoutInflater().inflate(R.layout.order_pay_dialog, null);
                    TextView current = (TextView) view.findViewById(R.id.current);
                    TextView old = (TextView) view.findViewById(R.id.old);
                    builder.setView(view);
                    citydialog = builder.create();
                    citydialog.setCanceledOnTouchOutside(false);
                    citydialog.show();
                    current.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            citydialog.dismiss();
                            setResult(RESULT_OK);
                            SelectPayActivity.this.finish();
                        }
                    });
                    old.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            citydialog.dismiss();
                            setResult(RESULT_OK);
                            SelectPayActivity.this.finish();
                        }
                    });

                case RQF_LOGIN:
                {
                    // Toast.makeText(context,result.string2JSON(result.getResult(),"resultStatus").toString(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(SelectPayActivity.this, result.getResult() == null ? "" : result.getResult(), Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    break;
            }
        }

        ;
    };
    private static final String APP_SECRET = "6b665975c71fb923e467b8ac26f4bbce"; // wxd930ea5d5a258f4f 对应的密钥
    public               String tag        = "SelectPayActivity";
    public               String appsecret  = "";
    public               String appkey     = "";
    public               String notify_url = "";
    public               String partnerkey = "";
    public String r_sn;
    boolean aPayShow     = false;
    boolean UpayShow     = false;
    boolean weixinShow   = false;
    boolean lianlianShow = false;
    @InjectView(R.id.alipay)
    RelativeLayout mAlipay;
    @InjectView(R.id.upay)
    RelativeLayout mUpay;
    @InjectView(R.id.weixin)
    RelativeLayout weixin;
    @InjectView(R.id.lianlian)
    RelativeLayout lianlian;
    //    @InjectView(R.id.weixin)
//    TextView mWeixin;
    Handler handler = new Handler();
    long    timeStamp;
    boolean isPayCallback;
    boolean isPayFakuan = false;
    private IWXAPI api;
    private String nonceStr, packageValue;
    private List<OrderFormInterface.ConfirmToPayNew.PayTypeInfo> playList;

    private String mAPayUrl, mAPayRsn;
    private String mUPayUrl, mUPayRsn;
    private String mWeixinPayUrl, mWeixinPayRsn;

    @OnClick(R.id.alipay)
    public void aliPayClick()
    {
//        Config.showProgressDialog(context, false, null);
//        toAlipay();
        queryPayOrderInfo(OrderFormInterface.ThirdPayType.ALI_PAY);
    }

    @OnClick(R.id.upay)
    public void upayClick()
    {
        queryPayOrderInfo(OrderFormInterface.ThirdPayType.U_PAY);


    }

    @OnClick(R.id.weixin)
    public void weixinClick()
    {
        queryPayOrderInfo(OrderFormInterface.ThirdPayType.WECHAT_PAY);
//        Config.showProgressDialog(context, false, null);
//        AbHttpUtil ab = AbHttpUtil.getInstance(context);
//        ab.post(ServerMutualConfig.wx_pay, new MyAbStringHttpResponseListener(context) {
//            @Override
//            public void onSuccess(int statusCode, String content) {
//
//                super.onSuccess(statusCode, content);
//                MLog.e(tag, "wx_pay json = " + content);
//                try {
//                    JSONObject json = new JSONObject(content);
//                    if (json.getString("status").equals("1")) {
//                        appsecret = json.getJSONObject("content").getString("appsecret");
//                        appkey = json.getJSONObject("content").getString("appkey");
//                        partnerkey = json.getJSONObject("content").getString("partnerkey");
//                        notify_url = json.getJSONObject("content").getString("notify_url");
//                        new GetAccessTokenTask().execute();
//                    } else {
//                        Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//
//                super.onFinish();
//                Config.dismissProgress();
//            }
//
//            @Override
//            public void onFailure(int statusCode, String content, Throwable error) {
//
//                super.onFailure(statusCode, content, error);
//            }
//        });
    }

    @OnClick(R.id.lianlian)
    public void lianlianClick()
    {
        queryPayOrderInfo(OrderFormInterface.ThirdPayType.LIANLIAN_PAY);
    }

    OrderFormInterface.QueryPayOrderInfo.PayOrderInfo payOrderInfo;

    public void queryPayOrderInfo(final OrderFormInterface.ThirdPayType type)
    {
        showProgress(false);
        OrderFormInterface.QueryPayOrderInfo.Request.Builder request = OrderFormInterface.QueryPayOrderInfo.Request.newBuilder();
        request.setOrderId(r_sn);
        request.setChargeAmout(getIntent().getExtras().getFloat(SysConfig.NEED_RECHANGE_AMOUNT));
        request.setPayType(type);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.QueryPayOrderInfo_VALUE);
        task.setBusiData(request.build().toByteArray());
        task.setTag("QueryPayOrderInfo");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override public void onSuccessResponse(UUResponseData responseData)
            {
                showResponseCommonMsg(responseData.getResponseCommonMsg());
                if (responseData.getRet() == 0)
                {
                    try
                    {
                        OrderFormInterface.QueryPayOrderInfo.Response response = OrderFormInterface.QueryPayOrderInfo.Response.parseFrom(responseData.getBusiData());
                        payOrderInfo = response.getPayOrderInfo();
                        if (type.equals(OrderFormInterface.ThirdPayType.ALI_PAY))
                        {
                            toAlipay();
                        }
                        else if (type.equals(OrderFormInterface.ThirdPayType.U_PAY))
                        {
                            UmpayQuickPay.requestPayWithBind(context,// activity
                                                             payOrderInfo.getTradeNo(),// 流水号
                                                             "",// 商户标识
                                                             "",// 卡类型
                                                             "",// 银行简称
                                                             new UmpPayInfoBean(), 111);
                        }
                        else if (type.equals(OrderFormInterface.ThirdPayType.WECHAT_PAY))
                        {
                            appsecret = payOrderInfo.getWechatPayParam().getAppSecret();
                            appkey = payOrderInfo.getWechatPayParam().getAppkey();
                            partnerkey = payOrderInfo.getWechatPayParam().getPartnerKey();
                            notify_url = payOrderInfo.getCallbackUrl();
//                            new GetAccessTokenTask().execute();
                            sendPayReq();
                        }
                        else if (type.equals(OrderFormInterface.ThirdPayType.LIANLIAN_PAY))
                        {
                            PayOrder order = null;
                            order = constructGesturePayOrder();
                            String content4Pay = BaseHelper.toJSONString(order);
//                            MLog.e(tag, content4Pay);
                            MobileSecurePayer msp = new MobileSecurePayer();
                            boolean bRet = msp.pay(content4Pay, mHandler,
                                                   Constants.RQF_PAY, context, false);

//                            MLog.e(tag, String.valueOf(bRet));
                        }

                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override public void onError(VolleyError errorResponse)
            {
                Config.showFiledToast(context);
            }

            @Override public void networkFinish()
            {
                dismissProgress();
            }
        });
    }

    private PayOrder constructGesturePayOrder()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss",
                                                           Locale.getDefault());
        Date date = new Date();
        String timeString = dataFormat.format(date);

        PayOrder order = new PayOrder();
        order.setBusi_partner(payOrderInfo.getLianlianPayParam().getBusiPartner());
        order.setNo_order(payOrderInfo.getPayNo());
        order.setDt_order(payOrderInfo.getLianlianPayParam().getOrderDate());
        order.setName_goods(payOrderInfo.getLianlianPayParam().getGoodsName());
        order.setNotify_url(payOrderInfo.getCallbackUrl());
        // MD5 签名方式
        order.setSign_type(PayOrder.SIGN_TYPE_MD5);
        // RSA 签名方式
        // order.setSign_type(PayOrder.SIGN_TYPE_RSA);
        order.setValid_order(payOrderInfo.getLianlianPayParam().getExpireTime() + "");
        order.setUser_id(Config.getUser(context).userId + "");
        order.setId_no(payOrderInfo.getLianlianPayParam().getUserIdNo());
        order.setAcct_name(payOrderInfo.getLianlianPayParam().getUserFullName());
        order.setMoney_order(getIntent().getFloatExtra(SysConfig.NEED_RECHANGE_AMOUNT, 1) + "");
        order.setFlag_modify("1");
        // 风险控制参数。
        order.setRisk_item(constructRiskItem());
        String sign = "";
        EnvConstants.PARTNER = payOrderInfo.getLianlianPayParam().getPartnerId();
        EnvConstants.MD5_KEY = payOrderInfo.getLianlianPayParam().getParam();
        order.setOid_partner(EnvConstants.PARTNER);
        // TODO 对签名原串进行排序，并剔除不需要签名的串。
        String content = BaseHelper.sortParam(order);
        // MD5 签名方式, 签名方式包括两种，一种是MD5，一种是RSA 这个在商户站管理里有对验签方式和签名Key的配置。
        sign = Md5Algorithm.getInstance().sign(content, EnvConstants.MD5_KEY);
        // RSA 签名方式
        // sign = Rsa.sign(content, EnvConstants.RSA_PRIVATE);
        order.setSign(sign);
        return order;
    }

    /**
     * TODO 风险控制参数生成例子，请根据文档动态填写。最后返回时必须调用.toString()
     */
    private String constructRiskItem()
    {
        JSONObject mRiskItem = new JSONObject();
        try
        {
            String frms_ware_category = "2999";
            if (payOrderInfo.getLianlianPayParam().hasFrmsWareCategory())
            {
                frms_ware_category = payOrderInfo.getLianlianPayParam().getFrmsWareCategory();
            }
            mRiskItem.put("frms_ware_category", frms_ware_category);
            mRiskItem.put("user_info_mercht_userno", "" + Config.getUser(context).userId);
            mRiskItem.put("user_info_dt_register", payOrderInfo.getLianlianPayParam().getUserRegTime());
            mRiskItem.put("user_info_full_name", payOrderInfo.getLianlianPayParam().getUserFullName());
            mRiskItem.put("user_info_id_no", payOrderInfo.getLianlianPayParam().getUserIdNo());
            mRiskItem.put("user_info_identify_state", payOrderInfo.getLianlianPayParam().getUserIdentifyState());
            mRiskItem.put("user_info_identify_type", payOrderInfo.getLianlianPayParam().getUserIdentifyType());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return mRiskItem.toString();
    }

    @InjectView(R.id.pay_money)
    TextView pay_money;
    @InjectView(R.id.upay_name)
    TextView upayName;
    @InjectView(R.id.alipay_name)
    TextView alipayName;
    @InjectView(R.id.lianlian_name)
    TextView lianlianName;
    @InjectView(R.id.weixin_name)
    TextView weixinName;
    @InjectView(R.id.upay_desc)
    TextView upayDesc;
    @InjectView(R.id.alipay_desc)
    TextView alipayDesc;
    @InjectView(R.id.weixin_desc)
    TextView weixinDesc;
    @InjectView(R.id.lianlian_desc)
    TextView lianlianDesc;
    @InjectView(R.id.upay_tag)
    TextView upayTag;
    @InjectView(R.id.alipay_tag)
    TextView alipayTag;
    @InjectView(R.id.lianlian_tag)
    TextView lianlianTag;
    @InjectView(R.id.weixin_tag)
    TextView weixinTag;

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        playList = Config.payList;
        if (Config.payList.size() > 0)
        {
            playList = new ArrayList<OrderFormInterface.ConfirmToPayNew.PayTypeInfo>();
            playList = Config.payList;
        }


        setContentView(R.layout.select_pay_activity);
        ButterKnife.inject(this);

        r_sn = getIntent().getStringExtra(SysConfig.R_SN);
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.registerApp(APP_ID);
        initData();
        mAlipay.setVisibility(aPayShow ? View.VISIBLE : View.GONE);
        mUpay.setVisibility(UpayShow ? View.VISIBLE : View.GONE);
        lianlian.setVisibility(lianlianShow ? View.VISIBLE : View.GONE);
        pay_money.setText(getIntent().getExtras().getFloat(SysConfig.NEED_RECHANGE_AMOUNT) + "");

        if (Config.isAvilible(context, "com.tencent.mm") && weixinShow)
        {
            weixin.setVisibility(View.VISIBLE);
        }
        else
        {
            weixin.setVisibility(View.GONE);
        }

    }

    private String aliId;

    private void initData()
    {
        if (playList.size() > 0)
        {
            for (OrderFormInterface.ConfirmToPayNew.PayTypeInfo payInfo : playList)
            {
                if (payInfo.getType().equals(OrderFormInterface.ThirdPayType.ALI_PAY))
                {
                    aPayShow = true;
//                    mAPayUrl = payInfo.getCallbackUrl();
//                    mAPayRsn = payInfo.getTradeNo();
                    aliId = r_sn;
                    alipayName.setText(payInfo.getName());
                    alipayDesc.setText(payInfo.getDesc());
                    if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.NORMAL))
                    {
                        alipayTag.setVisibility(View.GONE);
                    }
                    else
                    {
                        if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.RECOMMEND))
                        {
                            alipayTag.setVisibility(View.VISIBLE);
                            alipayTag.setText("推荐");
                        }
                        else if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.LAST))
                        {
                            alipayTag.setVisibility(View.VISIBLE);
                            alipayTag.setText("上次使用");
                        }
                    }

//                    alipayDesc.setText(payInfo.);
                }
                else if (payInfo.getType().equals(OrderFormInterface.ThirdPayType.U_PAY))
                {
                    UpayShow = true;
//                    mUPayUrl = payInfo.getCallbackUrl();
//                    mUPayRsn = payInfo.getTradeNo();
                    upayName.setText(payInfo.getName());
                    upayDesc.setText(payInfo.getDesc());
                    if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.NORMAL))
                    {
                        upayTag.setVisibility(View.GONE);
                    }
                    else
                    {
                        if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.RECOMMEND))
                        {
                            upayTag.setVisibility(View.VISIBLE);
                            upayTag.setText("推荐");
                        }
                        else if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.LAST))
                        {
                            upayTag.setVisibility(View.VISIBLE);
                            upayTag.setText("上次使用");
                        }
                    }
                }
                else if (payInfo.getType().equals(OrderFormInterface.ThirdPayType.WECHAT_PAY))
                {
                    weixinShow = true;
//                    mWeixinPayUrl = payInfo.getCallbackUrl();
//                    mWeixinPayRsn = payInfo.getTradeNo();

                    weixinName.setText(payInfo.getName());
                    weixinDesc.setText(payInfo.getDesc());
                    if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.NORMAL))
                    {
                        weixinTag.setVisibility(View.GONE);
                    }
                    else
                    {
                        if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.RECOMMEND))
                        {
                            weixinTag.setVisibility(View.VISIBLE);
                            weixinTag.setText("推荐");
                        }
                        else if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.LAST))
                        {
                            weixinTag.setVisibility(View.VISIBLE);
                            weixinTag.setText("上次使用");
                        }
                    }
                }
                else if (payInfo.getType().equals(OrderFormInterface.ThirdPayType.LIANLIAN_PAY))
                {
                    lianlianShow = true;
//                    mWeixinPayUrl = payInfo.getCallbackUrl();
//                    mWeixinPayRsn = payInfo.getTradeNo();
                    lianlianName.setText(payInfo.getName());
                    lianlianDesc.setText(payInfo.getDesc());
                    if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.NORMAL))
                    {
                        lianlianTag.setVisibility(View.GONE);
                    }
                    else
                    {
                        if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.RECOMMEND))
                        {
                            lianlianTag.setVisibility(View.VISIBLE);
                            lianlianTag.setText("推荐");
                        }
                        else if (payInfo.getShowType().equals(OrderFormInterface.ConfirmToPayNew.PayShowType.LAST))
                        {
                            lianlianTag.setVisibility(View.VISIBLE);
                            lianlianTag.setText("上次使用");
                        }
                    }
                }
            }
        }
    }

    public ObserverListener paidListener = new ObserverListener()
    {
        @Override
        public void observer(String from, Object obj)
        {
            setResult(RESULT_OK);
            finish();
        }
    };

    public void onResume()
    {
        super.onResume();
        MLog.e(tag, "isPayCallback=" + isPayCallback);
        if(isPayCallback && isWECHAT)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = context.getLayoutInflater().inflate(R.layout.order_pay_dialog, null);
            TextView current = (TextView) view.findViewById(R.id.current);
            TextView old = (TextView) view.findViewById(R.id.old);
            builder.setView(view);
            citydialog = builder.create();
            citydialog.setCanceledOnTouchOutside(false);
            citydialog.show();
            current.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    citydialog.dismiss();
                    setResult(RESULT_OK);
                    SelectPayActivity.this.finish();
                }
            });
            old.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    citydialog.dismiss();
                    setResult(RESULT_OK);
                    SelectPayActivity.this.finish();
                }
            });
            isWECHAT = false;
        }
//        if (isPayCallback) {
//            isPayCallback = false;
//            Config.showProgressTextDialog(context, "支付结果确认中...");
//
//        } else {
//        }
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

    private String getSignType()
    {
        return "sign_type=\"RSA\"";
    }

    public void toAlipay()
    {
        try
        {
            MLog.i("ExternalPartner", "onItemClick");
            String info = getNewOrderInfo();
            String sign = SignUtils.sign(info, "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM0ju9Q332HuL5sMwUbyV1rIyHuOKnx2eFkfie9mojxZpAk+xidGEIbg45iz985PyJq7oc7KC7Mcb779HBLnGqyHpxKnlX1qj4BwB+a/4Rm11fwkqm3qUHsr4SAIo6ZBc4LaOVo4qExLIcbF9F7gdzELE+9Axua1P9qynnKQ+cTRAgMBAAECgYEAg79RYGhJ1PFOLbwxXUlDBREygPe7HZhQjpyMz+4Qf8Xqbe2dAZgkARvp0Ca1HhFresTKlK56eRvoQLb6EsHOBJ18zGuFUZxmlk8piRkMdAdckNsaeBtceOyemCsZba7DPHS9vnEq5v7DlZEeUi2Nqc8i9yqa/RbsK4MgDq8biYkCQQDym7gnyGr0/Hy64Vazt+Rp8zbSpUuzL1WW3BnRFebNQnwnkX+3Nzk9logJkLUUXMklj/+3pVPiTxF6JEUBnwI7AkEA2HaL/ogOeJcABfjPjejHZ37ToLQcf8WVZAY5SPnAOzIW1xY9BQcxxSNJN6PJQVUgmJhYgUWhdVPpQPh7cQ04YwJAAl91iKYULbs+eRF1KKLW0BZ55cuKFwGSg7w5YGsna8CHuFda+W7H14teX0GUE9Pof76N0L0EOgVt9VTfe+mTOwJBAKhLGrKtA1s8Qxdhh7UUOxw7Hbw+7D1m16wprpYPHyam3d0h/BURr99OfNlWbN1vmuUo5P60rUA0GhCaYBbYKXMCQE4KrCSpGzpdSUYvhYeBnqycJP4i02TPUYhGJRCiic01BhDw8pEl3/7kolxFukbrNt5YXNQAzzZq0wTUHUP6BcI=");
            sign = URLEncoder.encode(sign, "UTF-8");
            info += "&sign=\"" + sign + "\"&" + getSignType();
            MLog.i("ExternalPartner", "start pay");
            // start the pay.
            MLog.i(tag, "info = " + info);
            final String orderInfo = info;
            Runnable payRunnable = new Runnable()
            {

                @Override
                public void run()
                {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(context);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(orderInfo);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };

            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Config.showFiledToast(context);
        }
    }

    private String getNewOrderInfo() throws NumberFormatException, JSONException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append("2088411114097222");
        sb.append("\"&out_trade_no=\"");
//        sb.append(r_sn);// 订单号
        sb.append(payOrderInfo.getPayNo());
        sb.append("\"&subject=\"");
        sb.append("租车费用");// 标题
        sb.append("\"&body=\"");
        sb.append("租车需向个人账户注入足够的保证金. 用于支付租车过程中产生的租车,保险,燃油, 延时, 违章等费用.违章记录在违章15天后能查询结果，查询后用户方可提现.");// 详情
        sb.append("\"&total_fee=\"");
        sb.append(getIntent().getFloatExtra(SysConfig.NEED_RECHANGE_AMOUNT, 1));// 价格
        // sb.append("0.01");// 价格
        sb.append("\"&notify_url=\"");
        // 网址需要做URL编码
//        sb.append(URLEncoder.encode("http://pay.uuzuche.com.cn:1032/alipay_response.php"));
        sb.append(URLEncoder.encode(payOrderInfo.getCallbackUrl()));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append("admin@uuzuche.com");
        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
//        sb.append("\"&it_b_pay=\"1m");//超时时间
        sb.append("\"");
        return new String(sb);
    }

    boolean isWECHAT = false;
    private void sendPayReq()
    {
        PayReq req = new PayReq();
        if (payOrderInfo.getWechatPayParam().hasAppId())
        {
            req.appId = payOrderInfo.getWechatPayParam().getAppId();
        }
        else
        {
            req.appId = APP_ID;
        }
        if (payOrderInfo.getWechatPayParam().hasPartnerId())
        {

            req.partnerId = payOrderInfo.getWechatPayParam().getPartnerId();
        }
        else
        {
            req.partnerId = PARTNER_ID;
        }
        req.prepayId = payOrderInfo.getWechatPayParam().getPrePayId();
//        req.prepayId = result.prepayId;
        if (payOrderInfo.getWechatPayParam().hasNonceStr())
        {
            req.nonceStr = payOrderInfo.getWechatPayParam().getNonceStr();
        }
        else
        {
            req.nonceStr = genNonceStr();
        }
        if (payOrderInfo.getWechatPayParam().hasTimestamp())
        {
            req.timeStamp = String.valueOf(payOrderInfo.getWechatPayParam().getTimestamp());
        }
        else
        {
            req.timeStamp = String.valueOf(genTimeStamp());
        }
        req.packageValue = "Sign=WXPay";
//        req.packageValue = "Sign=" + packageValue;
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("appkey", appkey));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genSign(signParams);
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        boolean isOK = api.sendReq(req);
        if (isOK)
        {
            isPayCallback = true;
            isWECHAT = true;
        }
    }

    private String genSign(List<NameValuePair> params)
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i < params.size() - 1; i++)
        {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());
        String sha1 = Util.sha1(sb.toString());
        MLog.d(tag, "genSign, sha1 = " + sha1);
        return sha1;
    }

    private String genNonceStr()
    {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public String genOutTradNo()
    {
        return mWeixinPayRsn;
        // Random random = new Random();
        // return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String genProductArgs()
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("appid", APP_ID);
            String traceId = Config.getUser(context).phone; // traceId 由开发者自定义，可用于订单的查询与跟踪，建议根据支付用户信息生成此id
            json.put("traceid", traceId);
            nonceStr = genNonceStr();
            MLog.e(tag, "nonceStr=" + nonceStr);
            json.put("noncestr", nonceStr);
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            packageParams.add(new BasicNameValuePair("body", "租金和押金"));
            packageParams.add(new BasicNameValuePair("fee_type", "1"));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", payOrderInfo.getCallbackUrl()));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("partner", PARTNER_ID));
            packageParams.add(new BasicNameValuePair("spbill_create_ip",/* "115.28.173.178" */"196.168.1.1"));/* 196.168.1.1 */
            packageParams.add(new BasicNameValuePair("total_fee", "" + (int) (Float.parseFloat(getIntent().getStringExtra("price")) * 100)));
//             packageParams.add(new BasicNameValuePair("total_fee","1"));
            packageValue = genPackage(packageParams);
            json.put("package", packageValue);
            MLog.e(tag, "package=" + packageValue);
            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);
            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", APP_ID));
            signParams.add(new BasicNameValuePair("appkey", appkey));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));
            json.put("sign_method", "sha1");
        }
        catch (Exception e)
        {
            MLog.e(tag, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
        return json.toString();
    }

    private String genPackage(List<NameValuePair> params)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++)
        {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(partnerkey); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成
        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    private long genTimeStamp()
    {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        MLog.e(tag, "data = " + data);
        if (data == null)
        {
            return;
        }
        String payresult = data.getExtras().getString("umpResultMessage");
        String paycode = data.getExtras().getString("umpResultCode");
        if (paycode.equals("0000"))
        {
            if (isPayFakuan)
            {
                isPayFakuan = false;
                MobclickAgent.onEvent(context, "OrderSuccess");
            }
            isPayCallback = true;
        }
        else if (paycode.equals("1001"))
        {
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.order_pay_dialog, null);
        TextView current = (TextView) view.findViewById(R.id.current);
        TextView old = (TextView) view.findViewById(R.id.old);
        builder.setView(view);
        citydialog = builder.create();
        citydialog.setCanceledOnTouchOutside(false);
        citydialog.show();
        current.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                citydialog.dismiss();
                setResult(RESULT_OK);
                SelectPayActivity.this.finish();
            }
        });
        old.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                citydialog.dismiss();
                setResult(RESULT_OK);
                SelectPayActivity.this.finish();
            }
        });
//        Toast.makeText(context, payresult, Toast.LENGTH_SHORT).show();
    }

    private static enum LocalRetCode
    {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    private static class GetAccessTokenResult
    {
        private static final String       tag          = "MicroMsg.SDKSample.PayActivity.GetAccessTokenResult";
        public               LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String accessToken;
        public int    expiresIn;
        public int    errCode;
        public String errMsg;

        public void parseFrom(String content)
        {
            if (content == null || content.length() <= 0)
            {
                MLog.e(tag, "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }
            try
            {
                JSONObject json = new JSONObject(content);
                if (json.has("access_token"))
                { // success case
                    accessToken = json.getString("access_token");
                    expiresIn = json.getInt("expires_in");
                    localRetCode = LocalRetCode.ERR_OK;
                }
                else
                {
                    errCode = json.getInt("errcode");
                    errMsg = json.getString("errmsg");
                    localRetCode = LocalRetCode.ERR_JSON;
                }
            }
            catch (Exception e)
            {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private static class GetPrepayIdResult
    {
        private static final String       tag          = "MicroMsg.SDKSample.PayActivity.GetPrepayIdResult";
        public               LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String prepayId;
        public int    errCode;
        public String errMsg;

        public void parseFrom(String content)
        {
            if (content == null || content.length() <= 0)
            {
                MLog.e(tag, "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }
            try
            {
                JSONObject json = new JSONObject(content);
                if (json.has("prepayid"))
                { // success case
                    prepayId = json.getString("prepayid");
                    localRetCode = LocalRetCode.ERR_OK;
                }
                else
                {
                    localRetCode = LocalRetCode.ERR_JSON;
                }
                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");
            }
            catch (Exception e)
            {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private class GetAccessTokenTask extends AsyncTask<Void, Void, GetAccessTokenResult>
    {
        @Override
        protected void onPreExecute()
        {
            // dialog = ProgressDialog.show(context,getString(R.string.app_tip),getString(R.string.getting_access_token));
            Config.showProgressDialog(context, false, null);
        }

        @Override
        protected void onPostExecute(GetAccessTokenResult result)
        {
            if (result.localRetCode == LocalRetCode.ERR_OK)
            {
                // Toast.makeText(context,R.string.get_access_token_succ,Toast.LENGTH_LONG).show();
                MLog.d(tag, "onPostExecute, accessToken = " + result.accessToken);
                GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
                getPrepayId.execute();
            }
            else
            {
            }
        }

        @Override
        protected GetAccessTokenResult doInBackground(Void... params)
        {
            GetAccessTokenResult result = new GetAccessTokenResult();
            String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", APP_ID, APP_SECRET);
            MLog.d(tag, "get access token, url = " + url);
            byte[] buf = Util.httpGet(url);
            if (buf == null || buf.length == 0)
            {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }
            String content = new String(buf);
            result.parseFrom(content);
            return result;
        }
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult>
    {
        private ProgressDialog dialog;
        private String         accessToken;

        public GetPrepayIdTask(String accessToken)
        {
            this.accessToken = accessToken;
        }

        @Override
        protected void onPreExecute()
        {
            // dialog = ProgressDialog.show(context,getString(R.string.app_tip),getString(R.string.getting_prepayid));
            Config.showProgressDialog(context, false, null);
        }

        @Override
        protected void onPostExecute(GetPrepayIdResult result)
        {
            // if(dialog != null)
            // {
            // dialog.dismiss();
            // }
            if (result.localRetCode == LocalRetCode.ERR_OK)
            {
                // Toast.makeText(context,R.string.get_prepayid_succ,Toast.LENGTH_LONG).show();
                MLog.e(tag, "不知道干JB啥成功了");
//                sendPayReq(result);
            }
            else
            {
                MLog.e(tag, "不知道干JB啥失败了 = " + result.localRetCode.name());
            }
        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
        }

        @Override
        protected GetPrepayIdResult doInBackground(Void... params)
        {
            String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
            String entity = genProductArgs();
            MLog.d(tag, "doInBackground, url = " + url);
            MLog.d(tag, "doInBackground, entity = " + entity);
            GetPrepayIdResult result = new GetPrepayIdResult();
            byte[] buf = Util.httpPost(url, entity);
            if (buf == null || buf.length == 0)
            {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }
            String content = new String(buf);
            MLog.d(tag, "doInBackground, content = " + content);
            result.parseFrom(content);
            return result;
        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_OK);
        finish();
    }
}
