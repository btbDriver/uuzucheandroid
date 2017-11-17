package com.youyou.uucar.UI.Renter.filter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.DB.Model.FindCarListModel;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Main.rent.OneToOneWaitActivity;
import com.youyou.uucar.UI.Main.rent.SelectTime;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
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
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by 16515_000 on 2014/8/21.
 */
public class FilteredCarListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public String tag = "FilteredCarListActivity";
    public Activity context;
    SwipeRefreshLayout mSwiperefreshlayout;
    protected LoadingFooter mLoadingFooter;
    /**
     * 进入车辆详情,和去约车页面后回调这个值来刷新
     */
    public final int REFUSH = 1;
    @InjectView(R.id.list)
    ListView list;
//    @InjectView(R.id.tip1)
//    TextView tip;

    //    @InjectView(R.id.reset)
//    TextView reset;
    MyAdapter adapter;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    @InjectView(R.id.rent_root)
    RelativeLayout rentNumRoot;
    @InjectView(R.id.num)
    TextView rentNum;
    JSONObject filter;
    RelativeLayout headerView;

    public void initNoteDataRefush() {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllFramelayout.noDataReloading();
                mSwiperefreshlayout.setRefreshing(true);
                pageBuilder.setDirection(0);
                pageBuilder.setPointer("");
                loadData();
            }
        });
    }

    public void cancelHire() {

        showProgress(true, new Config.ProgressCancelListener() {
            @Override
            public void progressCancel() {
                NetworkUtils.cancleNetworkRequest("RenterCancelPreOrder");
            }
        });
        OrderFormInterface26.RenterCancelPreOrder.Request.Builder request = OrderFormInterface26.RenterCancelPreOrder.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelPreOrder_VALUE);
        task.setBusiData(request.build().toByteArray());
        task.setTag("RenterCancelPreOrder");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        OrderFormInterface26.RenterCancelPreOrder.Response response = OrderFormInterface26.RenterCancelPreOrder.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            ShowToast("取消成功");
                            Config.isUserCancel = true;
