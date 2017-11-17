package com.youyou.uucar.UI.operate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OperatePopActivity extends BaseActivity {

    @InjectView(R.id.img)
    BaseNetworkImageView mImg;

    @OnClick(R.id.img)
    public void imgClick() {
        Intent intent = new Intent();
        intent.putExtra("url", getIntent().getStringExtra("actionUrl"));
        intent.setClass(context, URLWebView.class);
        intent.putExtra("title", getIntent().getStringExtra("title"));
        startActivity(intent);
        finish();
    }

    @InjectView(R.id.close)
    ImageView mClose;

    @OnClick(R.id.close)
    public void closeClick() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_pop);
        ButterKnife.inject(this);
        UUAppCar.getInstance().display(getIntent().getStringExtra("imgUrl"), mImg, R.drawable.nodefimg);
        if (!getIntent().getBooleanExtra("canClose", false)) {
            mClose.setVisibility(View.GONE);
        }
    }

}
