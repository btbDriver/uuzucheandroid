package com.youyou.uucar.UI.Main.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uu.client.bean.banner.common.BannerCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by taurusxi on 14/10/27.
 */
public class BannerFragment extends Fragment
{

    private        View                 mRootView;
    private        BaseNetworkImageView mImageView;
    private        ImageView            mCloseImageView;
    private static CloseInterface       mCloseInterface;
    private        int                  banerId;
    private        boolean              canClose;
    private        String               actionUrl;
    private        String               imageUrl;
    private        String               name;
    private        int                  positon;
    private        int                  width;
    private        int                  height;
    private        int                  startTime;
    private        int                  endTime;

    public static BannerFragment newInstance(final BannerCommon.BannerItem bannerItem, int pos)
    {
        BannerFragment bannerFragment = new BannerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("banerId", bannerItem.getBannerId());
        bundle.putString("imageUrl", bannerItem.getImgUrl());
        bundle.putString("actionUrl", bannerItem.getActionUrl());
        if (bannerItem.hasName())
        {
            bundle.putString("name", bannerItem.getName());
        }
        bundle.putBoolean("close", bannerItem.getCanClose());
        bundle.putInt("POS", pos);
        bundle.putInt("width", bannerItem.getWidthPixel());
        bundle.putInt("height", bannerItem.getHeightPixel());
        bundle.putInt("startTime", bannerItem.getStartTime());
        bundle.putInt("endTime", bannerItem.getEndTime());
        bannerFragment.setArguments(bundle);
//        mCloseInterface = closeInterface;
        return bannerFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.banner_info_item, container, false);
        mImageView = (BaseNetworkImageView) mRootView.findViewById(R.id.imageview);
        mCloseImageView = (ImageView) mRootView.findViewById(R.id.close);
        Bundle arguments = getArguments();
        banerId = arguments.getInt("banerId");
        actionUrl = arguments.getString("actionUrl");
        imageUrl = arguments.getString("imageUrl");
        name = arguments.getString("name", "");
        canClose = arguments.getBoolean("close");
        positon = arguments.getInt("POS");
        width = arguments.getInt("width");
        height = arguments.getInt("height");
        startTime = arguments.getInt("startTime");
        endTime = arguments.getInt("endTime");
        float scale = width / height;
        if (scale == 309 / 1080)
        {
            UUAppCar.getInstance().display(imageUrl, mImageView, R.drawable.banner_big);
        }
        else
        {
            //TODO banner占位图
            UUAppCar.getInstance().display(imageUrl, mImageView, R.drawable.banner_big);
        }
        if (canClose)
        {
            mCloseImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            mCloseImageView.setVisibility(View.GONE);
        }
        final BannerCommon.BannerItem.Builder builder = BannerCommon.BannerItem.newBuilder();
        builder.setBannerId(banerId);
        builder.setActionUrl(actionUrl);
        builder.setImgUrl(imageUrl);
        builder.setName(name);
        builder.setCanClose(canClose);
        builder.setWidthPixel(width);
        builder.setHeightPixel(height);
        builder.setDisplayDuration(10);
        builder.setStartTime(startTime);
        builder.setEndTime(endTime);
        final BannerCommon.BannerItem bannerItem = builder.build();

        //bannerClick
        mImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String decode = URLDecoder.decode(actionUrl, "utf-8");
                    if (decode.indexOf("uuzuchemobile://") != -1)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URLDecoder.decode(actionUrl, "utf-8"))));
                    }
                    else
                    {
                        Intent intent = new Intent(Config.currentContext, URLWebView.class);
                        intent.putExtra("url", decode);
                        intent.putExtra(SysConfig.TITLE, name);
                        Config.currentContext.startActivity(intent);
                    }
                    if (mCloseInterface != null)
                    {
                        mCloseInterface.close(positon, bannerItem, BannerCommon.BannerActionType.CLICK_VALUE);
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        });
        mCloseImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCloseInterface != null)
                {
                    mCloseInterface.close(positon, bannerItem, BannerCommon.BannerActionType.CLOSE_VALUE);
                }
            }
        });
        return mRootView;
    }

    public void setCloseInterface(CloseInterface closeInterface)
    {
        mCloseInterface = closeInterface;
    }


    public static interface CloseInterface
    {
        void close(int position, BannerCommon.BannerItem bannerItem, int type);
    }
}
