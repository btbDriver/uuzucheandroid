package com.youyou.uucar.DB.Service;

import android.content.ContentValues;
import android.content.Context;

import com.youyou.uucar.DB.Model.UserSecurityModel;
import com.youyou.uucar.Utils.Support.DBUtils.BaseDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/7.
 */
public class UserSecurityService {
    public BaseDao dao;
    public static final String TABLE_NAME = "USER_SECURITY";

    public UserSecurityService(Context context) {
        dao = new BaseDao(context);
    }

    public List<UserSecurityModel> getModelList(Class clazz) {
        List<UserSecurityModel> result = new ArrayList<UserSecurityModel>();
        result = dao.getModelList(
                "select * from USER_SECURITY ", clazz,
                null);
        return result;
    }

    // 插入数据
    public Boolean insModel(UserSecurityModel model) {
        ContentValues cv = makeCv(model);
        boolean insFlag = dao.insert(TABLE_NAME, cv);
        return insFlag;
    }


    // 删除数据
    public Boolean deletModel(UserSecurityModel model) {
        boolean insFlag = dao.delete(TABLE_NAME, "  ID=?",
                new String[]{model.id});
        return insFlag;
    }

    // 修改保存数据
    public Boolean modifyModel(UserSecurityModel model) {
        ContentValues cv = makeCv(model);
        boolean insFlag = dao.update(TABLE_NAME, cv, " ID=?",
                new String[]{model.id});
        return insFlag;
    }

    // 构造用电申请信息的ContentValues
    public ContentValues makeCv(UserSecurityModel model) {
        ContentValues cv = new ContentValues();
        cv.put("B3_KEY", model.b3Key);
        cv.put("B2", model.b2);
        cv.put("B3", model.b3);
        cv.put("USER_ID", model.userId);
        cv.put("START_TIME", model.startTime);
        cv.put("VALID_SECS", model.validSecs);
        cv.put("TICKET_FAILURE_TIME", model.ticketFailureTime);
        return cv;
    }
}
