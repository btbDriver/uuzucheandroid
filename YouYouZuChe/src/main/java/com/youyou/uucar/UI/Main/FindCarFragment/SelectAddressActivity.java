package com.youyou.uucar.UI.Main.FindCarFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SelectAddressActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnCameraChangeListener
{
    public String tag = "SelectAddressActivity";
    public Activity context;
    public String currentAddress = "";
    @InjectView(R.id.map_root)
    RelativeLayout map_root;
    MyAdapter      adapter;
    MapView        mapView;
    ListView       listview;
    ImageView      search;
    LatLng         currentLatlng;
    RelativeLayout tosearch;
    double         lat, lng;
    MenuItem menu_search;
    LatLng   latlng;
    int           maxPage     = 0;
    boolean       isHasLatlng = true;
    int           selectIndex = 0;
    List<PoiItem> poiItems    = new ArrayList<PoiItem>();
    boolean       isFirst     = true;
    private AMap      aMap;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query           query;// Poi查询条件类
    private PoiSearch                 poiSearch;// POI搜索
    private OnLocationChangedListener mListener;
    private LocationManagerProxy      mAMapLocationManager;
    private UUProgressFramelayout     mUUProgressFramelayout;
    @InjectView(R.id.title_root)
    RelativeLayout titleRoot;

    @OnClick(R.id.title_root)
    public void addressClick()
    {
        searchisCollapse = false;
        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        Config.setActivityState(this);
        setContentView(R.layout.activity_select_address);
        ButterKnife.inject(this);
        searchisCollapse = !getIntent().getBooleanExtra("isexpaand", false);
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);
        if (lat != 0)
        {
            isHasLatlng = true;
        }
        else
        {
            isHasLatlng = false;
        }
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        aMap = mapView.getMap();
        listview = (ListView) findViewById(R.id.poi_list);
        mUUProgressFramelayout = (UUProgressFramelayout) findViewById(R.id.all_framelayout);
        listview.setDivider(null);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.new_rent_mydis_icon));// 设置小蓝点的图标
        setUpMap();
        initNoteDataRefush();


//        if (getIntent().getBooleanExtra("isexpaand", false))
//        {
//            menu_search.expandActionView();
//        }

        invalidateOptionsMenu();//重置actionBar

    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush = (TextView) mUUProgressFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Config.isNetworkConnected(context))
                {
                    setUpMap();
                }
                else
                {
                    mUUProgressFramelayout.makeProgreeNoData();
                    Config.showFiledToast(context);
                }
            }
        });
    }

    /**
     * 设置页面监听
     */
    private void setUpMap()
    {
        // 自定义系统定位小蓝点

        aMap.setMyLocationRotateAngle(180);
//        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
        if ((lat != 0 || lng != 0) && isHasLatlng)
        {
            aMap.moveCamera(new CameraUpdateFactory().changeLatLng(new LatLng(lat, lng)));
            getPOI(lat, lng);
            isHasLatlng = false;
        }
        else
        {
            Config.getCoordinates(this, new LocationListener()
            {
                @Override
                public void locationSuccess(double lat, double lng, String addr)
                {

                    final SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
                    for (int i = 0; i < Config.openCity.size(); i++)
                    {
                        if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName()))
                        {
                            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat, lng), Config.openCity.get(i).getZoom(), 0, 0)), 1000, null);
                            break;
                        }
                    }
                    getPOI(lat, lng);
                }
            });
        }
    }

    public void getPOI(double lat, double lng)
    {
        currentPage = 0;
        latlng = new LatLng(lat, lng);
        query = new PoiSearch.Query("写字楼|学校|小区", "", "");
        query.setPageSize(50);
        query.setPageNum(currentPage);
        poiSearch = new PoiSearch(context, query);
        MLog.e(tag, "latlnt = " + latlng + "   query = " + query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latlng.latitude, latlng.longitude), 3000));
        listview.setSelection(0);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener()
        {
            @Override
            public void onPoiSearched(PoiResult result, int rCode)
            {
                MLog.e(tag, "result = " + result + "  code = " + rCode);
                if (rCode == 0)
                {
                    if (result != null && result.getQuery() != null)
                    {
                        if (result.getQuery().equals(query))
                        {
                            poiResult = result;
                            maxPage = poiResult.getPageCount();
                            if (currentPage == 0)
                            {
                                poiItems.add(null);
                                GeocodeSearch geocoderSearch = new GeocodeSearch(context);
                                geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener()
                                {
                                    public void onRegeocodeSearched(RegeocodeResult result, int rCode)
                                    {
                                        //
                                        if (rCode == 0)
                                        {
                                            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null)
                                            {
                                                currentAddress = result.getRegeocodeAddress().getFormatAddress();
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onGeocodeSearched(GeocodeResult arg0, int arg1)
                                    {
                                    }
                                });
                                // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latlng.latitude, latlng.longitude), 200, GeocodeSearch.AMAP);
                                geocoderSearch.getFromLocationAsyn(query);
                                poiItems = poiResult.getPois();
                            }
                            else
                            {
                                poiItems.addAll(poiResult.getPois());
                            }
                            adapter.notifyDataSetChanged();
//                            dismissProgress();
                            mUUProgressFramelayout.makeProgreeDismiss();
                        }
                    }
                }
                else
                {
//                    dismissProgress();
                    mUUProgressFramelayout.makeProgreeNoData();
                    Config.showFiledToast(context);
//                    finish();
                }
            }

            @Override
            public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1)
            {
            }
        });
        poiSearch.searchPOIAsyn();
        selectIndex = 0;
    }

    boolean searchisCollapse = true;
    SearchView searchview;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.select_address_menu, menu);
