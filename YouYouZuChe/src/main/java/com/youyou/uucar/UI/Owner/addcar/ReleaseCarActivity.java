package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;


/**
 * 发布车辆界面（包含一系列简介模块）
 */
public class ReleaseCarActivity extends Activity {
    public static final String GOTO_OWNER_CAR_MANAGER = "owner_car_manager";
    public final int GOTO_WAIT_APPLY_SERVICE = 800;
    public final int CARINFO_RESULT = 101;
    public final int LOCATION_RESULT = 102;
    public final int PRICE_RESULT = 103;
    public final int RULE_RESULT = 104;
    public final int SAFETY_RESULT = 105;
    public final int PHOTO_RESULT = 106;
    public final int CAR_PLATE = 107;
    public String tag = ReleaseCarActivity.class.getSimpleName();
    public Activity context;
    @InjectViews({R.id.car_plate, R.id.car_info, R.id.address, R.id.price, R.id.desc, R.id.safety})
    List<RelativeLayout> roots;
    @InjectView(R.id.releasecar)
    TextView realseCarTv;
    @InjectView(R.id.photo_framelayout)
    FrameLayout photoFrameLayout;
    @InjectView(R.id.plate_number)
    TextView mPlateNumber;
    @InjectView(R.id.user_driver_front)
    BaseNetworkImageView mUserDriverFront;
    @InjectView(R.id.photo_icon)
    ImageView mPhotoIcon;
    @InjectView(R.id.progress)
    ProgressBar mProgress;
    @InjectView(R.id.progress_tv)
    TextView mProgressTv;
    @InjectView(R.id.applyService)
    TextView mApplyService;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private TextView mMapTv;
    boolean isCarInfoOk, isCarLocationOk, isCarPriceOk, isCarDescOk, isCarSafeOk, isCarPhotoOk, isCarPhotoAllOk;
    private String carSn;
    SharedPreferences citysp;
    public OnClickListener rootsClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Config.isNetworkConnected(context)) {
                if (carContentModel == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(SysConfig.CAR_SN, carSn);
                intent.putExtra(SysConfig.S_ID, Config.getUser(context).sid);
                switch (v.getId()) {
                    //车辆详情
                    case R.id.car_info:
                        intent.setClass(context, CarInfoNewActivity.class);
                        startActivityForResult(intent, CARINFO_RESULT);
                        break;
                    //交车地点
                    case R.id.address:
                        double lng = 0;
                        double lat = 0;
                        if (carContentModel.hasPosition()) {
                            UuCommon.LatlngPosition position = carContentModel.getPosition();
                            lng = position.getLng();
                            lat = position.getLat();
                        }
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        intent.putExtra(SysConfig.CAR_SN, carSn);
                        intent.setClass(context, CarLocationActivity.class);
                        startActivityForResult(intent, LOCATION_RESULT);
                        break;
                    //出租价格
                    case R.id.price:
                        intent.setClass(context, PriceActivity.class);
                        startActivityForResult(intent, PRICE_RESULT);
                        break;
                    //车辆描述
                    case R.id.desc:
                        intent.setClass(context, AddCarDescActivity.class);
                        startActivityForResult(intent, RULE_RESULT);
                        break;
                    //保险资料
                    case R.id.safety:
                        intent.setClass(context, SafetyActivity.class);
                        startActivityForResult(intent, SAFETY_RESULT);
                        break;
//                    case R.id.car_plate:
//                        intent.setClass(context, AddCarBrandActivity.class);
//                        intent.putExtra("brand_car",false);
//                        startActivityForResult(intent, CAR_PLATE);
//                        break;
                }
            } else {
                Config.showToast(context, context.getResources().getString(R.string.network_error));
            }
        }
    };
    private String sId;
    private String paizhao;
    private String pinpai;
    private String xinghao;
    private String city;
    private CarCommon.CarDetailInfo carContentModel;
    private View actionBarView;

    @OnClick(R.id.releasecar)
    public void onRelease() {
        Intent intent = new Intent();
        intent.setClass(context, OwnerCarInfoActivity.class);
        intent.putExtra("isRelease", true);
        if (isCarInfoOk && isCarLocationOk && isCarPriceOk && isCarDescOk && isCarSafeOk && isCarPhotoOk) {
            intent.putExtra("sure", true);
        } else {
            intent.putExtra("sure", false);
        }
        intent.putExtra(SysConfig.CAR_SN, carSn);
        context.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_desc, menu);
        menu.findItem(R.id.action_save).setTitle("删除车辆");
        return true;
    }


    @OnClick(R.id.applyService)
    public void applyServiceClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.applyservice_dialog));
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Config.showProgressDialog(context, false, null);
                CarInterface.AddCarWithAssistance.Request.Builder request = CarInterface.AddCarWithAssistance.Request.newBuilder();

                request.setLicensePlate(paizhao);// 汽车牌照
                request.setBrand(pinpai);// 汽车品牌
                request.setCarModel(xinghao);// 汽车型号
                String cityCode = "";
                for (int i = 0; i < Config.openCity.size(); i++) {
                    if (Config.openCity.get(i).getName().indexOf(citysp.getString("city", "北京")) != -1) {
                        cityCode = Config.openCity.get(i).getCityId();
                        break;
                    }
                }
                request.setCityId(cityCode);
                request.setCarId(carSn);
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AddCarWithAssistance_VALUE);
                networkTask.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                    @Override
                    public void onSuccessResponse(UUResponseData response) {
                        if (response.getRet() == 0) {
                            try {
                                CarInterface.AddCarWithAssistance.Response data = CarInterface.AddCarWithAssistance.Response.parseFrom(response.getBusiData());
                                if (data.getRet() == 0) {
                                    startActivityForResult(new Intent(context, WaitApplyServiceActivity.class), GOTO_WAIT_APPLY_SERVICE);
                                    ReleaseCarActivity.this.finish();
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError errorResponse) {

                    }

                    @Override
                    public void networkFinish() {
                        Config.dismissProgress();
                    }
                });
//                startActivity(new Intent(context, WaitApplyServiceActivity.class));
            }
        });
        builder.create().show();
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        getIntentData();
        Config.setActivityState(this);
        citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        context = this;
        setContentView(R.layout.release_car);
        ButterKnife.inject(this);
        initListener();
        getData();
        initNoteDataRefush();
        if (paizhao != null && !paizhao.trim().equals("")) {
            mPlateNumber.setText(paizhao.toUpperCase());
        }
        mApplyService.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        mApplyService.setText("申请客服协助");
    }


    public void initNoteDataRefush() {
        TextView noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.isNetworkConnected(context)) {
                    getData();
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    private void initListener() {
        for (RelativeLayout root : roots) {
            root.setOnClickListener(rootsClick);
        }

        photoFrameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SysConfig.CAR_SN, carSn);
                intent.putExtra(SysConfig.S_ID, Config.getUser(context).sid);
                intent.setClass(context, AddCarPhotoVolleyActivity.class);
                startActivityForResult(intent, PHOTO_RESULT);
            }
        });
    }

    public void setRootImage(RelativeLayout root) {
        ImageView sure = (ImageView) root.findViewById(R.id.sure);
        switch (root.getId()) {
            case R.id.car_info:
                if (Config.isCarInfoOkey(carContentModel)) {
                    sure.setBackgroundResource(R.drawable.add_car_check_sure);
                    isCarInfoOk = true;
                } else {
                    isCarInfoOk = false;
                }
                break;
            case R.id.address:
                if (carContentModel.hasAddress()) {
                    if (carContentModel.getAddress() != null && !carContentModel.getAddress().equals("")) {
                        sure.setBackgroundResource(R.drawable.add_car_check_sure);
                        isCarLocationOk = true;
                    } else {
                        isCarLocationOk = false;
                    }
                }
                break;
            case R.id.price:
                if (carContentModel.hasPriceByDay() && carContentModel.getPriceByDay() != 0f) {
                    sure.setBackgroundResource(R.drawable.add_car_check_sure);
                    isCarPriceOk = true;
                } else {
                    isCarPriceOk = false;
                }
                break;
            case R.id.desc:
                if (carContentModel.hasCarDesc() && !carContentModel.getCarDesc().equals("")) {
                    sure.setBackgroundResource(R.drawable.add_car_check_sure);
                    isCarDescOk = true;
                } else {
                    isCarDescOk = false;
                }
                break;
            case R.id.safety:
                boolean flag = false;
                int needUploadImgTypeCount = carContentModel.getNeedUploadImgTypeCount();
                if (needUploadImgTypeCount == 0) {
                    flag = true;
                } else {
                    List<CarCommon.CarImgType> needUploadImgTypeList = carContentModel.getNeedUploadImgTypeList();
                    for (int i = 0; i < needUploadImgTypeList.size(); i++) {
                        CarCommon.CarImgType carRefusedImgType = needUploadImgTypeList.get(i);
                        if (carRefusedImgType.getNumber() != CarCommon.CarImgType.CAR_LICENSE_BACK_VALUE && carRefusedImgType.getNumber() != CarCommon.CarImgType.CAR_LICENSE_FRONT_VALUE && carRefusedImgType.getNumber() != CarCommon.CarImgType.CONSTRAINT_INSURANCE_VALUE) {
                            flag = true;
                        } else {
                            flag = false;
                            break;
                        }
                    }
                }
                boolean refuseFlag = false;
                List<CarCommon.CarImgType> carRefusedImgTypeList = carContentModel.getCarRefusedImgTypeList();
                if (carRefusedImgTypeList.size() > 1) {
                    refuseFlag = true;
                }
                if (flag && !refuseFlag) {
                    sure.setBackgroundResource(R.drawable.add_car_check_sure);
                    isCarSafeOk = true;
                } else {
                    isCarSafeOk = false;
                }
                break;
//            case R.id.car_plate:
//                if (paizhao != null && !paizhao.trim().equals("")) {
//                    sure.setBackgroundResource(R.drawable.add_car_check_sure);
//                    mPlateNumber.setText(paizhao);
//                    isCarPlateOk = true;
//                } else {
//                    isCarPlateOk = false;
//                }
//                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GOTO_WAIT_APPLY_SERVICE) {
                setResult(SysConfig.RELEASE_WAIT_SERVICE);
                finish();
            } else {
                getData();
            }

        }
    }


    public void onResume() {
        super.onResume();

    }

    public void getIntentData() {
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
        sId = getIntent().getStringExtra(SysConfig.S_ID);
        city = getIntent().getStringExtra(SysConfig.CITY);
        pinpai = getIntent().getStringExtra(SysConfig.CAR_TYPE);
        xinghao = getIntent().getStringExtra(SysConfig.CAR_NAME);
        paizhao = getIntent().getStringExtra(SysConfig.PLATE_NUMBER);
    }

    public void getData() {
        CarInterface.GetCarDetailInfo.Request.Builder request = CarInterface.GetCarDetailInfo.Request.newBuilder();
        request.setCarId(carSn);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.GetCarDetailInfo_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                Config.showToast(context, responseData.getToastMsg());
                if (responseData.getRet() == 0) {
                    try {
                        CarInterface.GetCarDetailInfo.Response response = CarInterface.GetCarDetailInfo.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            carContentModel = response.getCarDetailInfo();
                            Config.sCarContentModel = carContentModel;
                            if (carContentModel == null) {
                                mAllFramelayout.makeProgreeNoData();
                                return;
                            } else {
                                mAllFramelayout.makeProgreeDismiss();
                            }

                            if (carContentModel != null) {
                                paizhao = carContentModel.getLicensePlate();
                                pinpai = carContentModel.getBrand();
                                xinghao = carContentModel.getCarModel();
                            }

                            boolean hasCover = false;
                            if (carContentModel.getCarImgsCount() > 0) {
                                List<CarCommon.CarImg> carImgsList = carContentModel.getCarImgsList();
                                for (CarCommon.CarImg carImg : carImgsList) {
                                    if (carImg.getType() == 2) {
                                        hasCover = true;
                                        String carImageUrl = carImg.getImgThumb();
                                        UUAppCar.getInstance().display(carImageUrl, mUserDriverFront, 0);
                                        break;
                                    }
                                }
                            }
                            isCarPhotoAllOk = carContentModel.getCarImgsCount() == 8;
                            setCoverPhoto();
                            isCarPhotoOk = hasCover;
                            int refuseCount = carContentModel.getCarRefusedImgTypeCount();
                            if (refuseCount > 0) {
                                List<CarCommon.CarImgType> carRefusedImgTypeList = carContentModel.getCarRefusedImgTypeList();
                                for (CarCommon.CarImgType carImgType : carRefusedImgTypeList) {
                                    if (carImgType.getNumber() == CarCommon.CarImgType.HEAD_IMG_VALUE) {
                                        isCarPhotoOk = false;
                                        isCarPhotoAllOk = false;
                                        break;
                                    }
                                }
                            }
                            for (RelativeLayout root : roots) {
                                setRootImage(root);
                            }
                            setProgress();

                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }
            }


            @Override
            public void onError(VolleyError errorResponse) {
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {
            }
        });


