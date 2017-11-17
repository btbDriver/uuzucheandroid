package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.PB.impl.StrokeModel;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.adapter.MyStrokeAdapter;
import com.youyou.uucar.UI.Main.fragment.BasicFragment;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.listen.OnClickNetworkListener;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.empty.EmptyOnScrollListener;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by 16515_000 on 2014/12/25.
 */
public class MyStrokeCurrentFragment extends BasicFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = MyStrokeCurrentFragment.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "MyStrokeCurrentFragment";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();
    View view;
    BaseActivity context;
    public int page = 0;
    public StrokeModel.StrokeRequestModel requestModel = new StrokeModel.StrokeRequestModel();

    protected ListView mListView;
    public SwipeRefreshLayout mSwiperefreshlayout;
    protected LoadingFooter mLoadingFooter;
    public MyStrokeAdapter adapter;
    boolean isLoadMoring = false;
    @InjectView(R.id.root)
    RelativeLayout root;
    @InjectView(R.id.nodata)
    RelativeLayout nodata;

    @OnClick(R.id.findcar)
    public void findCarClick() {
        MainActivityTab.instance.gotoFindCar();
    }

    public void cancleAllRequest() {

        if (mSwiperefreshlayout != null && mSwiperefreshlayout.isRefreshing()) {
            mSwiperefreshlayout.setRefreshing(false);
        }
        mLoadingFooter.setState(LoadingFooter.State.TheEnd);
        mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
        Config.missTip = "";
        Config.showPoint = false;
        Config.missDontRefresh = false;
        missRoot.setVisibility(View.GONE);
    }

    List<OrderFormCommon.TripOrderCard> data = new ArrayList<OrderFormCommon.TripOrderCard>();

    @Override
    public void initLoader() {

    }

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    public void initNoteDataRefush() {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Config.isNetworkConnected(getActivity())) {
                    mAllFramelayout.noDataReloading();
                    mSwiperefreshlayout.setRefreshing(true);
                    page = 1;
                    requestModel.pageRequest.setPageNo(page);
                    getData();
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    @Override
    public void resume() {
        if (Config.isNetworkConnected(getActivity())) {
            if (!Config.isGuest(context)) {
                if (MainActivityTab.instance.order.needRefush || MainActivityTab.instance.order.currentRefush) {
//                    MainActivityTab.instance.order.needRefush = false;
                    MainActivityTab.instance.order.currentRefush = false;
                    mListView.setSelection(0);
                    mSwiperefreshlayout.setRefreshing(true);
                    page = 1;
                    requestModel.pageRequest.setPageNo(page);
                    getData();
                } else /*if (isFirst)*/ {
                    isFirst = false;
                    mListView.setSelection(0);
                    mSwiperefreshlayout.setRefreshing(true);
                    page = 1;
                    requestModel.pageRequest.setPageNo(page);
                    getData();
                }
//                else
//                {
//                    mAllFramelayout.makeProgreeDismiss();
//                }

            } else {
                nodata.setVisibility(View.VISIBLE);
                root.setVisibility(View.GONE);
                mAllFramelayout.makeProgreeDismiss();
//                mAllFramelayout.makeProgreeNoData();
            }
        } else {
            nodata.setVisibility(View.VISIBLE);
            root.setVisibility(View.GONE);
            mAllFramelayout.makeProgreeNoData();
        }
        if (!Config.missTip.equals("")) {
            missRoot.setVisibility(View.VISIBLE);
            missTip.setText(Config.missTip);
        } else {
            Config.showPoint = false;
            Config.missDontRefresh = false;
            missRoot.setVisibility(View.GONE);
        }
    }

    public View.OnClickListener footerClick = new OnClickNetworkListener() {
        @Override
        public void onNetworkClick(View v) {
            isLoadMoring = true;
            page++;
            requestModel.pageRequest.setPageNo(page);
            getData();
        }
    };

    public static MyStrokeCurrentFragment newInstance() {
        MyStrokeCurrentFragment instance = new MyStrokeCurrentFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        context = (BaseActivity) getActivity();
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_stroke_current, null);
        ButterKnife.inject(this, view);
        initNoteDataRefush();

        TextView nodata_tip = (TextView) view.findViewById(R.id.nodata_tip);
        nodata_tip.setText("您还没有进行中的订单");
        view.findViewById(R.id.findcar).setVisibility(View.VISIBLE);
        requestModel.queryType = OrderFormCommon.TripListQueryType.UNDERWAY;
        mListView = (ListView) view.findViewById(R.id.listView);
        mSwiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.pullToRefresh);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);
        mSwiperefreshlayout.setOnRefreshListener(this);
        mLoadingFooter = new LoadingFooter(getActivity());
        mListView.addFooterView(mLoadingFooter.getView());
        mLoadingFooter.getView().setOnClickListener(footerClick);
        adapter = new MyStrokeAdapter(context, data, this);
        mListView.setAdapter(adapter);
        mListView.setOnScrollListener(new EmptyOnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
                if (mListView.getFooterViewsCount() != 0) {

                    if (firstVisibleItem + visibleItemCount >= totalItemCount
                            && totalItemCount != 0
                            && totalItemCount != mListView.getHeaderViewsCount()
                            + mListView.getFooterViewsCount() && adapter
                            .getCount() > 0) {
                        if (requestModel.pageNoResult != null) {
                            if (!isLoadMoring && requestModel.pageNoResult.getHasMore()) {
                                isLoadMoring = true;
                                page++;
                                requestModel.pageRequest.setPageNo(page);
                                getData();
                            }
                        }

                    }
                }
            }
        });
        mLoadingFooter.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestModel.pageNoResult != null) {
                    if (requestModel.pageNoResult.getHasMore()) {
                        page++;
                        requestModel.pageRequest.setPageNo(page);
                        getData();
                    }
                }

            }
        });


        ObserverManager.addObserver(ObserverManager.MYSTROKEFRAGMENT, refushListener);
        resume();//因为调用顺序的原因.第一次创建的时候会先调用resume然后才会oncreate,所以在外面屏蔽了第一次调用resume.在oncreate里手动调用
        return view;
    }


    public boolean isFirst = true;
    @InjectView(R.id.miss_root)
    RelativeLayout missRoot;
    @InjectView(R.id.miss_tip)
    TextView missTip;

    //
    @OnClick(R.id.close)
    public void MissTipClosegClick() {
        Config.showPoint = false;
        Config.missTip = "";
        Config.missDontRefresh = false;
        missRoot.setVisibility(View.GONE);

    }

    Handler handler = new Handler();
    public ObserverListener refushListener = new ObserverListener() {
        @Override
        public void observer(final String from, Object obj) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (from.equals("miss")) {
                        if (!Config.missTip.equals("")) {
                            missRoot.setVisibility(View.VISIBLE);
                            missTip.setText(Config.missTip);
                            ObserverManager.getObserver("进行中").observer("", "show");
                        } else {
                            Config.showPoint = false;
                            Config.missDontRefresh = false;
                            missRoot.setVisibility(View.GONE);
                            ObserverManager.getObserver("进行中").observer("", "");
                        }
                    } else {
                        MainActivityTab.instance.order.needRefush = false;
                        mSwiperefreshlayout.setRefreshing(true);
                        page = 1;
                        requestModel.pageRequest.setPageNo(page);
                        getData();
                    }
                }
            });


        }
    };


    public void getData() {
        if (page > 1) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
        }
        OrderFormInterface26.QueryTripList.Request.Builder builder = OrderFormInterface26.QueryTripList.Request.newBuilder();
        builder.setQueryType(requestModel.queryType);
        builder.setPageRequest(requestModel.pageRequest);
        final com.youyou.uucar.Utils.Network.NetworkTask task = new com.youyou.uucar.Utils.Network.NetworkTask(CmdCodeDef.CmdCode.QueryTripList_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("StrokeTask");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                StrokeModel.StrokeResponseModel model = new StrokeModel.StrokeResponseModel();
                try {
                    if (responseData.getRet() == 0) {
                        ((BaseActivity) getActivity()).showResponseCommonMsg(responseData.getResponseCommonMsg());
                        OrderFormInterface26.QueryTripList.Response response = OrderFormInterface26.QueryTripList.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            if (page == 1) {
                                data.clear();
                            }
                            data.addAll(response.getTripOrderCardsList());
                            UuCommon.PageNoResult pageNoResultData = response.getPageResult();

                            if (!pageNoResultData.getHasMore()) {
                                if (mListView.getFooterViewsCount() > 0) {
                                    mListView.removeFooterView(mLoadingFooter.getView());
                                }
                            } else {
                                if (mListView.getFooterViewsCount() == 0) {
                                    mListView.addFooterView(mLoadingFooter.getView());
                                }
                            }
                            if (page == 1 && data.size() == 0) {
                                nodata.setVisibility(View.VISIBLE);
                                MainActivityTab.instance.order.needRefush = true;
                                root.setVisibility(View.GONE);
                            } else {
                                root.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                            }
                        } else {

                            nodata.setVisibility(View.VISIBLE);
                            MainActivityTab.instance.order.needRefush = true;
                            root.setVisibility(View.GONE);
                        }
                        mAllFramelayout.makeProgreeDismiss();
                        requestModel.pageNoResult = response.getPageResult();
                        adapter.notifyDataSetChanged();
                        if (response.getWaitCommentCount() > 0) {
                            ObserverManager.getObserver("已完成").observer("", "show");
                            ObserverManager.getObserver("Miss").observer("", "show");
                            ObserverManager.getObserver(ObserverManager.MAINTABNUM).observer("", "showFinishNews");
                        } else {

                            ObserverManager.getObserver("已完成").observer("", "");
                            ObserverManager.getObserver(ObserverManager.MAINTABNUM).observer("", "");
                        }
                        if (response.getWaitRenterCommentCount() > 0) {

                            ObserverManager.getObserver("FinishTip").observer("", "show");
                        } else {
                            ObserverManager.getObserver("FinishTip").observer("", "");
                        }
                    } else {

                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
                mAllFramelayout.makeProgreeNoData();
                root.setVisibility(View.VISIBLE);
                nodata.setVisibility(View.GONE);
                Config.showFiledToast(context);
                mAllFramelayout.makeProgreeDismiss();
            }

            @Override
            public void networkFinish() {
                mSwiperefreshlayout.setRefreshing(false);
                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
                isLoadMoring = false;
            }
        });
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestModel.pageRequest.setPageNo(page);
        getData();
        MissTipClosegClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == context.RESULT_OK) {
            mSwiperefreshlayout.setRefreshing(true);

            page = 1;
            requestModel.pageRequest.setPageNo(page);
            getData();
        }
    }
}
