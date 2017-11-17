package com.youyou.uucar.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.youyou.uucar.UI.Main.fragment.BasicFragment;
import com.youyou.uucar.Utils.View.PagerSlidingTabStrip;

import java.util.Vector;

public class IconFragmentAdater
        extends FragmentStatePagerAdapter
        implements PagerSlidingTabStrip.IconTabProvider {
    private final Vector<BasicFragment> fragments = new Vector();

    public IconFragmentAdater(FragmentManager paramFragmentManager) {
        super(paramFragmentManager);
    }

    public void addFragment(BasicFragment paramBasicFragment) {
        this.fragments.add(paramBasicFragment);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.fragments.size();
    }

    public Fragment getItem(int paramInt) {
        return (BasicFragment) this.fragments.elementAt(paramInt);
    }

    public int getPageIconResId(int paramInt) {
        return ((BasicFragment) this.fragments.elementAt(paramInt)).getResourceId();
    }

    @Override
    public CharSequence getPageTitle(int paramInt) {
        return ((BasicFragment) this.fragments.elementAt(paramInt)).getTitle();
    }
}



/* Location:           D:\反编译\jdGui\classes_dex2jar.jar

 * Qualified Name:     com.mzba.ui.widget.adapter.IconFragmentAdater

 * JD-Core Version:    0.7.0.1

 */