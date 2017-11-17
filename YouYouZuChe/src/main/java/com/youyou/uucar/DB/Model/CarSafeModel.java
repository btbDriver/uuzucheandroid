package com.youyou.uucar.DB.Model;

/**
 * Created by taurusxi on 14-7-5.
 */
public class CarSafeModel {

    public String id;//自增加

    public String carSn;//车辆标识
    public String plateNumber;//车牌号

    public String carType;//汽车类型

    public String carName;//汽车名称

    public String idCardFrontPic;// 身份证正面照片路径
    public String idCardFrontState;// 身份证正面照片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String insuranceScanPic;//交强险扫描件路径
    public String insuranceScanState;// 交强险扫描件状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String driverFrontPic; //驾驶证正面图片路径
    public String driverFrontState;// 驾驶证正面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String driverBackPic;//驾驶证反面图片路径
    public String driverBackState;// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态
}
