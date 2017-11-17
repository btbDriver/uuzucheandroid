package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.DB.Model.CarSafeModel;
import com.youyou.uucar.DB.Service.CarSafeService;
import com.youyou.uucar.R;
import com.youyou.uucar.Utils.BitmapUtils;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUParams;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

public class SafetyActivity extends Activity {

    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    /**
     * 身份证
     */
    public final static int SHEN_FEN_ZHENG = 1;
    /**
     * 交强险
     */
    public final static int JIAO_QIANG_XIAN = 2;
    /**
     * 行驶证正面
     */
    public final static int XSZ_FRONT = 3;
    /**
     * 行驶证反面
     */
    public final static int XSZ_BACK = 4;
    /**
     * 从Intent获取图片路径的KEY
     */
    public static final String KEY_PHOTO_PATH = "photo_path";
    public static int current = -1;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("请选择 ");
            switch (v.getId()) {
                case R.id.front_root_transport:
                    stringBuilder.append("身份证正面信息 照片");
                    current = SHEN_FEN_ZHENG;
                    break;
                case R.id.front_photo:
                    stringBuilder.append("身份证正面信息 照片");
                    current = SHEN_FEN_ZHENG;
                    break;
                case R.id.back_root_transport:
                    stringBuilder.append("交强险信息 照片");
                    current = JIAO_QIANG_XIAN;
                    break;
                case R.id.back_photo:
                    stringBuilder.append("交强险信息 照片");
                    current = JIAO_QIANG_XIAN;
                    break;
                case R.id.xingshi_front_transport:
                    stringBuilder.append("行驶证正面信息 照片");
                    current = XSZ_FRONT;
                    break;
                case R.id.xingshi_front_photo:
                    stringBuilder.append("行驶证正面信息 照片");
                    current = XSZ_FRONT;
                    break;
                case R.id.xingshi_back_transport:
                    stringBuilder.append("行使证反面信息 照片");
                    current = XSZ_BACK;
                    break;
                case R.id.xingshi_back_photo:
                    stringBuilder.append("行使证反面信息 照片");
                    current = XSZ_BACK;
                    break;
            }

