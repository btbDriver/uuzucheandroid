package com.youyou.uucar.UI.Main.my.money;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.R.id;
import com.youyou.uucar.UI.Orderform.OwnerOrderInfoActivity;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MoneyDetail extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    public Activity context;
    public String tag = "MoneyDetail";
    public int page = 1;
    SwipeRefreshLayout mSwiperefreshlayout;
    protected LoadingFooter mLoadingFooter;
    String mode = "";
    ListView listview;
    TextView shouru, zhichu, yue, dongjie;
    ArrayList<Item> data = new ArrayList<Item>();
    RelativeLayout jiaoyi_root, dongjie_root;
    boolean isLoadMoring = false;
    MyAdapter adapter;
    TextView nodata;
    RelativeLayout root;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        mode = getIntent().getStringExtra("mode");
        setContentView(R.layout.money_detail);
        nodata = (TextView) findViewById(id.nodata);
        root = (RelativeLayout) findViewById(id.root);
        jiaoyi_root = (RelativeLayout) findViewById(id.jiaoyi_root);
        dongjie_root = (RelativeLayout) findViewById(id.dongjie_root);
        mSwiperefreshlayout = (SwipeRefreshLayout) findViewById(id.swiperefreshlayout);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);

//        ActionBarPullToRefresh.from(context)
//                .allChildrenArePullable()
//                .listener(this)
//                .setup(mPullToRefreshLayout);
        mSwiperefreshlayout.setOnRefreshListener(this);
        mLoadingFooter = new LoadingFooter(context);
        listview = (ListView) findViewById(id.list);
        listview.addFooterView(mLoadingFooter.getView());

        mLoadingFooter.getView().setOnClickListener(footerClick);
        listview.setDivider(new ColorDrawable(Color.parseColor("#00484949")));
        listview.setDividerHeight(1);
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        if (mode.equals("jiaoyi")) {
            setTitle("交易记录");
            shouru = (TextView) findViewById(id.shouru);
            zhichu = (TextView) findViewById(id.zhichu);
            yue = (TextView) findViewById(id.yue);
            jiaoyi_root.setVisibility(View.VISIBLE);
            dongjie_root.setVisibility(View.GONE);
        } else if (mode.equals("yushou")) {
            setTitle("预授权记录");
            dongjie = (TextView) findViewById(id.dongjie);
            dongjie_root.setVisibility(View.VISIBLE);
            jiaoyi_root.setVisibility(View.GONE);
        }
        Config.showProgressDialog(context, false, null);
        onRefresh();
    }

    public View.OnClickListener footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLoadMoring = true;
            page += 1;
            onMore();
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

    public void onRefresh1() {
        page = 1;
        Config.showProgressDialog(context, false, null);
        UserInterface.ExchangeHisto.Request.Builder request = UserInterface.ExchangeHisto.Request.newBuilder();

        UuCommon.PageNoRequest.Builder pageNoRequest = UuCommon.PageNoRequest.newBuilder();
        pageNoRequest.setPageNo(page);
        request.setPage(pageNoRequest);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ExchangeHisto_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData uuResponseData) {
                Config.showToast(context, uuResponseData.getToastMsg());
                if (uuResponseData.getRet() == 0) {
                    try {
                        UserInterface.ExchangeHisto.Response response = UserInterface.ExchangeHisto.Response.parseFrom(uuResponseData.getBusiData());
                        if (response.getRet() == 0) {
                            if (page == 1) {
                                data.clear();
                            }
                            int exchangeDetailsCount = response.getExchangeDetailsCount();
                            if (exchangeDetailsCount > 0) {
                                List<UserInterface.ExchangeHisto.ExchangeDetail> exchangeDetailsList = response.getExchangeDetailsList();

                                for (UserInterface.ExchangeHisto.ExchangeDetail exchangeDetail : exchangeDetailsList) {
                                    data.add(new Item(exchangeDetail));
                                }
                                if (listview.getFooterViewsCount() == 0) {
                                    listview.addFooterView(mLoadingFooter.getView());
                                }

                            } else if (exchangeDetailsCount == 0) {
                                listview.removeFooterView(mLoadingFooter.getView());
                            }
                            if (data.isEmpty()) {
                                root.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();

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
                Config.dismissProgress();
                mSwiperefreshlayout.setRefreshing(false);
                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
                isLoadMoring = false;
            }
        });
    }

    public void onMore() {
        page += 1;
        if (page > 1) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
        }

        UserInterface.ExchangeHisto.Request.Builder request = UserInterface.ExchangeHisto.Request.newBuilder();
        UuCommon.PageNoRequest.Builder pageNoRequest = UuCommon.PageNoRequest.newBuilder();
        pageNoRequest.setPageNo(page);
        request.setPage(pageNoRequest);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ExchangeHisto_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData uuResponseData) {
                Config.showToast(context, uuResponseData.getToastMsg());
                if (uuResponseData.getRet() == 0) {
                    try {
                        UserInterface.ExchangeHisto.Response response = UserInterface.ExchangeHisto.Response.parseFrom(uuResponseData.getBusiData());
                        if (response.getRet() == 0) {
                            int exchangeDetailsCount = response.getExchangeDetailsCount();
                            if (exchangeDetailsCount > 0) {
                                List<UserInterface.ExchangeHisto.ExchangeDetail> exchangeDetailsList = response.getExchangeDetailsList();
                                for (UserInterface.ExchangeHisto.ExchangeDetail exchangeDetail : exchangeDetailsList) {
                                    data.add(new Item(exchangeDetail));
                                }
                            }
                            if (exchangeDetailsCount == 0) {
                                listview.removeFooterView(mLoadingFooter.getView());

                            } else {
                                if (listview.getFooterViewsCount() == 0) {
                                    listview.addFooterView(mLoadingFooter.getView());
                                }
                            }
                            adapter.notifyDataSetChanged();

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
        onRefresh1();
    }

    public class Item {
        public String time = "";
        public String note = "";
        public String money = "";
        public String orderId = "";
        public int type;

        public Item(UserInterface.ExchangeHisto.ExchangeDetail exchangeDetail) {
            time = exchangeDetail.getOccurTime() + "";
            note = exchangeDetail.getNote();
            money = String.format("%.2f", exchangeDetail.getMoney());
            if (exchangeDetail.hasOrderId()) {
                orderId = exchangeDetail.getOrderId();
            }
            if (exchangeDetail.hasOrderType()) {
                type = exchangeDetail.getOrderType();
            }
        }
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
            convertView = context.getLayoutInflater().inflate(R.layout.money_detail_item, null);
            final Item item = data.get(position);
            TextView time = (TextView) convertView.findViewById(id.time);
            SimpleDateFormat tipformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long time_long = Long.parseLong(item.time) * 1000L;
            String dateString = tipformatter.format(time_long);
            time.setText(dateString);
            TextView note = (TextView) convertView.findViewById(id.content);
            String desc = "";


            desc = item.note;
            if (!item.orderId.equals("") && item.note.indexOf(item.orderId) != -1) {
//                desc = "订单" + item.orderId;
//                desc += item.orderId;
                SpannableStringBuilder style = new SpannableStringBuilder(desc);
                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c3)), desc.indexOf(item.orderId + ""), desc.indexOf(item.orderId + "") + ("" + item.orderId).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                note.setText(style);
            } else {
                note.setText(item.note);
            }
            TextView money = (TextView) convertView.findViewById(id.price);
            money.setText(item.money);
            if (!item.orderId.equals("")) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.type == 1)//租客
                        {
                            Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                            intent.putExtra(SysConfig.R_SN, item.orderId);
                            intent.putExtra("from", "money");
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(context, OwnerOrderInfoActivity.class);
                            intent.putExtra(SysConfig.R_SN, item.orderId);
                            intent.putExtra("from", "money");
                            startActivity(intent);
                        }

                    }
                });
            }
            return convertView;
        }
    }
}
