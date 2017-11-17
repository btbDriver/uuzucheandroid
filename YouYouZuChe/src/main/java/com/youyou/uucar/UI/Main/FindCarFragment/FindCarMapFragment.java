package com.youyou.uucar.UI.Main.FindCarFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Main.rent.FindCarAgreeActivity;
import com.youyou.uucar.UI.Main.rent.NoCarWait;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
import com.youyou.uucar.UI.Renter.filter.FilteredCarListActivity;
import com.youyou.uucar.UI.Renter.filter.RentFilterActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.List;


/**
 * Created by Administrator on 2014/7/7.
 */
public class FindCarMapFragment extends Fragment implements LocationSource, AMapLocationListener, AMap.OnCameraChangeListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener {

    public static final int FILTER_REQUEST = 100;

    public static final String TAG = FindCarMapFragment.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "FindCarMapFragment";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();

    public Activity context;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public View.OnClickListener addressClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SelectAddressActivity.class);
            intent.getBooleanExtra("isexpaand", true);
            SharedPreferences citysp = getActivity().getSharedPreferences("selectcity", Context.MODE_PRIVATE);
            intent.putExtra("city", citysp.getString("city", "北京"));
            intent.putExtra("current_address", address.getText().toString());
            intent.putExtra("isexpaand", true);
            startActivityForResult(intent, 154);
        }
    };

    public TranslateAnimation moveUpAnim;
    RelativeLayout /* timeRoot, */addressRoot;
    TextView start_time, end_time;
    TextView address;
    TextView rent;
    RelativeLayout titleRoot;
    View location_bt;
    View view;
    UUProgressFramelayout mAllFramelayout;
    ObserverListener observer = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
            final SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
            for (int i = 0; i < Config.openCity.size(); i++) {
                if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                    double lat = Config.openCity.get(i).getCenterPosition().getLat();
                    double lng = Config.openCity.get(i).getCenterPosition().getLng();
                    currentMyDisLatlng = new LatLng(lat, lng);
                    oldZoom = Config.openCity.get(i).getZoom();
                    oldLatlng = currentMyDisLatlng;
                    getcars(currentMyDisLatlng);
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentMyDisLatlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);
                    break;
                }
            }
        }
    };
    LatLng currentMyDisLatlng;
    double currentLat, currentLng;
    public View.OnClickListener rentClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!Config.isNetworkConnected(context))
            {
                Config.showFiledToast(context);
                return;
            }
            if (Config.isGuest(context))
            {
                MLog.e(TAG, "guest");
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("goto", LoginActivity.RENTER_REGISTER);
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
                if (Config.isSppedIng)
                {
                    if (Config.speedHasAgree)
                    {
                        Intent intent = new Intent(context, FindCarAgreeActivity.class);
                        intent.putExtra("maxtime", (long) (600000));
                        intent.putExtra("passedTime", (long) (0));
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(context, NoCarWait.class);
                        intent.putExtra("maxtime", (long) (600000));
                        intent.putExtra("passedTime", (long) (0));
                        startActivity(intent);
                    }
                }
                else if (Config.hasPayOrder)
                {
                    Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                    intent.putExtra(SysConfig.R_SN, Config.waitPayOrderId);
                    startActivity(intent);
                }
                else
                {
//                    Intent intent = new Intent(getActivity(), SpeedRentCarActivity.class);
                    Intent intent = new Intent(getActivity(), RentFilterActivity.class);
                    intent.putExtra("address", address.getText().toString());
                    intent.putExtra("lat", currentLat);
                    intent.putExtra("lng", currentLng);
                    intent.putExtra("ismap", true);
                    intent.putExtra("mult", true);//一对多流程
                    startActivityForResult(intent, 615);
                }
            }

        }
    };

    public com.amap.api.maps2d.model.BitmapDescriptor getCarIcon(CarInterface.MapSearchCarList.MapCarPoint point, boolean pressed)
    {
        if (point.getTransmissionType() == CarCommon.CarTransmissionType.AUTO)
        {
            if (pressed)
                return BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_d_a_pressed);
            else
                return BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_d_a);

        }
        else
        {

            if (pressed)
                return BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_d_m_pressed);
            else
                return BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_d_m);
        }
    }

    // NewRentWait wait;
    Marker currentMarker;
    String r_sn;
    boolean isHideMarker       = false;
    boolean isDoubleHideMarker = false;
    public AMap.OnMarkerClickListener MarkerClick = new AMap.OnMarkerClickListener()
    {
        @Override
        public boolean onMarkerClick(Marker marker)
        {
            if (marker.getObject() != null)
            {
                CarItem item = (CarItem) marker.getObject();
                if (currentMarker != null)
                {
                    CarItem item2 = (CarItem) currentMarker.getObject();
                    if (item2.point.getDuplicateCars() == 1)
                    {

                        if (item2.point.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO))
                        {
                            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_a));
                        }
                        else
                        {
                            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_m));
                        }
                    }
                    else
                    {
                        currentMarker.setIcon(getCarIcon(item2.point, false));
                    }
                }

                if (item.point.getDuplicateCars() == 1)
                {
                    if (item.point.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO))
                    {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_a_select));
                    }
                    else
                    {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_m_select));
                    }
                }
                else
                {

                    marker.setIcon(getCarIcon(item.point, true));
                }
                currentMarker = marker;

                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker.getPosition(), aMap.getCameraPosition().zoom, 0, 0)));
                isHideMarker = true;
                isDoubleHideMarker = true;
                showInfoWindows(marker);
            }
            return true;
        }
    };


    boolean isFirst = true;
    private AMap                      aMap;
    private MapView                   mapView;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy      mAMapLocationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = (View) inflater.inflate(R.layout.fragment_find_car_map, null);
        context = getActivity();
        location_bt = (View) view.findViewById(R.id.location_bt);
        location_bt.setVisibility(View.GONE);
        titleRoot = (RelativeLayout) view.findViewById(R.id.title_root);
        rent = (TextView) view.findViewById(R.id.rent);
        rent.setOnClickListener(rentClick);
        address = (TextView) view.findViewById(R.id.address_text);
        start_time = (TextView) view.findViewById(R.id.start_time);
        end_time = (TextView) view.findViewById(R.id.end_time);
        addressRoot = (RelativeLayout) view.findViewById(R.id.address_title);
        addressRoot.setOnClickListener(addressClick);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        aMap = mapView.getMap();
        if (view.getParent() != null)
        {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        MLog.e(TAG, "goto init");
        init();
        SharedPreferences sp = context.getSharedPreferences("first_rentcar", Context.MODE_PRIVATE);
        if (sp.getBoolean("first", false))
        {
        }
        ObserverManager.addObserver("rentmap", observer);
        ObserverManager.addObserver("FindCarMap", obListener);
        WindowsView = (LinearLayout) view.findViewById(R.id.windows_root);
        mAllFramelayout = (UUProgressFramelayout) view.findViewById(R.id.all_framelayout);
        initNoteDataRefush();
        if (Config.isNetworkConnected(getActivity()))
        {
            mAllFramelayout.makeProgreeDismiss();
        }
        else
        {
            mAllFramelayout.makeProgreeNoData();
        }


        return view;
    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Config.isNetworkConnected(getActivity()))
                {
                    mAllFramelayout.makeProgreeDismiss();
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    /**
     * 初始化AMap对象
     */
    private void init()
    {
        MLog.e(TAG, "1");
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
        MLog.e(TAG, "2");
        // aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setOnMarkerClickListener(MarkerClick);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        final SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                MLog.e(TAG, "onMapClick()");
                if (isHideMarker)
                {
                    isHideMarker = false;
                }
                else if (isDoubleHideMarker)
                {
                    isDoubleHideMarker = false;
                }
                else
                {
                    if (currentMarker != null)
                    {
                        CarItem item2 = (CarItem) currentMarker.getObject();
                        if (item2.point.getDuplicateCars() == 1)
                        {

                            if (item2.point.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO))
                            {
                                currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_a));
                            }
                            else
                            {
                                currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_m));
                            }
                        }
                        else
                        {
                            currentMarker.setIcon(getCarIcon(item2.point, false));
                        }
