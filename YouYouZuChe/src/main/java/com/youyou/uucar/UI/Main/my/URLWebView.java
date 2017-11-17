package com.youyou.uucar.UI.Main.my;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URLWebView extends BaseActivity
{
    Activity              context;
    WebView               view;
    UUProgressFramelayout mAllFramelayout;

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        context = this;
//        Config.setActivityState(this);
//        WebView view = new WebView(this);
        setContentView(R.layout.url_web_view);
        mAllFramelayout = (UUProgressFramelayout) findViewById(R.id.all_framelayout);
        view = (WebView) findViewById(R.id.webview);
        initNoteDataRefush();
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return true;
            }
        });
        view.setWebViewClient(new webViewClient());
        String title = getIntent().getStringExtra("title");
        if (title == null)
        {
            setTitle("平台规则");
        }
        else
        {
            setTitle(title);
        }
        if (Config.isNetworkConnected(context))
        {
            view.loadUrl(getIntent().getStringExtra("url"));
            mAllFramelayout.makeProgreeDismiss();
        }
        else
        {

            mAllFramelayout.makeProgreeNoData();
        }
//        setContentView(view);
    }

    class webViewClient extends WebViewClient
    {

        //重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {

            if (url.indexOf("uuzuchemobile://") != -1)
            {
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URLDecoder.decode(url, "utf-8"))));
//                    view.goBack();
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                return true;
            }
            else
            {

                view.loadUrl(url);
                //如果不需要其他对点击链接事件的处理返回true，否则返回false
                return false;
            }
        }


    }


    public void initNoteDataRefush()
    {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (Config.isNetworkConnected(context))
                {
                    mAllFramelayout.noDataReloading();
                    view.loadUrl(getIntent().getStringExtra("url"));
                    mAllFramelayout.makeProgreeDismiss();
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        return true;
    }

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(context);
    }
}
