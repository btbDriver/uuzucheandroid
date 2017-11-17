package com.youyou.uucar.PB.impl;


import com.youyou.uucar.PB.BaseModel;

/**
 * Created by wangyi on 12/25/14.
 */
public class ReserveInformationModel {

    public static class ReserveInformationRequestModel extends BaseModel
    {

        public String carSn;
        public int leaseStart;
        public int leaseEnd;

    }

    public static class ReserveInformationResponseModel extends BaseModel {

        public int ret = -1;
        public String leaseTerm;
        public float rent;
        public float insurance;
        public float deposit;
//        optional string rentURL = 6; //租金url
//        optional string insuranceURL =7; //保险url
//        optional string depositURL =8; //押金url

        public String rentURL;
        public String insuranceURL;
        public String depositURL;

    }
}
