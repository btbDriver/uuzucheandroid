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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.BitmapUtils;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUParams;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
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

/**
 * Created by taurusxi on 14-7-7.
 */
public class AddCarPhotoVolleyActivity extends Activity {

    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    /**
     * 封面
     */
    public final static int COVER = 1;

    public final static int ZHENG_QIAN = 2;

    public final static int YOU_HOU = 3;

    public final static int ZHENG_HOU = 4;

    public final static int ZUO_CE_MIAN = 5;

    public final static int ZHONG_KONG_TAI = 6;

    public final static int CAR_FRONT = 7;

    public final static int CAR_BACK = 8;
    public int current = -1;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("请选择 ");
            switch (v.getId()) {
                case R.id.fengmian_root:
                    stringBuilder.append("车牌左前 照片");
                    current = COVER;
                    break;
                case R.id.zhengqian_root:
                    stringBuilder.append("车牌正前 照片");
                    current = ZHENG_QIAN;
                    break;
                case R.id.youhou_root:
                    stringBuilder.append("车牌右后 照片");
                    current = YOU_HOU;
                    break;
                case R.id.zhenghou_root:
                    stringBuilder.append("车牌正后 照片");
                    current = ZHENG_HOU;
                    break;
                case R.id.zuocemian_root:
                    stringBuilder.append("左侧面 照片");
                    current = ZUO_CE_MIAN;
                    break;
                case R.id.zhongkongtai_root:
                    stringBuilder.append("中控台 照片");
                    current = ZHONG_KONG_TAI;
                    break;
                case R.id.cheneiqianpai_root:
                    stringBuilder.append("车内前排 照片");
                    current = CAR_FRONT;
                    break;

                case R.id.cheneihoupai_root:
                    stringBuilder.append("车内后排 照片");
                    current = CAR_BACK;
                    break;
            }

            new AlertDialog.Builder(AddCarPhotoVolleyActivity.this)
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
    static double lat = 0;
    static double lng = 0;
    private final String tag = SafetyActivity.class.getSimpleName();
    @InjectView(R.id.linear_main)
    LinearLayout mLinearMain;
    Bitmap bitmap;
    Cursor cursor;
    @InjectViews({R.id.fengmian_root, R.id.zhengqian_root, R.id.youhou_root, R.id.zhenghou_root, R.id.zuocemian_root, R.id.zhongkongtai_root, R.id.cheneiqianpai_root, R.id.cheneihoupai_root})
    List<RelativeLayout> rootList;
    @InjectViews({R.id.fengmian_imageview, R.id.zhengqian_imageview, R.id.youhou_imageview, R.id.zhenghou_imageview, R.id.zuocemian_imageview, R.id.zhongkongtai_imageview, R.id.cheneiqianpai_imageview, R.id.cheneihoupai_imageview})
    List<NetworkImageView> imageList;
    @InjectView(R.id.relative_layout)
    RelativeLayout relativeLayout;
    @InjectView(R.id.fengmian_photo)
    ImageView fengmianPhoto;
    @InjectView(R.id.sure)
    TextView mSure;
    Context context;
    /**
     * 获取到的图片路径
     */
    private String picPath;
    private Uri photoUri;
    private String bigPicPath;
    //    private String plateNumber;
//    private String carType;
    private String carSn, sId;
    private CarCommon.CarDetailInfo carContentModel;
    //    private CarReleaseModel carReleaseModel;
//    private CarReleaseService carReleaseService;
//    private CarPhotoModel carPhotoModel;
//    private CarPhotoService carPhotoService;
    private boolean coverFlag = false, zhengqianFlag = false, youhouFlag = false,
            zhenghouFlag = false, zuocemianFlag = false, zhongkongtaiFlag = false,
            carfrontFlag = false, carBackFlag = false;
    private String fileStreamPathName;

