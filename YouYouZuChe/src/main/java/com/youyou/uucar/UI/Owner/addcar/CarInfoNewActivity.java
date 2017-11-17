package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.youyou.uucar.Utils.Support.CarConfig;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;


/**
 * 车辆详细情况，包括车辆的所有信息（变速箱，排量，注册年份，可载人数等等）
 */
public class CarInfoNewActivity extends Activity
{
    public static String tag          = CarInfoNewActivity.class.getSimpleName();
    public        int    gearboxWhich = 0;
    public        int    yearWhich    = 0;
    public        int    CCWhich      = 0;
    public        int    seatWhich    = 0;
    public        int    mileageWhich = 0;
    public        int    oilWhich     = 0;
    @InjectViews({R.id.gearbox_text, R.id.cc, R.id.year, R.id.seat, R.id.mileage, R.id.oil, R.id.set})
    List<RelativeLayout> roots;
    String[] years = new String[11];
    Calendar calendar;
    @InjectView(R.id.sure)
    TextView sure;
    //
    Dialog  dialog;
    Context context;
    boolean[] setCheck     = new boolean[2];
    boolean[] tempSetCheck = new boolean[2];
    public OnClickListener rootsClick = new OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            switch (v.getId())
            {
                case R.id.gearbox_text:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.gearbox_items), gearboxWhich, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            gearboxWhich = which;
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.gearbox_text)
                                {
                                    ((TextView) root.findViewById(R.id.text)).setText(getResources().getStringArray(R.array.gearbox_items)[gearboxWhich]);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.cc:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.cc_items), CCWhich, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            CCWhich = which;
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.cc)
                                {
                                    ((TextView) root.findViewById(R.id.text)).setText(getResources().getStringArray(R.array.cc_items)[which]);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.year:
                    builder.setSingleChoiceItems(years, yearWhich, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            yearWhich = which;
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.year)
                                {
                                    ((TextView) root.findViewById(R.id.text)).setText(years[yearWhich]);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.seat:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.seat_items), seatWhich, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            seatWhich = which;
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.seat)
                                {
                                    ((TextView) root.findViewById(R.id.text)).setText(getResources().getStringArray(R.array.seat_items)[which]);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.mileage:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.mileage_items), mileageWhich, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mileageWhich = which;
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.mileage)
                                {
                                    ((TextView) root.findViewById(R.id.text)).setText(getResources().getStringArray(R.array.mileage_items)[which]);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.oil:
                    builder.setSingleChoiceItems(getResources().getStringArray(R.array.oil_items), oilWhich, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            oilWhich = which;
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.oil)
                                {
                                    ((TextView) root.findViewById(R.id.text)).setText(getResources().getStringArray(R.array.oil_items)[which]);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.set:
                    builder.setMultiChoiceItems(getResources().getStringArray(R.array.car_set_items), setCheck, new DialogInterface.OnMultiChoiceClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked)
                        {
                            tempSetCheck[which] = isChecked;
                        }
                    });
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            for (RelativeLayout root : roots)
                            {
                                if (root.getId() == R.id.set)
                                {
                                    String text = "";
                                    setCheck[0] = tempSetCheck[0];
                                    setCheck[1] = tempSetCheck[1];
                                    if (tempSetCheck[0])
                                    {
                                        text = getResources().getStringArray(R.array.car_set_items)[0];
                                    }
                                    if (tempSetCheck[1])
                                    {
                                        text += " " + getResources().getStringArray(R.array.car_set_items)[1];
                                    }
                                    ((TextView) root.findViewById(R.id.text)).setText(text);
                                    setButtonState();
                                    setRootImage((RelativeLayout) v);
//                                    setJson((RelativeLayout) v);
                                    break;
                                }
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    break;
            }
        }
    };
    //    CarInfoModel model;
