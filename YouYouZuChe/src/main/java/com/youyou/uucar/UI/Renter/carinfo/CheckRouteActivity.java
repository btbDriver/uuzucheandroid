package com.youyou.uucar.UI.Renter.carinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.R;
import com.youyou.uucar.Utils.Support.Config;

public class CheckRouteActivity extends Activity implements OnMarkerClickListener, OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnPoiSearchListener, OnRouteSearchListener, OnClickListener
{
    public  Activity context;
    private AMap     aMap;
    private MapView  mapView;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private int            walkMode   = RouteSearch.WalkDefault;// 步行默认模式
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private int         routeType  = 3;// 1代表公交模式，2代表驾车模式，3代表步行模式
    private LatLonPoint startPoint = null;
    private LatLonPoint endPoint   = null;
    private PoiSearch.Query startSearchQuery;
    private PoiSearch.Query endSearchQuery;
    private Marker          startMk, targetMk;
    private RouteSearch          routeSearch;
    public  ArrayAdapter<String> aAdapter;

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.map_activity);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(b); // 此方法必须重写
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items)
    {
        if (items.getItemId() == android.R.id.home || items.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            CheckRouteActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化AMap对象
     */
    private void init()
    {
        if (aMap == null)
        {
            aMap = mapView.getMap();
//            registerListener();
        }


        MarkerOptions op = new MarkerOptions();
        LatLng latlng = new LatLng(getIntent().getDoubleExtra("end_lat", 0), getIntent().getDoubleExtra("end_lng", 0));
        op.anchor(0.5f, 0.5f);
        op.position(latlng);
        op.title("~");
        if (getIntent().hasExtra("type"))
        {

            String intentStr = getIntent().getStringExtra("type");
            if (intentStr != null && intentStr.equals("auto"))
            {
                op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_a));
            }
            else
            {
                op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_car_icon_m));
            }
        }
        else
        {
            op.icon(BitmapDescriptorFactory.fromResource(R.drawable.find_car_map_mydis_icon));
        }
//                                op.perspective(true);
        Marker marker = aMap.addMarker(op);
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(marker.getPosition(), 12, 0, 0)));
//        routeSearch = new RouteSearch(this);
//        routeSearch.setRouteSearchListener(this);
//        routeType = 3;// 标识为步行模式
//        walkMode = RouteSearch.WalkMultipath;
//        startPoint = new LatLonPoint(getIntent().getDoubleExtra("start_lat", 0), getIntent().getDoubleExtra("start_lng", 0));
//        endPoint = new LatLonPoint(getIntent().getDoubleExtra("end_lat", 0), getIntent().getDoubleExtra("end_lng", 0));
//        searchRouteResult(startPoint, endPoint);
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint)
    {
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        // 步行路径规划
        WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
        routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog()
    {
        if (progDialog == null)
        {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog()
    {
        if (progDialog != null)
        {
            progDialog.dismiss();
        }
    }

    /**
     * 注册监听
     */
    private void registerListener()
    {
        aMap.setOnMapClickListener(CheckRouteActivity.this);
        aMap.setOnMarkerClickListener(CheckRouteActivity.this);
        aMap.setOnInfoWindowClickListener(CheckRouteActivity.this);
        aMap.setInfoWindowAdapter(CheckRouteActivity.this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(context);
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(context);
        mapView.onPause();
    }

    /**
     * // * 方法必须重写 //
     */
    // @Override
    // protected void onSaveInstanceState(Bundle outState)
    // {
    // super.onSaveInstanceState(outState);
    // mapView.onSaveInstanceState(outState);
    // }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        // 
    }

    @Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1)
    {
        // 
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult arg0, int arg1)
    {
        // 
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode)
    {
        dissmissProgressDialog();
        if (rCode == 0)
        {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0)
            {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();

                walkRouteOverlay.zoomToSpan();
                aMap.moveCamera(new CameraUpdateFactory().changeLatLng(new LatLng(endPoint.getLatitude(), endPoint.getLongitude())));
            }
            else
            {
            }
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1)
    {
        // 
    }

    @Override
    public void onPoiSearched(PoiResult arg0, int arg1)
    {
        // 
    }

    @Override
    public View getInfoContents(Marker arg0)
    {
        // 
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0)
    {
        // 
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0)
    {
        // 
    }

    @Override
    public void onMapClick(LatLng arg0)
    {
        // 
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if (marker.isInfoWindowShown())
        {
            marker.hideInfoWindow();
        }
        else
        {
            marker.showInfoWindow();
        }
        return false;
    }
}
