package com.youyou.uucar.UI.Owner.addcar;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.youyou.uucar.PB.TaskTool;
import com.youyou.uucar.PB.impl.CommonModel;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AutoReceiveOrderActivity extends BaseActivity
{

    private boolean autoAcceptOrder;
    private String  carSn;


    @InjectView(R.id.is_rent)
    ToggleButton is_rent;

    @InjectView(R.id.auto_receive_order_text)
    TextView auto_receive_order_text;

    @InjectView(R.id.content_auto_receive)
    TextView content_auto_receive;

    @InjectView(R.id.chakanweiyuejin)
    TextView chakanweiyuejin;

    UUAppCar app;

    private String acceptOrderDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_receive_order);
        ButterKnife.inject(this);

        app = (UUAppCar) getApplication();

        carSn = getIntent().getStringExtra(SysConfig.CAR_NAME);
        acceptOrderDesc = getIntent().getStringExtra("content");
        autoAcceptOrder = getIntent().getBooleanExtra("auto_receive_order", false);

        content_auto_receive.setText(acceptOrderDesc);

        chakanweiyuejin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        chakanweiyuejin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        is_rent.setChecked(autoAcceptOrder);

        is_rent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                int type = 0;
                if (!isChecked)
                {
                    type = 1;
                }
                TaskTool.setAutoAcceptOrder(carSn, type, app, new HttpResponse.NetWorkResponse<UUResponseData>()
                {
                    @Override public void onSuccessResponse(UUResponseData responseData)
                    {

                        if(responseData.getRet() == 0)
                        {
                            showResponseCommonMsg(responseData.getResponseCommonMsg());
                        }
                    }

                    @Override public void onError(VolleyError errorResponse)
                    {
                        Config.showFiledToast(context);
                    }

                    @Override public void networkFinish()
                    {

                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
