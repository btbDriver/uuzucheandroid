package com.youyou.uucar.PB.impl;

import com.uu.client.bean.car.CarInterface;
import com.youyou.uucar.PB.BaseModel;

import java.util.List;

/**
 * Created by wangyi on 12/30/14.
 */
public class CommonModel {

    //租期（最短最长）设置请求    （响应类型：uu_common.proto的CommonReportResponse）
//    message SetLeaseTermRequest{
//        required string carSn = 1;         // 车辆唯一标识
//        required int32 type = 2;           // 1：设置最短租期    2：设置最长租期
//        optional Leaseterm leaseterm =3;   // 租期
//    }

    //暂时不可租请求       （响应类型：uu_common.proto的CommonReportResponse）
//    message SetRefuseRentRequest{
//        required string carSn =1;  // car唯一标识
//        required int32 type = 2;  // 1:可租   2：不可租
//    }

    //自动接受订单请求   （响应类型：uu_common.proto的CommonReportResponse）
//    message SetAutoAcceptOrderRequest{
//        required string carSn =1;  // car唯一标识
//        required int32 type =2;   // 0：接受     1:不接受
//    }

    // 通用的上报响应
//    message CommonReportResponse {
//        required int32 ret = 1; // 0：成功；-1：失败
//    }

    public static class SetLeaseTermRequestModel extends BaseModel
    {
        public String carSn;
        public int type;
        public CarInterface.Leaseterm leaseterm;
    }

    public static class SetRefuseRentRequestModel extends BaseModel {
        public String carSn;
        public int type;
    }


    public static class SetAutoAcceptOrderRequestModel extends BaseModel {
        public String carSn;
        public int type;
    }

    public static class CommonReportResponseModel extends BaseModel {
        public int ret = -1;
    }

    // 车主进入设置不可租页面的请求
    public static class CarOwnerEnterSetNotRentRequestModel extends BaseModel {
        public String carSn;
    }

    // 车主进入设置不可租页面的响应
    public static class CarOwnerEnterSetNotRentResponseModel extends BaseModel {
        public int ret = -1;
        public List<Integer> noRentDaySecs;
        public List<Integer> orderNoRentDaySecs;
    }

    // 车主设置不可租时间请求
    public static class CarOwnerSetNotRentRequestModel extends BaseModel {
        public String carSn;
        public List<Integer> noRentDaySecs;
    }

    // 车主设置不可租时间响应
    public static class CarOwnerSetNotRentResponseModel extends BaseModel {
        public int ret = -1;
        public String tipsMsg;
    }

}
