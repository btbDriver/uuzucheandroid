package com.youyou.uucar.UI.Common.Car;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by taurusxi on 14-9-2.
 */
public class CarInfoAndLocationActivity extends BaseActivity {


    //    @InjectView(R.id.imageview_empty)
//    BaseNetworkImageView mImageviewEmpty;
//    @InjectView(R.id.car_type_empty)
//    TextView mCarTypeEmpty;
//    @InjectView(R.id.plate_number_empty)
//    TextView mPlateNumberEmpty;
//    @InjectView(R.id.price_empty)
//    TextView mPriceEmpty;
    @InjectView(R.id.map)
    MapView mMap;
    //    @InjectView(R.id.main_linear)
//    LinearLayout mMainLinear;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private String carSn;
    private boolean showEditBtFlag;
    private AMap aMap;
    private double lng, lat;
    private View actionBarView;
    private Button mMapTv;
    public TextView noDataRefush;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.car_info_and_location);
        ButterKnife.inject(this);
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllFramelayout.noDataReloading();
                queryCarRealTimeState(carSn);
            }
        });
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
        showEditBtFlag = getIntent().getBooleanExtra(SysConfig.SHOW_EDIT_BT, true);
        mMap.onCreate(b); // 此方法必须重写
        init();
        queryCarRealTimeState(carSn);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_desc, menu);
        menu.findItem(R.id.action_save).setTitle("");
        return true;
    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        aMap = mMap.getMap();
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_mydis_icon));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.parseColor("#00FFFFFF"));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.parseColor("#00FFFFFF"));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationRotateAngle(180);
        // aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMarkerClickListener(null);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(null);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(null);// 设置自定义InfoWindow样式
        final SharedPreferences sp = CarInfoAndLocationActivity.this.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
    }

    private void queryCarRealTimeState(String carSn) {

        CarInterface.QueryCarRealTimeState.Request.Builder request = CarInterface.QueryCarRealTimeState.Request.newBuilder();
        request.setCarId(carSn);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryCarRealTimeState_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData response) {
                if (response.getRet() == 0) {
                    mAllFramelayout.makeProgreeDismiss();
                    Config.showToast(context, response.getToastMsg());
                    try {
                        CarInterface.QueryCarRealTimeState.Response data = CarInterface.QueryCarRealTimeState.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0) {

                            CarCommon.CarBriefInfo carBriefInfo = data.getCarBriefInfo();
                            String carId = carBriefInfo.getCarId();// 车辆唯一标示id
                            String licensePlate = carBriefInfo.getLicensePlate();// 汽车牌照
//                            mPlateNumberEmpty.setText(licensePlate);
                            String brand = carBriefInfo.getBrand();// 汽车品牌
                            String carModel = carBriefInfo.getCarModel();// 汽车型号
//                            mCarTypeEmpty.setText(brand + carModel);
                            CarCommon.CarTransmissionType transmissionType = carBriefInfo.getTransmissionType(); // 变速箱类型
                            if (carBriefInfo.hasPricePerDay()) {
                                float pricePerDay = carBriefInfo.getPricePerDay();// 汽车价格，按天算
//                                mPriceEmpty.setText(((int) pricePerDay) + "");
                            }
                            if (carBriefInfo.hasThumbImg()) {
//                                String thumbImg = carBriefInfo.getThumbImg();// 车辆缩略图
//                                UUAppCar.getInstance().display(thumbImg, mImageviewEmpty, R.drawable.list_car_img_def);
                            }
                            if (carBriefInfo.hasAddress()) {
                                String address = carBriefInfo.getAddress();// 车辆地址
                            }
                            if (carBriefInfo.hasDistanceFromRenter()) {
                                float distanceFromRenter = carBriefInfo.getDistanceFromRenter();// 车辆离租客有多少公里，单位为公里
                            }
                            if (carBriefInfo.hasPosition()) {
                                UuCommon.LatlngPosition position = carBriefInfo.getPosition();
                                if (position.hasLat()) {
                                    lat = position.getLat();
                                }
                                if (position.hasLng()) {
                                    lng = position.getLng();
                                }
                            }
                            aMap.clear();
                            MarkerOptions op = new MarkerOptions();
                            LatLng latlng = new LatLng(lat, lng);
                            op.position(latlng);
                            op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_nobox_icon));
//                            op.perspective(true);
                            Marker marker = aMap.addMarker(op);
                            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, 12, 0, 0)), 1000, null);
                        } else {

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

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
//        else if (item.getItemId() == R.id.action_save) {
//            if (carSn != null) {
//                Intent intent = new Intent();
//                intent.putExtra(SysConfig.CAR_SN, carSn);
//                intent.setClass(CarInfoAndLocationActivity.this, CarInfoSimpleActivity.class);
//                CarInfoAndLocationActivity.this.startActivity(intent);
//            }
//        }
        return true;
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
