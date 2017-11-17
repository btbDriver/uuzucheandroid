package com.youyou.uucar.UI.Renter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.ImageView.CircleImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.MyListView;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by taurusxi on 14-7-18.
 */
public class RenterInfoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    //    @InjectView(R.id.image)
    CircleImageView mImage;
    //    @InjectView(R.id.name)
    TextView mName;
    //    @InjectView(R.id.star)
    RatingBar mStar;
    //    @InjectView(R.id.time)
//    @InjectView(R.id.share)
//    TextView mShare;
//    @InjectView(R.id.empty_sub)
//    TextView mEmptySub;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private int page = 1;

    OwnerReviewAdapter adapter;
    LoadingFooter loadingFooter;
    private int userId;
    private List<OrderFormInterface26.QueryRenterDetailInfo.ReviewItem> listData = new ArrayList<OrderFormInterface26.QueryRenterDetailInfo.ReviewItem>();

    SwipeRefreshLayout mSwiperefreshlayout;
    View headerView;
    TextView idnum;
    TextView times, age, drivier_age;
    BaseNetworkImageView renter_tag;

    View line;
    @InjectView(R.id.list)
    ListView mListview;
    RelativeLayout noCarRoot;
    RelativeLayout ownerTextRoot;//车主评价文案

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        setContentView(R.layout.renter_info_activity);
        ButterKnife.inject(this);
        adapter = new OwnerReviewAdapter();
        userId = getIntent().getIntExtra(SysConfig.S_ID, -1);
        mSwiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);
        mSwiperefreshlayout.setOnRefreshListener(this);

        loadingFooter = new LoadingFooter(context);
        mListview.addFooterView(loadingFooter.getView());
        loadingFooter.getView().setOnClickListener(footerClick);
        headerView = getLayoutInflater().inflate(R.layout.renter_info_header, null);
        line = headerView.findViewById(R.id.line);
        ownerTextRoot = (RelativeLayout) headerView.findViewById(R.id.owner_text_root);
        mListview.addHeaderView(headerView);
        mImage = (CircleImageView) headerView.findViewById(R.id.image);
        mName = (TextView) headerView.findViewById(R.id.name);
        mStar = (RatingBar) headerView.findViewById(R.id.star);
        idnum = (TextView) headerView.findViewById(R.id.idnum);
        times = (TextView) headerView.findViewById(R.id.times);
        drivier_age = (TextView) headerView.findViewById(R.id.drivier_age);
        noCarRoot = (RelativeLayout) headerView.findViewById(R.id.nocar_root);
