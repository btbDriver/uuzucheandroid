package com.youyou.uucar.UI.Owner.calculate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UI.Owner.addcar.AddCarBrandActivity;
import com.youyou.uucar.UI.Owner.addcar.BrandActivity;
import com.youyou.uucar.UI.Owner.help.OwnerHelpManager;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CalculatePriceActivity extends BaseActivity
{

    public final int BRAND_RESULT = 900;
    @InjectView(R.id.noprice)
    TextView       mNoprice;
    @InjectView(R.id.price)
    TextView       mPrice;
    @InjectView(R.id.unit_price)
    TextView       mUnitPrice;
    @InjectView(R.id.tip)
    TextView       mTip;
    @InjectView(R.id.city)
    TextView       mCity;
    @InjectView(R.id.city_root)
    RelativeLayout mCityRoot;

    String[] releaseShortNameCity;
    int cityWhich = 0;

    @OnClick(R.id.city_root)
    public void cityClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final NumberPicker city_picker = new NumberPicker(context);
        builder.setView(city_picker);
        city_picker.setMinValue(0);
        city_picker.setMaxValue(Config.openCity.size() - 1);
        city_picker.setDisplayedValues(releaseShortNameCity);
        for (int i = 0; i < city_picker.getChildCount(); i++)
        {
            city_picker.getChildAt(i).setFocusable(false);
        }
        city_picker.setWrapSelectorWheel(false);
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                cityWhich = city_picker.getValue();
                mCity.setText(releaseShortNameCity[city_picker.getValue()]);
                citySuccess = true;
                setButton();
            }
        });
        builder.create().show();
    }

    public void setButton()
    {
        if (yearSuccess && citySuccess && gearboxSuccess && brandSuccess)
        {
            mSure.setEnabled(true);
        }
        else
        {
            mSure.setEnabled(false);
        }
    }

    @InjectView(R.id.brand)
    TextView       mBrand;
    @InjectView(R.id.brand_root)
    RelativeLayout mBrandRoot;

    @OnClick(R.id.brand_root)
    public void brandClick()
    {
        startActivityForResult(new Intent(context, BrandActivity.class), BRAND_RESULT);
    }

    @InjectView(R.id.gear)
    TextView       mGear;
    @InjectView(R.id.gear_root)
    RelativeLayout mGearRoot;

    int gearboxWhich = 0;

    @OnClick(R.id.gear_root)
    public void gearClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.gearbox_items), gearboxWhich, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                gearboxWhich = which;
                mGear.setText(getResources().getStringArray(R.array.gearbox_items)[gearboxWhich]);
                dialog.dismiss();
                gearboxSuccess = true;
                setButton();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @InjectView(R.id.year)
    TextView mYear;

    String[] years     = new String[11];
    int      yearWhich = 0;
    Dialog dialog;

    @OnClick(R.id.year_root)
    public void yearClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(years, yearWhich, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                yearWhich = which;
                mYear.setText(years[yearWhich]);
                yearSuccess = true;
                setButton();
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @InjectView(R.id.year_root)
    RelativeLayout mYearRoot;
    @InjectView(R.id.reset)
    TextView       mReset;

    @OnClick(R.id.reset)
    public void resetClick()
    {
        firstSureClick();
    }

    @InjectView(R.id.sure)
    TextView mSure;

    public boolean citySuccess, brandSuccess, gearboxSuccess, yearSuccess;

    public void firstSureClick()
    {
        if (!Config.isNetworkConnected(context))
        {
            Config.showFiledToast(context);
            return;
        }
        if (mCity.getText().toString().length() == 0)
        {
            ShowToast("请选择出租城市");
            return;
        }
        if (mBrand.getText().toString().length() == 0)
        {
            showToast("请选择品牌型号");
            return;
        }
        if (mGear.getText().toString().length() == 0)
        {
            showToast("请选择变速箱");
            return;
        }
        if (mYear.getText().toString().length() == 0)
        {
            showToast("请选择年份");
            return;
        }

        showProgress(false);
        CarInterface.CalCarRentPriceRequest.Builder builder = CarInterface.CalCarRentPriceRequest.newBuilder();

        builder.setBrand(brand);
        builder.setCarModel(xinghao);
        builder.setCityId(Config.openCity.get(cityWhich).getCityId());
        if (gearboxWhich == 0)
        {
            builder.setTransmissionType( CarCommon.CarTransmissionType.AUTO);
        }
        else
        {
            builder.setTransmissionType(CarCommon.CarTransmissionType.HAND);
        }
        builder.setByYear(Integer.parseInt(years[yearWhich].substring(0, 4)));
        final com.youyou.uucar.Utils.Network.NetworkTask task = new com.youyou.uucar.Utils.Network.NetworkTask(CmdCodeDef.CmdCode.CalCarRentPrice_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("CalCarRentPrice");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                if(responseData.getRet() == 0)
                {
                    showResponseCommonMsg(responseData.getResponseCommonMsg());
                    try
                    {
                        CarInterface.CalCarRentPriceResponse response = CarInterface.CalCarRentPriceResponse.parseFrom(responseData.getBusiData());

                        mNoprice.setVisibility(View.GONE);
                        mPrice.setVisibility(View.VISIBLE);
                        mPrice.setText((int)response.getRentCarGuidePrice() + "");
                        mTip.setVisibility(View.VISIBLE);
                        mTip.setText(response.getTipsMsg());
                        mUnitPrice.setVisibility(View.VISIBLE);
                        mReset.setVisibility(View.VISIBLE);
                        mSure.setText("出租我的爱车");
                        firstMode = false;
                        dismissProgress();
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

    private boolean firstMode = true;

    public void secondSureClick()
    {

        if (!Config.isNetworkConnected(context))
        {
            Config.showFiledToast(context);
            return;
        }
        if (Config.isGuest(context))
        {
            Intent intent = new Intent(context, LoginActivity.class);
//            intent.putExtra("goto", RegisterPhone.OWNER_REGISTER);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(context, AddCarBrandActivity.class);
            intent.putExtra("cityIndex", cityWhich);
            intent.putExtra("brand", brand);
            intent.putExtra("xinghao", xinghao);
            intent.putExtra("year", yearWhich);
            intent.putExtra("gearbox", gearboxWhich);
            intent.putExtra("fromCalculate", true);
            intent.putExtra("num", getIntent().getStringExtra("num"));
            startActivity(intent);
        }
    }

    Calendar calendar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calculate_price, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_price);
        ButterKnife.inject(this);

        calendar = Calendar.getInstance();
        for (int i = 0; i < years.length; i++)
        {
            years[i] = ((calendar.get(Calendar.YEAR) - i) + "年");

        }
        mSure.setOnClickListener(sureClickListener);

        releaseShortNameCity = new String[Config.openCity.size()];
        for (int i = 0; i < Config.openCity.size(); i++)
        {
            releaseShortNameCity[i] = Config.openCity.get(i).getName();
        }


        yearWhich = getIntent().getIntExtra("year", -1);
        cityWhich = getIntent().getIntExtra("cityIndex", -1);
        gearboxWhich = getIntent().getIntExtra("gearbox", -1);
        if (yearWhich != -1)
        {
            mYear.setText(years[yearWhich]);
            yearSuccess = true;
        }
        if (cityWhich != -1)
        {
            mCity.setText(releaseShortNameCity[cityWhich]);
            citySuccess = true;
        }
        if (gearboxWhich == 0)
        {
            mGear.setText("自动挡");
            gearboxSuccess = true;
        }
        else if (gearboxWhich == 1)
        {
            mGear.setText("手动挡");
            gearboxSuccess = true;
        }
        if (getIntent().hasExtra("brand") && getIntent().hasExtra("xinghao") && getIntent().getStringExtra("brand") != null && getIntent().getStringExtra("xinghao") != null)
        {
            brand = getIntent().getStringExtra("brand");
            xinghao = getIntent().getStringExtra("xinghao");
            mBrand.setText(getIntent().getStringExtra("brand") + getIntent().getStringExtra("xinghao"));
            brandSuccess = true;
        }
        setButton();
    }

    public View.OnClickListener sureClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (firstMode)
            {
                firstSureClick();
            }
            else
            {
                secondSureClick();
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (item.getItemId() == R.id.owner_help)
        {

            Intent intent = new Intent();
            intent.setClass(context, OwnerHelpManager.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    String brand, xinghao;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == BRAND_RESULT)
            {
                String brandStr = data.getStringExtra("brand");
                if (brandStr != null && brandStr.indexOf("任意") != -1)
                {
                    return;
                }
                mBrand.setText(data.getStringExtra("brand") + " " + data.getStringExtra("xinghao"));
                brand = data.getStringExtra("brand");
                xinghao = data.getStringExtra("xinghao");
                brandSuccess = true;
                setButton();
            }
        }
    }
}
