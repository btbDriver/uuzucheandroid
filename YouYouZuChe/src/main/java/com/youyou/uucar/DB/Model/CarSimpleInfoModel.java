package com.youyou.uucar.DB.Model;

import com.uu.client.bean.car.common.CarCommon;

/**
 * Created by taurusxi on 14-7-10.
 */
public class CarSimpleInfoModel {

    public String sId;
    public String carType;
    public String carName;
    public String headImage;
    public String plateNumber;
    public float oneDayPrice;
    //    car_status_code 状态
//
//    1客服正在帮你发布
//
//    2暂不出租
//
//    3出租中
//
//    4待编辑
//
//    5审核不通过
//
//    6等待发布
    public String carSn;
    public CarCommon.CarStatus status;
}
