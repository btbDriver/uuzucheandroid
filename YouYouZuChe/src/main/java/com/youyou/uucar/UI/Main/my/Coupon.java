package com.youyou.uucar.UI.Main.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class Coupon extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public String tag = "Coupon";
    protected LoadingFooter mLoadingFooter;
    List<CouponItem> data = new ArrayList<CouponItem>();
    ListView listview;
    Myadapter adapter;
    EditText edit;
    ImageView clear;
    TextView verifi;
    boolean isRefreshFromTop = true;
    int page = 1;
    SwipeRefreshLayout mSwiperefreshlayout;
    @InjectView(R.id.progressFrame)
    UUProgressFramelayout progressFramelayout;
    @InjectView(R.id.root)
    RelativeLayout root;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.coupon);
        ButterKnife.inject(this);
        intView();
        initListen();
        if (adapter == null) {
            adapter = new Myadapter();
        }
        listview.setAdapter(adapter);
        mSwiperefreshlayout.setRefreshing(true);
        onRefresh();
    }

    protected void intView() {
        edit = (EditText) findViewById(R.id.coupon_pass);
        clear = (ImageView) findViewById(R.id.clear);
        clear.setVisibility(View.GONE);
        verifi = (TextView) findViewById(R.id.verifi_coupon);
        listview = (ListView) findViewById(R.id.list);
        mSwiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);
        mSwiperefreshlayout.setOnRefreshListener(this);
        mLoadingFooter = new LoadingFooter(this);
        listview.addFooterView(mLoadingFooter.getView());
        mLoadingFooter.getView().setOnClickListener(footerClick);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                super.onScroll(view, firstVisibleItem, visibleItemCount,
//                        totalItemCount);
                if (listview.getFooterViewsCount() != 0) {

                    if (firstVisibleItem + visibleItemCount >= totalItemCount
                            && totalItemCount != 0
                            && totalItemCount != listview.getHeaderViewsCount()
                            + listview.getFooterViewsCount() && adapter
                            .getCount() > 0) {
                        if (!isLoadMoring) {
                            isLoadMoring = true;
                            page += 1;
                            onRefresh();
                        }
                    }
                }
            }
        });
    }

    public View.OnClickListener footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLoadMoring = true;
            page += 1;
            onRefresh();
        }
    };

    @OnClick(R.id.get_friend)
    public void getFriendClick() {
        startActivity(new Intent(context, GetFriend.class));
    }

    protected void initListen() {

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
            }
        });
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        verifi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText().toString().trim().length() > 0) {
                    Config.showProgressDialog(Coupon.this, false, null);
                    UserInterface.ActivateCouponCode.Request.Builder request = UserInterface.ActivateCouponCode.Request.newBuilder();
                    request.setCouponCode(edit.getText().toString().trim());
                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ActivateCouponCode_VALUE);
                    networkTask.setBusiData(request.build().toByteArray());
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                        @Override
                        public void onSuccessResponse(UUResponseData response) {
                            if (response.getRet() == 0) {
                                try {
                                    UserInterface.ActivateCouponCode.Response data = UserInterface.ActivateCouponCode.Response.parseFrom(response.getBusiData());

                                    showToast(response.getResponseCommonMsg().getMsg());
                                    if (data.getRet() == 0) {
                                        edit.setText("");
                                    } else {
                                        edit.setSelectAllOnFocus(true);
                                        edit.selectAll();
                                    }
                                    page = 1;
                                    onRefresh();
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                edit.setSelectAllOnFocus(true);
                                edit.selectAll();
                            }
                        }

                        @Override
                        public void onError(VolleyError errorResponse) {
                            Config.showFiledToast(context);

                        }

                        @Override
                        public void networkFinish() {
                            Config.dismissProgress();
                        }
                    });
                } else {
                    Toast.makeText(context, "请输入优惠券 ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mLoadingFooter.getView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoadMoring) {
                    isLoadMoring = true;
//                            page += 1;
//                            onMore();
                    page++;
                    onRefresh();
                }
            }
        });

    }

    boolean isLoadMoring = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.coupon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        } else if (item.getItemId() == R.id.help) {
            Intent intent = new Intent();
            intent.putExtra("url", ServerMutualConfig.coupon);
            intent.putExtra("title", "优惠券规则");
            intent.setClass(Coupon.this, URLWebView.class);
            startActivity(intent);
        }
        return true;
    }


    @InjectView(R.id.nodata_root)
    RelativeLayout noDataRoot;

    @Override
    public void onRefresh() {

        this.page = 1;
        onRefresh1();
    }

    public void onRefresh1() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        UserInterface.QueryCouponCodes.Request.Builder request = UserInterface.QueryCouponCodes.Request.newBuilder();
        UuCommon.PageNoRequest.Builder pageNoRequest = UuCommon.PageNoRequest.newBuilder();
        pageNoRequest.setPageNo(page);
        request.setPage(pageNoRequest);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryCouponCodes_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData response) {
                Config.showToast(context, response.getToastMsg());
                if (response.getRet() == 0) {
                    try {
                        UserInterface.QueryCouponCodes.Response dataResponse = UserInterface.QueryCouponCodes.Response.parseFrom(response.getBusiData());
                        if (dataResponse.getRet() == 0) {
                            if (page == 1) {
                                data.clear();
                            }
                            int couponsCount = dataResponse.getCouponsCount();
                            if (couponsCount > 0) {
                                List<UserCommon.Coupon> couponsList = dataResponse.getCouponsList();
                                for (UserCommon.Coupon coupon : couponsList) {
                                    CouponItem item = new CouponItem();
                                    item.fromProtoBuf(coupon);
                                    data.add(item);
                                }
                            }
                            if (couponsCount == 0) {
                                listview.removeFooterView(mLoadingFooter.getView());
                                if (page == 1) {
                                    root.setVisibility(View.GONE);
                                    noDataRoot.setVisibility(View.VISIBLE);
                                }
                                progressFramelayout.makeProgreeDismiss();
                            } else {
                                root.setVisibility(View.VISIBLE);
                                noDataRoot.setVisibility(View.GONE);
                                if (dataResponse.getPageResult().getHasMore()) {
//                                    if(listview.getFooterViewsCount()>0)
//                                    {
//                                        listview.removeFooterView(mLoadingFooter.getView());
//                                    }
                                    if (listview.getFooterViewsCount() == 0) {
                                        listview.addFooterView(mLoadingFooter.getView());
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            progressFramelayout.makeProgreeDismiss();
                        } else {
                            progressFramelayout.makeProgreeNoData();
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                } else {

                    progressFramelayout.makeProgreeNoData();
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                progressFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                mLoadingFooter.setState(LoadingFooter.State.Idle, 400);
                mSwiperefreshlayout.setRefreshing(false);
                isLoadMoring = false;
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

//        AbRequestParams params = new AbRequestParams();
//        params.put("sid", Config.getUser(context).sid);
//        page = 1;
//        params.put("page", page + "");
//        ab.post(ServerMutualConfig.getCoupon, params, new MyAbStringHttpResponseListener(context) {
//            @Override
//            public void onSuccess(int statusCode, String content) {
//                super.onSuccess(statusCode, content);
//                try {
//                    JSONObject json = new JSONObject(content);
//                    if (json.getString("status").equals("1")) {
//                        data.clear();
//                        for (int i = 0; i < json.getJSONArray("content").length(); i++) {
//                            CouponItem item = new CouponItem();
//                            item.fromJson(json.getJSONArray("content").getJSONObject(i));
//                            data.add(item);
//                        }
//
//                        if (json.getJSONArray("content").length() == 0) {
//                            listview.removeFooterView(mLoadingFooter.getView());
//                        } else {
//                            if (listview.getFooterViewsCount() == 0) {
//                                listview.addFooterView(mLoadingFooter.getView());
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                Config.dismissProgress();
//                if (mPullToRefreshLayout.isRefreshing()) {
////                    mPullToRefreshLayout.setRefreshComplete();
//
//
//                    mPullToRefreshLayout.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPullToRefreshLayout
//                                    .setRefreshComplete();
//                        }
//                    }, 400);
//                }
//                mLoadingFooter.setState(LoadingFooter.State.TheEnd);
//                mLoadingFooter.setState(LoadingFooter.State.Idle, 1000);
//            }
//
//            @Override
//            public void onFailure(int statusCode, String content, Throwable error) {
//
//                super.onFailure(statusCode, content, error);
//                Config.showFiledToast(context);
//            }
//        });
    }

    public class CouponItem {
        public String name = "";
        //        public String desc     = "";
        public boolean isdouble = false;
        public String amount = "";
        public String time = "";
        public String isUse = "";
        public String desc = "";
        public UserCommon.CouponUseState state;

        public void fromProtoBuf(UserCommon.Coupon json) {
            name = json.getCouponName();
            desc = json.getDescription();
            isdouble = json.getIsMulti();
            amount = ((int) json.getAmount()) + "";
            long startTime = json.getValidStart() * 1000L;
            long endTime = json.getValidEnd() * 1000L;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            String start = formatter.format(startTime);
            String end = formatter.format(endTime);
            time = start + "-" + end + "有效";
            isUse = json.getState() + "";
            if (json.hasCouponUseState()) {
                state = json.getCouponUseState();
            }
        }
    }

    public class Myadapter extends BaseAdapter {
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
            convertView = getLayoutInflater().inflate(R.layout.coupon_item, null);
            TextView price = (TextView) convertView.findViewById(R.id.price);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView used = (TextView) convertView.findViewById(R.id.used);
            RelativeLayout priceRoot = (RelativeLayout) convertView.findViewById(R.id.price_root);
            RelativeLayout rightRoot = (RelativeLayout) convertView.findViewById(R.id.right_root);
            CouponItem item = data.get(position);
            price.setText(item.amount);
            name.setText(item.name);
            time.setText(item.time);
            desc.setText(item.desc);
            ImageView isdouble = (ImageView) convertView.findViewById(R.id.isdouble);

            if (item.isdouble) {
                isdouble.setVisibility(View.VISIBLE);
            } else {
                isdouble.setVisibility(View.GONE);
            }
            if (item.state != null && item.state.equals(UserCommon.CouponUseState.NOT_USED)) {
                used.setText("未使用");
                priceRoot.setBackgroundResource(R.drawable.coupon_event_left_bg);
                rightRoot.setBackgroundResource(R.drawable.coupon_event_left_bg);
                used.setTextColor(Color.parseColor("#00a0e3"));
                used.setBackgroundResource(R.drawable.coupon_item_use_bg);
            } else if (item.state != null && item.state.equals(UserCommon.CouponUseState.USED)) {
                used.setText("已使用");
                used.setTextColor(Color.parseColor("#b7b7b7"));
                priceRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                rightRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                used.setBackgroundResource(R.drawable.coupon_item_used_bg);
            } else if (item.state != null && item.state.equals(UserCommon.CouponUseState.EXPIRED)) {
                used.setText("已过期");
                used.setTextColor(Color.parseColor("#b7b7b7"));
                priceRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                rightRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                used.setBackgroundResource(R.drawable.coupon_item_used_bg);
            } else if (item.state != null && item.state.equals(UserCommon.CouponUseState.INVALID)) {
                used.setText("已失效");
                used.setTextColor(Color.parseColor("#b7b7b7"));
                priceRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                rightRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                used.setBackgroundResource(R.drawable.coupon_item_used_bg);
            } else if (item.state != null && item.state.equals(UserCommon.CouponUseState.NOT_START)) {
                used.setText("未开始");
                used.setTextColor(Color.parseColor("#b7b7b7"));
                priceRoot.setBackgroundResource(R.drawable.coupon_event_left_bg);
                rightRoot.setBackgroundResource(R.drawable.coupon_event_left_bg);
                used.setTextColor(Color.parseColor("#00a0e3"));
                used.setBackgroundResource(R.drawable.coupon_item_use_bg);
            } else {
                used.setText("");
                used.setTextColor(Color.parseColor("#b7b7b7"));
                priceRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                rightRoot.setBackgroundResource(R.drawable.coupon_used_left_bg);
                used.setBackgroundResource(R.drawable.coupon_item_used_bg);
            }
            return convertView;
        }
    }
}
