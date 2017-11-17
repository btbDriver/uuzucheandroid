package com.youyou.uucar.UI.Main.my;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectOpenCity extends BaseActivity {
    public String tag = "SelectOpenCity";
    public Activity context;
    public int select = 0;
    ListView list;
    SharedPreferences citysp;

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.select_open_city);
        ButterKnife.inject(this);
        context = this;
        citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        list = (ListView) findViewById(R.id.list);
        getCity();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    List<CarInterface.QueryAvailableCitys.City> citys = new ArrayList<CarInterface.QueryAvailableCitys.City>();

    public void getCity() {
        CarInterface.QueryAvailableCitys.Request.Builder request = CarInterface.QueryAvailableCitys.Request.newBuilder();
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryAvailableCitys_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {

            @Override
            public void onSuccessResponse(UUResponseData protoBuf) {
                if (protoBuf.getRet() == 0) {
                    try {
                        CarInterface.QueryAvailableCitys.Response response = CarInterface.QueryAvailableCitys.Response.parseFrom(protoBuf.getBusiData());
                        if (response.getRet() == 0) {
                            Config.openCity = response.getCitysList();
                            citys = Config.openCity;
//                    try
//                    {
                            for (int i = 0; i < citys.size(); i++) {
                                if (citysp.getString("city", "北京").indexOf(citys.get(i).getName()) != -1) {
                                    select = i;
                                    break;
                                }
                            }
//                    }
                            list.setAdapter(new MyAdapter());
                        }

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {

                mAllFramelayout.makeProgreeDismiss();
            }
        });
    }

    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return citys.size();
        }

        @Override
        public Object getItem(int position) {
            return citys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.select_open_city_item, null);
            final TextView name = (TextView) convertView.findViewById(R.id.name);
            if (position == select) {
                name.setTextColor(Color.parseColor("#333333"));
            } else {
                name.setTextColor(Color.parseColor("#b9b9b9"));
            }
            name.setText(citys.get(position).getName());
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Editor edit = citysp.edit();
                    edit.putString("city", name.getText().toString());
                    edit.commit();
                    Config.changeCityMap = true;
                    Config.changeCityList = true;
                    finish();
                }
            });
            return convertView;
        }
    }
}
