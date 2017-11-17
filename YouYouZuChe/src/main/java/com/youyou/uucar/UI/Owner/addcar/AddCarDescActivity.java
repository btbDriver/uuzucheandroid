package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.R;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 车辆描述
 */
public class AddCarDescActivity extends Activity
{
    public String tag = "Desc";
    @InjectView(R.id.desc)
    EditText ruleEt;
    Context                 context;
    //    CarReleaseModel model;
//    CarReleaseService service;
    CarCommon.CarDetailInfo carContentModel;
    private String planteNumber, carType, carSn, sId;

    @InjectView(R.id.desc)
    EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.add_car_desc);
        ButterKnife.inject(this);
        getIntentData();
    }


    public void getIntentData()
    {
        sId = getIntent().getStringExtra(SysConfig.S_ID);
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
        String descs;
        descs = getIntent().getStringExtra("car_desc");
        if (descs != null)
        {
            desc.setText(descs);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_desc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (id == R.id.action_save)
        {
            saveModel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData()
    {
        carContentModel = Config.sCarContentModel;


        if (carContentModel != null)
        {

            if (carContentModel.getCarDesc() != null && !carContentModel.getCarDesc().trim().equals(""))
            {
                String carDesc = carContentModel.getCarDesc();
                ruleEt.setText(carDesc);
                ruleEt.setSelection(carDesc.length());
            }
        }
    }

    private void saveModel()
    {

        Config.showProgressDialog(context, false, null);

        String remark = ruleEt.getText().toString().trim();
        CarInterface.UpdateCarInfo.Request.Builder request = CarInterface.UpdateCarInfo.Request.newBuilder();
        request.setCarId(carSn);
        List<CarInterface.UpdateCarInfo.UpodateCarParams> list = new ArrayList<CarInterface.UpdateCarInfo.UpodateCarParams>();
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("carDesc").setValue(remark).build());
        request.addAllParams(list);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UpdateCarInfo_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {

                if (responseData.getRet() == 0)
                {

                    try
                    {
                        CarInterface.UpdateCarInfo.Response response = CarInterface.UpdateCarInfo.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0)
                        {
                            AddCarDescActivity.this.setResult(Activity.RESULT_OK);
                            AddCarDescActivity.this.finish();
                        }

                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse)
            {

            }

            @Override
            public void networkFinish()
            {
                Config.dismissProgress();
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getData();
    }
}
