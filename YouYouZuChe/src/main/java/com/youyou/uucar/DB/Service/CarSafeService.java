package com.youyou.uucar.DB.Service;

import android.content.ContentValues;
import android.content.Context;

import com.youyou.uucar.DB.Model.CarSafeModel;
import com.youyou.uucar.Utils.Support.DBUtils.BaseDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taurusxi on 14-7-4.
 */
public class CarSafeService {


    public BaseDao dao;
    public static final String TABLE_NAME = "CAR_SAFE";

    public CarSafeService(Context context) {
        dao = new BaseDao(context);
    }


    // 获取数据
    public CarSafeModel getModel(Class clazz, String[] selectionArgs) {
        CarSafeModel model = (CarSafeModel) dao
                .getModel("select * from CAR_SAFE where CAR_SN=? ", clazz,
                        selectionArgs);
        return model;
    }


    // 获取数据列表
    public List<CarSafeModel> getModelList(Class clazz) {
        List<CarSafeModel> result = new ArrayList<CarSafeModel>();
        result = dao.getModelList(
                "select * from CAR_SAFE ", clazz,
                null);
        return result;
    }

    // 插入数据
    public Boolean insModel(CarSafeModel model) {
        ContentValues cv = makeCv(model);
        Boolean insFlag = (Boolean) dao.insert(TABLE_NAME, cv);
        return insFlag;
    }


    // 修改保存数据
    public Boolean modifyModel(CarSafeModel model) {
        ContentValues cv = makeCv(model);
        Boolean insFlag = (Boolean) dao.update(TABLE_NAME, cv, " ID=?",
                new String[]{model.id});
        return insFlag;
    }


    // 构造用电申请信息的ContentValues
    public ContentValues makeCv(CarSafeModel model) {
        ContentValues cv = new ContentValues();
        cv.put("PLATE_NUMBER", model.plateNumber);
        cv.put("CAR_TYPE", model.carType);
        cv.put("CAR_SN", model.carSn);
        cv.put("CAR_NAME", model.carName);
        cv.put("ID_CARD_FRONT_PIC", model.idCardFrontPic);
        cv.put("ID_CARD_FRONT_STATE", model.idCardFrontState);
        cv.put("INSURANCE_SCAN_PIC", model.insuranceScanPic);
        cv.put("INSURANCE_SCAN_STATE", model.insuranceScanState);
        cv.put("DRIVER_FRONT_PIC", model.driverFrontPic);
        cv.put("DRIVER_FRONT_STATE", model.driverFrontState);
        cv.put("DRIVER_BACK_PIC", model.driverBackPic);
        cv.put("DRIVER_BACK_STATE", model.driverBackState);

        return cv;
    }
}