//                            context.stopService(new Intent(context, RentingService.class));

                            getApp().quitRenting();
                            Config.isSppedIng = false;
                            Config.speedHasAgree = false;
                            Intent intent = new Intent(context, SelectTime.class);
                            String[] sns = new String[selectedCarItem.size()];
                            for (int i = 0; i < sns.length; i++) {
                                sns[i] = selectedCarItem.get(i).info.getCarId();
                            }
                            intent.putExtra(SysConfig.CAR_SNS, sns);
                            startActivity(intent);
                        } else {
                            showToast("取消失败");
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                dismissProgress();
            }
        });
    }

    @OnClick(R.id.rent_root)
    public void rentClick() {

        if (Config.isGuest(context)) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("goto", LoginActivity.RENTER_REGISTER);
            startActivity(intent);
        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
            Intent intent = new Intent(context, RenterRegisterIDActivity.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
            Intent intent = new Intent(context, RenterRegisterVerify.class);
            startActivity(intent);
        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO)) {
            Intent intent = new Intent(context, RenterRegisterVerifyError.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        } else {
            if (getIntent().getBooleanExtra("isMap", false)) {
                if (Config.isSppedIng)//快速约车中
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("您的快速约车请求已发送给多位车主,预约此车将终端之前的请求,是否继续预约此车");
                    builder.setNegativeButton("返回", null);
                    builder.setNeutralButton("预约此车", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showProgress(false);
                            OrderFormInterface26.RenterCancelPreOrder.Request.Builder request = OrderFormInterface26.RenterCancelPreOrder.Request.newBuilder();
                            NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelPreOrder_VALUE);
                            task.setBusiData(request.build().toByteArray());
                            task.setTag("RenterCancelPreOrder");
                            NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                @Override
                                public void onSuccessResponse(UUResponseData responseData) {
                                    if (responseData.getRet() == 0) {
                                        try {
                                            OrderFormInterface26.RenterCancelPreOrder.Response response = OrderFormInterface26.RenterCancelPreOrder.Response.parseFrom(responseData.getBusiData());
                                            if (response.getRet() == 0) {
                                                Config.isSppedIng = false;
//                                                context.stopService(new Intent(context, RentingService.class));

                                                getApp().quitRenting();
                                                Config.isOneToOneIng = false;
                                                Intent intent = new Intent(context, SelectTime.class);
                                                String[] sns = new String[selectedCarItem.size()];
                                                for (int i = 0; i < sns.length; i++) {
                                                    sns[i] = selectedCarItem.get(i).info.getCarId();
                                                }
                                                intent.putExtra(SysConfig.CAR_SNS, sns);
                                                startActivityForResult(intent, REFUSH);
                                            } else {
                                                showToast("取消快速预约失败");
                                            }
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onError(VolleyError errorResponse) {
                                    showToast("取消快速预约失败");
                                }

                                @Override
                                public void networkFinish() {
                                    dismissProgress();
                                }
                            });
                        }
                    });
                    builder.create().show();
                } else {
                    Intent intent = new Intent(context, SelectTime.class);
                    String[] sns = new String[selectedCarItem.size()];
                    for (int i = 0; i < sns.length; i++) {
                        sns[i] = selectedCarItem.get(i).info.getCarId();
                    }
                    intent.putExtra(SysConfig.CAR_SNS, sns);
                    startActivity(intent);
                }
            } else if (Config.isSppedIng)//快速约车中
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("您的快速约车请求已发送给多位车主,进行新的预约将中断之前的请求,是否继续发起新的约车请求?");
                builder.setNegativeButton("返回", null);
                builder.setNeutralButton("继续预约", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (filter != null && filter.has("start") && filter.has("end"))//如果选择过开始时间和结束时间
                        {
                            showProgress(false);
                            OrderFormInterface26.RenterStartPreOrder.Request.Builder request = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
                            for (FindCarListModel model : selectedCarItem) {
                                request.addCarIds(model.info.getCarId());
                            }
                            try {
                                request.setStartTime(Integer.parseInt(filter.getString("start")));
                                request.setEndTime(Integer.parseInt(filter.getString("end")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            request.setCancelLastPreOrder(true);
                            NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterStartPreOrder_VALUE);
                            task.setBusiData(request.build().toByteArray());
                            task.setTag("RenterStartPreOrder");
                            NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                @Override
                                public void onSuccessResponse(UUResponseData responseData) {
                                    if (responseData.getRet() == 0) {
                                        try {
                                            OrderFormInterface26.RenterStartPreOrder.Response response = OrderFormInterface26.RenterStartPreOrder.Response.parseFrom(responseData.getBusiData());
                                            if (response.getRet() == 0) {
                                                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                                FilteredCarListActivity.isNeedRefresh = true;
                                                startActivity(intent);
                                            }
//                                            else
//                                            {
//                                                if (response.getTipsType() == 0)//toast
//                                                {
//                                                    showToast(response.getMsg());
//                                                }
//                                                else
//                                                {
//                                                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
//                                                    msgBuilder.setMessage(response.getMsg());
//                                                    msgBuilder.setNeutralButton("知道了", null);
//                                                    msgBuilder.create().show();
//                                                }
//                                            }
                                            if (response.getRet() == -5) {
                                                //部分成功
                                                Config.showToast(context, response.getMsg());
                                                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                                startActivity(intent);
                                            }

                                            if (response.hasMsg() && response.getMsg().length() > 0) {
//                            showToast(responseData.getToastMsg());
                                                if (response.getTipsType() == 0)//toast
                                                {
                                                    showToast(response.getMsg());
                                                } else {
                                                    AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
                                                    msgBuilder.setMessage(response.getMsg());
                                                    msgBuilder.setNeutralButton("知道了", null);
                                                    msgBuilder.create().show();
                                                }

                                            }
                                        } catch (InvalidProtocolBufferException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onError(VolleyError errorResponse) {
                                    Config.showFiledToast(context);
                                }

                                @Override
                                public void networkFinish() {
                                    dismissProgress();
                                }
                            });
                        } else {
                            cancelHire();
                        }
                    }
                });
                builder.create().show();
            } else {


                if (filter == null || !filter.has("start") || !filter.has("end")) {
                    Intent intent = new Intent(context, SelectTime.class);
                    String[] sns = new String[selectedCarItem.size()];
                    for (int i = 0; i < sns.length; i++) {
                        sns[i] = selectedCarItem.get(i).info.getCarId();
                    }
                    intent.putExtra(SysConfig.CAR_SNS, sns);
                    startActivity(intent);
                } else {
                    showProgress(false);
                    OrderFormInterface26.RenterStartPreOrder.Request.Builder request = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
                    for (FindCarListModel model : selectedCarItem) {
                        request.addCarIds(model.info.getCarId());
                    }
                    try {
                        if (filter != null && filter.has("start") && filter.has("end")) {
                            request.setStartTime(Integer.parseInt(filter.getString("start")));
                            request.setEndTime(Integer.parseInt(filter.getString("end")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    request.setCancelLastPreOrder(false);
                    NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterStartPreOrder_VALUE);
                    task.setBusiData(request.build().toByteArray());
                    task.setTag("RenterStartPreOrder");
                    NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData) {
                            if (responseData.getRet() == 0) {
                                try {
                                    OrderFormInterface26.RenterStartPreOrder.Response response = OrderFormInterface26.RenterStartPreOrder.Response.parseFrom(responseData.getBusiData());
                                    if (response.getRet() == 0) {
                                        Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                        isNeedRefresh = true;
                                        startActivity(intent);
                                    }
//                                    else
//                                    {
//                                        if (response.getTipsType() == 0)//toast
//                                        {
//                                            showToast(response.getMsg());
//                                        }
//                                        else
//                                        {
//                                            AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
//                                            msgBuilder.setMessage(response.getMsg());
//                                            msgBuilder.setNeutralButton("知道了", null);
//                                            msgBuilder.create().show();
//                                        }
//                                    }
                                    if (response.getRet() == -2) {
                                        Config.isSppedIng = true;
                                        Config.isUserCancel = false;
//                                        startService(new Intent(context, RentingService.class));

                                        getApp().startRenting();
                                        rentClick();
                                    }
                                    if (response.getRet() == -5) {
                                        //部分成功
                                        Config.showToast(context, response.getMsg());
                                        Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                        startActivity(intent);
                                    }
                                    if (response.hasMsg() && response.getMsg().length() > 0) {
//                            showToast(responseData.getToastMsg());
                                        if (response.getTipsType() == 0)//toast
                                        {
                                            showToast(response.getMsg());
                                        } else {
                                            AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
                                            msgBuilder.setMessage(response.getMsg());
                                            msgBuilder.setNeutralButton("知道了", null);
                                            msgBuilder.create().show();
                                        }

                                    }
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(VolleyError errorResponse) {
                            Config.showFiledToast(context);
                        }

                        @Override
                        public void networkFinish() {
                            dismissProgress();
                        }
                    });
                }
            }
        }
    }

    //    @OnClick(R.id.reset)
    public void resetClick() {
//        setResult(RESULT_OK);
//        finish();
        onBackPressed();
    }

    /**
     * 是否需要刷新
     * 1.进入车辆详情,发起约车需要刷新
     * 2.从车辆详情,进入时间选择,需要刷新
     * 3.按钮约车 刷新
     * 4.按钮进入时间选择,刷新
     */
    public static boolean isNeedRefresh = true;
    TextView tip;
    TextView reset;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.activity_filtered_car_list);
        ButterKnife.inject(this);
        if (getIntent().getBooleanExtra("isMap", false)) {
            setTitle("查看车辆");
        }
        if (getIntent().getBooleanExtra("mult", false)) {
            setTitle("我要约车");
        }
        initNoteDataRefush();
        mSwiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);
        try {
            if (getIntent().hasExtra("filter")) {
                filter = new JSONObject(getIntent().getStringExtra("filter"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSwiperefreshlayout.setOnRefreshListener(this);
        mLoadingFooter = new LoadingFooter(context);
        list.addFooterView(mLoadingFooter.getView());


        if (getIntent().getBooleanExtra("isMap", false) || !getIntent().getBooleanExtra("mult", false)) {
            list.setDividerHeight(1);
        }
        mLoadingFooter.getView().setOnClickListener(footerClick);
        adapter = new MyAdapter();
        headerView = (RelativeLayout) getLayoutInflater().inflate(R.layout.filtered_header_view, null);
        tip = (TextView) headerView.findViewById(R.id.tip1);
        reset = (TextView) headerView.findViewById(R.id.reset);
        list.addHeaderView(headerView);
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
                            loadData();
                        }
                    }
                }
            }
        });
