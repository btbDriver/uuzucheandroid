package com.youyou.uucar.DB.Model;

import com.uu.client.bean.car.common.CarCommon;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2014/7/7.
 */
public class FindCarListModel {
    //    public String car_sn = "";
//    public String brand  = "";
//    public String xinghao;
//    public String price_hour;
//    public String price_day;
//    public float  dis;
//    public String gearbox;
//    public int    ban;
//    public String address;
//    public String img;
//    public String  juli     = "";
    public boolean isSelect = false;
    public CarCommon.CarBriefInfo info;

    public void setBriefInfo(CarCommon.CarBriefInfo info) {
        this.info = info;
    }

//    public void fromJson(JSONObject json) throws JSONException
//    {
//        car_sn = json.getString("car_sn");
//        img = json.getString("head_img");
//        xinghao = json.getString("xinghao");
//        price_hour = json.getString("jiage");
////        price_day = (Integer.parseInt(price_hour) * 8) + "";
//        if (json.has("jiage_day"))
//        {
//            price_day = json.getString("jiage_day");
//        }
//
//        gearbox = json.getString("boxiang");
//        address = json.getString("address");
//        // juli = json.getString("juli");
//        dis = Float.parseFloat(json.getString("juli"));
//        juli = json.get("juli") + "";
//        float djuli = Float.parseFloat(juli);
//        if (djuli > 1)
//        {
//            juli = String.format("%.1f", djuli) + " km";
//        }
//        else
//        {
//            juli = ((int) (djuli * 1000)) + " m";
//        }
//        ban = Integer.parseInt(json.getString("ban"));
//    }
}
