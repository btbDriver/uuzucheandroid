package com.youyou.uucar.UI.Common;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;
import com.youyou.uucar.Utils.Support.MLog;

/**
 * Created by taurusxi on 14-8-26.
 */
public abstract class BaseFragment extends Fragment {

    public String getName() {
        return BaseFragment.this.getClass().getSimpleName();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getName());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getName());
    }

}
