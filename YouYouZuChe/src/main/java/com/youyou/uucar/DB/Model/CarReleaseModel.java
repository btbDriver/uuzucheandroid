package com.youyou.uucar.DB.Model;

/**
 * Created by taurusxi on 14-7-4.
 */
public class CarReleaseModel {

    public String id;//自增加
    public String sId;// 用户标识
    public String carSn;// 车辆标识
    public String plateNumber;//车牌号
    public String carType;//汽车类型
    public String carName;//汽车名称
    public String carInfoFlag;//  车辆信息  0表示未完成，1表示已经完成
    public String locationFlag;// 交车地址 0表示未完成，1表示已经完成
    public String priceFlag;// 出租价格 0表示未完成，1表示已经完成
    public String ruleFlag;// 车辆描述 0表示未完成，1表示已经完成
    public String insuranceInfoFlag;//保险资料  0表示未完成，1表示已经完成
    public String carPhotoFlag;// 车辆照片  0表示未完成，1表示已经完成
    public String remark;//车辆描述
    public String rule;//车辆描述规则 0表示未选择，1表示选择
    public String rentalPrice;//出租价格
    public String state; //发布状态

}
