package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.CircleImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.MyListView;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by taurusxi on 14-7-18.
 */
public class OwnerInfoActivity extends Activity {

    @InjectView(R.id.image)
    CircleImageView mImage;
    @InjectView(R.id.name)
    TextView mName;
    @InjectView(R.id.star)
    RatingBar mStar;
    @InjectView(R.id.time)
    TextView mTime;
    @InjectView(R.id.share)
    TextView mShare;
    @InjectView(R.id.empty_sub)
    TextView mEmptySub;
    @InjectView(R.id.listview)
    MyListView mListview;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private int page = 1;

    OwnerReviewAdapter adapter;
    LoadingFooter loadingFooter;
    private Context context;
    private String carSn;
    private int userId;
    private List<UserInterface.QueryCarOwnerInfo.RenterComment> listData = new ArrayList<UserInterface.QueryCarOwnerInfo.RenterComment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.owner_info_activity);
        ButterKnife.inject(this);
        adapter = new OwnerReviewAdapter();
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
        userId = getIntent().getIntExtra(SysConfig.S_ID, -1);
        loadingFooter = new LoadingFooter(context);
//        View headerTv = this.getLayoutInflater().inflate(R.layout.renter_title, null, false);
//        mListview.addHeaderView(headerTv);
        mListview.addFooterView(loadingFooter.getView());
        mListview.setAdapter(adapter);
        loadingFooter.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                getData();
            }
        });
        getData();
        initNoteDataRefush();


    }

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
        UserInterface.QueryCarOwnerInfo.Request.Builder request = UserInterface.QueryCarOwnerInfo.Request.newBuilder();
        request.setUserId(userId);
        request.setCarId(carSn);
        UuCommon.PageNoRequest.Builder pageBuilder = UuCommon.PageNoRequest.newBuilder();
        pageBuilder.setPageNo(page);
        request.setPage(pageBuilder);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryCarOwnerInfo_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        UserInterface.QueryCarOwnerInfo.Response response = UserInterface.QueryCarOwnerInfo.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            UserCommon.UserBriefInfo carOwner = response.getCarOwner();
                            if (carOwner.hasAvatar()) {
                                UUAppCar.getInstance().display(carOwner.getAvatar(), mImage, R.drawable.header_neibu);
                            }
                            int gender = carOwner.getGender();
                            if (gender == 2) {
                                mName.setText(carOwner.getLastName() + "女士");
                            } else {
                                mName.setText(carOwner.getLastName() + "先生");
                            }
                            mStar.setRating(carOwner.getStars());
                            mTime.setText(response.getAverageResponseTimes() + "");
                            mShare.setText(response.getAcceptOrderPercent() + "");
                            if (page == 1) {
                                listData.clear();
                            }
                            int commentListCount = response.getCommentListCount();
                            if (commentListCount > 0) {
                                List<UserInterface.QueryCarOwnerInfo.RenterComment> commentListList = response.getCommentListList();
                                listData.addAll(commentListList);
                            }
                            UuCommon.PageNoResult pageResult = response.getPageResult();
                            if (pageResult.hasHasMore()) {
                                boolean hasMore = pageResult.getHasMore();
                                if (hasMore) {
                                    if (mListview.getFooterViewsCount() == 0) {
                                        mListview.addFooterView(loadingFooter.getView());
                                        loadingFooter.setState(LoadingFooter.State.Idle, 400);
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

//                            int size = 20 * page;
//                            if (listData.size() < size - 1) {
//                                mListview.removeFooterView(loadingFooter.getView());
//                            } else {
//                                loadingFooter.getView().setVisibility(View.VISIBLE);
//                                loadingFooter.setState(LoadingFooter.State.Idle, 400);
//                            }
                            adapter.setCount(listData);
                            if (listData.size() == 0) {
                                mEmptySub.setVisibility(View.VISIBLE);
                                mListview.setVisibility(View.GONE);
                            } else {
                                mEmptySub.setVisibility(View.GONE);
                                mListview.setVisibility(View.VISIBLE);
                            }
                            mAllFramelayout.makeProgreeDismiss();
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
        private List<UserInterface.QueryCarOwnerInfo.RenterComment> list;

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        public void setCount(List<UserInterface.QueryCarOwnerInfo.RenterComment> lisData) {
            list = lisData;
            notifyDataSetChanged();
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
                convertView = OwnerInfoActivity.this.getLayoutInflater().inflate(R.layout.renter_review_item, parent, false);
            }
            ButterknifeViewHolder holder = getViewHodel(convertView);
            UserInterface.QueryCarOwnerInfo.RenterComment review = list.get(position);
            UUAppCar.getInstance().display(review.getAvatar(), holder.mHead, R.drawable.header_neibu);
            holder.mName.setText(review.getName());
            holder.mTime.setText(Config.getFormatTime(review.getOccurTime()));
            holder.mStar.setRating(review.getStars());
            holder.mText.setText(review.getContent());
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
