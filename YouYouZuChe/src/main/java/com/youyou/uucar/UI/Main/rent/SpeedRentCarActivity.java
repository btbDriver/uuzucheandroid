package com.youyou.uucar.UI.Main.rent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.FindCarFragment.SelectAddressActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.RangeSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class SpeedRentCarActivity extends BaseActivity
{

    JSONObject filter = new JSONObject();
    public String tag = "SpeedRentCarActivity";
    public Activity context;
    @InjectView(R.id.seek)
    LinearLayout         barPressure;
    @InjectView(R.id.price_hour)
    TextView             price_hour;
    @InjectView(R.id.price_day)
    TextView             price_day;
    @InjectViews({R.id.gexing, R.id.suv, R.id.shushi, R.id.jingji, R.id.haohua, R.id.shangwu})
    List<RelativeLayout> types;
    @InjectView(R.id.address_text)
    TextView             address;
    String                add;
    RangeSeekBar<Integer> seekBar;
    long start_long, end_long = 0;

    String start_time = "", end_time = "";


    long startTimeLong = 0;

    @OnClick(R.id.start_time_root)
    public void startClick()
    {

        SimpleDateFormat tipformatter = new SimpleDateFormat("HH");
        startTimeLong = System.currentTimeMillis()/* + 4*HOUR_TIMES;*/;
        if (start_long != 0)
        {
            startTimeLong = start_long;
        }
        else
        {
            int hour = Integer.parseInt(tipformatter.format(startTimeLong));
            if (hour >= 18)//18点以后
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                cal.set(Calendar.HOUR_OF_DAY, 10);
                cal.set(Calendar.MINUTE, 0);
                startTimeLong = cal.getTimeInMillis();
            }
            else if (hour < 6)
            {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 10);
                cal.set(Calendar.MINUTE, 0);
                startTimeLong = cal.getTimeInMillis();
            }
            else
            {
                startTimeLong += 4 * HOUR_TIMES;
            }
        }


        Config.showDayFirstPickDialog(context, System.currentTimeMillis(), startTimeLong, Config.startTimeCount, new Config.TimePickResult()
        {
            @Override
            public boolean hook()
            {
                return true;
            }

            @Override
            public void timePickResult(long date)
            {

                if (date < System.currentTimeMillis() + HOUR_TIMES)
                {
                    showToast("取车时间最早为1小时以后");
                    return;
                }

                startTimeLong = date;
                start_long = date;
                try
                {
                    filter.put("start", (start_long / 1000) + "");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                updateTimeText(start_time_text, date);
                setRentTime();
                if (end_long == 0)
                {
                    end_long = date + DAY_TIMES;

                    try
                    {
                        filter.put("end", (end_long / 1000) + "");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    updateTimeText(end_time_text, end_long);
                    setRentTime();
                }
                else
                {
                    long start, end;
                    start = start_long;
                    end = end_long;
                    if (end < start || end - start < HOUR_TIMES * 4 || end - start > DAY_TIMES * 7)
                    {
                        end_long = 0;
                        end_time_text.setText("");
                    }
                    setRentTime();
                }
            }
        });
    }


    @OnClick(R.id.end_time_root)
    public void endClick()
    {
        if (start_long == 0)
        {
            showToast("请先选择取车时间");
        }
        else
        {
            long current = 0;
            if (end_long == 0)
            {
                current = start_long + HOUR_TIMES * 4;
            }
            else
            {
                current = end_long;
            }
            Config.showDaySecondPickDialog(context, System.currentTimeMillis(), current, start_long, Config.RentTimeCount, new Config.TimePickResult()
            {
                @Override
                public boolean hook()
                {
                    return true;
                }

                @Override
                public void timePickResult(long date)
                {

                    if (date < start_long + HOUR_TIMES * 4)
                    {
                        showToast("租期最少4个小时");
                        return;
                    }
                    else if (date > start_long + (DAY_TIMES * Config.RentTimeCount))
                    {
                        showToast("最长租期" + Config.RentTimeCount + "天");
                        return;
                    }
                    end_long = date;
                    try
                    {
                        filter.put("end", (end_long / 1000) + "");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    updateTimeText(end_time_text, end_long);
                    setRentTime();
                }
            });
        }
    }

    public final long DAY_TIMES    = 1000 * 60 * 60 * 24;
    public final long HOUR_TIMES   = 1000 * 60 * 60;
    public final long MINUTE_TIMES = 1000 * 60;
    @InjectView(R.id.start_time)
    TextView start_time_text;
    @InjectView(R.id.end_time)
    TextView end_time_text;

    @OnClick(R.id.address_root)
    public void addressClick()
    {
        Intent intent = new Intent(context, SelectAddressActivity.class);
        SharedPreferences citysp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        intent.putExtra("city", citysp.getString("city", "北京"));
        intent.putExtra("lat", currentLat);
        intent.putExtra("lng", currentLng);
        intent.getBooleanExtra("isexpaand", false);
        intent.putExtra("current_address", address.getText().toString());
        startActivityForResult(intent, 154);
    }


    private void updateTimeText(TextView view, long date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String dateString = formatter.format(date);
        view.setText(dateString);
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Long date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    int priceMin, priceMax;
    double currentLat, currentLng;
    @InjectView(R.id.rent_time)
    TextView rent_time;
    @InjectView(R.id.rent_title)
    TextView rentTitle;

    public void setRentTime()
    {
        long time = end_long - start_long;
        long day = time / 86400000; // 以天数为单位取整
        long hour = time % 86400000 / 3600000; // 以小时为单位取整
        long min = time % 86400000 % 3600000 / 60000; // 以分钟为单位取整
        String string = "";
        if (day > 0)
        {
            string = "" + day + "天";
        }
        if (hour > 0)
        {
            string += hour + "小时";
        }
        if (min > 0)
        {
            string += min + "分钟";
        }
        if (day > 0 || hour > 0 || min > 0)
        {
            rentTitle.setVisibility(View.VISIBLE);
            rent_time.setVisibility(View.VISIBLE);
            rent_time.setText(string);
        }
        else
        {
            rentTitle.setVisibility(View.GONE);
            rent_time.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.speed_rent_car);
        ButterKnife.inject(this);
        citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        //如果进入后,查看到有经纬度
        if (getIntent().hasExtra("lat") && getIntent().hasExtra("lng"))
        {
            currentLat = getIntent().getDoubleExtra("lat", 0);
            currentLng = getIntent().getDoubleExtra("lng", 0);
            String addressStr = getIntent().getStringExtra("address");
            address.setText(addressStr);
        }
        //如果所在城市不是选择城市
        else if (Config.currentCity != null && Config.currentCity.indexOf(citysp.getString("city", "北京")) == -1)
        {

            if (Config.openCity != null && Config.openCity.size() > 0)
            {
                for (int i = 0; i < Config.openCity.size(); i++)
                {
                    try
                    {
                        if (Config.openCity.get(i).getName().equals(citysp.getString("city", "北京")))
                        {
                            cityLat = Config.openCity.get(i).getCenterPosition().getLat();
                            cityLng = Config.openCity.get(i).getCenterPosition().getLng();
                            currentLat = cityLat;
                            currentLng = cityLng;
                            filter.put("lat", cityLat);
                            filter.put("lng", cityLng);
                            GeocodeSearch geocoderSearch = new GeocodeSearch(context);
                            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener()
                            {
                                public void onRegeocodeSearched(RegeocodeResult result, int rCode)
                                {

                                    if (rCode == 0)
                                    {
                                        if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null)
                                        {
                                            address.setText(result.getRegeocodeAddress().getFormatAddress());
                                            add = result.getRegeocodeAddress().getFormatAddress();
                                        }
                                    }
                                    else
                                    {
                                    }
                                }

                                @Override
                                public void onGeocodeSearched(GeocodeResult arg0, int arg1)
                                {

                                }
                            });
                            // // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(cityLat, cityLng), 200, GeocodeSearch.AMAP);
                            geocoderSearch.getFromLocationAsyn(query);

                            break;
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            {
                Config.getCoordinates(context, new LocationListener()
                {
                    @Override
                    public void locationSuccess(double lat, double lng, String addr)
                    {
                        try
                        {
                            currentLat = lat;
                            currentLng = lng;
                            filter.put("lat", lat);
                            filter.put("lng", lng);
                            address.setText(addr);
                            add = addr;
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }

        if (Config.startTimeLong - System.currentTimeMillis() < HOUR_TIMES)
        {
            Config.startTimeLong = 0;
            Config.endTimeLong = 0;
        }
        if (Config.startTimeLong != 0)
        {
            start_long = Config.startTimeLong;
            updateTimeText(start_time_text, Config.startTimeLong);
            try
            {
                filter.put("start", (start_long / 1000) + "");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        if (Config.endTimeLong != 0)
        {
            end_long = Config.endTimeLong;
            updateTimeText(end_time_text, Config.endTimeLong);
            try
            {
                filter.put("end", (end_long / 1000) + "");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        setRentTime();
        seekBar = new RangeSeekBar<Integer>(0, 25, this);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>()
        {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue)
            {
                // handle changed range values
                String min = (minValue * 20) + "";
                String max = "不限";
                priceMin = minValue * 20;
                priceMax = maxValue * 20;


                if (maxValue != 25)
                {
                    max = (maxValue * 20) + "";
                    price_hour.setText((int) (Math.ceil((Float.parseFloat(min) / 8))) + "-" + (int) (Math.ceil((Float.parseFloat(max) / 8))) + "元/小时");
                    price_day.setText(min + "-" + max + "元/天");
                    try
                    {
                        filter.put("price_min", (int) ((Math.ceil((Float.parseFloat(min))))));
                        filter.put("price_max", (int) ((Math.ceil((Float.parseFloat(max))))));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    if (minValue == 0)
                    {
                        price_hour.setText("任意");
                        price_day.setText("任意");
                        try
                        {
                            filter.put("price_min", "0");
                            filter.remove("price_max");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        price_hour.setText("最低" + (int) (Math.ceil((Float.parseFloat(min) / 8))) + "元/小时");
                        price_day.setText("最低" + min + "元/天");
                        try
                        {
                            filter.put("price_min", (int) (Math.ceil((Float.parseFloat(min)))) + "");
                            filter.remove("price_max");
//                            FindCarManagerFragment.filter.put("price_max",(Integer.parseInt(max) / 8) );
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        barPressure.addView(seekBar);
        seekBar.setSelectedMaxValue(25);
        seekBar.setSelectedMinValue(0);
        for (RelativeLayout view : types)
        {
            view.setOnClickListener(typeClick);
        }
    }


    double cityLat = 0, cityLng = 0;
    SharedPreferences citysp;

    @OnClick(R.id.sure)
    public void sureClick()
    {
        if (start_long == 0 || end_long == 0)
        {
            showToast("请选择用车时间");
            return;
        }
        else if (address.getText().toString().trim().length() == 0)
        {
            showToast("请选择取车位置");
            return;
        }
        Config.startTimeLong = start_long;
        Config.endTimeLong = end_long;
        if (Config.isGuest(context))
        {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
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
        else
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间


            String currentTime = formatter.format(curDate);
            Date /*startDate = null, endDate = null,*/ currentDate = null;
            try
            {
                Date startDate = new Date(start_long);
                Date endDate = new Date(end_long);
                currentDate = formatter.parse(currentTime);
                long start = startDate.getTime();
                long end = endDate.getTime();
                long current = currentDate.getTime();
                MLog.e(tag, "start = " + start + "   end = " + end + " current = " + current);
                if (start - current < Config.START_TIME_DIFFERENCE)
                {
                    Toast.makeText(context, "取车时间最早是60分钟之后", Toast.LENGTH_SHORT).show();
                }
                else if (start > end)
                {
                    Toast.makeText(context, "还车时间不能小于取车时间", Toast.LENGTH_SHORT).show();
                }
                else if (end - start < Config.MIN_HIRE_TIME)
                {
                    Toast.makeText(context, "最少租用4小时", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Config.startTimeLong = start_long;
                    Config.endTimeLong = end_long;
                    showProgress(false);
                    OrderFormInterface26.FastRentCar.Request.Builder builder = OrderFormInterface26.FastRentCar.Request.newBuilder();
                    for (int i = 0; i < Config.openCity.size(); i++)
                    {
                        if (Config.openCity.get(i).getName().equals(citysp.getString("city", "北京")))
                        {
                            builder.setCityId(Config.openCity.get(i).getCityId());
                            break;
                        }
                    }
                    builder.setPriceMin(priceMin);
                    if (priceMax != 25 * 20)
                    {
                        builder.setPriceMax(priceMax);
                    }

                    MLog.e(tag, "Price = priceMin__" + priceMin + " PriceMax__" + priceMax);
                    builder.setStartTime((int) (start_long / 1000));
                    builder.setEndTime((int) (end_long / 1000));


                    if (gearbox_auto_tv.isSelected() && gearbox_manual_tv.isSelected())
                    {
                        builder.clearCarTransmissionType();
                        filter.remove("boxiang");
                    }
                    else if (!gearbox_auto_tv.isSelected() && !gearbox_manual_tv.isSelected())
                    {
                        builder.clearCarTransmissionType();
                        filter.remove("boxiang");
                    }
                    else if (gearbox_manual_tv.isSelected())
                    {

                        builder.setCarTransmissionType(CarCommon.CarTransmissionType.HAND);
                        filter.put("boxiang", CarCommon.CarTransmissionType.HAND_VALUE);
                    }
                    else
                    {
                        builder.setCarTransmissionType(CarCommon.CarTransmissionType.AUTO);
                        filter.put("boxiang", CarCommon.CarTransmissionType.AUTO_VALUE);
                    }
//                    if (filter.has("boxiang")) {
//                        if (filter.getInt("boxiang") == CarCommon.CarTransmissionType.HAND_VALUE) {
//                            builder.setCarTransmissionType(CarCommon.CarTransmissionType.HAND);
//                        } else {
//                            builder.setCarTransmissionType(CarCommon.CarTransmissionType.AUTO);
//                        }
//                    }
                    UuCommon.LatlngPosition.Builder latlngBuilder = UuCommon.LatlngPosition.newBuilder();
                    try
                    {
                        latlngBuilder.setLat(filter.getDouble("lat"));
                        latlngBuilder.setLng(filter.getDouble("lng"));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    builder.setPosition(latlngBuilder);
                    builder.setCancelLastPreOrder(isCancellast);
                    if (types.get(0).isSelected())//个性
                    {
                        builder.addType(CarCommon.CarType.SPECIFIC);
                    }
                    if (types.get(1).isSelected())//SUV
                    {
                        builder.addType(CarCommon.CarType.SUV);
                    }
                    if (types.get(2).isSelected())//舒适型
                    {
                        builder.addType(CarCommon.CarType.COMFORTABLE);
                    }
                    if (types.get(3).isSelected())//经济型
                    {
                        builder.addType(CarCommon.CarType.ECONOMICAL);
                    }
                    if (types.get(4).isSelected())//豪华型
                    {
                        builder.addType(CarCommon.CarType.LUXURIOUS);
                    }
                    if (types.get(5).isSelected())//商务型
                    {
                        builder.addType(CarCommon.CarType.BUSINESS);
                    }
                    MLog.e("type", "fasttype___ =" + builder.getTypeList().toString());

                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.FastRentCar_VALUE);
                    networkTask.setTag("FastRentCar");
                    networkTask.setBusiData(builder.build().toByteArray());
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
                    {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData)
                        {
                            String serverMsg = responseData.getResponseCommonMsg().getMsg();
                            if (responseData.getRet() == 0)
                            {
                                try
                                {
                                    final OrderFormInterface26.FastRentCar.Response response = OrderFormInterface26.FastRentCar.Response.parseFrom(responseData.getBusiData());
                                    MLog.e(tag, "speed rent car ret = " + response.getRet());
                                    String msg = "";
                                    if (response.getRet() == 0)//成功
                                    {
                                        Config.isSppedIng = true;
                                        isCancellast = false;
                                        Intent intent = new Intent(context, NoCarWait.class);
                                        intent.putExtra("maxtime", (((long) response.getTotalWaitTime()) * 1000));
                                        intent.putExtra("passedTime", (((long) response.getPassedTime()) * 1000));
                                        Config.isUserCancel = false;
                                        getApp().startRenting();
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if (response.getRet() == -1)//失败
                                    {
                                        if (serverMsg.length() > 0)
                                        {
                                            msg = serverMsg;
                                        }
                                        showToast(msg);

                                    }
                                    else if (response.getRet() == -2)//没车
                                    {
                                        if (serverMsg.length() > 0)
                                        {
                                            msg = serverMsg;
                                        }
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(msg);
                                        builder.setNegativeButton("重新筛选", null);
                                        builder.create().show();
                                    }
                                    else if (response.getRet() == -3)//上一单未完成,需要弹窗确认
                                    {
                                        if (serverMsg.length() > 0)
                                        {
                                            msg = serverMsg;
                                        }
                                        else
                                        {
                                            msg = "您已发出预约请求,正在等待车主回应.若进行新的预约将中断之前的请求,是否继续预约附近的车?";
                                        }
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(msg);
                                        builder.setNegativeButton("返回", null);
                                        builder.setNeutralButton("一键约车", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                showProgress(false);
                                                isCancellast = true;
                                                sureClick();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    else if (response.getRet() == -4)
                                    {

                                        if (serverMsg.length() > 0)
                                        {
                                            msg = serverMsg;
                                        }
                                        else
                                        {
                                            msg = "您当前有未支付订单,支付完成后即可再次约车";
                                        }
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(msg);
                                        builder.setNegativeButton("返回", null);
                                        builder.setNeutralButton("去支付", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                                                intent.putExtra(SysConfig.R_SN, response.getWaitToPayOrderId());
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    else if (response.getRet() == -5)
                                    {

                                        if (serverMsg.length() > 0)
                                        {
                                            msg = serverMsg;
                                        }
                                        else
                                        {
                                            msg = "您当前有正在进行中的快速约车,不能再次快速约车";
                                        }
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(msg);
                                        builder.setNegativeButton("知道了", null);
                                        builder.create().show();
                                    }
                                    else if (response.getRet() == -6)
                                    {
                                        if (serverMsg.length() > 0)
                                        {
                                            msg = serverMsg;
                                        }
                                        else
                                        {
                                            msg = "您正在进行中的订单和此预约单时间有冲突,不能发起快速约车";
                                        }
                                        showToast(msg);
                                    }
                                    else
                                    {
                                        if (serverMsg.length() > 0)
                                        {
                                            showToast(serverMsg);
                                        }
                                        else
                                        {
                                            Config.showFiledToast(context);
                                        }
                                    }
                                }
                                catch (InvalidProtocolBufferException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            else
                            {
                                Config.showFiledToast(context);
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
            }
            catch (Exception e)
            {
                Toast.makeText(context, "请选择取车时间和还车时间", Toast.LENGTH_SHORT).show();
            }


        }


    }

    boolean isCancellast = false;

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public View.OnClickListener typeClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            v.setSelected(!v.isSelected());
            try
            {
                setTypeSelect();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }


    };

    public void setTypeSelect() throws JSONException
    {
        boolean ishas = false;
        String type = "";
        for (int i = 0; i < types.size(); i++)
        {
            if (types.get(i).isSelected())
            {
                ishas = true;
                type += (i + 1) + ",";
            }
        }
        if (!ishas)
        {
            filter.remove("type");
        }
        else
        {
            filter.put("type", type.subSequence(0, type.length() - 1));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (item.getItemId() == R.id.reset)
        {

            start_long = 0;
            start_time_text.setText("");
            end_long = 0;
            end_time = "";
            end_time_text.setText("");

            try
            {
                filter.put("price_min", "0");
                filter.remove("price_max");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            seekBar.setSelectedMaxValue(25);
            seekBar.setSelectedMinValue(0);
            gearbox_auto_tv.setSelected(false);
            gearbox_manual_tv.setSelected(false);
            for (RelativeLayout type : types)
            {
                type.setSelected(false);
            }
            filter = new JSONObject();

            Config.getCoordinates(context, new LocationListener()
            {
                @Override
                public void locationSuccess(double lat, double lng, String addr)
                {
                    try
                    {
                        filter.put("lat", lat);
                        filter.put("lng", lng);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    address.setText(addr);
                    add = addr;
                }
            });
            setRentTime();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == 154)
            {
                if (data.getBooleanExtra("current", false))
                {
                }
                else
                {
                    String name = data.getStringExtra("address");
                    try
                    {
                        filter.put("lat", data.getDoubleExtra("lat", 0));
                        filter.put("lng", data.getDoubleExtra("lng", 0));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    address.setText(name);
                    add = name;
                }
            }
        }
    }

    @InjectView(R.id.gearbox_auto)
    LinearLayout gearbox_auto_tv;
    @InjectView(R.id.gearbox_manual)
    LinearLayout gearbox_manual_tv;

    @OnClick(R.id.gearbox_auto)
    public void autoclick()
    {
        gearbox_auto_tv.setSelected(!gearbox_auto_tv.isSelected());
//        try {
//            if(gearbox_auto_tv.isSelected())
//            {
//                if(filter.has("boxiang") && filter.getInt("boxiang") == CarCommon.CarTransmissionType.HAND_VALUE)
//                {
//
//                }
//                filter.put("boxiang", CarCommon.CarTransmissionType.AUTO_VALUE);
//            }
//            else
//            {
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @OnClick(R.id.gearbox_manual)
    public void manualclick()
    {
        gearbox_manual_tv.setSelected(!gearbox_manual_tv.isSelected());
//        if (gearbox_auto_tv.isSelected()) {
//            gearbox_auto_tv.setSelected(false);
//
//        }
//        try {
//            filter.put("boxiang", CarCommon.CarTransmissionType.HAND_VALUE);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}
