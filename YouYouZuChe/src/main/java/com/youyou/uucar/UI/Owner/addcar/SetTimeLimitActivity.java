package com.youyou.uucar.UI.Owner.addcar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.youyou.uucar.PB.TaskTool;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.WindowUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SetTimeLimitActivity extends BaseActivity
{

    private String  carSn;
    private boolean isRent;

    UUAppCar app;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time_limit);
        ButterKnife.inject(this);

        app = (UUAppCar) getApplication();

        carSn = getIntent().getStringExtra(SysConfig.CAR_NAME);
        isRent = getIntent().getBooleanExtra("IS_RENT", false);

        judgeState(!isRent);
        initNoteDataRefush();
        not_rent_time_set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent();
                i.putExtra(SysConfig.CAR_NAME, carSn);
                i.setClass(SetTimeLimitActivity.this, SetNotRentTimeActivity.class);
                startActivity(i);
            }
        });

        if (!isRent)
        {
            is_rent.setChecked(true);
        }
        else
        {
            is_rent.setChecked(false);
        }
        is_rent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    WindowUtil.confirmOrCancel(SetTimeLimitActivity.this, "设置暂时不可租", "确认要暂时不出租吗？设置为不出租的话将收不到任何预约请求，无法继续赚钱哦！", "取消", "暂时不可租", new WindowUtil.CallBack()
                    {
                        @Override
                        public void onConfirm()
                        {
                            TaskTool.setRefuseRent(carSn, 2, app, new HttpResponse.NetWorkResponse<UUResponseData>()
                            {
                                @Override public void onSuccessResponse(UUResponseData responseData)
                                {

                                    isRent = false;
                                    judgeState(!isRent);
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

                        @Override
                        public void onCancel()
                        {
                            is_rent.setChecked(false);
                        }

                        @Override
                        public void onItemChoice(int position)
                        {

                        }
                    });
                }
                else
                {
                    TaskTool.setRefuseRent(carSn, 1, app, new HttpResponse.NetWorkResponse<UUResponseData>()
                    {
                        @Override public void onSuccessResponse(UUResponseData responseData)
                        {

                            isRent = true;
                            judgeState(!isRent);
                        }

                        @Override public void onError(VolleyError errorResponse)
                        {

                        }

                        @Override public void networkFinish()
                        {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    private String acceptOrderDesc;

    private List<CarInterface.Leaseterm> leasetermList;

    private String[] down;
    private String[] up;
    private String[] all;

    private void initData()
    {
        TaskTool.queryLeaseTerm(carSn, app, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override public void onSuccessResponse(UUResponseData responseData)
            {
                if (responseData.getRet() == 0)
                {
                    showResponseCommonMsg(responseData.getResponseCommonMsg());
                    try
                    {
                        CarInterface.QueryLeaseTermResponse response = CarInterface.QueryLeaseTermResponse.parseFrom(responseData.getBusiData());
                        if (response.getRet() == -1)
                        {
                            all_framelayout.noDataReloading();
                            initData();
                            return;
                        }

                        if (response.getAutoAcceptOrderType() == 1)
                        {
                            autoAcceptOrder = false;
                            tv_auto_receive_order.setText("已关闭");
                        }
                        else
                        {
                            autoAcceptOrder = true;
                            tv_auto_receive_order.setText("已打开");
                        }

                        if (response.getRefuseRentType() == 1)
                        {
                            is_rent.setChecked(false);
                            isRent = true;
                        }
                        else
                        {
                            is_rent.setChecked(true);
                            isRent = false;
                            tv_auto_receive_order.setText("已关闭");
                        }

                        if (response.getDisplayAcceptOrder() == 2)
                        {
                            auto_receive_order.setVisibility(View.GONE);
                        }
                        else
                        {
                            acceptOrderDesc = response.getAcceptOrderDesc();
                            auto_receive_order.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent i = new Intent();
                                    i.putExtra(SysConfig.CAR_NAME, carSn);
                                    i.putExtra("auto_receive_order", autoAcceptOrder);
                                    i.putExtra("content", acceptOrderDesc);
                                    i.setClass(SetTimeLimitActivity.this, AutoReceiveOrderActivity.class);
                                    startActivity(i);
                                }
                            });
                        }

                        leasetermList = response.getLeasetermListList();

                        mini_index = getIndex(response.getLeasetermShort());
                        more_index = getIndex(response.getLeasetermLong()) - 1;

                        all = new String[leasetermList.size()];
                        int index = 0;
                        for (CarInterface.Leaseterm leaseterm : leasetermList)
                        {
                            if (leaseterm.getDay() >= 1)
                            {
                                if (leaseterm.getDay() > 100)
                                {
                                    all[index] = "30天以上";
                                }
                                else
                                {
                                    all[index] = leaseterm.getDay() + "天";
                                }
                            }
                            else
                            {
                                all[index] = leaseterm.getHour() + "小时";
                            }
                            index++;
                        }

                        refreshShow();

                        rent_short_time.setText(all[mini_index]);
                        rent_long_time.setText(all[more_index + 1]);

                        down = new String[leasetermList.size() - 1];
                        up = new String[leasetermList.size() - 1];

                        for (int di = 0; di < leasetermList.size() - 1; di++)
                        {
                            down[di] = all[di];
                            up[di] = all[di + 1];
                        }

                        rent_short_time.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                WindowUtil.singleChoiceList(SetTimeLimitActivity.this, "选择时间", down, new WindowUtil.CallBack()
                                {

                                    @Override
                                    public void onConfirm()
                                    {

                                    }

                                    @Override
                                    public void onCancel()
                                    {

                                    }

                                    @Override
                                    public void onItemChoice(int position)
                                    {
                                        mini_index = position;
                                        if (mini_index > more_index)
                                        {
                                            Config.showToast(SetTimeLimitActivity.this, "最短租期不能大于最长租期");
                                            return;
                                        }
                                        TaskTool.setShortLeaseTerm(carSn, leasetermList.get(position), app, new HttpResponse.NetWorkResponse<UUResponseData>()
                                        {
                                            @Override
                                            public void onSuccessResponse(UUResponseData responseData)
                                            {

                                            }

                                            @Override public void onError(VolleyError errorResponse)
                                            {

                                                Config.showFiledToast(context);
                                            }

                                            @Override public void networkFinish()
                                            {

                                            }
                                        });
                                        refreshShow();
                                    }
                                });
                            }
                        });

                        rent_long_time.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                WindowUtil.singleChoiceList(SetTimeLimitActivity.this, "选择时间", up, new WindowUtil.CallBack()
                                {

                                    @Override
                                    public void onConfirm()
                                    {

                                    }

                                    @Override
                                    public void onCancel()
                                    {

                                    }

                                    @Override
                                    public void onItemChoice(int position)
                                    {
                                        more_index = position;
                                        if (mini_index > more_index)
                                        {
                                            Config.showToast(SetTimeLimitActivity.this, "最短租期不能大于最长租期");
                                            return;
                                        }
                                        TaskTool.setLongLeaseTerm(carSn, leasetermList.get(position + 1), app, new HttpResponse.NetWorkResponse<UUResponseData>()
                                        {
                                            @Override
                                            public void onSuccessResponse(UUResponseData responseData)
                                            {

                                            }

                                            @Override public void onError(VolleyError errorResponse)
                                            {
                                                Config.showFiledToast(context);
                                            }

                                            @Override public void networkFinish()
                                            {

                                            }
                                        });
                                        refreshShow();
                                    }
                                });
                            }
                        });

                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override public void onError(VolleyError errorResponse)
            {
                all_framelayout.makeProgreeNoData();
            }

            @Override public void networkFinish()
            {

                all_framelayout.makeProgreeDismiss();
            }
        });
    }

    private void refreshShow()
    {
        rent_short_time.setText(all[mini_index]);
        rent_long_time.setText(all[more_index + 1]);
    }

    private int getIndex(CarInterface.Leaseterm leaseterm)
    {
        int index = 0;
        for (; index <= leasetermList.size(); index++)
        {
            if (leasetermList.get(index).getHour() == leaseterm.getHour() && leasetermList.get(index).getDay() == leaseterm.getDay())
            {
                break;
            }
        }
        return index;
    }

    private int mini_index;
    private int more_index;

    private boolean autoAcceptOrder = false;

    @InjectView(R.id.is_rent)
    ToggleButton   is_rent;
    @InjectView(R.id.rent_short_time)
    TextView       rent_short_time;
    @InjectView(R.id.rent_long_time)
    TextView       rent_long_time;
    @InjectView(R.id.auto_receive_order_text)
    TextView       auto_receive_order_text;
    @InjectView(R.id.auto_receive_order)
    RelativeLayout auto_receive_order;
    @InjectView(R.id.tv_auto_receive_order)
    TextView       tv_auto_receive_order;
    @InjectView(R.id.not_rent_time_set)
    RelativeLayout not_rent_time_set;
    @InjectView(R.id.not_rent_time_set_text)
    TextView       not_rent_time_set_text;

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout all_framelayout;

    @InjectView(R.id.middle_zhi)
    TextView middle_zhi;
    @InjectView(R.id.zuichangzuiduan)
    TextView zuichangzuiduan;

    private void judgeState(boolean b)
    {
        if (b)
        {
            rent_short_time.setTextColor(getResources().getColor(R.color.c13));
            rent_long_time.setTextColor(getResources().getColor(R.color.c13));
            rent_short_time.setBackgroundResource(R.drawable.rent_time_bg_gray);
            rent_long_time.setBackgroundResource(R.drawable.rent_time_bg_gray);
            rent_short_time.setClickable(false);
            rent_long_time.setClickable(false);
            middle_zhi.setTextColor(getResources().getColor(R.color.c13));
            zuichangzuiduan.setTextColor(getResources().getColor(R.color.c13));
            rent_short_time.setClickable(false);
            rent_long_time.setClickable(false);
            auto_receive_order_text.setTextColor(getResources().getColor(R.color.c13));
            auto_receive_order.setClickable(false);
            not_rent_time_set.setClickable(false);
            tv_auto_receive_order.setTextColor(getResources().getColor(R.color.c13));
            not_rent_time_set_text.setTextColor(getResources().getColor(R.color.c13));
            TaskTool.setAutoAcceptOrder(carSn, 1, app, new HttpResponse.NetWorkResponse<UUResponseData>()
            {
                @Override public void onSuccessResponse(UUResponseData responseData)
                {

                    if (responseData.getRet() == 0)
                    {
                        showResponseCommonMsg(responseData.getResponseCommonMsg());

                        tv_auto_receive_order.setText("已关闭");
                        autoAcceptOrder = false;
                    }
                }

                @Override public void onError(VolleyError errorResponse)
                {

                }

                @Override public void networkFinish()
                {

                }
            });
        }
        else
        {
//            tv_auto_receive_order.setText("");
            rent_short_time.setTextColor(getResources().getColor(R.color.c8));
            rent_long_time.setTextColor(getResources().getColor(R.color.c8));
            rent_short_time.setClickable(true);
            rent_long_time.setClickable(true);
            middle_zhi.setTextColor(getResources().getColor(R.color.c8));
            zuichangzuiduan.setTextColor(getResources().getColor(R.color.c4));
            rent_short_time.setBackgroundResource(R.drawable.rent_time_bg);
            rent_long_time.setBackgroundResource(R.drawable.rent_time_bg);
            rent_short_time.setClickable(true);
            rent_long_time.setClickable(true);
            auto_receive_order_text.setTextColor(getResources().getColor(R.color.c4));
            auto_receive_order.setClickable(true);
            not_rent_time_set.setClickable(true);
            tv_auto_receive_order.setTextColor(getResources().getColor(R.color.c8));
            not_rent_time_set_text.setTextColor(getResources().getColor(R.color.c4));
        }
    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush;
        noDataRefush = (TextView) all_framelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (Config.isNetworkConnected(context))
                {
                    all_framelayout.noDataReloading();
                    initData();
                }
                else
                {
                    all_framelayout.makeProgreeNoData();
                }
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
