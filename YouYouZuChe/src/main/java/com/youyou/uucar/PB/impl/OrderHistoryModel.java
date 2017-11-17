package com.youyou.uucar.PB.impl;

import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.PB.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 16515_000 on 2014/12/31.
 */
public class OrderHistoryModel
{

    public static class OrderHistoryRequestModel extends BaseModel
    {
        public UuCommon.PageNoRequest.Builder pageNoRequest = UuCommon.PageNoRequest.newBuilder();
    }

    public static class OrderHistoryResponseModel extends BaseModel
    {
        public int                                     ret  = -1;
        public List<OrderFormCommon.PreOrderHistoCard> list = new ArrayList<OrderFormCommon.PreOrderHistoCard>();
        public UuCommon.PageNoResult pageNoResult;
    }
}