//        menu_search = menu.findItem(R.id.menu_search);
////        search.collapseActionView();
//        menu_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
//        {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item)
//            {
//                ActionBar actionBar = SelectAddressActivity.this.getActionBar();
//                actionBar.show();
//                actionBar.setDisplayHomeAsUpEnabled(true);
//                actionBar.setHomeButtonEnabled(true);
//                actionBar.setIcon(R.drawable.actionbar_icon);
//                map_root.setVisibility(View.GONE);
//                searchisCollapse = false;
////                invalidateOptionsMenu();
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item)
//            {
//                map_root.setVisibility(View.VISIBLE);
//                searchisCollapse = true;
////
////                invalidateOptionsMenu();
//                return true;
//            }
//        });
//        searchview = (SearchView) menu_search.getActionView();
//        searchview.setIconifiedByDefault(false);
//        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener()
//        {
//            @Override
//            public boolean onQueryTextSubmit(String query1)
//            {
//                SharedPreferences citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
//                String newText = query1.trim();
//                currentPage = 0;
//                selectIndex = 0;
//                query = new PoiSearch.Query(newText, "", citysp.getString("city", "北京"));
//                query.setPageNum(50);
//                query.setPageNum(currentPage);
//                PoiSearch poiSearch = new PoiSearch(context, query);
//                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener()
//                {
//                    @Override
//                    public void onPoiSearched(PoiResult result, int rCode)
//                    {
//                        if (rCode == 0)
//                        {
//                            if (result != null && result.getQuery() != null)
//                            {
//                                if (result.getQuery().equals(query))
//                                {
//                                    if (poiItems != null)
//                                    {
//                                        poiItems.clear();
//                                        poiResult = result;
//                                        maxPage = poiResult.getPageCount();
//                                        poiItems.addAll(poiResult.getPois());
//                                        adapter.notifyDataSetChanged();
//                                    }
//                                    dismissProgress();
//                                }
//                            }
//                        }
//                        else
//                        {
//                            dismissProgress();
//                            Config.showFiledToast(context);
//                        }
//                    }
//
//                    @Override
//                    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i)
//                    {
//
//                    }
//                });
//                poiSearch.searchPOIAsyn();
//                showProgress(false);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText)
//            {
//                return false;
//            }
//        });
//        SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchableInfo info = mSearchManager.getSearchableInfo(getComponentName());
//        searchview.setSearchableInfo(info); //需要在Xml文件加下建立searchable.xml,搜索框配置文件
//        MenuItem sure = menu.findItem(R.id.sure);
//        if (getIntent().getBooleanExtra("isexpaand", false))
//        {
//            menu_search.expandActionView();
//        }
//        return true;
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        for (int i = 0; i < menu.size(); i++)
        {
            menu.removeItem(i);
        }
        menu.clear();
        if (!searchisCollapse)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.select_address_menu, menu);
            menu_search = menu.findItem(R.id.menu_search);
            menu_search.expandActionView();
            menu_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
            {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item)
                {
                    ActionBar actionBar = SelectAddressActivity.this.getActionBar();
                    actionBar.show();
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeButtonEnabled(true);
                    actionBar.setIcon(R.drawable.actionbar_icon);
                    map_root.setVisibility(View.GONE);
                    titleRoot.setVisibility(View.GONE);
                    searchisCollapse = false;
                    invalidateOptionsMenu();
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item)
                {
                    map_root.setVisibility(View.VISIBLE);
                    titleRoot.setVisibility(View.VISIBLE);
                    searchisCollapse = true;
                    invalidateOptionsMenu();
                    return true;
                }
            });
            searchview = (SearchView) menu_search.getActionView();
            searchview.setIconifiedByDefault(false);
            searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query1)
                {
                    SharedPreferences citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
                    String newText = query1.trim();
                    if (newText.length() == 0)
                    {
                        ShowToast("请输入地址");
                        return false;
                    }
                    currentPage = 0;
                    selectIndex = 0;
                    query = new PoiSearch.Query(newText, "", citysp.getString("city", "北京"));
                    query.setPageNum(50);
                    query.setPageNum(currentPage);
                    PoiSearch poiSearch = new PoiSearch(context, query);
                    poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener()
                    {
                        @Override
                        public void onPoiSearched(PoiResult result, int rCode)
                        {
                            if (rCode == 0)
                            {
                                if (result != null && result.getQuery() != null)
                                {
                                    if (result.getQuery().equals(query))
                                    {
                                        if (poiItems != null)
                                        {
                                            poiItems.clear();
                                            poiResult = result;
                                            maxPage = poiResult.getPageCount();
                                            poiItems.addAll(poiResult.getPois());
                                            adapter.notifyDataSetChanged();
                                        }
                                        dismissProgress();
                                    }
                                }
                            }
                            else
                            {
                                dismissProgress();
                                Config.showFiledToast(context);
                            }
                        }

                        @Override
                        public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i)
                        {

                        }
                    });
                    poiSearch.searchPOIAsyn();
                    showProgress(false);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText)
                {
                    return false;
                }
            });
            SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo info = mSearchManager.getSearchableInfo(getComponentName());
            searchview.setSearchableInfo(info); //需要在Xml文件加下建立searchable.xml,搜索框配置文件


            ActionBar actionBar = SelectAddressActivity.this.getActionBar();
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setIcon(R.drawable.actionbar_icon);
            map_root.setVisibility(View.GONE);
            titleRoot.setVisibility(View.GONE);
            searchisCollapse = false;


        }
        else
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.select_address_menu1, menu);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(context);
        mapView.onResume();
        TestinAgent.onResume(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        TestinAgent.onStop(this);
    }


    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(context);
        mapView.onPause();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onCameraChange(CameraPosition arg0)
    {
    }

    boolean isDoubleFirst = true;

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition)
    {
        MLog.e(tag, "222222   lat = " + lat + "   lng = " + lng);
//        if ((lat != 0 || lng != 0) && isHasLatlng) {
//            aMap.moveCamera(new CameraUpdateFactory().changeLatLng(new LatLng(lat, lng)));
//            isHasLatlng = false;
//        } else {
        if (isFirst && isDoubleFirst)
        {
            currentPage = 0;
            latlng = cameraPosition.target;
            query = new PoiSearch.Query("写字楼|学校|小区", "", "");
            query.setPageSize(50);
            query.setPageNum(currentPage);
            poiSearch = new PoiSearch(context, query);
            MLog.e(tag, "latlnt = " + latlng + "   query = " + query);
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latlng.latitude, latlng.longitude), 3000));
            listview.setSelection(0);
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener()
            {
                @Override
                public void onPoiSearched(PoiResult result, int rCode)
                {
                    MLog.e(tag, "result = " + result + "  code = " + rCode);
                    if (rCode == 0)
                    {
                        if (result != null && result.getQuery() != null)
                        {
                            if (result.getQuery().equals(query))
                            {
                                poiResult = result;
                                maxPage = poiResult.getPageCount();
                                if (currentPage == 0)
                                {
                                    if (poiItems != null)
                                    {
                                        poiItems.add(null);
                                    }
                                    GeocodeSearch geocoderSearch = new GeocodeSearch(context);
                                    geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener()
                                    {
                                        public void onRegeocodeSearched(RegeocodeResult result, int rCode)
                                        {
                                            //
                                            if (rCode == 0)
                                            {
                                                if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null)
                                                {
                                                    currentAddress = result.getRegeocodeAddress().getFormatAddress();
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onGeocodeSearched(GeocodeResult arg0, int arg1)
                                        {
                                        }
                                    });
                                    // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                                    RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latlng.latitude, latlng.longitude), 200, GeocodeSearch.AMAP);
                                    geocoderSearch.getFromLocationAsyn(query);
                                    poiItems = poiResult.getPois();
                                }
                                else
                                {
                                    poiItems.addAll(poiResult.getPois());
                                }
                                adapter.notifyDataSetChanged();
                                dismissProgress();
                            }
                        }
                    }
                    else
                    {
                        dismissProgress();
                        Config.showFiledToast(context);
//                        finish();
                    }
                }

                @Override
                public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1)
                {
                }
            });
            poiSearch.searchPOIAsyn();
            selectIndex = 0;
        }
        else
        {
//            isFirst = true;
        }

        if (isFirst && !isDoubleFirst)
        {
            isDoubleFirst = true;
        }
        if (!isFirst)
        {
            isFirst = true;
        }
