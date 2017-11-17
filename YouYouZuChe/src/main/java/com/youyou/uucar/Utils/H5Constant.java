package com.youyou.uucar.Utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.youyou.uucar.Utils.Support.MLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuchao on 2015/3/18.
 */
public class H5Constant {
    public static final String SCHEME = "uuzuchemobile://";

    //scheme URL
    public static final String MURL = "murl";

    //scheme参数，判断是否刷新
    public static final String ISFLUSH = "isFlush";

    //scheme参数,判断是否关闭
    public static final String ISCLOSE = "isClose";

    //scheme参数，判断是否新打开
    public static final String ISNEW = "isNew";

    //用户类型-车主
    public static final int USER_TYPE_OWNER = 0;
    //用户类型-租客
    public static final int USER_TYPE_RENTER = 1;

    //租客订单详情url
    public static final String MY_STROKE_ORDER_DETAIL = "http://10.40.1.198/HelloHBuilder/web-client/list-all.html";

    /**
     * 同步一下cookie
     */
    public static void synCookies(Context context, String url, String cookie) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //cookieManager.removeSessionCookie();//移除

        Uri uri = null;
        String domain = "";
        try {
            uri = Uri.parse(URLDecoder.decode(url, "utf-8"));
            domain = uri.getHost();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String cookieValue = cookie + ";domain="+ domain;
        cookieManager.setCookie(url, cookieValue);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 解析URI中的query条件
     */
    public static Map<String, String> parseUriQuery(String query) {

        Map<String, String> map = new HashMap<String,String>();

        if (!TextUtils.isEmpty(query)) {
            String[] splitValue = query.split("&");
            for (String str : splitValue) {
                String[] key_value = str.split("=");
                map.put(key_value[0],key_value[1]);
            }
        }
        return map;
    }

}
