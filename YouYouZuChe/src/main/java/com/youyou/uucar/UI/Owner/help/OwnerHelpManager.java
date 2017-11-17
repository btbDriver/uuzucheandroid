package com.youyou.uucar.UI.Owner.help;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.youyou.uucar.Adapter.TextFragmentAdapter;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.Login.ResetPasswordPhone;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeCancelFragment;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeCurrentFragment;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeFinishFragment;
import com.youyou.uucar.Utils.Support.Empty.EmptyOnPagerListener;
import com.youyou.uucar.Utils.View.PagerSlidingTabStrip;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OwnerHelpManager extends BaseActivity
{
    private TextFragmentAdapter adapter;
    OwnerRaiders raiders;
    OwnerHelp    help;
    @InjectView(R.id.vPager)
    ViewPager content;
    private PagerSlidingTabStrip tabs;
    int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_help);
        ButterKnife.inject(this);
        adapter = new TextFragmentAdapter(getSupportFragmentManager(), this);
        raiders = OwnerRaiders.newInstance();
        help = OwnerHelp.newInstance();
        raiders.setTitle("车主秘籍");
        help.setTitle("车主须知");
        adapter.addFragment(raiders);
        adapter.addFragment(help);
        adapter.notifyDataSetChanged();
        content.setAdapter(adapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.PagerSlidingTabStrip);
        tabs.setViewPager(content);
        tabs.setOnPageChangeListener(new EmptyOnPagerListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                currentPage = position;
                switch (position)
                {
                    case 0:
                        help.cancleAllRequest();
                        raiders.resume();
                        break;
                    case 1:
                        raiders.cancleAllRequest();
                        help.resume();
                        break;
                }
            }
        });
    }

    /**
     * 第一次进入的时候不执行resume
     */
    boolean firstComein = true;

    @Override
    public void onResume()
    {
        super.onResume();
//        if (!isHidden()) {
        if (!firstComein && raiders != null && help != null)
        {

            switch (currentPage)
            {
                case 0:
                    help.cancleAllRequest();
                    raiders.resume();
                    break;
                case 1:
                    help.cancleAllRequest();
                    raiders.resume();
                    break;
            }
        }
        firstComein = false;
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
