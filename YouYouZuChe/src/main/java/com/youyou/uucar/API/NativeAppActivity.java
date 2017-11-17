package com.youyou.uucar.API;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
import com.youyou.uucar.UI.Welcome.StartActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.util.List;

/**
 * Created by 16515_000 on 2014/8/12.
 */
public class NativeAppActivity extends Activity
{
    public String tag = "NativeAppActivity";
    Activity context;

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        context = this;
        // 尝试获取WebApp页面上过来的URL
        Uri uri = getIntent().getData();
        if (uri != null)
        {
            StringBuffer sb = new StringBuffer();
            // 完整的url信息
            sb.append("url: " + uri.toString());
            // scheme部分
            sb.append("\nscheme: " + uri.getScheme());
            // host部分
            sb.append("\nhost: " + uri.getHost());
            // 访问路劲
            sb.append("\npath: ");
            List<String> pathSegments = uri.getPathSegments();
            for (int i = 0; pathSegments != null && i < pathSegments.size(); i++)
            {
                sb.append("/" + pathSegments.get(i));
            }
            // Query部分
            sb.append("\nquery: ?" + uri.getQuery());
            MLog.e(tag, " uri =  " + sb.toString());
            String query = uri.getQuery().substring(uri.getQuery().indexOf("=") + 1);
            Intent intent = new Intent();
            if (Config.outApp(context))
            {
                intent.setClass(context, StartActivity.class);
            }
            if (pathSegments.get(0).equals("carDetail"))
            {
                intent.putExtra(SysConfig.CAR_SN, query);
                if (Config.outApp(context))
                {
                    intent.putExtra("goto", MainActivityTab.GOTO_CAR_DETAIL);
                    intent.putExtra(SysConfig.CAR_SN, uri.getQuery().substring(uri.getQuery().indexOf("=") + 1));
                }
                else
                {
                    intent.setClass(context, OldCarInfoActivity.class);
                }
            }
            else if (pathSegments.get(0).equals("main"))
            {
                int gotos = Integer.parseInt(query);
                switch (gotos)
                {
                    case 0:
                        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);

                        break;
                    case 1:
                        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_STROKE);

                        break;
                    case 2:
                        intent.putExtra("goto", MainActivityTab.GOTO_FAST_RENT);
                        break;
                    case 3:
                        intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                        break;
                    case 4:
                        intent.putExtra("goto", MainActivityTab.GOTO_MY);
                        break;
                }
                if (Config.outApp(context))
                {

                }
                else
                {
                    intent.setClass(context, MainActivityTab.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
            }
            else if (pathSegments.get(0).equals("orderDetail"))
            {
                if (Config.outApp(context))
                {
                }
                else
                {
                    intent.setClass(context, OldCarInfoActivity.class);
                }

            }
            startActivity(intent);
            finish();
        }
    }

}