//                startActivity(new Intent(context, WaitApplyServiceActivity.class));
    }

    private void setCoverPhoto() {
        mPhotoIcon.setBackgroundResource(R.drawable.add_photo_icon);
        List<CarCommon.CarImgType> carRefusedImgTypeList = carContentModel.getCarRefusedImgTypeList();
        if (carRefusedImgTypeList != null && carRefusedImgTypeList.size() > 0) {
            for (CarCommon.CarImgType carType : carRefusedImgTypeList) {
                if (carType.getNumber() == CarCommon.CarImgType.HEAD_IMG_VALUE) {
                    isCarPhotoAllOk = false;
                    isCarPhotoOk = false;
                    mPhotoIcon.setBackgroundResource(R.drawable.error_photo);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivityTab.class);
        intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        } else if (id == R.id.action_save) {
            AlertDialog.Builder delBuilder = new AlertDialog.Builder(context);
            delBuilder.setMessage("确定要删除车辆吗？");
            delBuilder.setNegativeButton("取消", null);
            delBuilder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Config.showProgressDialog(context, false, null);
                    CarInterface.DeleteCar.Request.Builder request = CarInterface.DeleteCar.Request.newBuilder();
                    request.setCarId(carSn);
                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.DeleteCar_VALUE);
                    networkTask.setBusiData(request.build().toByteArray());
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData) {
                            if (responseData.getRet() == 0) {
                                try {
                                    CarInterface.DeleteCar.Response response = CarInterface.DeleteCar.Response.parseFrom(responseData.getBusiData());
                                    if (response.getRet() == 0) {
                                        setResult(RESULT_OK);
                                        ReleaseCarActivity.this.finish();
                                    }
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(VolleyError errorResponse) {
                        }

                        @Override
                        public void networkFinish() {
                            Config.dismissProgress();
                        }
                    });
                }
            });
            delBuilder.create().show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void setProgress() {
        int progress = 0;
        if (isCarInfoOk) {
            progress += 15;
        }
        if (isCarLocationOk) {
            progress += 15;
        }
        if (isCarPriceOk) {
            progress += 15;
        }
        if (isCarDescOk) {
            progress += 15;
        }
        if (isCarSafeOk) {
            progress += 15;
        }
        if (isCarPhotoAllOk) {
            progress += 25;
        } else {
            if (isCarPhotoOk) {
                progress += 15;
            }
        }
        mProgress.setProgress(progress);
        mProgressTv.setText(progress + "%");
    }

}
