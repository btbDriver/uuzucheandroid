package com.youyou.uucar.UI.Owner.addcar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SetNotRentTimeActivity extends BaseActivity
{

    private boolean undo = false;
    private CaldroidFragment caldroidFragment;

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    private void setCustomResourceForDates()
    {
        Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -18);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 16);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null)
        {
            caldroidFragment.setBackgroundResourceForDate(R.color.c11,
                                                          blueDate);
            caldroidFragment.setBackgroundResourceForDate(R.color.c11,
                                                          greenDate);
            caldroidFragment.setTextColorForDate(Color.BLACK, blueDate);
            caldroidFragment.setTextColorForDate(Color.BLACK, greenDate);
        }
    }

    private String   casSn;
    private UUAppCar app;

    @InjectView(R.id.gongzuori)
    RelativeLayout gongzuori;
    @InjectView(R.id.gongzuori_btn)
    ImageView      gongzuori_btn;
    @InjectView(R.id.gongzuori_tv)
    TextView       gongzuori_tv;

    @InjectView(R.id.zhouliu)
    RelativeLayout zhouliu;

    @InjectView(R.id.zhouliu_btn)
    ImageView      zhouliu_btn;
    @InjectView(R.id.zhouliu_tv)
    TextView       zhouliu_tv;
    @InjectView(R.id.zhouri)
    RelativeLayout zhouri;

    @InjectView(R.id.zhouri_btn)
    ImageView zhouri_btn;
    @InjectView(R.id.zhouri_tv)
    TextView  zhouri_tv;

    private boolean gongzuoriBeChoosed;
    private boolean zhouliuBeChoosed;
    private boolean zhouriBeChoosed;

    private void initOnClick()
    {
        gongzuori.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!gongzuoriBeChoosed)
                {
                    gongzuoriBeChoosed = true;
                    CaldroidGridAdapter.setChooseWorkDay(true);
                    gongzuori_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_red));
                    gongzuori_tv.setTextColor(getResources().getColor(R.color.c5));
                }
                else
                {
                    gongzuoriBeChoosed = false;
                    CaldroidGridAdapter.setChooseWorkDay(false);
                    gongzuori_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_white));
                    gongzuori_tv.setTextColor(getResources().getColor(R.color.c7));
                }
                caldroidFragment.refreshView();
            }
        });
        zhouliu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!zhouliuBeChoosed)
                {
                    zhouliuBeChoosed = true;
                    CaldroidGridAdapter.setChooseSat(true);
                    zhouliu_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_red));
                    zhouliu_tv.setTextColor(getResources().getColor(R.color.c5));
                }
                else
                {
                    zhouliuBeChoosed = false;
                    CaldroidGridAdapter.setChooseSat(false);
                    zhouliu_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_white));
                    zhouliu_tv.setTextColor(getResources().getColor(R.color.c7));
                }
                caldroidFragment.refreshView();
            }
        });
        zhouri.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!zhouriBeChoosed)
                {
                    zhouriBeChoosed = true;
                    CaldroidGridAdapter.setChooseSun(true);
                    zhouri_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_red));
                    zhouri_tv.setTextColor(getResources().getColor(R.color.c5));
                }
                else
                {
                    zhouriBeChoosed = false;
                    CaldroidGridAdapter.setChooseSun(false);
                    zhouri_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_white));
                    zhouri_tv.setTextColor(getResources().getColor(R.color.c7));
                }
                caldroidFragment.refreshView();
            }
        });

        calendar_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TaskTool.carOwnerSetNotRent(casSn, CaldroidGridAdapter.noRentDaySecs, app, new HttpResponse.NetWorkResponse<UUResponseData>()
                {
                    @Override public void onSuccessResponse(UUResponseData responseData)
                    {
                        onBackPressed();
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

    @InjectView(R.id.calendar_save)
    TextView calendar_save;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_not_rent_time);
        ButterKnife.inject(this);

        casSn = getIntent().getStringExtra(SysConfig.CAR_NAME);
        app = (UUAppCar) getApplication();

//        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        caldroidFragment = new CaldroidFragment();

        setCustomResourceForDates();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener()
        {

            @Override
            public void onSelectDate(Date date, View view)
            {
//                Toast.makeText(getApplicationContext(), formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
                CaldroidGridAdapter.setOneDay(date);
                judgeState();
                caldroidFragment.refreshView();
            }

            @Override
            public void onChangeMonth(int month, int year)
            {
//                String text = "month: " + month + " year: " + year;
//                Toast.makeText(getApplicationContext(), text,
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view)
            {
//                Toast.makeText(getApplicationContext(),
//                        "Long click " + formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated()
            {
                if (caldroidFragment.getLeftArrowButton() != null)
                {
//                    Toast.makeText(getApplicationContext(),
//                            "Caldroid view is created", Toast.LENGTH_SHORT)
//                            .show();
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        initOnClick();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    private List<Integer> noRentDaySecs;
    private List<Integer> orderNoRentDaySecs;

    private void initData()
    {
        TaskTool.carOwnerEnterSetNotRentRequest(casSn, app, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override public void onSuccessResponse(UUResponseData responseData)
            {
                try
                {
                    CarInterface.CarOwnerEnterSetNotRentResponse response = CarInterface.CarOwnerEnterSetNotRentResponse.parseFrom(responseData.getBusiData());
                    if (response.getRet() == -1)
                    {
                        mAllFramelayout.noDataReloading();
                        initData();
                        return;
                    }
                    noRentDaySecs = response.getNoRentDaySecsList();
                    orderNoRentDaySecs = response.getOrderNoRentDaySecsList();

                    CaldroidGridAdapter.setNoRentDaySecs(noRentDaySecs);
                    CaldroidGridAdapter.setOrderNoRentDaySecs(orderNoRentDaySecs);

                    judgeState();

                    mAllFramelayout.makeProgreeDismiss();
                    caldroidFragment.refreshView();
                }
                catch (InvalidProtocolBufferException e)
                {
                    e.printStackTrace();
                }
            }

            @Override public void onError(VolleyError errorResponse)
            {

                mAllFramelayout.makeProgreeNoData();
            }

            @Override public void networkFinish()
            {

            }
        });

    }

    private void judgeState()
    {

        noRentDaySecs = CaldroidGridAdapter.noRentDaySecs;
        orderNoRentDaySecs = CaldroidGridAdapter.orderNoRentDaySecs;

        gongzuoriBeChoosed = true;
        zhouliuBeChoosed = true;
        zhouriBeChoosed = true;

        Date now = new Date();
        now.setMinutes(0);
        now.setHours(0);
        now.setSeconds(0);
        int nowTime = (int) (now.getTime() / 1000);
        int startDay = now.getDay();
        for (int start = 0; start < 181; start++)
        {
            if (startDay >= 1 && startDay <= 5)
            {
                if (noRentDaySecs.contains(nowTime + (start * 24 * 60 * 60)))
                {
                }
                else
                {
                    gongzuoriBeChoosed = false;
                }
            }
            else if (startDay == 6)
            {
                if (noRentDaySecs.contains(nowTime + (start * 24 * 60 * 60)))
                {
                }
                else
                {
                    zhouliuBeChoosed = false;
                }
            }
            else if (startDay == 0)
            {

                if (noRentDaySecs.contains(nowTime + (start * 24 * 60 * 60)))
                {
                }
                else
                {
                    zhouriBeChoosed = false;
                }
            }
            startDay++;
            if (startDay == 7)
            {
                startDay = 0;
            }
        }

        if (gongzuoriBeChoosed)
        {
            gongzuori_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_red));
            gongzuori_tv.setTextColor(getResources().getColor(R.color.c5));
        }
        else
        {
            gongzuori_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_white));
            gongzuori_tv.setTextColor(getResources().getColor(R.color.c7));
        }

        if (zhouliuBeChoosed)
        {
            zhouliu_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_red));
            zhouliu_tv.setTextColor(getResources().getColor(R.color.c5));
        }
        else
        {
            zhouliu_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_white));
            zhouliu_tv.setTextColor(getResources().getColor(R.color.c7));
        }

        if (zhouriBeChoosed)
        {
            zhouri_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_red));
            zhouri_tv.setTextColor(getResources().getColor(R.color.c5));
        }
        else
        {
            zhouri_btn.setImageDrawable(getResources().getDrawable(R.drawable.calendar_white));
            zhouri_tv.setTextColor(getResources().getColor(R.color.c7));
        }

        CaldroidGridAdapter.chooseWorkDay = gongzuoriBeChoosed;
        CaldroidGridAdapter.chooseSat = zhouliuBeChoosed;
        CaldroidGridAdapter.chooseSun = zhouriBeChoosed;

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        CaldroidGridAdapter.setNoRentDaySecs(new ArrayList<Integer>());
        CaldroidGridAdapter.setOrderNoRentDaySecs(new ArrayList<Integer>());
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
