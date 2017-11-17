package com.youyou.uucar.Utils.Support;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;


public class GDLocationListener implements AMapLocationListener {
    LocationListener listener;

    public GDLocationListener(LocationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLocationChanged(Location location) {
        MLog.e("GDLocationListener", "   onLocationChanged ");

    }

    @Override
    public void onProviderDisabled(String provider) {
        MLog.e("GDLocationListener", "   onProviderDisabled ");

    }

    @Override
    public void onProviderEnabled(String provider) {
        MLog.e("GDLocationListener", "   onProviderEnabled ");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        MLog.e("GDLocationListener", "   onStatusChanged ");
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        MLog.e("GDLocationListener", "   changed " + location);
        if (location != null) {
            Config.locationTime = System.currentTimeMillis();
            double geoLat = location.getLatitude();
            double geoLng = location.getLongitude();
            Config.lat = geoLat;
            Config.lng = geoLng;
            Bundle locBundle = location.getExtras();
            Config.currentCity = location.getCity();
            Config.currentAddress = locBundle.getString("desc");
            if (Config.currentCity == null) {
                Config.currentCity = "北京市";
            }
            if (Config.currentAddress == null) {
                Config.currentAddress = "";
            }

            if (listener != null) {
                listener.locationSuccess(geoLat, geoLng, locBundle.getString("desc"));
            }
//            String cityCode = "";
//            String desc = "";
            if (locBundle != null) {
                Config.cityCode = locBundle.getString("citycode");
//                desc = locBundle.getString("desc");
            }
            String str = ("定位成功:(" + geoLng + "," + geoLat + ")" + "\n精    度    :" + location.getAccuracy() + "米" + "\n定位方式:" + location.getProvider() + "\n城市编码:" + Config.cityCode + "\n位置描述:" + Config.currentAddress + "\n省:" + location.getProvince() + "\n市：" + location.getCity() + "\n区(县)：" + location.getDistrict() + "\n城市编码：" + location.getCityCode() + "\n区域编码：" + location.getAdCode());
            MLog.e("GDLocationListener", str);
        } else {
            listener.locationSuccess(0, 0, "");
        }
        Config.disableMyLocation(this);

    }
}
