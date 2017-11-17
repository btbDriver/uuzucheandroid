package com.youyou.uucar.UI.Orderform;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.H5Constant;
import com.youyou.uucar.Utils.Network.HexUtil;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * h5 webview通用类，主要用于加载webview
 */
public class H5Activity extends BaseActivity {
    public static final String TAG = H5Activity.class.getSimpleName();

    @InjectView(R.id.mwebview)
    public WebView mWebView = null;
    @InjectView(R.id.all_framelayout)
    public UUProgressFramelayout mAllFramelayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h5);

        ButterKnife.inject(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String url = intent.getStringExtra(H5Constant.MURL);
        if (url != null && !"".equals(url)) {
            try {
                Uri uri = Uri.parse(URLDecoder.decode(url, "utf-8"));
                Map<String,String> queryMap =  H5Constant.parseUriQuery(uri.getQuery());
                if (queryMap.containsKey(H5Constant.ISFLUSH)) {
                    if ("true".equals(queryMap.get(H5Constant.ISFLUSH).trim())) {
                        //刷新页面 动作

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //初始化组件
    private void initView() {
        //屏蔽长按时间
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        //设置js代码可用，以及其他WebSettings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);

        MWebChromeClient mWebChromeClient = new MWebChromeClient();
        MWebViewClient mWebViewClient = new MWebViewClient();
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);

        Intent intent = getIntent();
        try {
            if (intent != null) {
                String weburl = intent.getStringExtra(H5Constant.MURL);
                //获取票据信息
                String sessionTicket = HexUtil.bytes2HexStr(UserSecurityConfig.b4_ticket);
                //if (weburl != null && !weburl.trim().equals("")) {
                    H5Constant.synCookies(this, "http://web.uuzuche.com.cn/cookie.php", "session_id=123");
                    mWebView.loadUrl("http://web.uuzuche.com.cn/cookie.php");
                    //mWebView.loadUrl("http://www.baidu.com");
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    class MWebChromeClient extends WebChromeClient {
        private Boolean isSetTitle = true;

        //获取webview中的title
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (isSetTitle) {
                setTitle(title);
            }
        }

        public void setIsSetTitle(Boolean isSetTitle) {
            this.isSetTitle = isSetTitle;
        }
    }


    class MWebViewClient extends WebViewClient {

        //URL拦截，重写URL跳转
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //解析scheme，拿到各个参数值，然后判断当前页面是否需要关闭，同时判断是否需要重新打开activity
            if (url.indexOf(H5Constant.SCHEME) != -1) {
                try {
                    Uri uri = Uri.parse(URLDecoder.decode(url, "utf-8"));
                    Map<String,String> queryMap =  H5Constant.parseUriQuery(uri.getQuery());
                    if (queryMap.containsKey(H5Constant.ISNEW)) {
                        if ("true".equals(queryMap.get(H5Constant.ISNEW).trim())) {
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        }
                    }
                    if (queryMap.containsKey(H5Constant.ISCLOSE)) {
                        if ("true".equals(queryMap.get(H5Constant.ISCLOSE).trim())) {
                            finish();
                        }
                    }
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            }
            else {
                view.loadUrl(url);
                //如果不需要其他对点击链接事件的处理返回true，否则返回false
                return false;
            }
        }
        //开始加载页面
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mAllFramelayout.makeProgreeNoData();
            //mSwipeRefreshLayout.setRefreshing(true);
        }
        //加载页面结束
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mAllFramelayout.makeProgreeDismiss();
        }

    }
}