//        renter_tag = (BaseNetworkImageView) headerView.findViewWithTag(R.id.renter_tag);
        age = (TextView) headerView.findViewById(R.id.age);
        mListview.setAdapter(adapter);
        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                super.onScroll(view, firstVisibleItem, visibleItemCount,
//                        totalItemCount);
                if (!listData.isEmpty() && mListview.getFooterViewsCount() != 0) {

                    if (firstVisibleItem + visibleItemCount >= totalItemCount
                            && totalItemCount != 0
                            && totalItemCount != mListview.getHeaderViewsCount()
                            + mListview.getFooterViewsCount() && adapter
                            .getCount() > 0) {
                        if (!isLoadMoring) {
                            isLoadMoring = true;
                            page++;
                            getData();
                        }
                    }
                }
            }
        });
        initNoteDataRefush();
        page = 1;
        getData();


    }

    boolean isLoadMoring = false;
    public View.OnClickListener footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isLoadMoring = true;
            page++;
            getData();
        }
    };

    public void initNoteDataRefush() {
        TextView noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.isNetworkConnected(context)) {
                    getData();
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    public void getData() {
        OrderFormInterface26.QueryRenterDetailInfo.Request.Builder request = OrderFormInterface26.QueryRenterDetailInfo.Request.newBuilder();
        request.setUserId(userId);
        if (page == 1) {
            request.setNeedRenterInfo(0);
        } else {
            request.setNeedRenterInfo(1);
        }
        UuCommon.PageNoRequest.Builder pageBuilder = UuCommon.PageNoRequest.newBuilder();
        pageBuilder.setPageNo(page);
        request.setPage(pageBuilder);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryRenterDetailInfo_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        showResponseCommonMsg(responseData.getResponseCommonMsg());
                        OrderFormInterface26.QueryRenterDetailInfo.Response response = OrderFormInterface26.QueryRenterDetailInfo.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            UserCommon.UserBriefInfo userInfo = response.getUserBriefInfo();
                            if (page == 1) {

                                if (userInfo.hasAvatar()) {
                                    UUAppCar.getInstance().display(userInfo.getAvatar(), mImage, R.drawable.header_neibu);
                                }
                                int gender = userInfo.getGender();
                                if (gender == 2) {
                                    mName.setText(userInfo.getLastName() + "女士");
                                } else {
                                    mName.setText(userInfo.getLastName() + "先生");
                                }
                                mStar.setRating(userInfo.getStars());
                                idnum.setText("身份证号 " + response.getIdSn());
                                if (response.getDrivingAge() < 12) {
                                    drivier_age.setText(response.getDrivingAge() + "月");
                                } else {
                                    drivier_age.setText(response.getDrivingAge() / 12 + "年");
                                }

                                age.setText(response.getAge() + "岁");
                                times.setText(userInfo.getRentCount() + "次");
                            }
//                            UUAppCar.getInstance().display(response.get);
                            if (page == 1) {
                                listData.clear();
                            }
                            int commentListCount = response.getReviewItemListCount();
                            if (commentListCount > 0) {
                                List<OrderFormInterface26.QueryRenterDetailInfo.ReviewItem> commentListList = response.getReviewItemListList();
                                listData.addAll(commentListList);
                            }
                            UuCommon.PageNoResult pageResult = response.getPageResult();
                            if (pageResult.hasHasMore()) {
                                boolean hasMore = pageResult.getHasMore();
                                if (hasMore) {
                                    if (mListview.getFooterViewsCount() == 0) {
                                        mListview.addFooterView(loadingFooter.getView());
//                                        loadingFooter.setState(LoadingFooter.State.Idle, 400);
                                    }
                                } else {
                                    if (mListview.getFooterViewsCount() == 1) {
                                        mListview.removeFooterView(loadingFooter.getView());
                                    }
                                }
                            } else {
                                if (mListview.getFooterViewsCount() == 1) {
                                    mListview.removeFooterView(loadingFooter.getView());
                                }

                            }
                            if (listData.isEmpty()) {
                                noCarRoot.setVisibility(View.VISIBLE);
                                ownerTextRoot.setVisibility(View.GONE);
                                line.setVisibility(View.GONE);
                            } else {
                                noCarRoot.setVisibility(View.GONE);
                                ownerTextRoot.setVisibility(View.VISIBLE);
                                line.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {
                mSwiperefreshlayout.setRefreshing(false);
                loadingFooter.setState(LoadingFooter.State.TheEnd);
                loadingFooter.setState(LoadingFooter.State.Idle, 1000);
                isLoadMoring = false;
                mAllFramelayout.makeProgreeDismiss();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {

        page = 1;
        getData();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'null'
     * for easy to all layout elements.
     *
     * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
     */
    static class ButterknifeViewHolder {
        @InjectView(R.id.head)
        CircleImageView mHead;
        @InjectView(R.id.name)
        TextView mName;
        @InjectView(R.id.time)
        TextView mTime;
        @InjectView(R.id.star)
        RatingBar mStar;
        @InjectView(R.id.text)
        TextView mText;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class OwnerReviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listData == null ? 0 : listData.size();
        }


        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = RenterInfoActivity.this.getLayoutInflater().inflate(R.layout.renter_review_item, parent, false);
            }
            ButterknifeViewHolder holder = getViewHodel(convertView);
            OrderFormInterface26.QueryRenterDetailInfo.ReviewItem review = listData.get(position);
            UUAppCar.getInstance().display(review.getOwnerInfo().getAvatar(), holder.mHead, R.drawable.header_neibu);


            int gender = review.getOwnerInfo().getGender();
            if (gender == 2) {
                holder.mName.setText(review.getOwnerInfo().getLastName() + "女士");
            } else {
                holder.mName.setText(review.getOwnerInfo().getLastName() + "先生");
            }
            holder.mTime.setText(Config.getFormatTimeYYYY_MM_DD(review.getOrderComment().getOccurTime()));
            holder.mStar.setRating(review.getOrderComment().getStars());
            holder.mText.setText(review.getOrderComment().getContent());
            return convertView;
        }

        private ButterknifeViewHolder getViewHodel(final View view) {
            ButterknifeViewHolder holder = (ButterknifeViewHolder) view.getTag();
            if (holder == null) {
                holder = new ButterknifeViewHolder(view);
                view.setTag(holder);
            }
            return holder;
        }
    }

}
