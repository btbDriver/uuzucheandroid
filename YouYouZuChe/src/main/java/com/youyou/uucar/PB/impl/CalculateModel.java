package com.youyou.uucar.PB.impl;

import com.uu.client.bean.car.common.CarCommon;
import com.youyou.uucar.PB.BaseModel;

/**
 * Created by 16515_000 on 2014/12/2.
 */
public class CalculateModel {
    public static class CalculateRequestModel extends BaseModel
    {
        public String brand;
        public String carModel;
        public String cityId;
        public CarCommon.CarTransmissionType gear;
        public int year;
    }

    public static class CalculateResponseModel extends BaseModel {
        public int ret = -1;
        public float price;
        public float handValue;
        public int day;
        public String tip;

        @Override
        public String toString() {
            return "ResponseModel{" +
                    "ret=" + ret +
                    ", price=" + price +
                    ", handValue=" + handValue +
                    ", day=" + day +
                    ", tip='" + tip + '\'' +
                    '}';
        }
    }

}
