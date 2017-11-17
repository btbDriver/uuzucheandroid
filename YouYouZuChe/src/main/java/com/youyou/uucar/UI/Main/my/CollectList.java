package com.youyou.uucar.UI.Main.my;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
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
import com.youyou.uucar.Utils.Support.NumbUtils;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectList extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = CollectList.class.getSimpleName();
    public String s_address;
    public double lat, lng;

    SwipeRefreshLayout mSwiperefreshlayout;
    protected LoadingFooter mLoadingFooter;
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
    int page = 1;
    UuCommon.PageNoRequest.Builder pageBuilder = UuCommon.PageNoRequest.newBuilder();
    Handler handler = new Handler();
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
                mSwiperefreshlayout.setRefreshing(true);
                mAllFramelayout.noDataReloading();
                pageBuilder.setPageNo(1);
                loadData();
            }
        });
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        setContentView(R.layout.fragment_find_car_list);
        ButterKnife.inject(this);
        initNoteDataRefush();
        mSwiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
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
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
        mSwiperefreshlayout.setRefreshing(true);
        Config.getCoordinates(context, new LocationListener() {
            @Override
            public void locationSuccess(double lat, double lng, String addr) {
                CollectList.this.lat = lat;
                CollectList.this.lng = lng;
                pageBuilder.setPageNo(1);
                loadData();
            }
        });
        TestinAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TestinAgent.onStop(this);
    }


    public void loadData() {
        if (page == 1) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
        }
        UserInterface.CollectCarList.Request.Builder builder = UserInterface.CollectCarList.Request.newBuilder();
        builder.setPage(pageBuilder);
        UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
        position.setLat(lat);
        position.setLng(lng);
        builder.setUserPosition(position);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.CollectCarList_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("CollectCarList");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {

                if (responseData.getRet() == 0) {
                    try {
                        UserInterface.CollectCarList.Response response = UserInterface.CollectCarList.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            if (page == 1) {
                                datas.clear();
                            }
                            datas.addAll(response.getCarsList());
                            UuCommon.PageNoResult pageNoResult = response.getPageResult();
                            if (pageNoResult.getHasMore()) {
                                if (list.getFooterViewsCount() == 0) {
                                    list.addFooterView(mLoadingFooter.getView());
                                }
                            } else {
                                list.removeFooterView(mLoadingFooter.getView());
                            }
                            if (datas.isEmpty()) {
                                mAllFramelayout.makeProgreeNoData();
                                TextView text = (TextView) mAllFramelayout.findViewById(R.id.text);
                                text.setText("您还没有收藏的车辆");
                                TextView noDataRefush;
                                noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
                                noDataRefush.setText("先去找车");
                                noDataRefush.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            } else {
                                mAllFramelayout.makeProgreeDismiss();
                            }
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

            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
//                mAllFramelayout.makeProgreeDismiss();
//                if (mPullToRefreshLayout.isRefreshing())
//                {
//                    mPullToRefreshLayout.postDelayed(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            mPullToRefreshLayout
//                                    .setRefreshComplete();
//                        }
//                    }, 400);
//                }
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
        isLoadMoring = true;
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
            final CarCommon.CarBriefInfo item = datas.get(position);
            final RelativeLayout root = (RelativeLayout) convertView.findViewById(R.id.root);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OldCarInfoActivity.class);
                    if (item.hasPassedMsg() && item.getPassedMsg() != null && !item.getPassedMsg().equals("")) {
                        intent.putExtra("passedMsg", item.getPassedMsg());
                    }
                    intent.putExtra("islist", true);
                    intent.putExtra(SysConfig.CAR_SN, item.getCarId());
                    intent.putExtra("index", position);
                    startActivityForResult(intent, 165);

                }
            });
            BaseNetworkImageView img = (BaseNetworkImageView) convertView.findViewById(R.id.car_img);
            UUAppCar.getInstance().display(item.getThumbImg(), img, R.drawable.list_car_img_def);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OldCarInfoActivity.class);
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