//                        currentMarker.hideInfoWindow();
                        hideWindowsView();
                        currentMarker = null;

                    }
                }
            }
        });
        location_bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Config.getCoordinates(context, new LocationListener()
                {
                    @Override
                    public void locationSuccess(double lat, double lng, String addr)
                    {
                        LatLng latlng = new LatLng(lat, lng);
                        for (int i = 0; i < Config.openCity.size(); i++)
                        {
                            if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName()))
                            {
                                isFirst = true;
                                currentMyDisLatlng = new LatLng(lat, lng);
//                                aMap.animateCamera(newCameraPosition(new CameraPosition(latlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);

//                                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);

                                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, Config.openCity.get(i).getZoom(), 0, 0)));
                                Config.changeCityMap = false;
                                break;
                            }
                        }
                    }
                });
            }
        });

        boolean isHas = false;
        if (Config.openCity != null && Config.openCity.size() > 0)
        {
            for (int i = 0; i < Config.openCity.size(); i++)
            {
                if (Config.currentCity == null)
                {
                    Config.currentCity = SysConfig.BEI_JING_CITY;
                }

                if (Config.currentCity.indexOf(sp.getString("city", "北京")) != -1)
                {
                    isHas = true;
                    break;
                }
//            else
//            {
//
//                if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName()))
//                {
//                    double lat = Config.openCity.get(i).getCenterPosition().getLat();
//                    double lng = Config.openCity.get(i).getCenterPosition().getLng();
//                    currentMyDisLatlng = new LatLng(lat, lng);
//                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentMyDisLatlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);
//                }
//            }
            }
        }
        if (isHas)
        {
            Config.getCoordinates(context, new LocationListener()
            {
                @Override
                public void locationSuccess(double lat, double lng, String addr)
                {
                    LatLng latlng = new LatLng(lat, lng);
                    for (int i = 0; i < Config.openCity.size(); i++)
                    {
                        if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName()))
                        {
                            isFirst = true;
                            currentMyDisLatlng = new LatLng(lat, lng);
//                            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);

                            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, Config.openCity.get(i).getZoom(), 0, 0)));
                            Config.changeCityMap = false;
                            getcars(latlng);
                            break;
                        }
                    }
                }
            });
        }
        else
        {
            for (int i = 0; i < Config.openCity.size(); i++)
            {
                if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName()))
                {
                    double lat = Config.openCity.get(i).getCenterPosition().getLat();
                    double lng = Config.openCity.get(i).getCenterPosition().getLng();
                    currentMyDisLatlng = new LatLng(lat, lng);
//                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentMyDisLatlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);

                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentMyDisLatlng, Config.openCity.get(i).getZoom(), 0, 0)));
                }
            }
        }
        if (Config.currentCity.indexOf(sp.getString("city", "北京")) != -1)
        {
            location_bt.setVisibility(View.VISIBLE);
        }
        else
        {
            location_bt.setVisibility(View.GONE);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

//        mapView.onResume();
//
        MLog.e(TAG, "isSpeed ? " + Config.isSppedIng);
        if (Config.isSppedIng) {
            rent.setText("约车中...");
        } else if (Config.hasPayOrder) {
            rent.setText("约车成功");
        } else {
            rent.setText("预约附近的车");

        }
        if (Config.changeCityMap) {
            SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
            for (int i = 0; i < Config.openCity.size(); i++) {
                if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                    isFirst = true;
                    double lat = Config.openCity.get(i).getCenterPosition().getLat();
                    double lng = Config.openCity.get(i).getCenterPosition().getLng();
                    currentMyDisLatlng = new LatLng(lat, lng);
//                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentMyDisLatlng, Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);

                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentMyDisLatlng, Config.openCity.get(i).getZoom(), 0, 0)));
                    Config.changeCityMap = false;
                    break;
                }
            }
            if (Config.currentCity.indexOf(sp.getString("city", "北京")) != -1) {
                location_bt.setVisibility(View.VISIBLE);
            } else {
                location_bt.setVisibility(View.GONE);
            }
        }
    }

    public ObserverListener obListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
            if (from.equals("MainActivityTab")) {
                startActivityForResult(new Intent(getActivity(), RentFilterActivity.class), FILTER_REQUEST);
            }
        }
    };

    public void onPause() {
        super.onPause();
//        mapView.onPause();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 165) {
                if (data.hasExtra("start")) {
                    start_time.setText(data.getStringExtra("start"));
                }
                if (data.hasExtra("end")) {
                    end_time.setText(data.getStringExtra("end"));
                }
            } else if (requestCode == FILTER_REQUEST)// 筛选了
            {
                ObserverManager.getObserver("rentlist").observer("filter", "");
                ObserverManager.getObserver("rentmap").observer("filter", "");
            } else if (requestCode == 154) {
                if (data.getBooleanExtra("current", false)) {
                } else {
                    String name = data.getStringExtra("address");
                    currentLat = data.getDoubleExtra("lat", 0);
                    currentLng = data.getDoubleExtra("lng", 0);
                    address.setText(name);
//                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lng", 0)), 18, 0, 0)), 1000, null);
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lng", 0))));
                }
            } else if (requestCode == 615) {
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public View getInfoContents(Marker arg0) {
        return null;
    }

    LinearLayout WindowsView;

    public void showInfoWindows(final Marker marker) {
        WindowsView.setVisibility(View.VISIBLE);

        LinearLayout carInfoRoot = (LinearLayout) WindowsView.findViewById(R.id.car_info_root);
        LinearLayout moreRoot = (LinearLayout) WindowsView.findViewById(R.id.more_info_root);
        final CarItem item = (CarItem) marker.getObject();
        carInfoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (Config.isNetworkConnected(getActivity())) {
                Intent intent = new Intent();
                intent.putExtra("islist", false);
                intent.putExtra(SysConfig.CAR_SN, item.point.getCarId());

                intent.setClass(context, OldCarInfoActivity.class);
                startActivity(intent);
//                } else {
//                    mAllFramelayout.makeProgreeNoData();
//                }
            }
        });
        TextView name = (TextView) WindowsView.findViewById(R.id.name);
        name.setText(item.point.getBrand() + item.point.getCarModel());
        BaseNetworkImageView img = (BaseNetworkImageView) WindowsView.findViewById(R.id.car_img);
        UUAppCar.getInstance().display(ImageBaseUrl + item.point.getThumbImgName(), img, R.drawable.list_car_img_def);
        TextView price_day = (TextView) WindowsView.findViewById(R.id.price_day);
        price_day.setText(item.point.getPricePerDay() + "元/天");
        ImageView gearbox_icon = (ImageView) WindowsView.findViewById(R.id.gearbox_icon);
        TextView gearbox = (TextView) WindowsView.findViewById(R.id.gearbox_text);
        if (item.point.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO)) {
            gearbox_icon.setBackgroundResource(R.drawable.find_car_list_gearbox_a_icon);
            gearbox.setText("自动挡");
        } else {
            gearbox_icon.setBackgroundResource(R.drawable.find_car_list_gearbox_m_icon);
            gearbox.setText("手动挡");
        }
        if (item.point.getDuplicateCars() > 1) {
            moreRoot.setVisibility(View.VISIBLE);
            moreRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FilteredCarListActivity.class);
                    intent.putExtra("lat", item.point.getPosition().getLat());
                    intent.putExtra("lng", item.point.getPosition().getLng());
                    intent.putExtra("address", address.getText().toString());
                    intent.putExtra("isMap", true);//从地图一点多车进来的
                    startActivity(intent);

                }
            });
        } else {
            moreRoot.setVisibility(View.GONE);
        }
    }

    public void hideWindowsView() {
        WindowsView.setVisibility(View.GONE);
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return view;
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {

        MLog.e(TAG, "onCameraChange");
        if (isHideMarker) {
            isHideMarker = false;
        } else if (isDoubleHideMarker) {
            isDoubleHideMarker = false;
        } else {
            if (currentMarker != null) {
                CarItem item2 = (CarItem) currentMarker.getObject();

                if (item2 != null && item2.point != null) {
                    if (item2.point.getDuplicateCars() == 1) {
                        if (item2.point.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO)) {
                            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_a));
                        } else {
                            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_m));
                        }
                    } else {

                        currentMarker.setIcon(getCarIcon(item2.point, false));
                    }
                }
                hideWindowsView();
                currentMarker = null;
            }
        }
    }

    //上一次刷新的坐标
    LatLng oldLatlng;
    //上一次刷新的中心点到屏幕左上角的距离
    float oldDistance;
    //上一次刷新的缩放等级
    float oldZoom;

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
//        Toast.makeText(context,"缩放等级: "+cameraPosition.zoom,Toast.LENGTH_SHORT).show();
        LatLng latlng = cameraPosition.target;
        GeocodeSearch geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

                if (rCode == 0) {
                    if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                        address.setText(result.getRegeocodeAddress().getFormatAddress());
                    }
                } else {
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

            }
        });
        // // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latlng.latitude, latlng.longitude), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
        currentLat = latlng.latitude;
        currentLng = latlng.longitude;
        MLog.e(TAG, "isfirst ?" + isFirst);
        Point point = new Point();
        point.set(0, 0);
        if (isFirst) {
            isFirst = false;
            oldZoom = cameraPosition.zoom;
            oldLatlng = latlng;
            oldDistance = AMapUtils.calculateLineDistance(aMap.getProjection().fromScreenLocation(point), latlng);
            getcars(latlng);
        } else {
            MLog.e(TAG, " 移动距离= " + oldDistance + "    当前移动 = " + AMapUtils.calculateLineDistance(oldLatlng, latlng) + "   缩放等级差距 = " + Math.abs(cameraPosition.zoom - oldZoom));
            //移动距离大于上次刷新中心点到左上点距离后刷新或者缩放等级大于5刷新
            if (AMapUtils.calculateLineDistance(oldLatlng, latlng) > (oldDistance / 2) || Math.abs(cameraPosition.zoom - oldZoom) > 3) {
                getcars(latlng);
                oldZoom = cameraPosition.zoom;
                oldLatlng = latlng;
                oldDistance = AMapUtils.calculateLineDistance(aMap.getProjection().fromScreenLocation(point), latlng);
            }
        }

    }

    public String ImageBaseUrl;

    public void getcars(LatLng latlng) {
        CarInterface.MapSearchCarList.Request.Builder builder = CarInterface.MapSearchCarList.Request.newBuilder();
        builder.setCityId("010");
        SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        for (int i = 0; i < Config.openCity.size(); i++) {
            if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                builder.setCityId(Config.openCity.get(i).getCityId());
                break;
            }
        }
        UuCommon.LatlngPosition.Builder latlngPosition = UuCommon.LatlngPosition.newBuilder();
        latlngPosition.setLat(latlng.latitude);
        latlngPosition.setLng(latlng.longitude);
        builder.setPosition(latlngPosition);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.MapSearchCarList_VALUE);
        task.setTag("MpSearchCarList");
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        CarInterface.MapSearchCarList.Response response = CarInterface.MapSearchCarList.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            aMap.clear();
                            if (currentMyDisLatlng != null) {
                                MarkerOptions op = new MarkerOptions();
                                op.position(currentMyDisLatlng);
                                op.icon(BitmapDescriptorFactory.fromResource(R.drawable.new_rent_mydis_icon));
//                                op.perspective(true);
                                Marker marker = aMap.addMarker(op);
                                marker.setObject(null);
                            }
                            ImageBaseUrl = response.getCarImgUrlPrefix();
                            List<CarInterface.MapSearchCarList.MapCarPoint> list = response.getCarResultListList();
                            for (CarInterface.MapSearchCarList.MapCarPoint point : list) {

                                CarItem item = new CarItem();
                                item.setMapCarPoint(point);
                                MarkerOptions op = new MarkerOptions();
                                UuCommon.LatlngPosition latlngPosition1 = item.point.getPosition();
                                LatLng latlng = new LatLng(latlngPosition1.getLat(), latlngPosition1.getLng());
                                op.anchor(0.5f, 0.5f);
                                op.position(latlng);
                                op.title("~");
                                if (item.point.getDuplicateCars() == 1) {

                                    if (item.point.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO)) {
                                        op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_a));
                                    } else {
                                        op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_m));
                                    }
                                } else {
//                                    op.icon(BitmapDescriptorFactory.fromBitmap(getNumIcon(item.point.getDuplicateCars(), false)));
                                    op.icon(getCarIcon(item.point, false));
                                }

//                                op.perspective(true);
                                Marker marker = aMap.addMarker(op);
                                marker.setObject(item);
                            }
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


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (mListener != null && aLocation != null) {
            Bundle locBundle = aLocation.getExtras();
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
            // CameraUpdate cu = CameraUpdateFactory.changeLatLng(new LatLng(aLocation.getLatitude(),aLocation.getLongitude()));
            // aMap.animateCamera(cu,1000,null);
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(context);
            /*
             * mAMapLocManager.setGpsEnable(false); 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location API定位采用GPS和网络混合定位方式 ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
             */
            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    public class CarItem {
        public CarInterface.MapSearchCarList.MapCarPoint point;

        public void setMapCarPoint(CarInterface.MapSearchCarList.MapCarPoint point) {
            this.point = point;
        }

    }
}
