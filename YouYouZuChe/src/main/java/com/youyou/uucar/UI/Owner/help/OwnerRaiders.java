package com.youyou.uucar.UI.Owner.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ListView;

import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.fragment.BasicFragment;
import com.youyou.uucar.Utils.View.LoadingFooter;
import com.youyou.uucar.Utils.empty.EmptyOnScrollListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import butterknife.ButterKnife;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by 16515_000 on 2015/2/5.
 */
public class OwnerRaiders extends BasicFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public void initLoader() {

    }

    @Override
    public void resume() {

    }


    public static OwnerRaiders newInstance() {
        OwnerRaiders instance = new OwnerRaiders();
        return instance;
    }

    BaseActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (BaseActivity) getActivity();
        WebView view = new WebView(context);
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.loadUrl(ServerMutualConfig.owner_ycbz);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        return view;
    }

    @Override
    public void onRefresh() {

    }
}
