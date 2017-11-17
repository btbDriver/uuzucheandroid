package com.youyou.uucar.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.uu.client.bean.banner.common.BannerCommon;
import com.youyou.uucar.UI.Main.fragment.BannerFragment;

import java.util.List;

/**
 * Created by taurusxi on 14/10/27.
 */
public class BannerAdapter extends FragmentStatePagerAdapter {
    private List<BannerCommon.BannerItem> bannerList;
    private static final String tag = BannerAdapter.class.getSimpleName();

    public BannerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final int pos = position;
        return BannerFragment.newInstance(bannerList.get(pos), pos);
    }

    @Override
    public int getCount() {
        return bannerList == null ? 0 : bannerList.size();
    }

    public void setCount(List<BannerCommon.BannerItem> list) {
        bannerList = list;
        notifyDataSetChanged();
    }

    public List<BannerCommon.BannerItem> getBannerList() {
        return bannerList;
    }
}
