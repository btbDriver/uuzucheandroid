package com.youyou.uucar.UI.Main.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.DB.Model.CarSimpleInfoModel;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.my.GetFriend;
import com.youyou.uucar.UI.Owner.addcar.AddCarBrandActivity;
import com.youyou.uucar.UI.Owner.addcar.CarInfoSimpleActivity;
import com.youyou.uucar.UI.Owner.addcar.ReleaseCarActivity;
import com.youyou.uucar.UI.Owner.calculate.CalculatePriceActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.empty.EmptyOnScrollListener;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;
import com.youyou.uucar.Utils.socket.SocketCommunication;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CarManagerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ObserverListener {

    public static final String TAG = CarManagerFragment.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "CarManagerFragment";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();
//    protected PullToRefreshLayout mPullToRefreshLayout;

    SwipeRefreshLayout mSwiperefreshlayout;
    List<CarSimpleInfoModel> listData = new ArrayList<CarSimpleInfoModel>();
    View view;
    @InjectView(R.id.listview)
    ListView mListview;
    CarManagerAdapter adapter;
    private Activity context;
    private String sId;
    private User user;
    private LoadingFooter loadingFooter;
    private List historyList;
    UuCommon.PageNoRequest.Builder pageBuilder = UuCommon.PageNoRequest.newBuilder();
    int page = 0;

    @OnClick(R.id.more)
    public void moreClick() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("拨打客服电话");
        builder.setMessage(Config.kefuphone);
        builder.setNegativeButton("拨打", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputStr = Config.kefuphone;
                if (inputStr.trim().length() != 0) {
                    MobclickAgent.onEvent(context, "ContactService");
                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                    startActivity(phoneIntent);
                }
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObserverManager.addObserver(SocketCommunication.SCENE_OWNER_CAR_MANAGER, this);
    }

    public void addCar() {
        if (Config.isGuest(getActivity())) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("goto", LoginActivity.OWNER_REGISTER);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), AddCarBrandActivity.class);
            getActivity().startActivityForResult(intent, SysConfig.ADD_CAR);
        }
    }

    View addCarFooter;
    @InjectView(R.id.ad_root)
    RelativeLayout adRoot;
    @InjectView(R.id.list_root)
    LinearLayout listRoot;
    UuCommon.PageNoResult pageNoResult;

    @OnClick(R.id.addcar)
    public void addCarClick() {
        addCar();
    }

    @OnClick(R.id.get_friend)
    public void getFriendClick() {
        if (Config.isGuest(getActivity())) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("goto", LoginActivity.GETFRIEND);
            startActivity(intent);
        } else {
            startActivity(new Intent(context, GetFriend.class));
        }
    }

    @OnClick(R.id.calculate)
    public void calculatePriceClick() {
        startActivity(new Intent(context, CalculatePriceActivity.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.owner_car_manager_fragment, container, false);
        context = getActivity();
        ButterKnife.inject(this, view);
        adapter = new CarManagerAdapter();
//        ScaleInAnimationAdapter animationAdapter = new
//                ScaleInAnimationAdapter(adapter);
//        animationAdapter.setAbsListView(mListview);
//        animationAdapter.setAnimationDelayMillis(300);

        mSwiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.c1,
                R.color.c1,
                R.color.c1);
//        ActionBarPullToRefresh.from(getActivity())
//                .allChildrenArePullable()
//                .listener(this)
//                .setup(mPullToRefreshLayout);
        mSwiperefreshlayout.setOnRefreshListener(this);
        loadingFooter = new LoadingFooter(context);
        addCarFooter = inflater.inflate(R.layout.car_manager_add_car_footer, null);
        addCarFooter.findViewById(R.id.add_car).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });

        mListview.addFooterView(addCarFooter);
        mListview.addFooterView(loadingFooter.getView());
        loadingFooter.getView().setOnClickListener(footerClick);
        mListview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mListview.setOnScrollListener(new EmptyOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (loadingFooter.getState() == LoadingFooter.State.Loading
                        || loadingFooter.getState() == LoadingFooter.State
                        .TheEnd) {
                    return;
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != mListview.getHeaderViewsCount()
                        + mListview.getFooterViewsCount() && adapter
                        .getCount() > 0) {
                    if (pageNoResult != null && pageNoResult.getHasMore()) {
                        page++;
                        pageBuilder.setPageNo(page);
                        getListData();
                    }
                }
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarSimpleInfoModel model = listData.get(position);
                if (model != null) {
                    Intent intent = new Intent();
                    if (model.status != null) {
                        if (model.status.equals(CarCommon.CarStatus.WAIT_PUBLISHING)) {
//                        str ="等待发布"; 不跳转
                            Config.showToast(context, context.getString(R.string.please_wait));
                            return;
                        } else if (model.status.equals(CarCommon.CarStatus.CHECK_NOT_PASSED)) {
//                        str ="审核不通过";  跳转到添加车辆步骤首页 ---有状态提示未通过步骤
                            intent.setClass(context, ReleaseCarActivity.class);
                        } else if (model.status.equals(CarCommon.CarStatus.SUSPEND_RENT)) {
                            intent.setClass(context, CarInfoSimpleActivity.class);
//                        str ="暂不出租";  跳转到编辑车辆页面
                        } else if (model.status.equals(CarCommon.CarStatus.CAN_RENT)) {
                            intent.setClass(context, CarInfoSimpleActivity.class);
//                        str ="出租中"; 点击跳转到 车辆订单 页面
                        } else if (model.status.equals(CarCommon.CarStatus.WAIT_COMPLETE_EDIT)) {
//                        str ="待编辑"; 填写了部分信息，还没发不到额车辆，跳转到添加车辆步骤首页--有状态提示未通过步骤
                            intent.setClass(context, ReleaseCarActivity.class);
                            intent.putExtra(SysConfig.PLATE_NUMBER, model.plateNumber);
                        } else if (model.status.equals(CarCommon.CarStatus.CUSTOMER_SERVICE_PUBLISHING)) {
//                        str ="客服正在帮你发布"; 不跳转，提示耐心等待，客服会尽快帮您联系发布车辆
                            Config.showToast(context, context.getString(R.string.please_wait));
                            return;
                        }
                        intent.putExtra(SysConfig.S_ID, user.sid);
                        intent.putExtra(SysConfig.CAR_SN, model.carSn);
                        context.startActivity(intent);
                    }
                }
            }
        });
