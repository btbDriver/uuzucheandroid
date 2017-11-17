package com.youyou.uucar.DB.Model;

import com.youyou.uucar.Utils.Support.Config;

/**
 * Created by Administrator on 2014/7/7.
 */
public class UserModel {
    public String id;
    public int uid;
    public int userId;
    public String phone = "";
    public String sid = "";

    public String idCardFrontPic = "";// 身份证正面照片路径
    public String idCardFrontState = "";// 身份证正面照片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态
    public String idCardBackPic = "";// 身份证正面照片路径
    public String idCardBackState = "";// 身份证正面照片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String driverFrontPic = ""; //驾驶证正面图片路径
    public String driverFrontState = "";// 驾驶证正面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    public String driverBackPic = "";//驾驶证反面图片路径
    public String driverBackState = "";// 驾驶证反面图片状态 0代表未拍照状态 1代表拍照合格状态 2代表拍照审核未通过状态

    /**
     * 是否签协议过
     * 0=没签过
     * 1 =签过,
     */
    public String sign = "";
    /**
     * 用户身份状态
     * 1=新用户
     * 2=身份验证中(身份证,驾照都上传完毕)
     * 3=租客身份
     * 4=申请驳回(refuse_reason驳回原因 refuse_pic 有问题图片)
     */
    public String userStatus = User.USER_STATUS_NEW;
    /**
     * 车主身份状态
     * 1.没车
     * 2.身份验证中
     * 3.车主身份
     * 4.申请驳回 (refuse_reason驳回原因 refuse_pic 有问题图片)
     */
    public String carStatus = User.CAR_STATUS_NOCAR;
    /**
     * 续sid的时候需要用的密码
     */
    public String pwdcode = "";
    /**
     * 姓名
     */
    public String name = "";
    /**
     * 头像连接
     */
    public String head = "";
}
