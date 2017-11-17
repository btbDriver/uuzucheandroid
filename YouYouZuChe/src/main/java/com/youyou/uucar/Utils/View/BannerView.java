/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.youyou.uucar.Utils.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ab.adapter.AbViewPagerAdapter;
import com.ab.global.AbAppData;
import com.ab.util.AbFileUtil;
import com.ab.view.listener.AbOnChangeListener;
import com.ab.view.listener.AbOnItemClickListener;
import com.ab.view.listener.AbOnScrollListener;
import com.ab.view.listener.AbOnTouchListener;
import com.ab.view.sample.AbInnerViewPager;
import com.uu.client.bean.banner.common.BannerCommon;
import com.youyou.uucar.Adapter.BannerAdapter;
import com.youyou.uucar.UI.Main.fragment.BannerFragment;
import com.youyou.uucar.Utils.Support.MLog;

import java.util.ArrayList;
import java.util.List;

// : Auto-generated Javadoc

/**
 * 名称：AbPlayView
 * 描述：可播放显示的View.
 *
 * @author zhaoqp
 * @date 2011-11-28
 */
public class BannerView extends LinearLayout
{

    /**
     * The tag.
     */
    private static String TAG = "AbSlidingPlayView";

    /**
     * The Constant D.
     */
    private static final boolean D = AbAppData.DEBUG;

    /**
     * The context.
     */
    private Context context;

    /**
     * The m view pager.
     */
    private AbInnerViewPager mViewPager;

    /**
     * The page line layout.
     */
    private LinearLayout pageLineLayout;

    /**
     * The layout params pageLine.
     */
    public LayoutParams pageLineLayoutParams = null;

    /**
     * The i.
     */
    private int count, position;

    /**
     * The hide image.
     */
    public Bitmap displayImage, hideImage;

    /**
     * The m on item click listener.
     */
    private AbOnItemClickListener mOnItemClickListener;

    /**
     * The m ab change listener.
     */
    private AbOnChangeListener mAbChangeListener;

    /**
     * The m ab scrolled listener.
     */
    private AbOnScrollListener mAbScrolledListener;

    /**
     * The m ab touched listener.
     */
    private AbOnTouchListener mAbOnTouchListener;

    /**
     * The layout params ff.
     */
    public LayoutParams layoutParamsFF = null;

    /**
     * The layout params fw.
     */
    public LayoutParams layoutParamsFW = null;

    /**
     * The layout params wf.
     */
    public LayoutParams layoutParamsWF = null;

    /**
     * The m list views.
     */

    /**
     * The m ab view pager adapter.
     */
    private BannerAdapter mBannerAdapter = null;

    /**
     * 导航的点父View
     */
    private LinearLayout mPageLineLayoutParent = null;

    /**
     * The page line horizontal gravity.
     */
    private int pageLineHorizontalGravity = Gravity.RIGHT;

    /**
     * 播放的方向
     */
    private int playingDirection = 0;

    /**
     * 播放的开关
     */
    private boolean play = false;

    /**
     * 创建一个AbSlidingPlayView
     *
     * @param context
     */
    public BannerView(Context context)
    {
        super(context);
        initView(context);
    }