//        getListData();
        ObserverManager.addObserver(ObserverManager.CARMANAGERFRAGMENT, refushListener);
        return view;
    }

    public ObserverListener refushListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
            MainActivityTab.instance.owner.needRefush = false;

            MLog.e(TAG, "CarManager__refushListener__");
            page = 1;
            pageBuilder.setPageNo(page);
            getListData();
        }
    };

    public View.OnClickListener footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            page += 1;
            pageBuilder.setPageNo(page);
            getListData();
        }
    };

    public void getListData() {
        mSwiperefreshlayout.setRefreshing(true);
        if (page == 1) {

        } else {
            loadingFooter.setState(LoadingFooter.State.Loading);
        }
        MLog.e(TAG, "getListData");
        CarInterface.GetCarList.Request.Builder request = CarInterface.GetCarList.Request.newBuilder();
//        request.setPage(pageBuilder);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.GetCarList_VALUE);
        request.setPage(pageBuilder);
        networkTask.setTag("GetCarList");
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData protoBuf) {
                Config.showToast(context, protoBuf.getToastMsg());
                if (protoBuf.getRet() == 0) {
                    try {
                        CarInterface.GetCarList.Response response = CarInterface.GetCarList.Response.parseFrom(protoBuf.getBusiData());
                        if (response.getRet() == 0) {
                            if (page == 1) {
                                listData.clear();
                                ObserverManager.getObserver(ObserverManager.MAINTABNUM).observer("", "");
                            }
                            List<CarCommon.CarBriefInfo> list = response.getCarListList();
                            for (int i = 0; i < list.size(); i++) {
                                CarCommon.CarBriefInfo map = list.get(i);
                                CarSimpleInfoModel model = new CarSimpleInfoModel();
                                model.status = map.getCarStatus();
                                model.carName = map.getCarModel();
                                model.carType = map.getBrand();
                                model.oneDayPrice = (int) map.getPricePerDay();
                                model.carSn = map.getCarId();
                                model.headImage = map.getThumbImg();
                                model.plateNumber = map.getLicensePlate();
                                listData.add(model);
                            }
                            if (page == 1 && list.isEmpty()) {
                                adRoot.setVisibility(View.VISIBLE);
                                listRoot.setVisibility(View.GONE);
                            } else {
                                adRoot.setVisibility(View.GONE);
                                listRoot.setVisibility(View.VISIBLE);
                            }
                            pageNoResult = response.getPageResult();
                            if (!pageNoResult.getHasMore()) {
                                if (mListview.getFooterViewsCount() == 2) {
                                    mListview.removeFooterView(loadingFooter.getView());
                                }
                            } else {
                                if (mListview.getFooterViewsCount() == 1) {
                                    mListview.addFooterView(loadingFooter.getView());
                                }

                            }
                            adapter.notifyDataSetChanged();


                        } else {

                            adRoot.setVisibility(View.VISIBLE);
                            listRoot.setVisibility(View.GONE);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                } else {

                    adRoot.setVisibility(View.VISIBLE);
                    listRoot.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {

                Config.showFiledToast(context);
                adRoot.setVisibility(View.VISIBLE);
                listRoot.setVisibility(View.GONE);
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
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
                loadingFooter.setState(LoadingFooter.State.TheEnd);
                loadingFooter.setState(LoadingFooter.State.Idle, 400);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Config.isNetworkConnected(context)) {
            if (Config.getUser(context).sid.equals("") || Integer.parseInt(Config.getUser(context).sid) == 0) {
                //广告页
                adRoot.setVisibility(View.VISIBLE);
                listRoot.setVisibility(View.GONE);
            } else {
                user = Config.getUser(context);
//            if (MainActivityTab.instance.owner.needRefush) {
//                MainActivityTab.instance.owner.needRefush = false;
                page = 1;
                pageBuilder.setPageNo(page);
                if (mListview.getFooterViewsCount() == 0) {
                    mListview.addFooterView(loadingFooter.getView());
                }
                mListview.setSelection(0);
                getListData();
//            }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void observer(String from, Object obj) {
        if (from.equals("push")) {
            page = 1;
//            pageBuilder.setDirection(0);
            pageBuilder.setPageNo(page);
            if (mListview.getFooterViewsCount() == 0) {
                mListview.addFooterView(loadingFooter.getView());
            }
            getListData();
        }
    }

    @Override
    public void onRefresh() {

        page = 1;
//        pageBuilder.setDirection(0);
        pageBuilder.setPageNo(page);
        if (mListview.getFooterViewsCount() == 0) {
            mListview.addFooterView(loadingFooter.getView());
        }

        getListData();
    }

    static class ViewHolder {
        @InjectView(R.id.car_type)
        TextView mCarType;
        @InjectView(R.id.plate_number)
        TextView mPlateNumber;
        @InjectView(R.id.price)
        TextView mPrice;
        @InjectView(R.id.state)
        TextView mState;
        @InjectView(R.id.image)
        ImageView mImage;
        @InjectView(R.id.head_image)
        BaseNetworkImageView mHeadImage;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwiperefreshlayout.setRefreshing(false);
        loadingFooter.setState(LoadingFooter.State.TheEnd);
        loadingFooter.setState(LoadingFooter.State.Idle, 1000);
    }

    class CarManagerAdapter extends BaseAdapter {
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = CarManagerFragment.this.getActivity().getLayoutInflater().
                        inflate(R.layout.car_manger_listitem, parent, false);
            }
            holder = getHolder(convertView);
            CarSimpleInfoModel model = listData.get(position);
            if (model != null) {
                String carTypeStr = model.carType == null ? "" : model.carType;
                String carNameStr = model.carName == null ? "" : model.carName;
                holder.mCarType.setText(carTypeStr + carNameStr);
                holder.mPlateNumber.setText(model.plateNumber == null ? "" : model.plateNumber);
                holder.mPrice.setText(model.oneDayPrice == 0 ? 0 + "" : ((int) model.oneDayPrice) + "");
                holder.mImage.setVisibility(View.VISIBLE);
                if (model.status != null) {
                    if (model.status.equals(CarCommon.CarStatus.WAIT_PUBLISHING)) {
                        holder.mImage.setVisibility(View.INVISIBLE);
                        holder.mState.setText("审核中");
                    } else if (model.status.equals(CarCommon.CarStatus.CUSTOMER_SERVICE_PUBLISHING)) {
                        holder.mImage.setVisibility(View.INVISIBLE);
                        holder.mState.setText("客服发布中");
                    } else if (model.status.equals(CarCommon.CarStatus.SUSPEND_RENT)) {
                        holder.mState.setText("暂不出租");
                    } else if (model.status.equals(CarCommon.CarStatus.CAN_RENT)) {
                        holder.mState.setText("可出租");
                    } else if (model.status.equals(CarCommon.CarStatus.WAIT_COMPLETE_EDIT)) {
                        holder.mState.setText("待编辑");
                    } else if (model.status.equals(CarCommon.CarStatus.CHECK_NOT_PASSED)) {
                        holder.mState.setText("审核未通过");
                    } else {
                        holder.mState.setText("未知状态");
                    }
                }
//                holder.mState.setText(model.status == null ? "" : model.status);
                UUAppCar.getInstance().display(model.headImage, holder.mHeadImage, R.drawable.list_car_img_def);
            }
            return convertView;
        }


        private ViewHolder getHolder(final View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            return holder;
        }
    }
}