            if (current == SHEN_FEN_ZHENG) {
                if (isIdCardSuccFlag) {
                    Toast.makeText(SafetyActivity.this, "您已上传过身份证资料，无需再次上传！", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            new AlertDialog.Builder(SafetyActivity.this)
                    .setTitle(stringBuilder.toString())
                    .setItems(
                            R.array.select_photo, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        pickPhoto();
                                    } else if (which == 1) {
                                        getImageFromCamera();
                                    }

                                    MLog.e(tag, "which__" + which);
                                }
                            }
                    )
                    .create().show();

        }
    };

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            bigPicPath = SysConfig.SD_IMAGE_PATH + getPhotoBigFileName(current);
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(bigPicPath)));
            startActivityForResult(getImageByCamera, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    private final String tag = SafetyActivity.class.getSimpleName();
    @InjectView(R.id.sure)
    TextView sureTv;
    @InjectViews({R.id.front_image, R.id.back_image, R.id.xingshi_front_image, R.id.xingshi_back_image})
    List<ImageView> imageViewList;
    @InjectViews({R.id.front_photo, R.id.back_photo, R.id.xingshi_front_photo, R.id.xingshi_back_photo})
    List<ImageView> photoList;
    @InjectViews({R.id.front_root, R.id.back_root, R.id.xingshi_front_root, R.id.xingshi_back_root})
    List<RelativeLayout> rootList;
    @InjectViews({R.id.front_root_transport, R.id.back_root_transport, R.id.xingshi_front_transport, R.id.xingshi_back_transport})
    List<RelativeLayout> rootListTransport;
    boolean idCardFlag = false, insuranceScanFlag = false, driverFrontFlag = false,
            driverBackFlag = false;
    CarSafeModel safeModel;
    CarSafeService safeService;
    Bitmap bitmap;
    Cursor cursor;
    CarCommon.CarDetailInfo carContentModel;
    Context context;
    private Uri photoUri;
    private String carSn;
    /**
     * 获取到的图片路径
     */
    private String picPath;
    private String bigPicPath;


    private boolean isIdCardSuccFlag = false;

    public static boolean saveImage(Bitmap photo, String spath) {
        try {
            File mPhotoFile = new File(spath);
            if (!mPhotoFile.exists()) {
                mPhotoFile.createNewFile();
            }
            photo = BitmapUtils.getInSampleBitmapByBitmap(photo);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false)
            );
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        Config.setActivityState(this);
        context = this;
        carContentModel = Config.sCarContentModel;
        setContentView(R.layout.addcar_safety);
        ButterKnife.inject(this);
        initListener();
        safeModel = getModel();
        showData(safeModel);

    }

    public CarSafeModel getModel() {
        safeService = new CarSafeService(this);
        safeModel = safeService.getModel(CarSafeModel.class, new String[]{carSn});
        if (safeModel == null || safeModel.id == null) {
            safeModel = new CarSafeModel();
            safeModel.carSn = carSn;
            safeModel.idCardFrontState = "0";
            safeModel.insuranceScanState = "0";
            safeModel.driverFrontState = "0";
            safeModel.driverBackState = "0";
            boolean flag = safeService.insModel(safeModel);
            if (flag) {
                safeModel = safeService.getModel(CarSafeModel.class, new String[]{carSn});
            }
        }

        return safeModel;
    }

    private void showData(CarSafeModel carSafeModel) {
        if (carSafeModel != null) {
            if (carSafeModel.idCardFrontState != null && carSafeModel.idCardFrontState.equals
                    ("1")) {
                idCardFlag = true;
                rootList.get(0).setVisibility(View.GONE);
                bitmap = BitmapUtils.getInSampleBitmap(carSafeModel.idCardFrontPic, 152, 96);
                imageViewList.get(0).setImageBitmap(bitmap);
            }
            if (carSafeModel.insuranceScanState != null && carSafeModel.insuranceScanState.equals
                    ("1")) {
                insuranceScanFlag = true;
                rootList.get(1).setVisibility(View.GONE);
                bitmap = BitmapUtils.getInSampleBitmap(carSafeModel.insuranceScanPic, 152, 96);
                imageViewList.get(1).setImageBitmap(bitmap);
            }
            if (carSafeModel.driverFrontState != null && carSafeModel.driverFrontState.equals
                    ("1")) {
                driverFrontFlag = true;
                rootList.get(2).setVisibility(View.GONE);
                bitmap = BitmapUtils.getInSampleBitmap(carSafeModel.driverFrontPic, 152, 96);
                imageViewList.get(2).setImageBitmap(bitmap);
            }
            if (carSafeModel.driverBackState != null && carSafeModel.driverBackState.equals("1")) {
                driverBackFlag = true;
                rootList.get(3).setVisibility(View.GONE);
                bitmap = BitmapUtils.getInSampleBitmap(carSafeModel.driverBackPic, 152, 96);
                imageViewList.get(3).setImageBitmap(bitmap);
            }
        }

        if (carContentModel != null) {
            if (carContentModel.getNeedUploadImgTypeCount() > 0) {
                boolean needIdFrontFlag = false;
                boolean insuranceFlag = false;
                boolean licenseFrontFlag = false;
                boolean licenseBackFlag = false;
                List<CarCommon.CarImgType> needUploadImgTypeList = carContentModel.getNeedUploadImgTypeList();
                for (CarCommon.CarImgType carImgType : needUploadImgTypeList) {
                    int number = carImgType.getNumber();
                    if (number == CarCommon.CarImgType.ID_FRONT_VALUE) {
                        needIdFrontFlag = true;
                    } else if (number == CarCommon.CarImgType.CONSTRAINT_INSURANCE_VALUE) {
                        insuranceFlag = true;
                    } else if (number == CarCommon.CarImgType.CAR_LICENSE_FRONT_VALUE) {
                        licenseFrontFlag = true;
                    } else if (number == CarCommon.CarImgType.CAR_LICENSE_BACK_VALUE) {
                        licenseBackFlag = true;
                    }
                }
                idCardFlag = !needIdFrontFlag;
                insuranceScanFlag = !insuranceFlag;
                driverFrontFlag = !licenseFrontFlag;
                driverBackFlag = !licenseBackFlag;
            }
            int carRefusedImgTypeCount = carContentModel.getCarRefusedImgTypeCount();
            if (carRefusedImgTypeCount > 0) {
                List<CarCommon.CarImgType> carRefusedImgTypeList = carContentModel.getCarRefusedImgTypeList();
                for (CarCommon.CarImgType carRefusedImgType : carRefusedImgTypeList) {
                    if (carRefusedImgType.getNumber() == CarCommon.CarImgType.CAR_LICENSE_FRONT_VALUE) {
                        driverFrontFlag = false;
                        photoList.get(2).setBackgroundResource(R.drawable.error_photo);
                    } else if (carRefusedImgType.getNumber() == CarCommon.CarImgType.CAR_LICENSE_BACK_VALUE) {
                        driverBackFlag = false;
                        photoList.get(3).setBackgroundResource(R.drawable.error_photo);
                    } else if (carRefusedImgType.getNumber() == CarCommon.CarImgType.CONSTRAINT_INSURANCE_VALUE) {
                        insuranceScanFlag = false;
                        photoList.get(1).setBackgroundResource(R.drawable.error_photo);
                    } else if (carRefusedImgType.getNumber() == CarCommon.CarImgType.ID_FRONT_VALUE) {
                        insuranceScanFlag = false;
                        photoList.get(0).setBackgroundResource(R.drawable.error_photo);
                    }
                }
            }
            isIdCardSuccFlag = carContentModel.getIdFrontPassed();
            if (isIdCardSuccFlag) {
                idCardFlag = true;
            }
        }
        setSureButton();
    }

    private void setSureButton() {

        if (idCardFlag && insuranceScanFlag && driverFrontFlag && driverBackFlag) {
            sureTv.setEnabled(true);
        } else {
            sureTv.setEnabled(false);
        }

    }

    private void initListener() {
        for (ImageView imageView : photoList) {
            imageView.setOnClickListener(onClickListener);
        }

        for (RelativeLayout root : rootListTransport) {
            root.setOnClickListener(onClickListener);
        }
    }

    public void getIntentData() {
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
    }

    private String getPhotoFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {
            case SHEN_FEN_ZHENG:
                fileName.append(carSn + "_idcard.jpg");
                break;
            case JIAO_QIANG_XIAN:
                fileName.append(carSn + "_jiaoqiangxian.jpg");
                break;
            case XSZ_FRONT:
                fileName.append(carSn + "_xsz_zhengmian.jpg");
                break;
            case XSZ_BACK:
                fileName.append(carSn + "_xsz_fanmian.jpg");
                break;
        }
        return fileName.toString();
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                doPhoto(requestCode, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) throws IOException {
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO)  //从相册取图片，有些手机有异常情况，请注意
        {

            ContentResolver resolver = getContentResolver();
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            String photoStr = getRealPathFromURI(photoUri);
            if (photoStr == null) {

                if (photoUri.toString().contains("media")) {
                    Bitmap bitmapTemp = MediaStore.Images.Media.getBitmap(resolver, photoUri);
                    picPath = SysConfig.SD_IMAGE_PATH + getPhotoFileName(current);
                    saveImage(bitmapTemp, picPath);
//                bitmapTemp.recycle();
                    bitmap = BitmapUtils.getInSampleBitmap(picPath, 152, 96);

                    if (bitmapTemp != null) {
                        bitmapTemp.recycle();
                    }
                } else {
                    Toast.makeText(this, "选择图片文件格式出错", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                if (photoStr.endsWith(".png") || photoStr.endsWith(".PNG") || photoStr.endsWith(".jpg")
                        || photoStr.endsWith(".JPG") || photoStr.endsWith(".jpeg") || photoStr.endsWith(".JPEG")) {
                    Bitmap bitmapTemp = MediaStore.Images.Media.getBitmap(resolver, photoUri);
                    picPath = SysConfig.SD_IMAGE_PATH + getPhotoFileName(current);
                    saveImage(bitmapTemp, picPath);
//                bitmapTemp.recycle();
                    bitmap = BitmapUtils.getInSampleBitmap(picPath, 152, 96);

                    if (bitmapTemp != null) {
                        bitmapTemp.recycle();
                    }
                } else {
                    Toast.makeText(this, "选择图片文件格式出错", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        } else if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {
            Bitmap bitmapTemp = null;

            if (bigPicPath != null) {
                bitmapTemp = BitmapUtils.getInSampleBitmapByFile(bigPicPath);
            }
            picPath = SysConfig.SD_IMAGE_PATH + getPhotoFileName(current);
            saveImage(bitmapTemp, picPath);
            if (bitmapTemp != null) {
                bitmapTemp.recycle();
            }
            try {
//                bitmap = BitmapFactory.decodeFile(picPath);
                bitmap = BitmapUtils.getInSampleBitmap(picPath, 152, 96);

            } catch (Exception e) {

            }
        }
//        postPic(picPath, current);
        postPicByVolley(picPath, current);
    }


    private String getRealPathFromURI(Uri contentUri) {
        String path = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return path;
        }
    }

    private void postPicByVolley(final String pic, final int current) {

        if (Config.isNetworkConnected(SafetyActivity.this)) {


            Config.showProgressDialog(context, false, null);
            UUParams params = new UUParams();
            if (current == SHEN_FEN_ZHENG) {
                params.put("idfront", new File(pic));
            } else if (current == JIAO_QIANG_XIAN) {
                params.put("insurance_image", new File(pic));
            } else if (current == XSZ_FRONT) {
                params.put("vin", new File(pic));
            } else if (current == XSZ_BACK) {
                params.put("vin_back", new File(pic));
            }
            NetworkTask networkTask = new NetworkTask(params);
            networkTask.setTag("TAG");
            NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<JSONObject>() {
                @Override
                public void onSuccessResponse(JSONObject json) {

                    try {
                        int ret = json.getInt("ret");
                        if (ret == 0) {
                            String responseData = json.getString("sessionKey");
                            if (responseData != null) {
                                if (responseData.equals("-1")) {
                                    Config.dismissProgress();
                                    //表示 照片上传失败
                                    Config.showToast(context, "照片上传失败，请重新上传！");
                                } else {
                                    NetworkTask photoTask = null;
                                    switch (current) {
                                        case SHEN_FEN_ZHENG:
                                            photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadIdCardFront_VALUE);
                                            UserInterface.UploadIdCardFront.Request.Builder idCard = UserInterface.UploadIdCardFront.Request.newBuilder();
                                            idCard.setIdCode(responseData);
                                            idCard.setType(1);
                                            idCard.setScene(1);
                                            photoTask.setBusiData(idCard.build().toByteArray());
                                            photoTask.setTag("id_card");
                                            break;
                                        case JIAO_QIANG_XIAN:

                                            photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadConstraintInsurancePic_VALUE);
                                            CarInterface.UploadConstraintInsurancePic.Request.Builder jqx = CarInterface.UploadConstraintInsurancePic.Request.newBuilder();
                                            jqx.setIdCode(responseData);
                                            jqx.setCarId(carSn);
                                            photoTask.setBusiData(jqx.build().toByteArray());
                                            photoTask.setTag("jiao_qiang_xian");
                                            break;
                                        case XSZ_FRONT:
                                            photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadCarLicense_VALUE);
                                            CarInterface.UploadCarLicense.Request.Builder xszFont = CarInterface.UploadCarLicense.Request.newBuilder();
                                            xszFont.setIdCode(responseData);
                                            xszFont.setCarId(carSn);
                                            xszFont.setType(1);
                                            photoTask.setBusiData(xszFont.build().toByteArray());
                                            photoTask.setTag("xsz_front");
                                            break;
                                        case XSZ_BACK:
                                            photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadCarLicense_VALUE);
                                            CarInterface.UploadCarLicense.Request.Builder xszBack = CarInterface.UploadCarLicense.Request.newBuilder();
                                            xszBack.setIdCode(responseData);
                                            xszBack.setCarId(carSn);
                                            xszBack.setType(2);
                                            photoTask.setBusiData(xszBack.build().toByteArray());
                                            photoTask.setTag("xsz_back");
                                            break;
                                    }

                                    NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData) {

                                            if (responseData.getRet() == 0) {
                                                try {
                                                    switch (current) {
                                                        case SHEN_FEN_ZHENG:

                                                            UserInterface.UploadIdCardFront.Response response = UserInterface.UploadIdCardFront.Response.parseFrom(responseData.getBusiData());

                                                            if (response.getRet() == 0) {

                                                                //图片上传成功！
                                                                setImageView(bitmap, current);
                                                                setSureButton();
                                                            }


                                                            break;
                                                        case JIAO_QIANG_XIAN:

                                                            CarInterface.UploadConstraintInsurancePic.Response jqxResponse = CarInterface.UploadConstraintInsurancePic.Response.parseFrom(responseData.getBusiData());

                                                            if (jqxResponse.getRet() == 0) {

                                                                //图片上传成功！
                                                                setImageView(bitmap, current);
                                                                setSureButton();
                                                            }
                                                            break;
                                                        case XSZ_FRONT:
                                                            CarInterface.UploadCarLicense.Response xszResponse = CarInterface.UploadCarLicense.Response.parseFrom(responseData.getBusiData());

                                                            if (xszResponse.getRet() == 0) {

                                                                //图片上传成功！
                                                                setImageView(bitmap, current);
                                                                setSureButton();
                                                            }
                                                            break;
                                                        case XSZ_BACK:
                                                            CarInterface.UploadCarLicense.Response backResponse = CarInterface.UploadCarLicense.Response.parseFrom(responseData.getBusiData());

                                                            if (backResponse.getRet() == 0) {

                                                                //图片上传成功！
                                                                setImageView(bitmap, current);
                                                                setSureButton();
                                                            }
                                                            break;
                                                    }


                                                } catch (InvalidProtocolBufferException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {

                                                Config.showToast(context, "照片上传失败，请重新上传！");
                                            }

                                        }

                                        @Override
                                        public void onError(VolleyError errorResponse) {
                                            Config.showToast(context, "照片上传失败，请重新上传！");
                                        }

                                        @Override
                                        public void networkFinish() {
                                            Config.dismissProgress();
                                        }
                                    });


                                }

                            }


                        } else {
                            Config.dismissProgress();
                            //表示 照片上传失败
                            Config.showToast(context, "照片上传失败，请重新上传！");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(VolleyError errorResponse) {
                    Config.dismissProgress();
                }

                @Override
                public void networkFinish() {

                }
            });
        } else {

            Toast.makeText(context, SysConfig.NETWORK_PHOTO_FAIL, Toast.LENGTH_SHORT).show();
        }
    }

    private String getPhotoBigFileName(int current) {
        StringBuilder fileName = new StringBuilder();
        switch (current) {
            case SHEN_FEN_ZHENG:
                fileName.append(carSn + "_big_idcard.jpg");
                break;
            case JIAO_QIANG_XIAN:
                fileName.append(carSn + "_big_jiaoqiangxian.jpg");
                break;
            case XSZ_FRONT:
                fileName.append(carSn + "_big_xsz_zhengmian.jpg");
                break;
            case XSZ_BACK:
                fileName.append(carSn + "_big_xsz_fanmian.jpg");
                break;
        }
        return fileName.toString();
    }


    private void setImageView(Bitmap bitmap, int current) {
        switch (current) {
            case SHEN_FEN_ZHENG:
                rootList.get(0).setVisibility(View.GONE);
                imageViewList.get(0).setImageBitmap(bitmap);
                idCardFlag = true;
                photoList.get(0).setBackgroundResource(R.drawable.camere_button);
                safeModel.idCardFrontPic = picPath;
                safeModel.idCardFrontState = "1";
                break;
            case JIAO_QIANG_XIAN:
                rootList.get(1).setVisibility(View.GONE);
                imageViewList.get(1).setImageBitmap(bitmap);
                insuranceScanFlag = true;
                photoList.get(1).setBackgroundResource(R.drawable.camere_button);
                safeModel.insuranceScanPic = picPath;
                safeModel.insuranceScanState = "1";
                break;
            case XSZ_FRONT:
                rootList.get(2).setVisibility(View.GONE);
                imageViewList.get(2).setImageBitmap(bitmap);
                driverFrontFlag = true;
                photoList.get(2).setBackgroundResource(R.drawable.camere_button);
                safeModel.driverFrontPic = picPath;
                safeModel.driverFrontState = "1";
                break;
            case XSZ_BACK:
                rootList.get(3).setVisibility(View.GONE);
                imageViewList.get(3).setImageBitmap(bitmap);
                driverBackFlag = true;
                photoList.get(3).setBackgroundResource(R.drawable.camere_button);
                safeModel.driverBackPic = picPath;
                safeModel.driverBackState = "1";
                break;
        }
        safeService.modifyModel(safeModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (cursor != null) {
            cursor.close();
        }
    }


    @OnClick(R.id.sure)
    public void sureClick() {

        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        this.finish();
    }
}
