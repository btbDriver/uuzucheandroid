package com.youyou.uucar.PB.impl;

import com.uu.client.bean.car.common.CarCommon;
import com.youyou.uucar.PB.BaseModel;

/**
 * Created by wangyi on 12/26/14.
 */
public class GetCarDetailInfoModel {

    public static class GetCarDetailInfoRequestModel extends BaseModel
    {
        public String carId;
        public String passedMsg;
    }

    public static class GetCarDetailInfoResponseModel extends BaseModel {
        public int ret = -1;
        public CarCommon.CarDetailInfo carDetailInfo;
    }
}
