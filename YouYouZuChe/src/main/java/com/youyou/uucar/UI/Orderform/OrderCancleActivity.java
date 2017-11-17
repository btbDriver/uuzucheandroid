package com.youyou.uucar.UI.Orderform;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

/**
 * Created by taurusxi on 14-9-1.
 */
public class OrderCancleActivity extends BaseActivity {

    private View actionBarView;
    private Button mMapTv;
    Dialog dialog;
    private String mOrderId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        mOrderId = getIntent().getStringExtra(SysConfig.R_SN);
        if (mOrderId == null) {
            mOrderId = "1";
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBarView = getLayoutInflater().inflate(R.layout.action_bar_order, null,
                false);
        TextView titleTv = (TextView) actionBarView.findViewById(R.id.title);
        titleTv.setText(OrderCancleActivity.this.getResources().getString(R.string.order_cancle_idea));
        mMapTv = (Button) actionBarView.findViewById(R.id.map);
        mMapTv.setText("取消订单");
        mMapTv.setEnabled(true);
        mMapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancleOrder(mOrderId);
            }
        });
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarView);

        context = this;
        Config.setActivityState(this);
        WebView view = new WebView(this);
        view.loadUrl(getIntent().getStringExtra("url"));
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MLog.e("webview ", "longclick");
                return true;
            }
        });
        setTitle("保险资料规则");
        setContentView(view);


        boolean isCanCancle = getIntent().getBooleanExtra("cancle", true);
        if (isCanCancle) {
            mMapTv.setVisibility(View.VISIBLE);
        } else {
            mMapTv.setVisibility(View.INVISIBLE);
        }
    }


    private void cancleOrder(String orderId) {

        Config.showProgressDialog(context, false, null);
        NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelOrder_VALUE);
        OrderFormInterface26.RenterCancelOrder.Request.Builder request = OrderFormInterface26.RenterCancelOrder.Request.newBuilder();
        request.setOrderId(orderId);
        network.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData response) {
                if (response.getRet() == 0) {
                    Config.showToast(context, response.getToastMsg());
                    try {
                        OrderFormInterface26.RenterCancelOrder.Response data = OrderFormInterface26.RenterCancelOrder.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0) {
                            //取消订单成功
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(data.getMsg());
                            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dia, int which) {
                                    dialog.cancel();

                                }
                            }).setPositiveButton("取消订单", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dia, int whichButton) {
                                    cancleOrderConfirm(mOrderId);
                                }
                            });

                            dialog = builder.create();
                            dialog.show();

                        } else if (data.getRet() == -1) {
                            //结束行程失败
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
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
            }
        });

    }

    private void cancleOrderConfirm(String orderId) {

        Config.showProgressDialog(context, false, null);
        NetworkTask network = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelOrderConfirm_VALUE);
        OrderFormInterface26.RenterCancelOrderConfirm.Request.Builder request = OrderFormInterface26.RenterCancelOrderConfirm.Request.newBuilder();
        request.setOrderId(orderId);
        network.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(network, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData response) {
                if (response.getRet() == 0) {
                    Config.showToast(context, response.getToastMsg());
                    try {
                        OrderFormInterface26.RenterCancelOrderConfirm.Response data = OrderFormInterface26.RenterCancelOrderConfirm.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0) {
                            //取消订单成功
                            MLog.e("TAG", "订单取消成功");
                            setResult(RESULT_OK);
                            OrderCancleActivity.this.finish();

                        } else if (data.getRet() == -1) {
                            //取消订单失败
                            MLog.e("TAG", "订单取消失败");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        setResult(RESULT_OK);
        finish();
    }
}
