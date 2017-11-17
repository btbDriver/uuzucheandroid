package com.youyou.uucar.DB.Model;

/**
 * Created by taurusxi on 14-7-5.
 */
public class CarPhotoModel {

    public String id;//自增加

    public String carSn;//车辆标识

    public String plateNumber;//车牌号

    public String carType;//汽车类型
    public String carName;//汽车名称

    public String coverPic;// 车辆封面照片
    public String coverState;// 车辆封面照片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String zhengqianPic;//车辆正前照片路径
    public String zhengqianState;// 交强险扫描件状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String youhouPic; //车辆右后图片路径
    public String youhouState;// 车辆右后图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String zhenghouPic;//驾驶证反面图片路径
    public String zhenghouState;// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String zuocemianPic;//驾驶证反面图片路径
    public String zuocemianState;// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String zhongkongtaiPic;//驾驶证反面图片路径
    public String zhongkongtaiState;// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态


    public String carfrontPic;//驾驶证反面图片路径
    public String carfrontState;// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String carBackPic;//驾驶证反面图片路径
    public String carBackState;// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态


}
