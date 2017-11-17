package com.youyou.uucar.DB.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16515_000 on 2014/9/7.
 */
public class OpenCityModel {
    public int version = 0;
    private static OpenCityModel model;

    private static Context context;

    //如果没有过CityModel,读取默认的....这个方法会随时加的哟
    public static OpenCityModel getInstance(Context context) {
        OpenCityModel.context = context;
        SharedPreferences sp = context.getSharedPreferences("opencity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        if (sp.getInt("length", 0) == 0) {
            edit.putInt("length", 1);
            edit.commit();
            for (int i = 0; i < sp.getInt("length", 0); i++) {
                edit.putString("length" + i + "name", "北京");
                edit.putInt("length" + i + "zoom", 1);
                edit.putString("length" + i + "cityid", "010");
                edit.putInt("length" + i + "region", 1);
                edit.putString("length" + i + "shortName", "京");
                edit.putString("length" + i + "lat", "40.078731536865234");
                edit.putString("length" + i + "lng", "116.34552764892578");
            }
            edit.commit();
        }
        ArrayList<CarInterface.QueryAvailableCitys.City> openCity = new ArrayList<CarInterface.QueryAvailableCitys.City>();
        for (int i = 0; i < sp.getInt("length", 0); i++) {
            CarInterface.QueryAvailableCitys.City.Builder builder = CarInterface.QueryAvailableCitys.City.newBuilder();
            builder.setZoom(sp.getInt("length" + i + "zoom", 0));
            builder.setName(sp.getString("length" + i + "name", ""));
            builder.setCityId(sp.getString("length" + i + "cityid", ""));
            builder.setRegion(sp.getInt("length" + i + "region", 0));
            builder.setShortName(sp.getString("length" + i + "shortName", ""));
            UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
            position.setLat(Double.parseDouble(sp.getString("length" + i + "lat", "0")));
            position.setLng(Double.parseDouble(sp.getString("length" + i + "lng", "0")));
            builder.setCenterPosition(position);
//            MLog.e("OpenCityModel", "name = " + builder.getName() + "     " + builder.build().getName() + "    " + sp.getString("length" + i + "name", ""));
            openCity.add(builder.build());
        }
        Config.openCity.clear();
        Config.openCity.addAll(openCity);

        if (model == null) {
            model = new OpenCityModel();
        }
        return model;
    }

    public void setVersion(int version) {
        this.version = version;
        SharedPreferences sp = context.getSharedPreferences("opencity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("version", version);
        edit.commit();

    }

    public int getVersion() {
        SharedPreferences sp = context.getSharedPreferences("opencity", Context.MODE_PRIVATE);
        version = sp.getInt("version", 0);
        return version;
    }


    public void setOpenCity(List<CarInterface.QueryAvailableCitys.City> city) {
        Config.openCity.clear();
        Config.openCity.addAll(city);
        SharedPreferences sp = context.getSharedPreferences("opencity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("length", city.size());
        edit.clear();
        for (int i = 0; i < city.size(); i++) {
            edit.putString("length" + i + "name", Config.openCity.get(i).getName());
            edit.putInt("length" + i + "zoom", Config.openCity.get(i).getZoom());
            edit.putString("length" + i + "cityid", Config.openCity.get(i).getCityId());
            edit.putInt("length" + i + "region", Config.openCity.get(i).getRegion());
            edit.putString("length" + i + "shortName", Config.openCity.get(i).getShortName());
            edit.putString("length" + i + "lat", Config.openCity.get(i).getCenterPosition().getLat() + "");
            edit.putString("length" + i + "lng", Config.openCity.get(i).getCenterPosition().getLng() + "");
        }
        edit.commit();
    }

    public List<CarInterface.QueryAvailableCitys.City> getOpenCity() {
        if (Config.openCity.size() == 0) {
            SharedPreferences sp = context.getSharedPreferences("opencity", Context.MODE_PRIVATE);
            for (int i = 0; i < sp.getInt("length", 0); i++) {
                CarInterface.QueryAvailableCitys.City.Builder builder = CarInterface.QueryAvailableCitys.City.newBuilder();
                builder.setZoom(sp.getInt("length" + i + "zoom", 0));
                builder.setName(sp.getString("length" + i + "name", ""));
                builder.setCityId(sp.getString("length" + i + "cityid", ""));
                builder.setRegion(sp.getInt("length" + i + "region", 0));
                builder.setShortName(sp.getString("length" + i + "shortName", ""));
                UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
                position.setLat(Double.parseDouble(sp.getString("length" + i + "lat", "0")));
                position.setLng(Double.parseDouble(sp.getString("length" + i + "lng", "0")));
                builder.setCenterPosition(position);
                Config.openCity.add(builder.build());
            }
        }
        return Config.openCity;
    }


}
