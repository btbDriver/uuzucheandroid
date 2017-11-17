package com.youyou.uucar.PB;

import android.os.Bundle;

import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.PB.impl.CommonModel;
import com.youyou.uucar.PB.impl.GetCarDetailInfoModel;
import com.youyou.uucar.PB.impl.QueryLeaseTermModel;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;

import java.util.List;

/**
 * Created by wangyi on 12/26/14.
 */
public class TaskTool
{

    public static interface FLAG
    {
        int SetLeaseTermRequest            = 1001;
        int SetRefuseRentRequest           = 1002;
        int SetAutoAcceptOrderRequest      = 1003;
        int CarOwnerEnterSetNotRentRequest = 1004;
        int CarOwnerSetNotRentRequest      = 1005;
        int getCarInfo                     = 1006;
        int queryLeaseTerm                 = 1007;
    }

    public static void getCarDetailInfo(String carId, String passedMsg, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        GetCarDetailInfoModel.GetCarDetailInfoRequestModel requestModel = new GetCarDetailInfoModel.GetCarDetailInfoRequestModel();
        requestModel.carId = carId;
        requestModel.passedMsg = passedMsg;
        genwangluojiaohuyixia(FLAG.getCarInfo, requestModel, callback);

    }

    public static void queryLeaseTerm(String carSn, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        QueryLeaseTermModel.QueryLeaseTermRequestModel requestModel = new QueryLeaseTermModel.QueryLeaseTermRequestModel();
        requestModel.carSn = carSn;
        genwangluojiaohuyixia(FLAG.queryLeaseTerm, requestModel, callback);
    }

    public static void setShortLeaseTerm(String carSn, CarInterface.Leaseterm leaseterm, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        setLeaseTerm(carSn, 1, leaseterm, app, callback);
    }

    public static void setLongLeaseTerm(String carSn, CarInterface.Leaseterm leaseterm, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        setLeaseTerm(carSn, 2, leaseterm, app, callback);
    }


    private static void setLeaseTerm(String carSn, int type, CarInterface.Leaseterm leaseterm, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        CommonModel.SetLeaseTermRequestModel requestModel = new CommonModel.SetLeaseTermRequestModel();
        requestModel.carSn = carSn;
        requestModel.type = type;
        requestModel.leaseterm = leaseterm;
        genwangluojiaohuyixia(FLAG.SetLeaseTermRequest, requestModel, callback);
    }

    public static void setRefuseRent(String carSn, int type, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        CommonModel.SetRefuseRentRequestModel requestModel = new CommonModel.SetRefuseRentRequestModel();
        requestModel.carSn = carSn;
        requestModel.type = type;
        genwangluojiaohuyixia(FLAG.SetRefuseRentRequest, requestModel, callback);
    }

    public static void setAutoAcceptOrder(String carSn, int type, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        CommonModel.SetAutoAcceptOrderRequestModel requestModel = new CommonModel.SetAutoAcceptOrderRequestModel();
        requestModel.carSn = carSn;
        requestModel.type = type;
        genwangluojiaohuyixia(FLAG.SetAutoAcceptOrderRequest, requestModel, callback);
    }

