package com.youyou.uucar.PB.impl;

import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.youyou.uucar.PB.BaseModel;

import java.util.List;

/**
 * Created by wangyi on 12/26/14.
 */
public class QueryLeaseTermModel {

    public static class QueryLeaseTermRequestModel extends BaseModel
    {
        public String carSn;
    }

    public static class QueryLeaseTermResponseModel extends BaseModel {
        public int ret = -1;
        public List<CarInterface.Leaseterm> leasetermList;
        public CarInterface.Leaseterm leasetermShort;
        public CarInterface.Leaseterm leasetermLong;
        public int refuseRentType;
        public int autoAcceptOrderType;
        public int displayAcceptOrder;
        public String AcceptOrderDesc;
    }
}
