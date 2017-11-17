package com.youyou.uucar.DB.Model;

import android.content.Context;

import com.uu.client.bean.login.LoginInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.DB.Service.UserService;

import org.json.JSONObject;

import java.util.List;

public class User extends UserModel
{
    public              String orderSN                 = "";
    public              String yue                     = "";
    /**
     * 优惠码
     */
    public              String invitation_code         = "";
    /**
     * 新用户
     */
    public static final String USER_STATUS_NEW         = "1";
    /**
     * 身份证驾照都传完
     */
    public static final String USER_STATUS_ALL         = "2";
    /**
     * 租客身份审核完成
     */
    public static final String USER_STATUS_ZUKE        = "3";
    /**
     * 租客身份审核未通过
     */
    public static final String USER_STATUS_ZUKE_NO     = "4";
    /**
     * 没车
     */
    public static final String CAR_STATUS_NOCAR        = "1";
    /**
     * 行驶证上传完成
     */
    public static final String CAR_STATUS_XINGSHIZHENG = "2";
    /**
     * 车主身份审核完成
     */
    public static final String CAR_STATUS_OWNER        = "3";
    /**
     * 车主身份审核未通过
     */
    public static final String CAR_STATUS_OWNER_NO     = "4";

    // user_status (
    // 1.新用户
    // 2.身份证传完,驾照没传完
    // 3.身份证驾照都传完
    // 4.租客身份审核完成 )
    // car_status(
    // 1.没车
    // 2.行驶证上传完成
    // 3.车主身份审核完成)
    public void fromJson(JSONObject content, Context context)
    {
        try
        {
            if (content.has("invitation_code"))
            {
                invitation_code = content.getString("invitation_code");
            }

            if (content.has("sign"))
            {
                sign = content.getString("sign");
            }

            if (content.has("sid"))
            {
                sid = content.getString("sid");
            }

            if (content.has("car_status"))
            {
                carStatus = content.getString("car_status");
            }

            if (content.has("user_status"))
            {
                userStatus = content.getString("user_status");
            }

            if (content.has("pwdcode"))
            {
                pwdcode = content.getString("pwdcode");
            }

            if (content.has("avatar"))
            {
                head = content.getString("avatar");
            }

            if (content.has("name"))
            {
                name = content.getString("name");
            }

            if (content.has("yue"))
            {
                yue = content.getString("yue");
            }

            saveModel(context);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    // user_status (
    // 1.新用户
    // 2.身份证传完,驾照没传完
    // 3.身份证驾照都传完
    // 4.租客身份审核完成 )
    // car_status(
    // 1.没车
    // 2.行驶证上传完成
    // 3.车主身份审核完成)
    public void fromJson(LoginInterface.SmsLoginSSL.Response content, Context context)
    {
        try
        {
//            if (content.has) {
//                invitation_code = content.getString("invitation_code");
//            }
            if (content.hasUserId())
            {
                sid = content.getUserId() + "";

            }
//            if (content.hasUserSecurityTicket()) {
//                sid = content.hasUserSecurityTicket() + "";
//            }
//            if (content.hasSign()) {
//                sign = content.getSign() + "";
//            }
            UserCommon.UserStatus userStatus1 = content.getUserStatus();
            if (userStatus1.hasCarStatus())
            {
                carStatus = userStatus1.getCarStatus() + "";
            }
            if (userStatus1.hasUserStatus())
            {
                userStatus = userStatus1.getUserStatus() + "";
            }
//            if (content.has("pwdcode")) {
//                pwdcode = content.getString("pwdcode");
//            }
            if (content.hasAvatar())
            {
                head = content.getAvatar();
            }
            if (content.hasName())
            {
                name = content.getName();
            }
//            if (content.has("yue")) {
//                yue = content.getString("yue");
//            }
            saveModel(context);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // user_status (
    // 1.新用户
    // 2.身份证传完,驾照没传完
    // 3.身份证驾照都传完
    // 4.租客身份审核完成 )
    // car_status(
    // 1.没车
    // 2.行驶证上传完成
    // 3.车主身份审核完成)
    public void fromPasswordJson(LoginInterface.LoginResponse content, Context context)
    {
        try
        {
//            if (content.has) {
//                invitation_code = content.getString("invitation_code");
//            }
            if (content.hasUserId())
            {
                sid = content.getUserId() + "";

            }
//            if (content.hasUserSecurityTicket()) {
//                sid = content.hasUserSecurityTicket() + "";
//            }
//            if (content.hasSign()) {
//                sign = content.getSign() + "";
//            }
            UserCommon.UserStatus userStatus1 = content.getUserStatus();
            if (userStatus1.hasCarStatus())
            {
                carStatus = userStatus1.getCarStatus() + "";
            }
            if (userStatus1.hasUserStatus())
            {
                userStatus = userStatus1.getUserStatus() + "";
            }
//            if (content.has("pwdcode")) {
//                pwdcode = content.getString("pwdcode");
//            }
            if (content.hasAvatar())
            {
                head = content.getAvatar();
            }
            if (content.hasName())
            {
                name = content.getName();
            }
//            if (content.has("yue")) {
//                yue = content.getString("yue");
//            }
            saveModel(context);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    UserModel   model;
    UserService service;

    public void saveModel(Context context)
    {
        service = new UserService(context);
        List<UserModel> models = service.getModelList(UserModel.class);
        if (models.size() == 0)
        {
            model = new UserModel();
            model.phone = phone;
            boolean flag = service.insModel(model);
            if (flag)
            {
                model = service.getModel(UserModel.class, new String[] {""});
            }
        }
        else
        {
            model = models.get(0);
        }
        model.phone = phone;
        model.sign = sign;
        model.sid = sid;
        model.userId = Integer.parseInt(sid);
        model.userStatus = userStatus;
        model.carStatus = carStatus;
        model.pwdcode = pwdcode;
        model.head = head;
        model.name = name;
        service.modifyModel(model);
    }

}
