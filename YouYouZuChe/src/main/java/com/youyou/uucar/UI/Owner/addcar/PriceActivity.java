package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.PB.TaskTool;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.Empty.EmptyTextWatcher;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PriceActivity extends BaseActivity
{

    public String tag = "Price";
    //    CarReleaseModel model;
//    CarReleaseService service;
    CarCommon.CarDetailInfo carContentModel;
    @InjectView(R.id.price_tip)
    TextView              mPriceTip;
    @InjectView(R.id.price)
    EditText              mPrice;
    @InjectView(R.id.hour_price)
    TextView              mHourPrice;
    @InjectView(R.id.week_price)
    TextView              mWeekPrice;
    @InjectView(R.id.tips)
    TextView              mTips;
    @InjectView(R.id.sure)
    TextView              mSure;
    @InjectView(R.id.bottom_root)
    LinearLayout          mBottomRoot;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    List<CarInterface.ToSetCarRentPrice.PriceIntervalTips> priceIntervalTipsList;
    private String carSn;
    float suggestPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getIntentData();
        setContentView(R.layout.addcar_price);
        ButterKnife.inject(this);
        initListen();
        initNoteDataRefush();
        getSuggestedPrice(carSn);
        mPrice.setText("");
        getCarDetailIno();
    }

    public void getCarDetailIno()
    {

        UUAppCar app = (UUAppCar) getApplication();
        TaskTool.getCarDetailInfo(carSn, "", app, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override public void onSuccessResponse(UUResponseData responseData)
            {
                if (responseData.getRet() == 0)
                {
                    showResponseCommonMsg(responseData.getResponseCommonMsg());

                }
                CarInterface.GetCarDetailInfo.Response model = null;
                try
                {
                    model = CarInterface.GetCarDetailInfo.Response.parseFrom(responseData.getBusiData());
                    carContentModel = model.getCarDetailInfo();
                    if (carContentModel.getPriceByDay() != 0f)
                    {
                        mPrice.setText((int) carContentModel.getPriceByDay() + "");
                    }
                    else
                    {
                        mPrice.setText((int) suggestPrice + "");

                    }
                }
                catch (InvalidProtocolBufferException e)
                {
                    e.printStackTrace();
                }

            }

            @Override public void onError(VolleyError errorResponse)
            {

            }

            @Override public void networkFinish()
            {

            }
        });
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

                if (Config.isNetworkConnected(context))
                {
                    mAllFramelayout.noDataReloading();
                    getSuggestedPrice(carSn);
                    getCarDetailIno();
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    private void initListen()
    {

        mPrice.addTextChangedListener(new EmptyTextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                double price = 0;
                try
                {
                    String str = s.toString();
                    price = Double.parseDouble(str);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                float hourPrice = (float) (price / 8);
                int priceInt = Math.round(hourPrice);
                int weekPrice = (int) price * 6;
                mWeekPrice.setText(weekPrice + "元/周");
                mHourPrice.setText(priceInt + "元/时");
                double difference = price - suggestPrice;
                int percent = (int) (difference * 100 / suggestPrice);
                if (priceIntervalTipsList != null)
                {
                    int size = priceIntervalTipsList.size();
                    for (int i = 0; i < size; i++)
                    {
                        CarInterface.ToSetCarRentPrice.PriceIntervalTips priceIntervalTips = priceIntervalTipsList.get(i);
                        int begin = priceIntervalTips.getPercentStart();
                        int end = priceIntervalTips.getPercentEnd();

                        if (percent >= begin && percent < end)
                        {
                            String msg = priceIntervalTips.getMsg();
                            String tips = msg.replace("{x}", percent + "%");
                            mTips.setText(tips);
                            break;
                        }
                        else
                        {
                            mTips.setText("");
                        }
                    }
                }

            }
        });

        mSure.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveModel();
            }
        });
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
        return super.onOptionsItemSelected(item);
    }

    private void saveModel()
    {
        final String priceStr = mPrice.getText() + "";
        if (priceStr.trim().equals(""))
        {
            Config.showToast(context, "价格不能为空！");
            return;
        }
        if (priceStr.trim().equals("0"))
        {
            Config.showToast(context, "价格不能为0元！");
            return;
        }
        if (priceStr.trim().equals("00") || priceStr.trim().equals("000"))
        {
            Config.showToast(context, "请输入正确的价格！");
            return;
        }
        if (!mTips.getText().toString().trim().equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(mTips.getText().toString().trim());
            builder.setNegativeButton("返回修改", null);
            builder.setNeutralButton("保存", new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    Config.showProgressDialog(context, false, null);
                    CarInterface.UpdateCarInfo.Request.Builder request = CarInterface.UpdateCarInfo.Request.newBuilder();
                    request.setCarId(carSn);
                    List<CarInterface.UpdateCarInfo.UpodateCarParams> list = new ArrayList<CarInterface.UpdateCarInfo.UpodateCarParams>();
                    list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("priceByDay").setValue(priceStr).build());
                    request.addAllParams(list);
                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UpdateCarInfo_VALUE);
                    networkTask.setBusiData(request.build().toByteArray());
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
                    {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData)
                        {
                            Config.showToast(context, responseData.getToastMsg());
                            if (responseData.getRet() == 0)
                            {
                                try
                                {
                                    CarInterface.UpdateCarInfo.Response response = CarInterface.UpdateCarInfo.Response.parseFrom(responseData.getBusiData());
                                    if (response.getRet() == 0)
                                    {
                                        PriceActivity.this.setResult(Activity.RESULT_OK);
                                        PriceActivity.this.finish();
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
            Dialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else
        {
            Config.showProgressDialog(context, false, null);
            CarInterface.UpdateCarInfo.Request.Builder request = CarInterface.UpdateCarInfo.Request.newBuilder();
            request.setCarId(carSn);
            List<CarInterface.UpdateCarInfo.UpodateCarParams> list = new ArrayList<CarInterface.UpdateCarInfo.UpodateCarParams>();
            list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("priceByDay").setValue(priceStr).build());
            request.addAllParams(list);
            NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UpdateCarInfo_VALUE);
            networkTask.setBusiData(request.build().toByteArray());
            NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
            {
                @Override
                public void onSuccessResponse(UUResponseData responseData)
                {
                    Config.showToast(context, responseData.getToastMsg());
                    if (responseData.getRet() == 0)
                    {
                        try
                        {
                            CarInterface.UpdateCarInfo.Response response = CarInterface.UpdateCarInfo.Response.parseFrom(responseData.getBusiData());
                            if (response.getRet() == 0)
                            {
                                PriceActivity.this.setResult(Activity.RESULT_OK);
                                PriceActivity.this.finish();
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

    @Override
    protected void onResume()
    {
        super.onResume();

    }


    public void getIntentData()
    {
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
    }

    public void getSuggestedPrice(String carSn)
    {
        CarInterface.ToSetCarRentPrice.Request.Builder request = CarInterface.ToSetCarRentPrice.Request.newBuilder();
        request.setCarId(carSn);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ToSetCarRentPrice_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                Config.showToast(context, responseData.getToastMsg());
                if (responseData.getRet() == 0)
                {
                    try
                    {
                        CarInterface.ToSetCarRentPrice.Response response = CarInterface.ToSetCarRentPrice.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0)
                        {
                            int priceIntervalTipsCount = response.getPriceIntervalTipsCount();
                            if (priceIntervalTipsCount > 0)
                            {
                                priceIntervalTipsList = response.getPriceIntervalTipsList();
                            }
                            if (response.hasSuggestPricePerDay())
                            {
                                suggestPrice = response.getSuggestPricePerDay();
                                mPriceTip.setText((int) response.getSuggestPricePerDay() + "元/天");
                                if (carContentModel != null && carContentModel.hasPriceByDay() && carContentModel.getPriceByDay() != 0f)
                                {

                                }
                                else
                                {
//                                    mPrice.setText((int) response.getSuggestPricePerDay() + "");
                                }

                            }
                            else
                            {

                            }
                            mAllFramelayout.makeProgreeDismiss();
                        }
                        else
                        {
                            mAllFramelayout.makeProgreeNoData();
                        }
                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                        mAllFramelayout.makeProgreeNoData();
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
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish()
            {
//                mAllFramelayout.makeProgreeDismiss();
            }
        });
    }
}
