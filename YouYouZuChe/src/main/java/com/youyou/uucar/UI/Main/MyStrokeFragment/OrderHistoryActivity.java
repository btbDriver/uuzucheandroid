package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.PB.impl.OrderHistoryModel;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import android.support.v4.widget.SwipeRefreshLayout;

public class OrderHistoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.listView)
    ListView list;
    @InjectView(R.id.pullToRefresh)
    SwipeRefreshLayout mSwiperefreshlayout;
    OrderHistoryModel.OrderHistoryResponseModel responseModel = new OrderHistoryModel.OrderHistoryResponseModel();
    UuCommon.PageNoRequest.Builder pageBuilder = UuCommon.PageNoRequest.newBuilder();
    protected LoadingFooter mLoadingFooter;
    MyAdapter adapter;
    boolean isLoadMoring = false;
    int page = 1;

    public List<OrderFormCommon.PreOrderHistoCard> data = new ArrayList<OrderFormCommon.PreOrderHistoCard>();
    @InjectView(R.id.nodata)
    RelativeLayout nodata;

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.inject(this);
        initNoteDataRefush();
        mSwiperefreshlayout.setOnRefreshListener(this);
        mLoadingFooter = new LoadingFooter(context);
        list.addFooterView(mLoadingFooter.getView());
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
                if (list.getFooterViewsCount() != 0) {

                    if (firstVisibleItem + visibleItemCount >= totalItemCount
                            && totalItemCount != 0
                            && totalItemCount != list.getHeaderViewsCount()
                            + list.getFooterViewsCount() && adapter
                            .getCount() > 0 && list.getFooterViewsCount() > 0) {
                        if (!isLoadMoring) {
                            isLoadMoring = true;
                            page += 1;
                            pageBuilder.setPageNo(page);
                            loadData();
                        }
                    }
                }
            }
        });
        if (!Config.isGuest(context)) {
            loadData();
        } else {
            mAllFramelayout.makeProgreeDismiss();
            findViewById(R.id.root).setVisibility(View.GONE);
            findViewById(R.id.nodata).setVisibility(View.VISIBLE);
        }
    }

    boolean isNetWorkError = false;

    public void initNoteDataRefush() {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Config.isNetworkConnected(context)) {
                    mAllFramelayout.noDataReloading();
                    mSwiperefreshlayout.setRefreshing(true);
                    page = 1;
                    pageBuilder.setPageNo(1);
                    loadData();
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    public View.OnClickListener
            footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLoadMoring = true;
            page += 1;
            pageBuilder.setPageNo(page);
            loadData();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if (page == 1) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
        }
        OrderFormInterface26.QueryPreOrderHistoRequest.Builder builder = OrderFormInterface26.QueryPreOrderHistoRequest.newBuilder();
        builder.setPageRequest(pageBuilder);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.QueryPreOrderHisto_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("QueryPreOrderHisto");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        OrderFormInterface26.QueryPreOrderHistoResponse response = OrderFormInterface26.QueryPreOrderHistoResponse.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {

                            if (page == 1) {
                                data.clear();
                            }
                            data.addAll(response.getCardsList());
                            UuCommon.PageNoResult pageNoResult = response.getPageResult();
                            if (pageNoResult.getHasMore()) {
                                if (list.getFooterViewsCount() == 0) {
                                    list.addFooterView(mLoadingFooter.getView());
                                }
                            } else {
                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            if (page == 1 && data.size() == 0) {
                                nodata.setVisibility(View.VISIBLE);
                                mSwiperefreshlayout.setVisibility(View.GONE);
                            } else {
                                mSwiperefreshlayout.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                            }
                            mAllFramelayout.makeProgreeDismiss();
                            adapter.notifyDataSetChanged();

                        } else {
                            mAllFramelayout.makeProgreeNoData();

                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                } else {

                    mAllFramelayout.makeProgreeNoData();
                }
                showResponseCommonMsg(responseData.getResponseCommonMsg());

            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
                mAllFramelayout.makeProgreeNoData();
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

    @Override
    public void onRefresh() {

        page = 1;
        pageBuilder.setPageNo(page);
        loadData();
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final OrderFormCommon.PreOrderHistoCard item = data.get(position);
            convertView = getLayoutInflater().inflate(R.layout.order_history_item, null);
            TextView createTime = (TextView) convertView.findViewById(R.id.create_time);
            TextView msg = (TextView) convertView.findViewById(R.id.msg);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView state = (TextView) convertView.findViewById(R.id.state);
            SimpleDateFormat tipformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat planformatter = new SimpleDateFormat("MM月dd日 HH:mm");
            createTime.setText(formatSecond(item.getOccurTime(), tipformatter));
            String startTime = formatSecond(item.getPlanStartTime(), planformatter);
            String endTime = formatSecond(item.getPlanEndTime(), planformatter);
            time.setText("用车时间：" + startTime + " 至 " + endTime);
            msg.setText(item.getRentInfo());
            if (item.getUserType() == 1)//车主
            {
                if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.RENT_SUC))//预约成功
                {
                    state.setTextColor(getResources().getColor(R.color.c1));
                    state.setText("接单成功");
                } else if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.CAR_OWNER_NOT_ACCEPT))//车主未接受
                {
                    state.setTextColor(getResources().getColor(R.color.c8));
                    state.setText("您未接受");
                } else if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.RENTER_NOT_SELECT))//租客未选择
                {
                    state.setTextColor(getResources().getColor(R.color.c8));
                    state.setText("被别人抢单");
                } else if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.RENTER_CANCEL))//租客取消
                {
                    state.setTextColor(getResources().getColor(R.color.c8));
                    state.setText("被别人抢单");
                }

            } else if (item.getUserType() == 2)//租客
            {
                if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.RENT_SUC))//预约成功
                {
                    state.setTextColor(getResources().getColor(R.color.c1));
                    state.setText("预约成功");
                } else if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.CAR_OWNER_NOT_ACCEPT))//车主未接受
                {
                    state.setTextColor(getResources().getColor(R.color.c8));
                    state.setText("车主未接受");
                } else if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.RENTER_NOT_SELECT))//租客未选择
                {
                    state.setTextColor(getResources().getColor(R.color.c8));
                    state.setText("您未选择");

                } else if (item.getPreOrderEndStatus().equals(OrderFormCommon.PreOrderEndStatus.RENTER_CANCEL))//租客取消
                {
                    state.setTextColor(getResources().getColor(R.color.c8));
                    state.setText("您已取消");
                }
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OldCarInfoActivity.class);
                    intent.putExtra("islist", true);
                    intent.putExtra(SysConfig.CAR_SN, item.getCarSn());
//                    if (item.hasPassedMsg() && item.getPassedMsg() != null && !item.getPassedMsg().equals(""))
//                    {
//                        intent.putExtra("passedMsg", item.getPassedMsg());
//                    }
//                    intent.putExtra("index", position);
                    startActivityForResult(intent, 165);
                }
            });
            return convertView;
        }
    }

    //秒变成时间格式
    public String formatSecond(int second, SimpleDateFormat format) {
        long time = second * 1000L;
        String dateString = format.format(time);
        return dateString;
    }

}
