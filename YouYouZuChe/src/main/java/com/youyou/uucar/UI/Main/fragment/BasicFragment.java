package com.youyou.uucar.UI.Main.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.BaseAdapter;

import com.youyou.uucar.UI.Common.BaseFragment;

//import com.nostra13.universalimageloader.core.ImageLoader;

//import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;


public abstract class BasicFragment
        extends BaseFragment
        implements Callback {

    //    public ImageLoader imageLoader = ImageLoader.getInstance();
    public int CanvasHeight;
    public int CanvasWidth;
    private BaseAdapter adapter;
    public AlertDialog alert = null;
    public boolean alertIsShow = false;
    private int resourceId;
    private String title;
    protected static final String TITLE = "title";
    protected static final String RESOURCEID = "resourceId";
    protected static final String ARGUMENTS = "arguments";

    public int getResourceId() {
        return this.resourceId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAdapter(BaseAdapter paramBaseAdapter) {
        this.adapter = paramBaseAdapter;
    }

    public void setResourceId(int paramInt) {
        this.resourceId = paramInt;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }


    public void sysMesNegativeButtonEvent(int paramInt) {
    }

    public void sysMesOnCancelEvent(int paramInt) {
    }

    public void sysMesPositiveButtonEvent(int paramInt) {
    }

    @Override
    public boolean handleMessage(Message msg) {
        return true;
    }


    public static abstract interface MenuButtonClickListener {
        public abstract void onMenuButtonClick(View paramView, int paramInt,
                                               long paramLong);
    }

    @Override
    public void onStop() {
        super.onStop();
//        RequestManager.cancelAll(this);
    }

//    protected void executeRequest(Request request) {
//        RequestManager.addRequest(request, this);
//    }


    public abstract void initLoader();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE, getTitle());
        outState.putInt(RESOURCEID, getResourceId());
        outState.putBundle(ARGUMENTS, getArguments());
    }

    public void cancleAllRequest() {
//        for (ForegroundImageView imageView : imageList)
//        {
//            if (imageView != null)
//            {
//                imageLoader.cancelDisplayTask(imageView);
//            }
//        }
//        Log.e("TAG", imageList.toString());
//        imageList.clear();
    }

    public abstract void resume();

}


