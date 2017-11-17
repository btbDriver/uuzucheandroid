package com.youyou.uucar.UI.Main.FindCarFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.banner.BannerInterface;
import com.uu.client.bean.banner.common.BannerCommon;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.Adapter.BannerAdapter;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.fragment.BannerFragment;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
import com.youyou.uucar.UI.Renter.filter.RentFilterActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.DisplayUtil;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.BladeView;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.PinnedHeaderListView;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.BannerView;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2014/7/7.
 */
public class FindCarListFragment extends Fragment implements BannerFragment.CloseInterface {

    public static final int FILTER_REQUEST = 100;
    public static final String TAG = FindCarListFragment.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "FindCarListFragment";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();
    public String s_address;
    public double lat, lng;
    //    protected PullToRefreshLayout mPullToRefreshLayout;
    SwipeRefreshLayout mSwiperefreshlayout;
    protected LoadingFooter mLoadingFooter;
    View view;
    @InjectView(R.id.list)
    ListView list;
    List<CarCommon.CarBriefInfo> datas = new ArrayList<CarCommon.CarBriefInfo>();
    MyAdapter adapter;
    Activity context;
    boolean thisReset;
    @InjectView(R.id.nocar_root)
    RelativeLayout noCarRoot;
    @InjectView(R.id.list_root)
    RelativeLayout listRoot;
    boolean isLoadMoring = false;
    UuCommon.PageRequest.Builder pageBuilder = UuCommon.PageRequest.newBuilder();
    UuCommon.PageResult pageResult;
    private int bannerVersion = 0;
    private static final String TAGG = FindCarListFragment.class.getSimpleName();
    public ObserverListener observier = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
            if (obj.toString().equals("selectCity")) {

            } else {
                if (!thisReset) {
                } else {
                    thisReset = false;
                }
            }
//            mPullToRefreshLayout.setRefreshing(true);
            mSwiperefreshlayout.setRefreshing(true);
            pageBuilder.setDirection(0);
            MLog.e(TAG, "----observier----");
            loadData();

        }
    };
    SharedPreferences citysp;
    Dialog citydialog;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    public void initNoteDataRefush() {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPullToRefreshLayout.setRefreshing(true);
                mSwiperefreshlayout.setRefreshing(true);
                mAllFramelayout.noDataReloading();
                pageBuilder.setDirection(0);
                MLog.e(TAG, "----initNoteDataRefush----");
                loadData();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_find_car_list, null);
        ButterKnife.inject(this, view);


        initNoteDataRefush();
        ObserverManager.addObserver("rentlist", observier);
        mSwiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);
        mSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageBuilder.setDirection(0);
                pageBuilder.setPointer("");
                pageResult = null;
                isLoadMoring = true;
                MLog.e(TAG, "----onRefreshStarted----");
                loadData();
            }
        });
        mLoadingFooter = new LoadingFooter(context);
        list.addFooterView(mLoadingFooter.getView());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarCommon.CarBriefInfo item = datas.get(position - list.getHeaderViewsCount());
                Intent intent = new Intent(getActivity(), OldCarInfoActivity.class);
                intent.putExtra("islist", true);
                intent.putExtra(SysConfig.CAR_SN, item.getCarId());
                if (item.hasPassedMsg() && item.getPassedMsg() != null && !item.getPassedMsg().equals("")) {
                    intent.putExtra("passedMsg", item.getPassedMsg());
                }
                intent.putExtra("index", position - list.getHeaderViewsCount());
                startActivityForResult(intent, 165);
            }
        });
        mLoadingFooter.getView().setOnClickListener(footerClick);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                super.onScroll(view, firstVisibleItem, visibleItemCount,
//                        totalItemCount);
                if (list.getFooterViewsCount() != 0) {

                    if (firstVisibleItem + visibleItemCount >= totalItemCount
                            && totalItemCount != 0
                            && totalItemCount != list.getHeaderViewsCount()
                            + list.getFooterViewsCount() && adapter
                            .getCount() > 0) {
                        if (!isLoadMoring) {
                            isLoadMoring = true;
                            pageBuilder.setDirection(1);
                            pageBuilder.setPointer(pageResult.getNextPageStart());
                            MLog.e(TAG, "----onScroll----");
                            loadData();
                        }
                    }
                }
            }
        });
        citysp = getActivity().getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        bannerVersion = citysp.getInt("bannerVersion", 0);
        ObserverManager.addObserver("FindCarMap", obListener);
        Config.getCoordinates(getActivity(), new LocationListener() {
            @Override
            public void locationSuccess(double lat, double lng, String addr) {
                FindCarListFragment.this.lat = lat;
                FindCarListFragment.this.lng = lng;
                MLog.e(TAG, "LocationListener___locationSuccess lat = " + lat + "  lng = " + lng);
                s_address = addr;
                mSwiperefreshlayout.setRefreshing(true);
                pageBuilder.setDirection(0);
                if (citysp.getBoolean("selectcity", false)) {
                    if (Config.currentCity != null && Config.currentCity.indexOf(citysp.getString("city", "北京")) == -1) {
                        boolean isOpen = false;
                        //如果当前城市是开通城市的话,弹窗让用户选择城市
                        for (int i = 0; i < Config.openCity.size(); i++) {
                            if (Config.currentCity.indexOf(Config.openCity.get(i).getName()) != -1) {
                                isOpen = true;
                                break;
                            }
                        }
                        if (isOpen) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View view = context.getLayoutInflater().inflate(R.layout.change_city_dialog, null);
                            TextView tip = (TextView) view.findViewById(R.id.tip);
                            TextView current = (TextView) view.findViewById(R.id.current);
                            TextView old = (TextView) view.findViewById(R.id.old);
                            String currentCity = Config.currentCity.replace("市", "");
                            tip.setText("系统检测您定位在" + currentCity + ",是否切换为" + currentCity + "?");
                            current.setText("切换到" + currentCity);
                            old.setText("继续浏览" + citysp.getString("city", "北京"));
                            builder.setView(view);
                            citydialog = builder.create();
                            citydialog.setCanceledOnTouchOutside(false);
                            citydialog.show();
                            current.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    citydialog.dismiss();
                                    for (int i = 0; i < Config.openCity.size(); i++) {
                                        if (Config.currentCity.indexOf(Config.openCity.get(i).getName()) != -1) {
                                            SharedPreferences.Editor edit = citysp.edit();
                                            edit.putString("city", Config.openCity.get(i).getName());
                                            edit.putBoolean("selectcity", true);
                                            edit.commit();
                                            ObserverManager.getObserver("rentlist").observer("filter", "selectCity");
                                            ObserverManager.getObserver("rentmap").observer("filter", "selectCity");
                                            ObserverManager.getObserver("mainTabChangeCity").observer("list", Config.openCity.get(i).getName());
                                            break;
                                        }
                                    }
                                }
                            });
                            old.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    citydialog.dismiss();
                                }
                            });
                        }
                    }
                } else {
                    boolean isHasOpen = false;
                    if (Config.openCity != null) {
                        for (int i = 0; i < Config.openCity.size(); i++) {
                            if (Config.currentCity == null) {
                                Config.currentCity = SysConfig.BEI_JING_CITY;
                            }
                            if (Config.currentCity.indexOf(Config.openCity.get(i).getName()) != -1) {
                                SharedPreferences.Editor edit = citysp.edit();
                                edit.putString("city", Config.openCity.get(i).getName());
                                edit.putBoolean("selectcity", true);
                                edit.commit();
                                isHasOpen = true;
                                break;
                            }
                        }
                    }
                    if (citysp.getBoolean("selectcity", false) && !isHasOpen)//第一次进入,没选择过城市(如果我所在城市不再开放城市列表中)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = context.getLayoutInflater().inflate(R.layout.first_select_city_dialog, null);
                        LinearLayout root = (LinearLayout) view.findViewById(R.id.city_root);
                        for (int i = 0; i < Config.openCity.size(); i++) {
                            TextView tv = (TextView) context.getLayoutInflater().inflate(R.layout.first_select_city_dialog_item, null);
                            tv.setText(Config.openCity.get(i).getName());
                            final int finalI = i;
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor edit = citysp.edit();
                                    edit.putString("city", Config.openCity.get(finalI).getName());
                                    edit.putBoolean("selectcity", true);
                                    edit.commit();
                                    ObserverManager.getObserver("mainTabChangeCity").observer("list", Config.openCity.get(finalI).getName());
                                    firstSelectCitydialog.dismiss();
                                }
                            });
                            root.addView(tv);
                        }
                        builder.setView(view);
                        firstSelectCitydialog = builder.create();
                        firstSelectCitydialog.setCanceledOnTouchOutside(false);
                        firstSelectCitydialog.show();
                    }

                }
                mSwiperefreshlayout.setRefreshing(true);
                pageBuilder.setDirection(0);
                MLog.e(TAG, "location finish loaddata");
                loadData();
            }
        });

        return view;
    }

    private BannerAdapter bannerAdapter;
    private BannerView bannerView;
    private List<String> demoList;
    private View headerView;
    List<BannerCommon.BannerItem> bannerItemsList;
    Dialog firstSelectCitydialog;
    private List<BannerCommon.BannerItem> showBannerList = new ArrayList<BannerCommon.BannerItem>();
    public View.OnClickListener footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLoadMoring = true;
            pageBuilder.setDirection(1);
            if (pageResult != null) {
                if (pageResult.hasNextPageStart()) {
                    pageBuilder.setPointer(pageResult.getNextPageStart());
                }
            }
            MLog.e(TAG, "----OnClickListener----");
            loadData();
        }
    };
    public ObserverListener obListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
            if (from.equals("MainActivity")) {
                startActivityForResult(new Intent(getActivity(), RentFilterActivity.class), FILTER_REQUEST);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FindCarListFragment"); //统计页面
        if (mSwiperefreshlayout.isRefreshing()) {
            mSwiperefreshlayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwiperefreshlayout
                            .setRefreshing(false);
                }
            }, 400);
        }
        mLoadingFooter.setState(LoadingFooter.State.TheEnd);
        mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FindCarListFragment"); //统计页面
        MLog.e(TAG, "Config.changeCityList = " + Config.changeCityList + "   datas = " + datas.size());
        if (Config.changeCityList /*|| datas.size() == 0*/) {
            Config.changeCityList = false;
//            Config.showProgressDialog(getActivity(), false, null);
            if (mSwiperefreshlayout != null && !mSwiperefreshlayout.isRefreshing()) {
                mSwiperefreshlayout.setRefreshing(true);
                pageBuilder.setDirection(0);
                MLog.e(TAG, "----onResume----");
                loadData();
            }
        }
    }


    boolean isLogon = false;

    public void loadData() {
        if (pageBuilder.getDirection() == 1) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
        }
        final FragmentManager fm = getFragmentManager();
        final CarInterface.QueryCarList.Request.Builder builder = CarInterface.QueryCarList.Request.newBuilder();
        SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        //TODO 修复Crash
        builder.setCityId("010");
        for (int i = 0; i < Config.openCity.size(); i++) {
            if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                builder.setCityId(Config.openCity.get(i).getCityId());
                break;
            }
        }
        builder.setPage(pageBuilder);
        builder.setScene(CarInterface.QueryCarList.QueryCarScene.FIRST_PAGE_LIST);
        builder.setIsPrecise(false);
        UuCommon.LatlngPosition.Builder latlngPositionBuilder = UuCommon.LatlngPosition.newBuilder();
        latlngPositionBuilder.setLat(lat);
        latlngPositionBuilder.setLng(lng);
        builder.setBannerVersion(bannerVersion);
        builder.setPosition(latlngPositionBuilder);
        final NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.QueryCarList_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("QueryCarList");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        CarInterface.QueryCarList.Response response = CarInterface.QueryCarList.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            final long currentTime = System.currentTimeMillis();
                            if (list.getHeaderViewsCount() == 1) {
                                if (headerView != null) {
                                    boolean flag = list.removeHeaderView(headerView);
                                    if (flag) {
                                        headerView = null;
                                    }
                                }
                            }
                            if (response.hasBanners()) {
                                BannerCommon.BannerList banners = response.getBanners();
                                bannerVersion = banners.getVersion();
                                SharedPreferences.Editor edit = citysp.edit();
                                edit.putInt("bannerVersion", bannerVersion).apply();
                                int count = banners.getBannerItemsCount();
//                                if (count > 0) {
                                if (list.getHeaderViewsCount() == 0) {
                                    bannerItemsList = banners.getBannerItemsList();
                                    if (UserSecurityConfig.userId > 0) {
                                        isLogon = true;
                                    } else {
                                        isLogon = false;
                                    }

                                    Config.writeBannerToDisk(bannerItemsList, isLogon, getActivity());
                                    if (showBannerList != null) {
                                        showBannerList.clear();
                                    }
                                    if (isLogon) {
                                        for (BannerCommon.BannerItem item : bannerItemsList) {
                                            int startTime = item.getStartTime();
                                            int endTime = item.getEndTime();
                                            if (startTime <= (int) (currentTime / 1000) && endTime >= (int) (currentTime / 1000)) {
                                                showBannerList.add(item);
                                            }
                                        }
                                    } else {
                                        for (BannerCommon.BannerItem item : bannerItemsList) {
                                            int startTime = item.getStartTime();
                                            int endTime = item.getEndTime();
                                            if (startTime <= (int) (currentTime / 1000) && endTime >= (int) (currentTime / 1000)) {
                                                int bannerId = item.getBannerId();
                                                boolean isClicked = false;
                                                for (Integer clickInterger : guestClick) {
                                                    if (clickInterger == bannerId) {
                                                        isClicked = true;
                                                    }
                                                }
                                                if (!isClicked) {
                                                    showBannerList.add(item);
                                                }
                                            }
                                        }
                                    }
//                                    }
                                }
                            } else {
                                if (list.getHeaderViewsCount() == 0) {
                                    bannerItemsList = Config.getBannerFromDisk(getActivity());
                                    isLogon = Config.isBannerLogon(getActivity());
                                    if (showBannerList != null) {
                                        showBannerList.clear();
                                    }
                                    boolean isGuset = Config.isGuest(context);
                                    if (!(isGuset & isLogon)) {
                                        for (BannerCommon.BannerItem item : bannerItemsList) {
                                            int startTime = item.getStartTime();
                                            int endTime = item.getEndTime();
                                            if (startTime <= (int) (currentTime / 1000) && endTime >= (int) (currentTime / 1000)) {
                                                showBannerList.add(item);
                                            }
                                        }
                                    }
                                }
                            }
                            if (showBannerList != null && showBannerList.size() > 0) {
                                BannerCommon.BannerItem bannerItem = showBannerList.get(0);
                                float heightPixel = bannerItem.getHeightPixel();
                                float widthPixel = bannerItem.getWidthPixel();
                                float sclae = heightPixel / widthPixel;
                                int heightPx = (int) (DisplayUtil.screenWidthPx * sclae);
                                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                                headerView = layoutInflater.inflate(R.layout.banner_info,
                                        list, false);
                                bannerView = (BannerView) headerView.findViewById(R.id.bannerview);
                                AbsListView.LayoutParams bannerLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, heightPx);
                                bannerView.setLayoutParams(bannerLayoutParams);
                                bannerView.setCloseInterface(FindCarListFragment.this);
                                bannerView.setPageLineHorizontalGravity(Gravity.CENTER);
                                bannerView.displayImage = BitmapFactory.decodeResource(getResources(), R.drawable.play_img_dis);
                                bannerView.hideImage = BitmapFactory.decodeResource(getResources(), R.drawable.play_img_nodis);
                                bannerAdapter = new BannerAdapter(fm);
                                bannerAdapter.setCount(showBannerList);
                                bannerView.setAdapter(bannerAdapter);
                                bannerAdapter.notifyDataSetChanged();
                                list.addHeaderView(headerView);
                                bannerView.startPlay();
                            } else {
                                if (list.getHeaderViewsCount() == 1) {
                                    list.removeHeaderView(headerView);
                                }
                            }
                            isLoadMoring = false;
                            if (pageBuilder.getDirection() == 0) {
                                datas.clear();
                            }
                            datas.addAll(response.getCarResultListList());
                            UuCommon.PageResult pageResultData = response.getPageResult();
                            if (pageResultData.getHasMore()) {
                                if (list.getFooterViewsCount() == 0) {
                                    list.addFooterView(mLoadingFooter.getView());
                                }
                            } else {
//                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            if (datas.isEmpty()) {
                                TextView noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
                                TextView text = (TextView) mAllFramelayout.findViewById(R.id.text);
                                noDataRefush.setText("切换城市");
                                text.setText("该城市暂无车辆");
                                noDataRefush.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        View view = context.getLayoutInflater().inflate(R.layout.first_select_city_dialog, null);
                                        LinearLayout root = (LinearLayout) view.findViewById(R.id.city_root);
                                        for (int i = 0; i < Config.openCity.size(); i++) {
                                            View tvRoot = context.getLayoutInflater().inflate(R.layout.first_select_city_dialog_item, null);
                                            TextView tv = (TextView) tvRoot.findViewById(R.id.tv);
                                            tv.setText(Config.openCity.get(i).getName());
                                            final int finalI = i;
                                            tvRoot.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SharedPreferences.Editor edit = citysp.edit();
                                                    edit.putString("city", Config.openCity.get(finalI).getName());
                                                    edit.putBoolean("selectcity", true);
                                                    edit.commit();
                                                    ObserverManager.getObserver("mainTabChangeCity").observer("list", Config.openCity.get(finalI).getName());
                                                    firstSelectCitydialog.dismiss();
                                                }
                                            });
                                            root.addView(tvRoot);
                                        }
                                        builder.setView(view);
                                        firstSelectCitydialog = builder.create();
                                        firstSelectCitydialog.setCanceledOnTouchOutside(false);
                                        firstSelectCitydialog.show();
                                    }
                                });
                                adapter.notifyDataSetChanged();
                                mAllFramelayout.makeProgreeNoData();
                                pageResult = pageResultData;
                            } else {
                                adapter.notifyDataSetChanged();
//                                if (pageResult == null)
//                                {
                                mAllFramelayout.makeProgreeDismiss();
//                                }
                                pageResult = pageResultData;
                            }
                        } else {
                            mAllFramelayout.makeProgreeNoData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mAllFramelayout.makeProgreeNoData();
                    }
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }

            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
                if (mSwiperefreshlayout.isRefreshing()) {
//                    mSwiperefreshlayout.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mSwiperefreshlayout
//                                    .setRefreshing(false);
//                        }
//                    }, 400);

                    mSwiperefreshlayout
                            .setRefreshing(false);
                }
                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
            }
        });

    }

    private List<Integer> guestClick = new ArrayList<Integer>();


    @Override
    public void close(final int position, final BannerCommon.BannerItem bannerItem, final int type) {

        BannerAdapter bnnAdapter = bannerView.getAdapter();
        showBannerList = bnnAdapter.getBannerList();
        BannerCommon.BannerItem removeItem = null;
        switch (type) {
            case BannerCommon.BannerActionType.CLICK_VALUE:
//                removeItem = showBannerList.get(position);
                break;
            case BannerCommon.BannerActionType.CLOSE_VALUE:
                removeItem = showBannerList.remove(position);
                boolean removeflag = false;
                List<BannerCommon.BannerItem> diskList = new ArrayList<BannerCommon.BannerItem>();
                final int length = bannerItemsList.size();
                for (int i = 0; i < length; i++) {
                    BannerCommon.BannerItem next = bannerItemsList.get(i);
                    if (next.getBannerId() == removeItem.getBannerId()) {
                        removeflag = true;
                    } else {
                        diskList.add(next);
                    }
                }
                if (removeflag) {
                    Config.writeBannerToDisk(diskList, isLogon, getActivity());
                }
                break;
            case BannerCommon.BannerActionType.SHOW_VALUE:
                break;
        }
        if (removeItem != null) {
            int bannerId = removeItem.getBannerId();
            guestClick.add(bannerId);
            BannerInterface.BannerActionReportRequest.Builder builder = BannerInterface.BannerActionReportRequest.newBuilder();
            builder.setBannerId(bannerId);
            switch (type) {
                case BannerCommon.BannerActionType.CLICK_VALUE:
                    builder.setActionType(BannerCommon.BannerActionType.CLICK);
                    break;
                case BannerCommon.BannerActionType.CLOSE_VALUE:
                    builder.setActionType(BannerCommon.BannerActionType.CLOSE);
                    break;
                case BannerCommon.BannerActionType.SHOW_VALUE:
                    builder.setActionType(BannerCommon.BannerActionType.SHOW);
                    break;
            }


            NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.BannerActionReport_VALUE);
            if (Config.isGuest(getActivity())) {
                task.setUsePublic(true);
            } else {
                task.setUsePublic(false);
            }
            task.setBusiData(builder.build().toByteArray());
            task.setTag("BannerActionReport");
            NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
                @Override
                public void onSuccessResponse(UUResponseData responseData) {
                    if (responseData.getRet() == 0) {
                        try {
                            UuCommon.CommonReportResponse commonReportResponse = UuCommon.CommonReportResponse.parseFrom(responseData.getBusiData());
                            int ret = commonReportResponse.getRet();
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
        if (showBannerList.size() == 0) {
            bannerView.stopPlay();
            list.removeHeaderView(headerView);
            bannerAdapter.notifyDataSetChanged();
            return;
        } else if (showBannerList.size() == 1) {
            bannerView.stopPlay();
            bannerAdapter.setCount(showBannerList);
            bannerView.setAdapter(bannerAdapter);
            bannerAdapter.notifyDataSetChanged();
        } else {
            bannerAdapter.setCount(showBannerList);
            bannerView.setAdapter(bannerAdapter);
            bannerAdapter.notifyDataSetChanged();
            bannerView.startPlay();
        }
        MLog.e(TAGG, "position = " + position + " BannerItem = [ id : " + bannerItem.getBannerId() + " ]");
    }


    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.find_car_list_item, null);
            final CarCommon.CarBriefInfo item = datas.get(position);
            final RelativeLayout root = (RelativeLayout) convertView.findViewById(R.id.root);
//            convertView.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    Intent intent = new Intent(getActivity(), OldCarInfoActivity.class);
//                    intent.putExtra("islist", true);
//                    intent.putExtra(SysConfig.CAR_SN, item.getCarId());
//                    if (item.hasPassedMsg() && item.getPassedMsg() != null && !item.getPassedMsg().equals(""))
//                    {
//                        intent.putExtra("passedMsg", item.getPassedMsg());
//                    }
//                    intent.putExtra("index", position);
//                    startActivityForResult(intent, 165);
//                }
//            });
            BaseNetworkImageView img = (BaseNetworkImageView) convertView.findViewById(R.id.car_img);
            UUAppCar.getInstance().display(item.getThumbImg(), img, R.drawable.list_car_img_def);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OldCarInfoActivity.class);

                    if (item.hasPassedMsg() && item.getPassedMsg() != null && !item.getPassedMsg().equals("")) {
                        intent.putExtra("passedMsg", item.getPassedMsg());
                    }
                    intent.putExtra("islist", true);
                    intent.putExtra(SysConfig.CAR_SN, item.getCarId());
                    intent.putExtra("index", position);
                    startActivityForResult(intent, 165);
                }
            });
            BaseNetworkImageView waterMarkimg = (BaseNetworkImageView) convertView.findViewById(R.id.water_mark_img);
            waterMarkimg.setVisibility(View.GONE);
            if (item.hasWaterMarkPic() && item.getWaterMarkPic() != null && !item.getWaterMarkPic().equals("")) {
                waterMarkimg.setVisibility(View.VISIBLE);
                UUAppCar.getInstance().display(item.getWaterMarkPic(), waterMarkimg, R.drawable.nodefimg);
            }

            TextView brand = (TextView) convertView.findViewById(R.id.brand);
            brand.setText(item.getBrand() + item.getCarModel());
            TextView dis = (TextView) convertView.findViewById(R.id.dis);
            String juli = "";


            float djuli = -1;
            if (item.hasDistanceFromRenter()) {
                djuli = item.getDistanceFromRenter();
            }
            if (djuli < 0) {
                djuli = 0;
            } else if (djuli < 0.1f) {
                djuli = 0.1f;
            }

