package com.youyou.uucar.Adapter;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.youyou.uucar.UI.Main.fragment.BasicFragment;

import java.util.Vector;

public class TextFragmentAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private final Vector<BasicFragment> fragments = new Vector();

    public TextFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void addFragment(BasicFragment paramBasicFragment) {
        this.fragments.add(paramBasicFragment);
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        // container.removeView((View) object);
        super.destroyItem(container, position, object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {

        super.finishUpdate(container);
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        // if (arg1 == 1) {
        //
        // }
        return super.instantiateItem(arg0, arg1);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return super.isViewFromObject(view, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public Fragment getItem(int arg0) {

        return (BasicFragment) this.fragments.elementAt(arg0);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((BasicFragment) this.fragments.elementAt(position)).getTitle();
    }
}