//    CarInfoService service;
//    CarReleaseService carReleaseService;
//    CarReleaseModel carReleaseModel;
    private String                  carSn;
    private CarCommon.CarDetailInfo carContentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        calendar = Calendar.getInstance();
        for (int i = 0; i < years.length; i++)
        {
            years[i] = ((calendar.get(Calendar.YEAR) - i) + "年");
        }
        context = this;
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
//        model = initData();
        setContentView(R.layout.car_info);
        ButterKnife.inject(this);
        initListener();
        carContentModel = Config.sCarContentModel;
        showData(carContentModel);
    }

    private void showData(CarCommon.CarDetailInfo carInfoModel)
    {

        if (carInfoModel != null)
        {
            if (carInfoModel.hasTransmissionType())
            {
                String[] transmissionTypeArray = getResources().getStringArray(R.array.gearbox_items);
                CarCommon.CarTransmissionType transmissionType = carInfoModel.getTransmissionType();
                gearboxWhich = transmissionType.getNumber() - 1;
                setRootText(roots.get(0), transmissionTypeArray[gearboxWhich]);
            }
            if (carInfoModel.hasDisplacementType())
            {
                String[] ccArray = getResources().getStringArray(R.array.cc_items);
                CCWhich = carInfoModel.getDisplacementType() - 1;
                setRootText(roots.get(1), ccArray[CCWhich]);
            }
            if (carInfoModel.hasCarRegYear())
            {
                for (int i = 0; i < years.length; i++)
                {
                    if ((carInfoModel.getCarRegYear() + "年").equals(years[i]))
                    {
                        yearWhich = i;
                        break;
                    }
                }
                setRootText(roots.get(2), carInfoModel.getCarRegYear() + "年");
            }
            if (carInfoModel.hasSeatsCount())
            {

                String[] seatArray = getResources().getStringArray(R.array.seat_items);
                seatWhich = carInfoModel.getSeatsCount() - 1;
                setRootText(roots.get(3), seatArray[seatWhich]);
            }
            if (carInfoModel.hasDrivingKM())
            {
                String[] MILEAGEArray = getResources().getStringArray(R.array.mileage_items);
                mileageWhich = carInfoModel.getDrivingKM() - 1;
                setRootText(roots.get(4), MILEAGEArray[mileageWhich]);
            }
            if (carInfoModel.hasGasType())
            {

                String[] oilArray = getResources().getStringArray(R.array.oil_items);
                oilWhich = carInfoModel.getGasType() - 1;
                setRootText(roots.get(5), oilArray[oilWhich]);
            }
            String str = "";
            tempSetCheck[0] = false;
            setCheck[0] = false;
            if (carInfoModel.hasHasRadar())
            {
                if (carInfoModel.getHasRadar())
                {
                    str += "倒车雷达";
                    tempSetCheck[0] = true;
                    setCheck[0] = true;
                }
            }
            tempSetCheck[1] = false;
            setCheck[1] = false;
            if (carInfoModel.hasHasGPS())
            {
                if (carInfoModel.getHasGPS())
                {
                    if (str.trim().equals(""))
                    {
                        str += "导航仪";
                    }
                    else
                    {
                        str += " 导航仪";
                    }
                    tempSetCheck[1] = true;
                    setCheck[1] = true;
                }
            }
            setRootText(roots.get(6), str);
        }
        for (RelativeLayout root : roots)
        {
            setRootImage(root);
        }
        setButtonState();
    }

    private void setButtonState()
    {

        boolean flag = true;

        for (int i = 0; i < roots.size() - 1; i++)
        {

            RelativeLayout layout = roots.get(i);
            TextView text = (TextView) layout.findViewById(R.id.text);
            if (text.getText().toString() == null || text.getText().toString().trim().equals(""))
            {
                flag = false;
                break;
            }

        }
        sure.setEnabled(flag);
    }

    private void initListener()
    {
        for (RelativeLayout root : roots)
        {
            root.setOnClickListener(rootsClick);
        }
    }

    @OnClick(R.id.sure)
    public void sureClick()
    {
        saveModel();
    }

    private void saveModel()
    {
        Config.showProgressDialog(context, false, null);
        CarInterface.UpdateCarInfo.Request.Builder request = CarInterface.UpdateCarInfo.Request.newBuilder();
        request.setCarId(carSn);
        List<CarInterface.UpdateCarInfo.UpodateCarParams> list = getUpodateCarParams(carContentModel);
        request.addAllParams(list);
        List<CarInterface.UpdateCarInfo.UpodateCarParams> paramsList = request.getParamsList();
        StringBuilder logStr = new StringBuilder();
        logStr.append("uploadCarParams = [");
        for (CarInterface.UpdateCarInfo.UpodateCarParams params : paramsList)
        {
            logStr.append(params.getKey() + "=" + params.getValue() + "; ");
        }
        logStr.append("]");
        MLog.e(tag, logStr.toString());
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UpdateCarInfo_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {

                if (responseData.getRet() == 0)
                {
                    Config.showToast(context, responseData.getToastMsg());
                    try
                    {
                        CarInterface.UpdateCarInfo.Response data = CarInterface.UpdateCarInfo.Response.parseFrom(responseData.getBusiData());
                        if (data.getRet() == 0)
                        {
                            CarInfoNewActivity.this.setResult(Activity.RESULT_OK);
                            CarInfoNewActivity.this.finish();
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
//        showData(model);
    }

    public void setRootText(RelativeLayout root, String str)
    {
        TextView text = (TextView) root.findViewById(R.id.text);
        text.setText(str);
    }

    public void setRootImage(RelativeLayout root)
    {
        TextView text = (TextView) root.findViewById(R.id.text);
        ImageView icon = (ImageView) root.findViewById(R.id.icon);
        ImageView arrow = (ImageView) root.findViewById(R.id.arrow);
        if (!text.getText().toString().trim().equals(""))
        {
            switch (root.getId())
            {
                case R.id.gearbox_text:
                    icon.setBackgroundResource(R.drawable.car_info_gearbox_success_icon);
                    break;
                case R.id.cc:
                    icon.setBackgroundResource(R.drawable.car_info_cc_success_icon);
                    break;
                case R.id.year:
                    icon.setBackgroundResource(R.drawable.car_info_year_success_icon);
                    break;
                case R.id.seat:
                    icon.setBackgroundResource(R.drawable.car_info_seat_success_icon);
                    break;
                case R.id.mileage:
                    icon.setBackgroundResource(R.drawable.car_info_mileage_success_icon);
                    break;
                case R.id.oil:
                    icon.setBackgroundResource(R.drawable.car_info_oil_success_icon);
                    break;
                case R.id.set:
                    icon.setBackgroundResource(R.drawable.car_info_set_success_icon);
                    break;
            }
            arrow.setBackgroundResource(R.drawable.add_car_check_sure);
        }
        else
        {
            switch (root.getId())
            {
                case R.id.gearbox_text:
                    icon.setBackgroundResource(R.drawable.car_info_gearbox_success_icon);
                    break;
                case R.id.cc:
                    icon.setBackgroundResource(R.drawable.car_info_cc_success_icon);
                    break;
                case R.id.year:
                    icon.setBackgroundResource(R.drawable.car_info_year_success_icon);
                    break;
                case R.id.seat:
                    icon.setBackgroundResource(R.drawable.car_info_seat_success_icon);
                    break;
                case R.id.mileage:
                    icon.setBackgroundResource(R.drawable.car_info_mileage_success_icon);
                    break;
                case R.id.oil:
                    icon.setBackgroundResource(R.drawable.car_info_oil_success_icon);
                    break;
                case R.id.set:
                    icon.setBackgroundResource(R.drawable.car_info_set_success_icon);
                    break;
            }
            arrow.setBackgroundResource(R.drawable.add_car_check_no);
        }
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
        return super.onOptionsItemSelected(item);
    }

    public List<CarInterface.UpdateCarInfo.UpodateCarParams> getUpodateCarParams(CarCommon.CarDetailInfo model)
    {
        List<CarInterface.UpdateCarInfo.UpodateCarParams> list = new ArrayList<CarInterface.UpdateCarInfo.UpodateCarParams>();
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("transmissionType").setValue(getUploadParams(getTextviewStr(0), getResources().getStringArray(R.array.gearbox_items))).build());
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("displacementType").setValue(getUploadParams(getTextviewStr(1), getResources().getStringArray(R.array.cc_items))).build());
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("carRegYear").setValue(getTextviewStr(2).substring(0, 4)).build());
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("seatsCount").setValue(getUploadParams(getTextviewStr(3), CarConfig.SEAT_TYPE)).build());
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("drivingKm").setValue(getUploadParams(getTextviewStr(4), getResources().getStringArray(R.array.mileage_items))).build());
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("gasType").setValue(getUploadParams(getTextviewStr(5), getResources().getStringArray(R.array.oil_items))).build());
        String daoche = "";
        String gps = "";
        String cheliangStr = getTextviewStr(6);
        if (!cheliangStr.trim().equals(""))
        {
            if (cheliangStr.trim().equals("倒车雷达"))
            {
                daoche = "1";
                gps = "2";
            }
            else if (cheliangStr.trim().equals("导航仪"))
            {
                daoche = "2";
                gps = "1";
            }
            else if (cheliangStr.equals("倒车雷达 导航仪"))
            {
                daoche = "1";
                gps = "1";
            }
        }
        else
        {
            daoche = "2";
            gps = "2";
        }
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("hasRadar").setValue(daoche).build());
        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("hasGps").setValue(gps).build());
        return list;
    }


    public String getTextviewStr(int position)
    {
        TextView textView = (TextView) roots.get(position).findViewById(R.id.text);
        return textView.getText() + "";
    }

    public String getUploadParams(String input, String[] array)
    {
        int numb = 1;
        char[] inputChar = input.toCharArray();
        MLog.e("TAG", "inputChar____" + inputChar.toString());
        for (int i = 0; i < array.length; i++)
        {
            String temp = array[i];
            char[] tempChar = temp.toCharArray();
            MLog.e("TAG", "TEMPCHAR___" + tempChar.toString());
            if (input.trim().equals(temp.trim()))
            {
                numb += i;
                break;
            }
        }
        return numb + "";
    }
}