//        if (getIntent().getBooleanExtra("isMap", false)) {
//            mPullToRefreshLayout.setRefreshing(true);
//            pageBuilder.setDirection(0);
//            loadData();
//        }
        isNeedRefresh = true;
        //当有车主同意了之后,
        ObserverManager.addObserver(ObserverManager.RENTERTOPAY_STROKE, new ObserverListener() {
            @Override
            public void observer(String from, Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (FindCarListModel model : datas) {
                            ObserverManager.getObserver(model.info.getCarId()).observer("", "");
                            model.isSelect = false;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    UuCommon.PageResult pageResult;
    UuCommon.PageRequest.Builder pageBuilder = UuCommon.PageRequest.newBuilder();

    boolean isLoadMoring = false;
    List<FindCarListModel> datas = new ArrayList<FindCarListModel>();
    ArrayList<FindCarListModel> selectedCarItem = new ArrayList<FindCarListModel>();
    public View.OnClickListener
            footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLoadMoring = true;
            pageBuilder.setDirection(1);
            pageBuilder.setPointer(pageResult.getNextPageStart());
            loadData();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }

    /**
     * 一个点多车查询接口
     */
    public void getMapData() {
        Config.getCoordinates(context, new LocationListener() {
            @Override
            public void locationSuccess(double lat, double lng, String addr) {
                CarInterface.QueryCarList.Request.Builder builder = CarInterface.QueryCarList.Request.newBuilder();
                SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
                for (int i = 0; i < Config.openCity.size(); i++) {
                    if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                        builder.setCityId(Config.openCity.get(i).getCityId());
                        break;
                    }
                }
                UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
                position.setLat(getIntent().getDoubleExtra("lat", 0));
                position.setLng(getIntent().getDoubleExtra("lng", 0));
                UuCommon.LatlngPosition.Builder currentPosition = UuCommon.LatlngPosition.newBuilder();
                currentPosition.setLat(lat);
                currentPosition.setLng(lng);
                builder.setUserPosition(currentPosition);
                builder.setPosition(position);
                builder.setPage(pageBuilder);
                builder.setScene(CarInterface.QueryCarList.QueryCarScene.MULTI_CAR_LIST);
                builder.setIsPrecise(true);
                NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.QueryCarList_VALUE);
                task.setTag("QueryCarList");
                task.setBusiData(builder.build().toByteArray());
                NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
                    @Override
                    public void onSuccessResponse(UUResponseData responseData) {

                        if (responseData.getRet() == 0) {
                            try {
                                CarInterface.QueryCarList.Response response = CarInterface.QueryCarList.Response.parseFrom(responseData.getBusiData());
                                MLog.e(tag, "response ret = " + response.getRet());
                                if (response.getRet() == 0) {

                                    tip.setText(getIntent().getStringExtra("address") + "的车辆(共" + response.getCarResultListList().size() + "辆),您可同时预约多辆车,提高订车速度");
                                    isLoadMoring = false;
                                    isNeedRefresh = false;
                                    if (pageBuilder.getDirection() == 0) {
                                        datas.clear();
                                        selectedCarItem.clear();
                                        rentNumRoot.setVisibility(View.GONE);
                                    }
                                    MLog.e(tag, "response.getCarResultListList() = " + response.getCarResultListList().size());
                                    for (CarCommon.CarBriefInfo info : response.getCarResultListList()) {
                                        FindCarListModel model = new FindCarListModel();
                                        model.setBriefInfo(info);
                                        datas.add(model);
                                    }

                                    pageResult = response.getPageResult();
                                    if (pageResult.getHasMore()) {
                                        if (list.getFooterViewsCount() == 0) {
                                            list.addFooterView(mLoadingFooter.getView());
                                        }
                                    } else {
                                        list.removeFooterView(mLoadingFooter.getView());
                                    }
                                    adapter.notifyDataSetChanged();
                                    mAllFramelayout.makeProgreeDismiss();
                                } else {
                                    if (list.getFooterViewsCount() == 0) {
                                    } else {
                                        list.removeFooterView(mLoadingFooter.getView());
                                    }
                                    if (datas.isEmpty()) {
                                        mAllFramelayout.makeProgreeNoData();
                                    } else {
                                        Config.showFiledToast(context);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (datas.isEmpty()) {
                                    mAllFramelayout.makeProgreeNoData();
                                } else {
                                    Config.showFiledToast(context);
                                }
                            }
                        } else {
                            if (list.getFooterViewsCount() == 0) {
                            } else {
                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            if (datas.isEmpty()) {
                                mAllFramelayout.makeProgreeNoData();
                            } else {
                                Config.showFiledToast(context);
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError errorResponse) {
                        if (datas.isEmpty()) {
                            mAllFramelayout.makeProgreeNoData();
                        } else {
                            Config.showFiledToast(context);
                        }
                    }

                    @Override
                    public void networkFinish() {

                        mSwiperefreshlayout.setRefreshing(false);
                        mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                        isLoadMoring = false;
                        mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
                    }
                });
            }
        });

    }


    public void getFilterData() {
        MLog.e(tag, "getFilterData");
        CarInterface.FilterCarList.Request.Builder builder = CarInterface.FilterCarList.Request.newBuilder();
        SharedPreferences sp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        builder.setCityId("010");
        for (int i = 0; i < Config.openCity.size(); i++) {
            if (sp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                builder.setCityId(Config.openCity.get(i).getCityId());
                break;
            }
        }
        builder.setPage(pageBuilder);
        UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
        try {
            if (filter.has("brand") && !filter.getString("brand").equals("任意")) {
                builder.setBrand(filter.getString("brand"));
            }
            if (filter.has("xinghao") && !filter.getString("xinghao").equals("任意")) {
                builder.setModel(filter.getString("xinghao"));
            }
            if (filter.has("lat") && filter.has("lng")) {
                position.setLat(filter.getDouble("lat"));
                position.setLng(filter.getDouble("lng"));
                builder.setPosition(position);
            }
            if (filter.has("start") && filter.has("end")) {
                builder.setStartTime(Integer.parseInt(filter.getString("start")));
                builder.setEndTime(Integer.parseInt(filter.getString("end")));
            }
            if (filter.has("price_min")) {
                builder.setPriceMin(filter.getInt("price_min"));
            }
            if (filter.has("price_max")) {
                builder.setPriceMax(filter.getInt("price_max"));
            }
            if (filter.has("boxiang")) {
                //TODO 修改自动档
                if (filter.getString("boxiang").equals(String.valueOf(CarCommon.CarTransmissionType.HAND_VALUE))) {
                    builder.setCarTransmissionType(CarCommon.CarTransmissionType.HAND);
                } else {
                    builder.setCarTransmissionType(CarCommon.CarTransmissionType.AUTO);
                }
            }
            MLog.e(tag, "boxiang : " + builder.getCarTransmissionType().getNumber());
            //TODO 修改车型
            if (filter.has("type")) {
                JSONArray jsonArray = filter.getJSONArray("type");
                for (int i = 0; i < jsonArray.length(); i++) {
                    switch (jsonArray.getInt(i)) {
                        case CarCommon.CarType.COMPACT_VALUE:
                            builder.addType(CarCommon.CarType.COMPACT_VALUE);
                            break;
                        case CarCommon.CarType.SUV_VALUE:
                            builder.addType(CarCommon.CarType.SUV_VALUE);
                            break;
                        case CarCommon.CarType.COMFORTABLE_VALUE:
                            builder.addType(CarCommon.CarType.COMFORTABLE_VALUE);
                            break;
                        case CarCommon.CarType.ECONOMICAL_VALUE:
                            builder.addType(CarCommon.CarType.ECONOMICAL_VALUE);
                            break;
                        case CarCommon.CarType.LUXURIOUS_VALUE:
                            builder.addType(CarCommon.CarType.LUXURIOUS_VALUE);
                            break;
                        case CarCommon.CarType.BUSINESS_VALUE:
                            builder.addType(CarCommon.CarType.BUSINESS_VALUE);
                            break;
                        case CarCommon.CarType.SPECIFIC_VALUE:
                            builder.addType(CarCommon.CarType.SPECIFIC_VALUE);
                            break;
                    }
                }
            }

            MLog.e("type", "type___ =" + builder.getTypeList().toString());
            if (filter.has("gps")) {
                builder.setGps(1);
            } else {
                builder.setGps(0);
            }
            if (filter.has("daocheleida")) {
                builder.setDaoCheLeiDa(1);
            } else {
                builder.setDaoCheLeiDa(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setFilterCarListScene(CarCommon.FilterCarListScene.RENT_MULTI_CAR);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.FilterCarList_VALUE);
        task.setTag("FilterCarList");
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        CarInterface.FilterCarList.Response response = CarInterface.FilterCarList.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            pageResult = response.getPageResult();
                            isNeedRefresh = false;
                            if (pageBuilder.getDirection() == 0) {
                                datas.clear();
                                selectedCarItem.clear();
                                rentNumRoot.setVisibility(View.GONE);
                            }
                            for (CarCommon.CarBriefInfo info : response.getCarResultListList()) {
                                FindCarListModel model = new FindCarListModel();
                                model.setBriefInfo(info);
                                datas.add(model);
                            }
                            if (getIntent().getBooleanExtra("mult", false)) {
                                tip.setText(R.string.filtered_list_tip);
                            } else {

                                tip.setText("以下车辆符合筛选条件，您可连续预约多辆车，提高订车速度。");
                            }
                            reset.setVisibility(View.GONE);
                            if (pageResult.getHasMore()) {
                                if (list.getFooterViewsCount() == 0) {
                                    list.addFooterView(mLoadingFooter.getView());
                                }
                            } else {
                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            adapter.notifyDataSetChanged();
                        } else if (response.getRet() == -1) {
                            if (list.getFooterViewsCount() != 0) {
                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            Config.showFiledToast(context);
                            finish();
                        } else if (response.getRet() == 1) {
                            tip.setText(R.string.reset_filter_tip);
                            reset.setVisibility(View.VISIBLE);
                            reset.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    resetClick();
                                }
                            });
                            pageResult = response.getPageResult();
                            if (pageBuilder.getDirection() == 0) {
                                datas.clear();
                                selectedCarItem.clear();
                                rentNumRoot.setVisibility(View.GONE);
                            }
                            for (CarCommon.CarBriefInfo info : response.getCarResultListList()) {
                                FindCarListModel model = new FindCarListModel();
                                model.setBriefInfo(info);
                                datas.add(model);
                            }
                            if (pageResult.getHasMore()) {
                                if (list.getFooterViewsCount() == 0) {
                                    list.addFooterView(mLoadingFooter.getView());
                                }
                            } else {
                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            adapter.notifyDataSetChanged();
                        }
                        mAllFramelayout.makeProgreeDismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (datas.isEmpty()) {
                            mAllFramelayout.makeProgreeNoData();
                        } else {
                            Config.showFiledToast(context);
                        }

                    }
                }

            }

            @Override
            public void onError(VolleyError errorResponse) {


                if (datas.isEmpty()) {
                    mAllFramelayout.makeProgreeNoData();
                } else {
                    Config.showFiledToast(context);
                }
            }

            @Override
            public void networkFinish() {

                mSwiperefreshlayout.setRefreshing(false);

                isLoadMoring = false;
                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
            }
        });
    }

    public void loadData() {
        if (pageBuilder.getDirection() == 1) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
        } else {
            pageBuilder.setPointer("");
        }
        if (getIntent().getBooleanExtra("isMap", false)) {
            getMapData();
        } else {
            getFilterData();
        }

    }


    @Override
    public void onRefresh() {

        pageBuilder.setDirection(0);
        pageBuilder.setPointer("");
        loadData();
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
            convertView = context.getLayoutInflater().inflate(R.layout.find_car_list_item, null);
            final FindCarListModel item = datas.get(position);

            final RelativeLayout root = (RelativeLayout) convertView.findViewById(R.id.root);

            convertView.setBackgroundColor(getResources().getColor(R.color.c11));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OldCarInfoActivity.class);
                    intent.putExtra("islist", true);
                    if (item.info.hasPassedMsg() && item.info.getPassedMsg() != null && !item.info.getPassedMsg().equals("")) {
                        intent.putExtra("passedMsg", item.info.getPassedMsg());
                    }
                    intent.putExtra(SysConfig.CAR_SN, item.info.getCarId());
                    try {
                        if (filter != null && filter.has("start") && filter.has("end")) {
                            intent.putExtra("start", filter.getString("start"));
                            intent.putExtra("end", filter.getString("end"));
                            intent.putExtra("mult", true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("index", position);
                    startActivityForResult(intent, REFUSH);
                }
            });
            BaseNetworkImageView img = (BaseNetworkImageView) convertView.findViewById(R.id.car_img);
            UUAppCar.getInstance().display(item.info.getThumbImg(), img, R.drawable.list_car_img_def);

            BaseNetworkImageView waterMarkimg = (BaseNetworkImageView) convertView.findViewById(R.id.water_mark_img);
            waterMarkimg.setVisibility(View.GONE);
            if (item.info.hasWaterMarkPic() && item.info.getWaterMarkPic() != null && !item.info.getWaterMarkPic().equals("")) {
                waterMarkimg.setVisibility(View.VISIBLE);
                UUAppCar.getInstance().display(item.info.getWaterMarkPic(), waterMarkimg, R.drawable.nodefimg);
            }
            String juli = "";
            float djuli = -1;
            if (item.info.hasDistanceFromRenter()) {
                djuli = item.info.getDistanceFromRenter();
            }
            TextView brand = (TextView) convertView.findViewById(R.id.brand);
            brand.setText(item.info.getBrand() + item.info.getCarModel());
            TextView dis = (TextView) convertView.findViewById(R.id.dis);
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
            price_day.setText("￥" + ((int) item.info.getPricePerDay()));
            TextView gearbox = (TextView) convertView.findViewById(R.id.gearbox_text);
            if (item.info.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO)) {

                gearbox.setText("自动挡");
            } else {
                gearbox.setText("手动挡");
            }
            TextView address = (TextView) convertView.findViewById(R.id.address_text);
            // xianxing.setText();
            address.setText(item.info.getAddress());

            if (item.info.hasColoredAddress()) {
                if (item.info.getColoredAddress().hasTextHexColor()) {
                    address.setTextColor(Color.parseColor(item.info.getColoredAddress().getTextHexColor()));
                }
                if (item.info.getColoredAddress().hasText()) {
                    address.setText(item.info.getColoredAddress().getText());
                }

            }
            TextView banDay = (TextView) convertView.findViewById(R.id.banday);
            if (item.info.hasCarLimitedInfo()) {
                banDay.setVisibility(View.VISIBLE);
                banDay.setText(item.info.getCarLimitedInfo());
            } else {
                banDay.setVisibility(View.INVISIBLE);
            }