//            if (djuli > 1) {
            juli = String.format("%.1f", djuli) + " km";
//            } else {
//                juli = ((int) (djuli * 1000)) + " m";
//            }
            if (djuli == 0) {
                dis.setVisibility(View.GONE);
            } else {
                dis.setVisibility(View.VISIBLE);
                dis.setText(juli);
            }
            TextView price_day = (TextView) convertView.findViewById(R.id.price_day);
            price_day.setText("￥" + ((int) item.getPricePerDay()));
            TextView gearbox = (TextView) convertView.findViewById(R.id.gearbox_text);
            if (item.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO)) {
                gearbox.setText("自动挡");
            } else {
                gearbox.setText("手动挡");
            }
            TextView address = (TextView) convertView.findViewById(R.id.address_text);
            // xianxing.setText();
            address.setText(item.getAddress());
            if (item.hasColoredAddress()) {
                if (item.getColoredAddress().hasTextHexColor()) {
                    address.setTextColor(Color.parseColor(item.getColoredAddress().getTextHexColor()));
                }
                if (item.getColoredAddress().hasText()) {
                    address.setText(item.getColoredAddress().getText());
                }

            }
            TextView banDay = (TextView) convertView.findViewById(R.id.banday);
            if (item.hasCarLimitedInfo()) {
                banDay.setVisibility(View.VISIBLE);
                banDay.setText(item.getCarLimitedInfo());
            } else {
                banDay.setVisibility(View.INVISIBLE);
            }
//            RatingBar rating = (RatingBar) convertView.findViewById(R.id.rating);
//
//            if (item.hasStars()) {
//                rating.setVisibility(View.VISIBLE);
//                rating.setRating(item.getStars());
//            } else {
//                rating.setVisibility(View.GONE);
//            }
            return convertView;
        }
    }
}