    public static boolean saveImage(Bitmap photo, String spath) {
        try {
            File mPhotoFile = new File(spath);
            if (!mPhotoFile.exists()) {
                mPhotoFile.createNewFile();
            }
            int width = photo.getWidth();
            int length = photo.getHeight();
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
        setContentView(R.layout.add_car_photo_activity);
        ButterKnife.inject(this);
        carContentModel = Config.sCarContentModel;
        initListener();
        setImageViewSize();
        showData();
        Config.getCoordinates(
                this, new LocationListener() {
                    @Override
                    public void locationSuccess(double lat, double lng, String addr) {
                        AddCarPhotoVolleyActivity.lat = lat;
                        AddCarPhotoVolleyActivity.lng = lng;
                    }
                }
        );
    }

    private void setImageViewSize() {

//        float picScale = 0.50f;
//        int width = (int) (DisplayUtil.screenWidthDip * DisplayUtil.density);
//        int height = (int) (width * picScale);
//        FrameLayout.LayoutParams coverSize = new FrameLayout.LayoutParams(width, height);
////        imageList.get(0).setLayoutParams(coverSize);
//        relativeLayout.setLayoutParams(coverSize);
    }

    private void showData() {

        if (carContentModel != null) {
            if (carContentModel.getCarImgsCount() > 0) {

                for (CarCommon.CarImg carImg : carContentModel.getCarImgsList()) {
                    String type = String.valueOf(carImg.getType());
                    String imageStr = carImg.getImgThumb();
                    if (type.equals("2")) {
                        //左前
                        UUAppCar.getInstance().display(imageStr, imageList.get(0),
                                R.drawable.add_car_fengmian);
                        coverFlag = true;
                        mLinearMain.setVisibility(View.VISIBLE);
                    } else if (type.equals("3")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(1),
                                R.drawable.add_car_zhengqian);
                        zhengqianFlag = true;
                    } else if (type.equals("4")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(2),
                                R.drawable.add_car_youhou);
                        youhouFlag = true;
                    } else if (type.equals("5")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(3),
                                R.drawable.add_car_zhenghou);
                        zhenghouFlag = true;
                    } else if (type.equals("6")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(4),
                                R.drawable.add_car_zuocemian);
                        zuocemianFlag = true;
                    } else if (type.equals("7")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(5),
                                R.drawable.add_car_zhongkongtai);
                        zhongkongtaiFlag = true;
                    } else if (type.equals("8")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(6),
                                R.drawable.add_car_cheneiqianpai);
                        carfrontFlag = true;
                    } else if (type.equals("9")) {
                        UUAppCar.getInstance().display(imageStr, imageList.get(7),
                                R.drawable.add_car_cheneihoupai);
                        carBackFlag = true;
                    }
                }
            }
            int carRefusedImgTypeCount = carContentModel.getCarRefusedImgTypeCount();
            if (carRefusedImgTypeCount > 0) {

                for (CarCommon.CarImgType carRefusedImgType : carContentModel.getCarRefusedImgTypeList()) {
                    if (carRefusedImgType.getNumber() == CarCommon.CarImgType.HEAD_IMG_VALUE) {
                        coverFlag = false;
                        fengmianPhoto.setBackgroundResource(R.drawable.error_photo);
                    }
                }
            }
        }
        setSureButton();
    }

    private void setSureButton() {
        if (coverFlag) {
            mSure.setEnabled(true);
        } else {
            mSure.setEnabled(false);
        }

    }

    private void initListener() {
        for (RelativeLayout root : rootList) {
            root.setOnClickListener(onClickListener);
        }

    }

    private String getPhotoFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {
            case COVER:
                fileName.append(carSn + "_cover.jpg");
                break;
            case ZHENG_QIAN:
                fileName.append(carSn + "_ZHENG_QIAN.jpg");
                break;
            case YOU_HOU:
                fileName.append(carSn + "_YOU_HOU.jpg");
                break;
            case ZHENG_HOU:
                fileName.append(carSn + "_ZHENG_HOU.jpg");
                break;
            case ZUO_CE_MIAN:
                fileName.append(carSn + "_ZUO_CE_MIAN.jpg");
                break;
            case ZHONG_KONG_TAI:
                fileName.append(carSn + "_ZHONG_KONG_TAI.jpg");
                break;
            case CAR_FRONT:
                fileName.append(carSn + "_CAR_FRONT.jpg");
                break;
            case CAR_BACK:
                fileName.append(carSn + "_CAR_BACK.jpg");
                break;
        }
        return fileName.toString();
    }

    private String getPhotoBigFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {
            case COVER:
                fileName.append(carSn + "_big_cover.jpg");
                break;
            case ZHENG_QIAN:
                fileName.append(carSn + "_big_ZHENG_QIAN.jpg");
                break;
            case YOU_HOU:
                fileName.append(carSn + "_big_YOU_HOU.jpg");
                break;
            case ZHENG_HOU:
                fileName.append(carSn + "_big_ZHENG_HOU.jpg");
                break;
            case ZUO_CE_MIAN:
                fileName.append(carSn + "_big_ZUO_CE_MIAN.jpg");
                break;
            case ZHONG_KONG_TAI:
                fileName.append(carSn + "_big_ZHONG_KONG_TAI.jpg");
                break;
            case CAR_FRONT:
                fileName.append(carSn + "_big_CAR_FRONT.jpg");
                break;
            case CAR_BACK:
                fileName.append(carSn + "_big_CAR_BACK.jpg");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {

            onBackPressed();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();

    }

    public void getIntentData() {
        sId = getIntent().getStringExtra(SysConfig.S_ID);
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
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
//            setImageView(bitmap, current);
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

    private void postPicByVolley(final String pic, final int current) {

        if (Config.isNetworkConnected(AddCarPhotoVolleyActivity.this)) {
            Config.showProgressDialog(context, false, null);
            UUParams uuParams = new UUParams();
            uuParams.put("img", new File(pic));
            NetworkTask networkTask = new NetworkTask(uuParams);
            networkTask.setTag("CAR_PHOTO");
            networkTask.setHttpUrl(NetworkTask.PHOTO_UPLOAD_HTTP + "?public=1");
            NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<JSONObject>() {
                @Override
                public void onSuccessResponse(JSONObject json) {
                    try {
                        int ret = json.getInt("ret");
                        if (ret == 0) {
                            String responseData = json.getString("sessionKey");
                            if (responseData != null) {

                                if (responseData.equals("-1")) {
                                    Config.showToast(context, "照片上传失败！");

                                } else {

                                    NetworkTask photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadCarPhoto_VALUE);
                                    photoTask.setTag("PHOTO_UPLODE");

                                    CarInterface.UploadCarPhoto.Request.Builder idCard = CarInterface.UploadCarPhoto.Request.newBuilder();
                                    idCard.setIdCode(responseData);
                                    idCard.setCarId(carSn);
                                    int type = -1;
                                    switch (current) {
                                        case COVER:
                                            type = 2;
                                            break;
                                        case ZHENG_QIAN:
                                            type = 3;
                                            break;
                                        case YOU_HOU:
                                            type = 4;
                                            break;
                                        case ZHENG_HOU:
                                            type = 5;
                                            break;
                                        case ZUO_CE_MIAN:
                                            type = 6;
                                            break;
                                        case ZHONG_KONG_TAI:
                                            type = 7;
                                            break;
                                        case CAR_FRONT:
                                            type = 8;
                                            break;
                                        case CAR_BACK:
                                            type = 9;
                                            break;

                                    }
                                    idCard.setType(type);
                                    UuCommon.LatlngPosition.Builder positon = UuCommon.LatlngPosition.newBuilder();
                                    positon.setLat(lat);
                                    positon.setLng(lng);
                                    idCard.setPosition(positon);
                                    photoTask.setBusiData(idCard.build().toByteArray());

                                    NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                        @Override
                                        public void onSuccessResponse(UUResponseData responseData) {
                                            if (responseData.getRet() == 0) {
                                                try {
                                                    CarInterface.UploadCarPhoto.Response response = CarInterface.UploadCarPhoto.Response.parseFrom(responseData.getBusiData());

                                                    if (response.getRet() == 0) {
                                                        if (current == COVER) {
                                                            mLinearMain.setVisibility(View.VISIBLE);
                                                        }
                                                        setImageView(bitmap, current, response.getImgUrl());
                                                        setSureButton();

                                                    } else {
                                                        Toast.makeText(context, "照片上传失败，请重新上传！", Toast.LENGTH_SHORT).show();
                                                        if (bitmap != null) {
                                                            bitmap.recycle();
                                                        }
                                                    }
                                                } catch (InvalidProtocolBufferException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                if (bitmap != null) {
                                                    bitmap.recycle();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(VolleyError errorResponse) {
                                            if (bitmap != null) {
                                                bitmap.recycle();
                                            }

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
                            Toast.makeText(context, "照片上传失败，请重新上传！", Toast.LENGTH_SHORT).show();
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

    private void setImageView(Bitmap bitmap, int current, String imageUrl) {
        switch (current) {
            case COVER:
                imageList.get(0).setImageBitmap(bitmap);
                coverFlag = true;
                fengmianPhoto.setBackgroundResource(R.drawable.camere_button);
                UUAppCar.getInstance().display(imageUrl, imageList.get(0));
                break;
            case ZHENG_QIAN:
                imageList.get(1).setImageBitmap(bitmap);
                zhengqianFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(1));
                break;
            case YOU_HOU:
                imageList.get(2).setImageBitmap(bitmap);
                youhouFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(2));
                break;
            case ZHENG_HOU:
                imageList.get(3).setImageBitmap(bitmap);
                zhenghouFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(3));
                break;
            case ZUO_CE_MIAN:
                imageList.get(4).setImageBitmap(bitmap);
                zuocemianFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(4));
                break;
            case ZHONG_KONG_TAI:
                imageList.get(5).setImageBitmap(bitmap);
                zhongkongtaiFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(5));
                break;
            case CAR_FRONT:
                imageList.get(6).setImageBitmap(bitmap);
                carfrontFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(6));
                break;
            case CAR_BACK:
                imageList.get(7).setImageBitmap(bitmap);
                carBackFlag = true;
                UUAppCar.getInstance().display(imageUrl, imageList.get(7));
                break;

        }
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
        finish();
    }
}
