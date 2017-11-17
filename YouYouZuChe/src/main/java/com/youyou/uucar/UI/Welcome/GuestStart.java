package com.youyou.uucar.UI.Welcome;

import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MyScrollLayout;
import com.youyou.uucar.Utils.Support.OnViewChangeListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuestStart extends Activity implements OnViewChangeListener {
    public Activity context;
    public String tag = "GuestStart";
    MyScrollLayout mScrollLayout;
    LinearLayout pointLLayout;
    LinearLayout startBtn;
    private int count;
    private ImageView[] imgs;
    private int currentItem;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        UUAppCar.getInstance().addActivity(this);
        getActionBar().hide();
        setContentView(R.layout.guest_guide);
        mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
        pointLLayout = (LinearLayout) findViewById(R.id.llayout);
        startBtn = (LinearLayout) findViewById(R.id.goto_main);
        startBtn.setOnClickListener(onClick);
        count = mScrollLayout.getChildCount();
        imgs = new ImageView[count];
        for (int i = 0; i < count; i++) {
            imgs[i] = (ImageView) pointLLayout.getChildAt(i);
            imgs[i].setEnabled(true);
            imgs[i].setTag(i);
        }
        currentItem = 0;
        imgs[currentItem].setEnabled(false);
        mScrollLayout.SetOnViewChangeListener(this);
    }

    /**
     * 进入主界面
     */
    void GoToMainActivity() {
        Intent intent = new Intent(context, MainActivityTab.class);
        intent.putExtra("goto", getIntent().getStringExtra("goto"));
        startActivity(intent);
        SharedPreferences sp = context.getSharedPreferences("guide", Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        String pkName = getPackageName();
        try {
            edit.putString("version", context.getPackageManager().getPackageInfo(pkName, 0).versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        edit.commit();
        finish();
    }

    @Override
    public void OnViewChange(int position) {
        setcurrentPoint(position);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.goto_main:
                    // Intent intent = new Intent(context,RegisterPhone.class);
                    // intent.putExtra("toowner",false);
                    // startActivity(intent);
                    // finish();
                    GoToMainActivity();
                    break;
            }
        }
    };

    private void setcurrentPoint(int position) {
        if (position < 0 || position > count - 1 || currentItem == position) {
            return;
        }
        imgs[currentItem].setEnabled(true);
        imgs[position].setEnabled(false);
        currentItem = position;
    }
}
