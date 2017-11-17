package com.youyou.uucar.Utils.Drawer;

import android.view.View;

/**
 * Created by taurusxi on 14-7-30.
 */
public interface DrawerAction {

    public void onDrawerClosed(View view);

    public void onDrawerSlide(View drawerView, float slideOffset);

    public void onDrawerOpened(View drawerView);

}