    /**
     * 从xml初始化的AbSlidingPlayView
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public BannerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    private static BannerFragment.CloseInterface closeInterface;

    /**
     * 描述：初始化这个View
     *
     * @param context
     * @throws
     */
    public void initView(Context context)
    {
        this.context = context;
        layoutParamsFF = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        layoutParamsFW = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParamsWF = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        pageLineLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(Color.rgb(255, 255, 255));

        RelativeLayout mRelativeLayout = new RelativeLayout(context);

        mViewPager = new AbInnerViewPager(context);
        //手动创建的ViewPager,如果用fragment必须调用setId()方法设置一个id
        mViewPager.setId(1985);
        //导航的点
        mPageLineLayoutParent = new LinearLayout(context);
        mPageLineLayoutParent.setPadding(0, 5, 0, 5);
        pageLineLayout = new LinearLayout(context);
        pageLineLayout.setPadding(15, 1, 15, 1);
        pageLineLayout.setVisibility(View.INVISIBLE);
        mPageLineLayoutParent.addView(pageLineLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        lp1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mRelativeLayout.addView(mViewPager, lp1);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        mRelativeLayout.addView(mPageLineLayoutParent, lp2);
        addView(mRelativeLayout, layoutParamsFW);

        displayImage = AbFileUtil.getBitmapFormSrc("image/play_display.png");
        hideImage = AbFileUtil.getBitmapFormSrc("image/play_hide.png");

//        mBannerAdapter = new BannerAdapter(context, mListViews);
//        mViewPager.setAdapter(mBannerAdapter);
        mViewPager.setFadingEdgeLength(0);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position)
            {
                makesurePosition();
                onPageSelectedCallBack(position);


                final long time = System.currentTimeMillis();
                final int timeInt = (int) (time / 1000);
                List<BannerCommon.BannerItem> bannerList = adapter.getBannerList();
                final int count = bannerList.size();
                if (bannerList != null)
                {
                    for (int i = bannerList.size() - 1; i >= 0; i--)
                    {
                        BannerCommon.BannerItem bannerItem = bannerList.get(i);
                        int endTime = bannerItem.getEndTime();
                        if (timeInt > endTime)
                        {
                            bannerList.remove(i);
                        }
                    }
                }
                final int newCount = bannerList.size();
                if (newCount < count)
                {
                    stopPlay();
                    adapter.setCount(bannerList);
                    adapter.notifyDataSetChanged();
                    setAdapter(adapter);
                    startPlay();
                }
                else
                {
                    BannerFragment fragment = (BannerFragment) adapter.getItem(position);
                    if (fragment != null)
                    {
                        fragment.setCloseInterface(closeInterface);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                onPageScrolledCallBack(position);
            }

        });
    }

    public BannerAdapter getAdapter()
    {
        return adapter;
    }

    private BannerAdapter adapter;

    public void setAdapter(BannerAdapter bannerAdapter)
    {
        adapter = bannerAdapter;
        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        count = adapter.getCount();
        creatIndex(count);
        BannerFragment fragment = (BannerFragment) adapter.getItem(0);
        if (fragment != null)
        {
            fragment.setCloseInterface(closeInterface);
        }
    }

    public void setCloseInterface(BannerFragment.CloseInterface cinterface)
    {
        closeInterface = cinterface;
    }


    public void creatIndex(final int numb)
    {
        //显示下面的点
        pageLineLayout.removeAllViews();
        mPageLineLayoutParent.setHorizontalGravity(pageLineHorizontalGravity);
        pageLineLayout.setGravity(Gravity.CENTER);
        pageLineLayout.setVisibility(View.VISIBLE);
        if (numb > 1)
        {
            for (int j = 0; j < numb; j++)
            {
                ImageView imageView = new ImageView(context);
                pageLineLayoutParams.setMargins(5, 5, 5, 5);
                imageView.setLayoutParams(pageLineLayoutParams);
                if (j == 0)
                {
                    imageView.setImageBitmap(displayImage);
                }
                else
                {
                    imageView.setImageBitmap(hideImage);
                }
                pageLineLayout.addView(imageView, j);
            }
        }
    }

    /**
     * 定位点的位置.
     */
    public void makesurePosition()
    {
        position = mViewPager.getCurrentItem();
        for (int j = 0; j < count; j++)
        {
            if (position == j)
            {
                ((ImageView) pageLineLayout.getChildAt(position)).setImageBitmap(displayImage);
            }
            else
            {
                ((ImageView) pageLineLayout.getChildAt(j)).setImageBitmap(hideImage);
            }
        }
    }


    /**
     * 描述：设置页面切换事件.
     *
     * @param position the position
     */
    private void onPageScrolledCallBack(int position)
    {
        if (mAbScrolledListener != null)
        {
            mAbScrolledListener.onScroll(position);
        }

    }

    /**
     * 描述：设置页面切换事件.
     *
     * @param position the position
     */
    private void onPageSelectedCallBack(int position)
    {
        if (mAbChangeListener != null)
        {
            mAbChangeListener.onChange(position);
        }

    }


