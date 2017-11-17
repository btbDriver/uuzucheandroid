package com.youyou.uucar.DB.Model;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.youyou.uucar.Utils.Support.CarConfig;

public class Car
{
    public static final String KEY_CITY    = "city";
    public static final String KEY_NUMBER  = "number";
    public static final String KEY_BRAND   = "brand";
    public static final String KEY_XINGHAO = "xinghao";
    public static final String KEY_CARINFO = "car_info";

    public Car()
    {
    }

    SharedPreferences sp;
    public CarInfo info;

    public Car(SharedPreferences sp) throws JSONException
    {
        this.sp = sp;
        city = sp.getString(KEY_CITY, "京 A");
        number = sp.getString(KEY_NUMBER, "");
        brand = sp.getString(KEY_BRAND, "");
        xinghao = sp.getString(KEY_XINGHAO, "");
        info = new CarInfo(new JSONObject(sp.getString(KEY_CARINFO, new JSONObject().toString())));
    }

    public void put(String key, String value)
    {
        Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String city    = "";
    public String number  = "";
    public String brand   = "";
    public String xinghao = "";

    public class CarInfo
    {
        public static final String  KEY_GEARBOX     = "gearbox";
        public static final String  KEY_CC          = "CC";
        public static final String  KEY_YEAR        = "yead";
        public static final String  KEY_SEAT        = "seat";
        public static final String  KEY_MILEAGE     = "mileage";
        public static final String  KEY_OIL         = "oil";
        public static final String  KEY_DAOCHELEIDA = "daocheleida";
        public static final String  KEY_GPS         = "gps";
        public              String  gearbox         = "";
        public              String  CC              = "";
        public              String  year            = "";
        public              String  seat            = "";
        public              String  mileage         = "";
        public              String  oil             = "";
        public              String  daocheleida     = "";
        public              String  gps             = "";
        public              boolean infoSuccess     = false;
        public JSONObject json;

        public CarInfo(JSONObject json) throws JSONException
        {
            this.json = json;
            int num = 0;
            if (json.has(KEY_GEARBOX))
            {
                num += 1;
                gearbox = CarConfig.getBOXIANG(json.getString(KEY_GEARBOX));
            }
            if (json.has(KEY_CC))
            {
                num += 1;
                CC = CarConfig.getCC(json.getString(KEY_CC));
            }
            if (json.has(KEY_YEAR))
            {
                num += 1;
                year = json.getString(KEY_YEAR);
            }
            if (json.has(KEY_SEAT))
            {
                num += 1;
                seat = CarConfig.getSEAT(json.getString(KEY_SEAT));
            }
            if (json.has(KEY_MILEAGE))
            {
                num += 1;
                mileage = CarConfig.getMILEAGE(json.getString(KEY_MILEAGE));
            }
            if (json.has(KEY_OIL))
            {
                num += 1;
                oil = CarConfig.getOIL(json.getString(KEY_OIL));
            }
            if (json.has(KEY_DAOCHELEIDA))
            {
                daocheleida = CarConfig.getDaocheleida(json.getString(KEY_DAOCHELEIDA));
            }
            if (json.has(KEY_GPS))
            {
                gps = CarConfig.getGPS(json.getString(KEY_GPS));
            }
            if (num == json.length() - 2)
            {
                infoSuccess = true;
            }
        }
    }
}