    /**
     * 跟网络交互1下
     */
    private static void genwangluojiaohuyixia(final int flag, BaseModel model, final HttpResponse.NetWorkResponse<UUResponseData> callback)
    {

        NetworkTask task = null;
        switch (flag)
        {
            case FLAG.queryLeaseTerm:
                CarInterface.QueryLeaseTermRequest.Builder builder1007 = CarInterface.QueryLeaseTermRequest.newBuilder();
                QueryLeaseTermModel.QueryLeaseTermRequestModel requestModel = (QueryLeaseTermModel.QueryLeaseTermRequestModel) model;
                builder1007.setCarSn(requestModel.carSn);
                task = new NetworkTask(CmdCodeDef.CmdCode.QueryLeaseTerm_VALUE);
                task.setBusiData(builder1007.build().toByteArray());
                task.setTag("QueryLeaseTerm");
                break;
            case FLAG.getCarInfo:
                GetCarDetailInfoModel.GetCarDetailInfoRequestModel requestModel1006 = (GetCarDetailInfoModel.GetCarDetailInfoRequestModel) model;
                CarInterface.GetCarDetailInfo.Request.Builder builder = CarInterface.GetCarDetailInfo.Request.newBuilder();
                builder.setCarId(requestModel1006.carId);
                builder.setPassedMsg(requestModel1006.passedMsg);
                task = new NetworkTask(CmdCodeDef.CmdCode.GetCarDetailInfo_VALUE);
                task.setBusiData(builder.build().toByteArray());
                task.setTag("GetCarDetailInfo");
                break;
            case FLAG.SetLeaseTermRequest:
                CommonModel.SetLeaseTermRequestModel requestModel1001 = (CommonModel.SetLeaseTermRequestModel) model;
                CarInterface.SetLeaseTermRequest.Builder builder1001 = CarInterface.SetLeaseTermRequest.newBuilder();
                builder1001.setCarSn(requestModel1001.carSn);
                builder1001.setType(requestModel1001.type);
                builder1001.setLeaseterm(requestModel1001.leaseterm);
                task = new NetworkTask(CmdCodeDef.CmdCode.SetLeaseTerm_VALUE);
                task.setBusiData(builder1001.build().toByteArray());
                task.setTag("SetLeaseTerm");
                break;
            case FLAG.SetRefuseRentRequest:
                CommonModel.SetRefuseRentRequestModel requestModel1002 = (CommonModel.SetRefuseRentRequestModel) model;
                CarInterface.SetRefuseRentRequest.Builder builder1002 = CarInterface.SetRefuseRentRequest.newBuilder();
                builder1002.setCarSn(requestModel1002.carSn);
                builder1002.setType(requestModel1002.type);
                task = new NetworkTask(CmdCodeDef.CmdCode.SetRefuseRent_VALUE);
                task.setBusiData(builder1002.build().toByteArray());
                task.setTag("SetRefuseRent");
                break;
            case FLAG.SetAutoAcceptOrderRequest:
                CommonModel.SetAutoAcceptOrderRequestModel requestModel1003 = (CommonModel.SetAutoAcceptOrderRequestModel) model;
                CarInterface.SetAutoAcceptOrderRequest.Builder builder1003 = CarInterface.SetAutoAcceptOrderRequest.newBuilder();
                builder1003.setCarSn(requestModel1003.carSn);
                builder1003.setType(requestModel1003.type);
                task = new NetworkTask(CmdCodeDef.CmdCode.SetAutoAcceptOrder_VALUE);
                task.setBusiData(builder1003.build().toByteArray());
                task.setTag("SetAutoAcceptOrder");
                break;
            case FLAG.CarOwnerEnterSetNotRentRequest:
                CommonModel.CarOwnerEnterSetNotRentRequestModel requestModel1004 = (CommonModel.CarOwnerEnterSetNotRentRequestModel) model;
                CarInterface.CarOwnerEnterSetNotRentRequest.Builder builder1004 = CarInterface.CarOwnerEnterSetNotRentRequest.newBuilder();
                builder1004.setCarSn(requestModel1004.carSn);
                task = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerEnterSetNotRent_VALUE);
                task.setBusiData(builder1004.build().toByteArray());
                task.setTag("CarOwnerEnterSetNotRent");
                break;
            case FLAG.CarOwnerSetNotRentRequest:
                CommonModel.CarOwnerSetNotRentRequestModel requestModel1005 = (CommonModel.CarOwnerSetNotRentRequestModel) model;
                CarInterface.CarOwnerSetNotRentRequest.Builder builder1005 = CarInterface.CarOwnerSetNotRentRequest.newBuilder();
                builder1005.setCarSn(requestModel1005.carSn);
                for (int val : requestModel1005.noRentDaySecs)
                {
                    builder1005.addNoRentDaySecs(val);
                }
                task = new NetworkTask(CmdCodeDef.CmdCode.CarOwnerSetNotRent_VALUE);
                task.setBusiData(builder1005.build().toByteArray());
                task.setTag("CarOwnerSetNotRent");
                break;
            default:
                break;
        }
        NetworkUtils.executeNetwork(task, callback);
    }

    public static void carOwnerSetNotRent(String carSn, List<Integer> noRentDaySecs, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        CommonModel.CarOwnerSetNotRentRequestModel requestModel = new CommonModel.CarOwnerSetNotRentRequestModel();
        requestModel.carSn = carSn;
        requestModel.noRentDaySecs = noRentDaySecs;
        genwangluojiaohuyixia(FLAG.CarOwnerSetNotRentRequest, requestModel, callback);
    }

    public static void carOwnerEnterSetNotRentRequest(String carSn, UUAppCar app, HttpResponse.NetWorkResponse<UUResponseData> callback)
    {
        CommonModel.CarOwnerEnterSetNotRentRequestModel requestModel = new CommonModel.CarOwnerEnterSetNotRentRequestModel();
        requestModel.carSn = carSn;
        genwangluojiaohuyixia(FLAG.CarOwnerEnterSetNotRentRequest, requestModel, callback);
    }


}