//            RatingBar rating = (RatingBar) convertView.findViewById(R.id.rating);
//            if (item.info.hasStars()) {
//                rating.setVisibility(View.VISIBLE);
//                rating.setRating(item.info.getStars());
//            } else {
//                rating.setVisibility(View.GONE);
//            }
            LinearLayout bottomRoot = (LinearLayout) convertView.findViewById(R.id.bottom_root);
            if (!getIntent().getBooleanExtra("isMap", false) && getIntent().getBooleanExtra("mult", false)) {
                bottomRoot.setVisibility(View.VISIBLE);
            } else {
                bottomRoot.setVisibility(View.GONE);
            }
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            View v1 = convertView.findViewById(R.id.v1);
            desc.setVisibility(View.GONE);
            v1.setVisibility(View.GONE);
            if (item.info.hasCarDetailInfo()) {
                desc.setVisibility(View.VISIBLE);
                v1.setVisibility(View.VISIBLE);
                desc.setText(item.info.getCarDetailInfo());
            }
            final TextView rentButton = (TextView) convertView.findViewById(R.id.rent);
            rentButton.setText("预约此车");
            if (item.info.getRentingType() == 1 || item.isSelect) {
                rentButton.setText("约车中...");
            }
            ObserverManager.addObserver(item.info.getCarId(), new ObserverListener() {
                @Override
                public void observer(String from, Object obj) {
                    if (obj.equals("renting")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.isSelect = true;
                                rentButton.setText("约车中...");
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                item.isSelect = false;
                                rentButton.setText("预约此车");
                            }
                        });
                    }
                }
            });
            rentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rentButton.getText().toString().equals("约车中..."))//约车中 ...进入到约车等待页
                    {
                        Intent intent = new Intent(context, OneToOneWaitActivity.class);
                        startActivityForResult(intent, REFUSH);
                        return;
                    }

                    Intent intent = new Intent(context, SelectTime.class);
                    if (item.info.hasDisplayPosition()) {

                        intent.putExtra("displayPosition", item.info.getDisplayPosition());
                    } else {
                        intent.putExtra("displayPosition", 1);
                    }
                    intent.putExtra("fromCarInfo", true);
                    String car_sn = item.info.getCarId();
                    intent.putExtra(SysConfig.CAR_SN, car_sn);
                    intent.putExtra("CAR_NAME", item.info.getBrand() + item.info.getCarModel());
                    intent.putExtra("start", getIntent().getStringExtra("start"));
                    intent.putExtra("end", getIntent().getStringExtra("end"));
                    if (filter.has("start") && filter.has("end")) {
                        try {
                            intent.putExtra("start", filter.getString("start"));
                            intent.putExtra("end", filter.getString("end"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    intent.putExtra("noSelectTime", true);
                    startActivity(intent);
//                    if (!Config.isNetworkConnected(context))
//                    {
//                        Config.showFiledToast(context);
//                        return;
//                    }
//                    if (rentButton.getText().toString().equals("约车中..."))//约车中 ...进入到约车等待页
//                    {
//                        Intent intent = new Intent(context, OneToOneWaitActivity.class);
//                        startActivityForResult(intent, REFUSH);
//                        return;
//                    }
//
//
//                    showProgress(false);
//                    Bundle req = new Bundle();
//                    ReserveInformationModel.ReserveInformationRequestModel model = new ReserveInformationModel.ReserveInformationRequestModel();
//                    model.carSn = item.info.getCarId();
//                    try
//                    {
//                        model.leaseStart = Integer.parseInt(filter.getString("start"));
//                        model.leaseEnd = Integer.parseInt(filter.getString("end"));
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                    }
//                    req.putParcelable("req", model);
//                    UUAppCar app = (UUAppCar) getApplication();
//                    CoreTask.Callback callback = new CoreTask.Callback(app)
//                    {
//                        @Override
//                        public void onSuccess(Bundle result)
//                        {
//                            ReserveInformationModel.ReserveInformationResponseModel resp = result.getParcelable("resp");
//                            if (resp.ret == 0)
//                            {
//                                Dialog dialog;
//                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                View view = getLayoutInflater().inflate(R.layout.reserve_info_dialog, null);
//                                TextView brand = (TextView) view.findViewById(R.id.brand);
//                                brand.setText(item.info.getBrand() + item.info.getCarModel());
//                                TextView start = (TextView) view.findViewById(R.id.start);
//                                TextView end = (TextView) view.findViewById(R.id.end);
//                                try
//                                {
//                                    long startTime = Integer.parseInt(filter.getString("start")) * 1000L;
//                                    long endTime = Integer.parseInt(filter.getString("end")) * 1000L;
//                                    SimpleDateFormat tipformatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//                                    start.setText(tipformatter.format(startTime));
//                                    end.setText(tipformatter.format(endTime));
//                                }
//                                catch (JSONException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                                TextView rentTime = (TextView) view.findViewById(R.id.rent_time);
//                                rentTime.setText(resp.leaseTerm);
//                                TextView price = (TextView) view.findViewById(R.id.rent_price);
//                                price.setText(resp.rent + "元");
//                                TextView safe = (TextView) view.findViewById(R.id.safe);
//                                safe.setText(resp.insurance + "元");
//                                TextView yajin = (TextView) view.findViewById(R.id.yajin);
//                                yajin.setText(resp.deposit + "元");
//                                builder.setView(view);
//                                builder.setNegativeButton("取消", null);
//                                builder.setNeutralButton("确认约车", new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which)
//                                    {
//                                        showProgress(false);
//                                        OrderFormInterface26.RenterStartPreOrder.Request.Builder request = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
//                                        request.addCarIds(item.info.getCarId());
//                                        try
//                                        {
//                                            request.setStartTime(Integer.parseInt(filter.getString("start")));
//                                            request.setEndTime(Integer.parseInt(filter.getString("end")));
//                                        }
//                                        catch (JSONException e)
//                                        {
//                                            e.printStackTrace();
//                                        }
//                                        request.setCancelLastPreOrder(false);
//                                        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterStartPreOrder_VALUE);
//                                        task.setBusiData(request.build().toByteArray());
//                                        task.setTag("RenterStartPreOrder");
//                                        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
//                                        {
//                                            @Override
//                                            public void onSuccessResponse(UUResponseData responseData)
//                                            {
//                                                if (responseData.getRet() == 0)
//                                                {
//                                                    try
//                                                    {
//                                                        OrderFormInterface26.RenterStartPreOrder.Response response = OrderFormInterface26.RenterStartPreOrder.Response.parseFrom(responseData.getBusiData());
//                                                        if (response.getRet() == 0)
//                                                        {
////                                                            Intent intent = new Intent(context, OneToOneWaitActivity.class);
////                                                            startActivityForResult(intent, REFUSH);
////                                                            FilteredCarListActivity.isNeedRefresh = true;
//                                                            rentButton.setText("约车中...");
//                                                            item.isSelect = true;
//                                                        }
//                                                        if (response.getRet() == -2)
//                                                        {
//                                                            Config.isSppedIng = true;
//                                                            Config.isUserCancel = false;
//                                                            getApp().startRenting();
//                                                            rentClick();
//                                                        }
//                                                        if (responseData.getResponseCommonMsg().getMsg().length() > 0)
//                                                        {
//                                                            showResponseCommonMsg(responseData.getResponseCommonMsg());
//                                                        }
//                                                    }
//                                                    catch (InvalidProtocolBufferException e)
//                                                    {
//                                                        e.printStackTrace();
//                                                    }
//                                                    dismissProgress();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError(VolleyError errorResponse)
//                                            {
//                                                Config.showFiledToast(context);
//                                            }
//
//                                            @Override
//                                            public void networkFinish()
//                                            {
//                                                dismissProgress();
//                                            }
//                                        });
//                                    }
//                                });
//
//                                dialog = builder.create();
//                                dialog.setCanceledOnTouchOutside(false);
//                                dialog.setCancelable(false);
//                                dialog.show();
//                            }
//                            dismissProgress();
//                            showToast(resp.getMsg().getMsg());
//                        }
//
//                        @Override
//                        public void onFailed()
//                        {
//                            dismissProgress();
//                            Config.showFiledToast(context);
//                            return;
//                        }
//
//                        @Override
//                        public void onProgress(int progress)
//                        {
//
//                        }
//                    };
//                    callback.setArguments(req);
//                    CoreTask task = new ReserveInformationTask().setCallback(callback).build();
//                    app.getCoreLooper().addTask(task);
                }
            });