//        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener)
    {
//        mListener = listener;
//        if (mAMapLocationManager == null) {
//            mAMapLocationManager = LocationManagerProxy.getInstance(context);
//            /*
//             * mAMapLocManager.setGpsEnable(false); 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location API定位采用GPS和网络混合定位方式 ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
//             */
//            mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
//        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation)
    {
//        if (mListener != null && aLocation != null) {
//            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
//            float bearing = aMap.getCameraPosition().bearing;
//            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
//        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate()
    {
//        mListener = null;
//        if (mAMapLocationManager != null) {
//            mAMapLocationManager.removeUpdates(this);
//            mAMapLocationManager.destory();
//        }
//        mAMapLocationManager = null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        MLog.e(tag, "onLocationChanged");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        MLog.e(tag, "item id = " + item.getItemId());
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
//
            if (!searchisCollapse)
            {
                menu_search.collapseActionView();
                searchisCollapse = true;
                invalidateOptionsMenu();
            }
            else
            {
                onBackPressed();
            }
            return true;
        }
        else if (item.getItemId() == R.id.sure)
        {

            if (searchisCollapse)
            {
                Intent intent = new Intent();
                if (poiItems.isEmpty())
                {
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
                else
                {
                    intent.putExtra("address", poiItems.get(selectIndex).getTitle());
                    intent.putExtra("lat", poiItems.get(selectIndex).getLatLonPoint().getLatitude());
                    intent.putExtra("lng", poiItems.get(selectIndex).getLatLonPoint().getLongitude());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
            else
            {
                String newText = searchview.getQuery().toString().trim();
                if (newText.length() == 0)
                {
                    ShowToast("请输入地址");
                    return false;
                }
                SharedPreferences citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
                currentPage = 0;
                selectIndex = 0;
                query = new PoiSearch.Query(newText, "", citysp.getString("city", "北京"));
                query.setPageNum(50);
                query.setPageNum(currentPage);
                PoiSearch poiSearch = new PoiSearch(context, query);
                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener()
                {
                    @Override
                    public void onPoiSearched(PoiResult result, int rCode)
                    {
                        if (rCode == 0)
                        {
                            if (result != null && result.getQuery() != null && result.getPois() != null)
                            {
                                if (result.getQuery().equals(query))
                                {
                                    poiItems.clear();
                                    poiResult = result;
                                    maxPage = poiResult.getPageCount();
                                    poiItems.addAll(poiResult.getPois());
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        }
                        else
                        {
                            Config.showFiledToast(context);
                        }
                        dismissProgress();
                    }

                    @Override
                    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i)
                    {

                    }
                });
                poiSearch.searchPOIAsyn();
                showProgress(false);
            }


        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return poiItems == null ? 0 : poiItems.size();
        }

        @Override
        public Object getItem(int position)
        {
            return poiItems == null ? null : poiItems.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            convertView = context.getLayoutInflater().inflate(R.layout.route_inputs, null);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView address = (TextView) convertView.findViewById(R.id.address_text);
            ImageView select = (ImageView) convertView.findViewById(R.id.select);
//            if (position == 0) {
//                title.setText("位置");
//                address.setText(currentAddress);
//            } else {
            PoiItem item = (PoiItem) getItem(position);
//            title.setText(item.getTitle());
//            address.setText(item.getSnippet());

            if (position == 0)
            {
                title.setText("位置");
//                title.setText(item.getTitle());
                address.setText(currentAddress);
            }
            else
            {
                title.setText(item.getTitle());
                address.setText(item.getSnippet());
            }
//            }
            if (selectIndex == position)
            {
                select.setVisibility(View.VISIBLE);
            }
            else
            {
                select.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectIndex = position;
                    if (!searchisCollapse)
                    {
                        menu_search.collapseActionView();
                        searchisCollapse = true;
                        PoiItem item = (PoiItem) getItem(position);
                        aMap.moveCamera(new CameraUpdateFactory().newLatLngZoom(new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude()), 15));
                        invalidateOptionsMenu();
                    }
                    else
                    {
                        PoiItem item = (PoiItem) getItem(position);
                        aMap.moveCamera(new CameraUpdateFactory().newLatLngZoom(new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude()), 15));
                        isFirst = false;
                        isDoubleFirst = false;
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

}
