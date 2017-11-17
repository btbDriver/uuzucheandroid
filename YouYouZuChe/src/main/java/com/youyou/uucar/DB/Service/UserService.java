package com.youyou.uucar.DB.Service;

import android.content.ContentValues;
import android.content.Context;

import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.Utils.Support.DBUtils.BaseDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/7.
 */
public class UserService {
    public BaseDao dao;
    public static final String TABLE_NAME = "USER_TABLE";

    public UserService(Context context) {
        dao = new BaseDao(context);
    }

    public UserModel getModel(Class clazz, String[] selectionArgs) {
        UserModel model = (UserModel) dao.getModel("select * from USER_TABLE where PHONE=? ", clazz, selectionArgs);
        return model;
    }

    public User getUser(Class clazz, String[] selectionArgs) {
        User model = (User) dao.getModel("select * from USER_TABLE where PHONE=? ", clazz, selectionArgs);
        return model;
    }

    public List<UserModel> getModelList(Class clazz) {
        List<UserModel> result = new ArrayList<UserModel>();
        result = dao.getModelList(
                "select * from USER_TABLE ", clazz,
                null);
        return result;
    }

    public List<User> getUserList(Class clazz) {
        List<User> result = new ArrayList<User>();
        result = dao.getModelList(
                "select * from USER_TABLE ", clazz,
                null);
        return result;
    }

    // 插入数据
    public Boolean insModel(UserModel model) {
        ContentValues cv = makeCv(model);
        Boolean insFlag = (Boolean) dao.insert(TABLE_NAME, cv);
        return insFlag;
    }


    // 修改保存数据
    public Boolean modifyModel(UserModel model) {
        ContentValues cv = makeCv(model);
        Boolean insFlag = (Boolean) dao.update(TABLE_NAME, cv, " ID=?",
                new String[]{model.id});
        return insFlag;
    }

    // 修改保存数据
    public Boolean delModel(UserModel model) {
        ContentValues cv = makeCv(model);
        Boolean insFlag = (Boolean) dao.delete(TABLE_NAME, " ID=?",
                new String[]{model.id});
        return insFlag;
    }

    // 构造用电申请信息的ContentValues
    public ContentValues makeCv(UserModel model) {
        ContentValues cv = new ContentValues();
        cv.put("PHONE", model.phone);
        cv.put("SID", model.sid);
//        cv.put("USER_ID",model.userId);
//        cv.put("USER_ID", Integer.parseInt(model.sid));
        cv.put("ID_CARD_FRONT_PIC", model.idCardFrontPic);
        cv.put("ID_CARD_FRONT_STATE", model.idCardFrontState);
        cv.put("ID_CARD_BACK_PIC", model.idCardBackPic);
        cv.put("ID_CARD_BACK_STATE", model.idCardBackState);
        cv.put("DRIVER_FRONT_PIC", model.driverFrontPic);
        cv.put("DRIVER_FRONT_STATE", model.driverFrontState);
        cv.put("DRIVER_BACK_PIC", model.driverBackPic);
        cv.put("DRIVER_BACK_STATE", model.driverBackState);
        cv.put("PWDCODE", model.pwdcode);
        cv.put("NAME", model.name);
        cv.put("HEAD", model.head);
        cv.put("SIGN", model.sign);
        cv.put("USER_STATUS", model.userStatus);
        cv.put("CAR_STATUS", model.carStatus);

        return cv;
    }
}
