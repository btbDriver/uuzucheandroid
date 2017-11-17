package com.youyou.uucar.PB.impl;

import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.PB.BaseModel;

import java.util.List;

/**
 * Created by 16515_000 on 2014/12/27.
 */
public class StrokeModel
{
    public static class StrokeRequestModel extends BaseModel
    {
        public OrderFormCommon.TripListQueryType queryType;
        public UuCommon.PageNoRequest.Builder pageRequest = UuCommon.PageNoRequest.newBuilder();
        public UuCommon.PageNoResult pageNoResult;
    }

    public static class StrokeResponseModel extends BaseModel
    {
        public int                                 ret;
        public List<OrderFormCommon.TripOrderCard> cards;//行程页card
        public UuCommon.PageNoResult               pageResult;//翻页返回
        public int waitCommentCount       = 0;//待评价订单数
        public int waitRenterCommentCount = 0;//租客待评价订单数
    }
}