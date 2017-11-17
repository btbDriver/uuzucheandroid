package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

/**
 * Created by 16515_000 on 2014/7/18.
 */
public class CarRealTimeStatusInfoActivity extends Activity
{
    public String tag = "CarrealTimeStatusInfoActivity";
    public Activity context;
    public String   carsn;
    /**
     * 1=
     */
//    public String code = "";
    public String   plan_start, plan_end;
    TextView text_show;
    long     end_time_long;
    Handler handler = new Handler();
    private AMap    aMap;
    private MapView mapView;

    private double lat, lng;

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        carsn = getIntent().getStringExtra(SysConfig.CAR_SN);
        setContentView(R.layout.car_real_time_status_info);
        text_show = (TextView) findViewById(R.id.text_show);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(b); // 此方法必须重写
        aMap = mapView.getMap();
        init();
        getData();
    }

    /**
     * 初始化AMap对象
     */
    private void init()
    {
        aMap = mapView.getMap();
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_mydis_icon));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.parseColor("#00FFFFFF"));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.parseColor("#00FFFFFF"));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationRotateAngle(180);
        MLog.e(tag, "2");
        // aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMarkerClickListener(null);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(null);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(null);// 设置自定义InfoWindow样式
        final SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.car_real_time_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (item.getItemId() == R.id.refush)
        {
            getData();
        }
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void getData()
    {

        Config.showProgressDialog(context, false, null);
        CarInterface.QueryCarRealTimeState.Request.Builder request = CarInterface.QueryCarRealTimeState.Request.newBuilder();
        request.setCarId(carsn);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryCarRealTimeState_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {

                if (responseData.getRet() == 0)
                {


                    try
                    {
                        CarInterface.QueryCarRealTimeState.Response response = CarInterface.QueryCarRealTimeState.Response.parseFrom(responseData.getBusiData());

                        if (response.getRet() == 0)
                        {

                            CarCommon.CarBriefInfo carBriefInfo = response.getCarBriefInfo();
                            if (carBriefInfo.hasPosition())
                            {

                                UuCommon.LatlngPosition position = carBriefInfo.getPosition();
                                if (position.hasLng())
                                {
                                    lng = position.getLng();
                                }
                                if (position.hasLat())
                                {
                                    lat = position.getLat();
                                }
                            }
                            aMap.clear();
                            MarkerOptions op = new MarkerOptions();
                            LatLng latlng = new LatLng(lat, lng);
                            op.position(latlng);
                            op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_nobox_icon));
//                            op.perspective(true);
                            Marker marker = aMap.addMarker(op);
                            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, 11, 0, 0)), 1000, null);
//                            plan_start = json.getJSONObject("content").get("plan_start").toString();
//                            plan_end = json.getJSONObject("content").get("plan_end").toString();
//                            plan_start =
//                            long start_time_long = Long.parseLong((plan_start + "000"));
//                            Date start_date = new Date();
//                            start_date.setTime(start_time_long);
//                            Date end_date = new Date();
//                            end_time_long = Long.parseLong((plan_end + "000"));
//                            end_date.setTime(end_time_long);
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
