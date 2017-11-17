package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
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
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.Adapter.TextFragmentAdapter;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Common.BaseFragment;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Orderform.OwnerOrderInfoActivity;
import com.youyou.uucar.UI.Orderform.OwnerWishInfoActivity;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Owner.addcar.CarRealTimeStatusInfoActivity;
import com.youyou.uucar.UI.Owner.addcar.OwnerEvaluationRenterActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Network.listen.OnClickNetworkListener;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.Empty.EmptyOnPagerListener;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.View.PagerSlidingTabStrip;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.empty.EmptyOnScrollListener;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by 16515_000 on 2014/8/26.
 */
public class MyStrokeFragment extends BaseFragment {
    BaseActivity context;
    View view;
    public static final String TAG = MyStrokeFragment.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "MyStrokeFragment";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();

    @Override
    public void onPause() {
        super.onPause();
        finish.cancleAllRequest();
        current.cancleAllRequest();
        cancel.cancleAllRequest();

    }


    private TextFragmentAdapter adapter;
    MyStrokeCurrentFragment current;
    MyStrokeFinishFragment finish;
    MyStrokeCancelFragment cancel;
    @InjectView(R.id.vPager)
    ViewPager content;
    private PagerSlidingTabStrip tabs;
    int currentPage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (BaseActivity) getActivity();
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_mystroke_manager, null);
        ButterKnife.inject(this, view);
        adapter = new TextFragmentAdapter(getActivity().getSupportFragmentManager(), getActivity());
        current = MyStrokeCurrentFragment.newInstance();
        finish = MyStrokeFinishFragment.newInstance();
        cancel = MyStrokeCancelFragment.newInstance();
        current.setTitle("进行中");
        finish.setTitle("已完成");
        cancel.setTitle("已取消");
        adapter.addFragment(current);
        adapter.addFragment(finish);
        adapter.addFragment(cancel);
        adapter.notifyDataSetChanged();
        content.setAdapter(adapter);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.PagerSlidingTabStrip);
        tabs.setViewPager(content);
        tabs.setOnPageChangeListener(new EmptyOnPagerListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                switch (position) {
                    case 0:
                        finish.cancleAllRequest();
                        cancel.cancleAllRequest();
                        current.resume();
                        break;
                    case 1:
                        current.cancleAllRequest();
                        cancel.cancleAllRequest();
                        finish.resume();
                        break;
                    case 2:
                        current.cancleAllRequest();
                        finish.cancleAllRequest();
                        cancel.resume();
                        break;
                }
            }
        });
//        current.resume();
        ObserverManager.addObserver("gotoFinish", new ObserverListener() {
            @Override
            public void observer(String from, Object obj) {
                gotoFinish();
            }
        });
        return view;
    }

    /**
     * 第一次进入的时候不执行resume
     */
    boolean firstComein = true;

    public void gotoFinish() {
        currentPage = 1;
        content.setCurrentItem(currentPage);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isHidden()) {
        if (!firstComein && current != null && finish != null) {

            switch (currentPage) {
                case 0:
                    finish.cancleAllRequest();
                    cancel.cancleAllRequest();
                    current.resume();
                    break;
                case 1:
                    current.cancleAllRequest();
                    cancel.cancleAllRequest();
                    finish.resume();
                    break;
                case 2:
                    current.cancleAllRequest();
                    finish.cancleAllRequest();
                    cancel.resume();
                    break;
            }
        }
        firstComein = false;
//        }
    }

}