//            final ImageView check = (ImageView) convertView.findViewById(R.id.check);
//            check.setVisibility(View.GONE);
//            for (int i = 0; i < selectedCarItem.size(); i++) {
//                if (selectedCarItem.get(i).info.getCarId().equals(item.info.getCarId())) {
//                    item.isSelect = true;
//                    check.setSelected(item.isSelect);
//                    if (item.isSelect) {
//                        convertView.setBackgroundColor(getResources().getColor(R.color.c14));
//                    }
//
//                    break;
//                }
//
//            }
//            if (!item.isSelect) {
//                check.setSelected(false);
//                convertView.setBackgroundColor(getResources().getColor(R.color.c11));
//            }
//            check.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    item.isSelect = !item.isSelect;
//                    int selected = 0;
//                    for (int i = 0; i < datas.size(); i++) {
//                        if (datas.get(i).isSelect) {
//                            selected += 1;
//                        }
//                    }
//                    if (selected > 20) {
//                        item.isSelect = !item.isSelect;
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setMessage("最多可预约20辆车");
//                        builder.setNegativeButton("确定", null);
//                        builder.create().show();
//                    } else {
//                        v.setSelected(item.isSelect);
//                        if (item.isSelect) {
//                            if (rentNumRoot.getVisibility() == View.GONE) {
//                                rentNumRoot.setVisibility(View.VISIBLE);
//                            }
//                            selectedCarItem.add(datas.get(position));
//                            rentNum.setText(selected + "");
//                        } else {
//                            selectedCarItem.remove(datas.get(position));
//                            if (selected == 0) {
//                                rentNumRoot.setVisibility(View.GONE);
//                            } else {
//                                rentNum.setText(selected + "");
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            });

            return convertView;
        }
    }


    public void onResume() {
        super.onResume();
        if (isNeedRefresh) {
            mSwiperefreshlayout.setRefreshing(true);
            pageBuilder.setDirection(0);
            loadData();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REFUSH) {
//                mPullToRefreshLayout.setRefreshing(true);
//                pageBuilder.setDirection(0);
//                loadData();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

}