    /**
     * 用与轮换的 handler.
     */
    private Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 0)
            {
                int count = adapter.getCount();
                List<BannerCommon.BannerItem> bannerList = adapter.getBannerList();
                if (bannerList != null && bannerList.size() > 0)
                {
                    int i = mViewPager.getCurrentItem();
                    BannerCommon.BannerItem bannerItem = bannerList.get(i);
                    if (playingDirection == 0)
                    {
                        if (i == count - 1)
                        {
                            playingDirection = -1;
                            i--;
                        }
                        else
                        {
                            i++;
                        }
                    }
                    else
                    {
                        if (i == 0)
                        {
                            playingDirection = 0;
                            i++;
                        }
                        else
                        {
                            i--;
                        }
                    }
                    mViewPager.setCurrentItem(i, true);
                    if (play)
                    {
                        handler.postDelayed(runnable, bannerItem.getDisplayDuration() * 1000);
                    }
                }
                else
                {
                    if (play)
                    {
                        handler.sendEmptyMessage(1);
                    }
                }
            }
            else if (msg.what == 1)
            {
                stopPlay();
            }
        }

    };

    /**
     * 用于轮播的线程.
     */
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            if (mViewPager != null)
            {
                handler.sendEmptyMessage(0);
            }
        }
    };


    /**
     * 描述：自动轮播.
     */
    public void startPlay()
    {
        stopPlay();
        if (handler != null)
        {
            play = true;
            int displayDuration = 5000;
            if (adapter != null)
            {
                displayDuration = adapter.getBannerList().get(0).getDisplayDuration() * 1000;
            }
            handler.postDelayed(runnable, displayDuration);
        }
    }

    /**
     * 描述：自动轮播.
     */
    public void stopPlay()
    {
        if (handler != null)
        {
            play = false;
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * 设置点击事件监听.
     *
     * @param onItemClickListener the new on item click listener
     */
    public void setOnItemClickListener(AbOnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }


    /**
     * 描述：设置页面切换的监听器.
     *
     * @param abChangeListener the new on page change listener
     */
    public void setOnPageChangeListener(AbOnChangeListener abChangeListener)
    {
        mAbChangeListener = abChangeListener;
    }

    /**
     * 描述：设置页面滑动的监听器.
     *
     * @param abScrolledListener the new on page scrolled listener
     */
    public void setOnPageScrolledListener(AbOnScrollListener abScrolledListener)
    {
        mAbScrolledListener = abScrolledListener;
    }

    /**
     * 描述：设置页面Touch的监听器.
     *
     * @param abOnTouchListener
     * @throws
     */
    public void setOnTouchListener(AbOnTouchListener abOnTouchListener)
    {
        mAbOnTouchListener = abOnTouchListener;
    }


//    /**
//     * Sets the page line image.
//     *
//     * @param displayImage the display image
//     * @param hideImage    the hide image
//     */
//    public void setPageLineImage(Bitmap displayImage, Bitmap hideImage) {
//        this.displayImage = displayImage;
//        this.hideImage = hideImage;
//        creatIndex();
//
//    }

    /**
     * 描述：获取这个滑动的ViewPager类.
     *
     * @return the view pager
     */
    public ViewPager getViewPager()
    {
        return mViewPager;
    }


    /**
     * 描述：设置页显示条的位置,在AddView前设置.
     *
     * @param horizontalGravity the new page line horizontal gravity
     */
    public void setPageLineHorizontalGravity(int horizontalGravity)
    {
        pageLineHorizontalGravity = horizontalGravity;
    }

    /**
     * 如果外层有ScrollView需要设置.
     *
     * @param parentScrollView the new parent scroll view
     */
    public void setParentScrollView(ScrollView parentScrollView)
    {
        this.mViewPager.setParentScrollView(parentScrollView);
    }

    /**
     * 如果外层有ListView需要设置.
     *
     * @param parentListView the new parent list view
     */
    public void setParentListView(ListView parentListView)
    {
        this.mViewPager.setParentListView(parentListView);
    }

    /**
     * 描述：设置导航点的背景
     *
     * @param resid
     * @throws
     */
    public void setPageLineLayoutBackground(int resid)
    {
        pageLineLayout.setBackgroundResource(resid);
    }

}
