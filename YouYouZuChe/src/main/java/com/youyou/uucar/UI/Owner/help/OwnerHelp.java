package com.youyou.uucar.UI.Owner.help;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.fragment.BasicFragment;


/**
 * Created by 16515_000 on 2015/2/5.
 */
public class OwnerHelp extends BasicFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public void initLoader() {

    }

    @Override
    public void resume() {

    }


    public static OwnerHelp newInstance() {
        OwnerHelp instance = new OwnerHelp();
        return instance;
    }

    BaseActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (BaseActivity) getActivity();
        WebView view = new WebView(context);
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.loadUrl(ServerMutualConfig.owner_help);
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