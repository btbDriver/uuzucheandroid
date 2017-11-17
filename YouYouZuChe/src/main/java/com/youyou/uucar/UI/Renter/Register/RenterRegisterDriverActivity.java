package com.youyou.uucar.UI.Renter.Register;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RenterRegisterDriverActivity extends Activity {

    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    /**
     * 行驶证正面
     */
    public final static int XSZ_FRONT = 3;
    /**
     * 行驶证有效期
     */
    public final static int XSZ_USEFULL = 4;
    /**
     * 从Intent获取图片路径的KEY
     */
    public static final String KEY_PHOTO_PATH = "photo_path";
    private static final String TAG = "RenterRegisterDriverActivity";
    public static int current = -1;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("请选择 ");
            switch (v.getId()) {
                case R.id.front_root_transport:
                    stringBuilder.append("清晰驾驶证 照片");
                    current = XSZ_FRONT;
                    break;
                case R.id.jsz_photo:
                    stringBuilder.append("清晰驾驶证 照片");
                    current = XSZ_FRONT;
                    break;
                case R.id.back_root_transport:
                    stringBuilder.append("驾照有效期 照片");
                    current = XSZ_USEFULL;
                    break;
                case R.id.jsz_yxq_photo:
                    stringBuilder.append("驾照有效期 照片");
                    current = XSZ_USEFULL;
                    break;

            }

            new AlertDialog.Builder(RenterRegisterDriverActivity.this)
                    .setTitle(stringBuilder.toString())
                    .setItems(R.array.select_photo, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                pickPhoto();
                            } else if (which == 1) {
                                getImageFromCamera();
                            }

                            MLog.e(tag, "which__" + which);
                        }
                    })
                    .create().show();

        }
    };
    public String tag = RenterRegisterDriverActivity.class.getSimpleName();
    public Activity context;
    Bitmap bitmap;
    Cursor cursor;
    @InjectView(R.id.step_1)
    TextView mStep1;
    @InjectView(R.id.next)
    TextView mNext;
    @InjectView(R.id.jsz_textview)
    TextView jszTv;
    @InjectView(R.id.jszyxq_textview)
    TextView jszyxqTv;
    boolean driverCardFlag = false, driverUsefulFlag = false;
    UserModel userModel;
    UserService userService;
    @InjectView(R.id.user_driver_front)
    ImageView mUserDriverFront;
    @InjectView(R.id.front_root)
    RelativeLayout mFrontRoot;
    @InjectView(R.id.jsz_photo)
    ImageView mJszPhoto;
    @InjectView(R.id.user_driver_back)
    ImageView mUserDriverBack;
    @InjectView(R.id.back_root)
    RelativeLayout mBackRoot;
    @InjectView(R.id.jsz_yxq_photo)
    ImageView mJszYxqPhoto;
    @InjectView(R.id.front_root_transport)
    RelativeLayout frontTransport;
    @InjectView(R.id.back_root_transport)
    RelativeLayout backTransport;
    private String bigPicPath;
    /**
     * 获取到的图片路径
     */
    private String picPath;
    private Uri photoUri;
    private String planteNumber, carType;
    private List<String> errorPic = new ArrayList<String>();
    private ArrayList<String> needUploadPic = new ArrayList<String>();

    public static boolean saveImage(Bitmap photo, String spath) {
        try {
            File mPhotoFile = new File(spath);
            if (!mPhotoFile.exists()) {
                mPhotoFile.createNewFile();
            }
            photo = BitmapUtils.getInSampleBitmapByBitmap(photo);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @OnClick(R.id.next)
    public void nextClick() {

        if (!driverCardFlag) {

            Toast.makeText(RenterRegisterDriverActivity.this, "请上传驾驶证正面照片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!driverUsefulFlag) {
            Toast.makeText(RenterRegisterDriverActivity.this, "请上传驾驶证背面照片", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean flag = saveModel();
        if (flag) {
            startActivity(new Intent(context, RenterRegisterVerify.class));
            setResult(RESULT_OK);
            finish();
//            Intent intent = new Intent(context, MainActivityTab.class);
//            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_VERIFY);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
        }

    }

    private boolean saveModel() {
        boolean flag = userService.modifyModel(userModel);
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setActivityState(this);
        context = this;
        userModel = getModel();
        setContentView(R.layout.activity_renter_register_driver);
        ButterKnife.inject(this);
        findViewById(R.id.step_1).setSelected(true);
        findViewById(R.id.step_1_line).setSelected(true);
        findViewById(R.id.step_2).setSelected(true);
        findViewById(R.id.step_1_text).setSelected(true);
        findViewById(R.id.step_2_text).setSelected(true);
        initListener();
        showData();
        setSureButton();
        getNewEx();
    }

    public void setUserStatus() {
        if (userModel.idCardFrontState.equals("1") && userModel.idCardBackState.equals("1") && userModel.driverFrontState.equals("1") && userModel.driverBackState.equals("1")) {
            userModel.userStatus = User.USER_STATUS_ALL;
            saveModel();
        }
    }

    private void initListener() {
        frontTransport.setOnClickListener(onClickListener);
        backTransport.setOnClickListener(onClickListener);
        mJszPhoto.setOnClickListener(onClickListener);
        mJszYxqPhoto.setOnClickListener(onClickListener);

    }

    private void showData() {
        if (userModel.driverFrontState != null && userModel.driverFrontState.equals("1")) {
            mFrontRoot.setVisibility(View.INVISIBLE);
            bitmap = BitmapUtils.getInSampleBitmap(userModel.driverFrontPic, 152, 96);
            mUserDriverFront.setImageBitmap(bitmap);
            mJszPhoto.setBackgroundResource(R.drawable.camere_button);
            jszTv.setText("点击上传");
//            mJszPhoto.setImageResource(R.drawable.camere_button);
            driverCardFlag = true;
        }
        if (userModel.driverBackState != null && userModel.driverBackState.equals("1")) {
            mBackRoot.setVisibility(View.INVISIBLE);
            bitmap = BitmapUtils.getInSampleBitmap(userModel.driverBackPic, 152, 96);
            mUserDriverBack.setImageBitmap(bitmap);
            mJszYxqPhoto.setBackgroundResource(R.drawable.camere_button);
            jszyxqTv.setText("点击上传");
            driverUsefulFlag = true;
        }
        setUserStatus();
        setSureButton();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public UserModel getModel() {
        userService = new UserService(this);
        userModel = userService.getModelList(UserModel.class).get(0);
        return userModel;
    }

    private String getPhotoFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {

            case XSZ_FRONT:
                fileName.append(planteNumber + "_zhuce_jsz_front.jpg");
                break;
            case XSZ_USEFULL:
                fileName.append(planteNumber + "_zhuce_jsz_youxiaoqi.jpg");
                break;
        }
        return fileName.toString();
    }

    private String getPhotoBigFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {

            case XSZ_FRONT:
                fileName.append(planteNumber + "_big_zhuce_jsz_front.jpg");
                break;
            case XSZ_USEFULL:
                fileName.append(planteNumber + "_big_zhuce_jsz_youxiaoqi.jpg");
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
//            setImageView(bitmap, current);
//                    setImageView(bitmap, current);
                    setImageViewByVolley(bitmap, current);
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
//                    setImageView(bitmap, current);
                    setImageViewByVolley(bitmap, current);
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
//                setImageView(bitmap, current);
                setImageViewByVolley(bitmap, current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setSureButton();
    }

    private void setSureButton() {
        if (driverCardFlag && driverUsefulFlag) {
            mNext.setEnabled(true);
            findViewById(R.id.step_2).setSelected(true);
        } else {
            mNext.setEnabled(false);
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

    private void setImageViewByVolley(final Bitmap bitmap, int current) {

        if (Config.isNetworkConnected(RenterRegisterDriverActivity.this)) {
            UUParams params = new UUParams();
            Config.showProgressDialog(this, false, null);
            switch (current) {
                case XSZ_FRONT:
                    params.put("dlcardfont", new File(picPath));
                    NetworkTask networkTask = new NetworkTask(params);
                    networkTask.setTag("dlcardfont");
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<JSONObject>() {
                        @Override
                        public void onSuccessResponse(JSONObject json) {

                            try {
                                int ret = json.getInt("ret");
                                if (ret == 0) {
                                    String responseData = json.getString("sessionKey");
                                    if (responseData != null) {
                                        if (responseData.equals("-1")) {
                                            Config.showToast(context, "照片上传失败，请重试！");
                                            Config.dismissProgress();
                                        } else {
                                            NetworkTask photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadDrivingicenseFront_VALUE);
                                            photoTask.setTag("dlcard");
                                            UserInterface.UploadDrivingicenseFront.Request.Builder request = UserInterface.UploadDrivingicenseFront.Request.newBuilder();
                                            request.setIdCode(responseData);
                                            photoTask.setBusiData(request.build().toByteArray());
                                            NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                                @Override
                                                public void onSuccessResponse(UUResponseData responseData) {

                                                    if (responseData.getRet() == 0) {
                                                        try {
                                                            UserInterface.UploadDrivingicenseFront.Response response = UserInterface.UploadDrivingicenseFront.Response.parseFrom(responseData.getBusiData());

                                                            if (response.getRet() == 0) {

                                                                mFrontRoot.setVisibility(View.INVISIBLE);
                                                                mUserDriverFront.setImageBitmap(bitmap);
                                                                driverCardFlag = true;
                                                                userModel.driverFrontPic = picPath;
                                                                userModel.driverFrontState = "1";
                                                                userService.modifyModel(userModel);
                                                                driverCardFlag = true;
                                                                jszTv.setText("点击上传");
                                                                mJszPhoto.setBackgroundResource(R.drawable.camere_button);
                                                                setUserStatus();
                                                                setSureButton();

                                                            }

                                                        } catch (InvalidProtocolBufferException e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        Config.showToast(context, "照片上传失败，请重试！");
                                                        Config.dismissProgress();

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
                    break;
                case XSZ_USEFULL:

                    params.put("dlcardback", new File(picPath));
                    NetworkTask networkBackTask = new NetworkTask(params);
                    networkBackTask.setTag("dlcardback");
                    NetworkUtils.executeNetwork(networkBackTask, new HttpResponse.NetWorkResponse<JSONObject>() {
                        @Override
                        public void onSuccessResponse(JSONObject json) {
                            try {
                                int ret = json.getInt("ret");
                                if (ret == 0) {
                                    String responseData = json.getString("sessionKey");
                                    if (responseData != null) {
                                        if (responseData.equals("-1")) {
                                            Config.showToast(context, "照片上传失败，请重试！");
                                            Config.dismissProgress();
                                        } else {
                                            NetworkTask photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadDrivingicenseBack_VALUE);
                                            photoTask.setTag("dlcard");
                                            UserInterface.UploadDrivingicenseBack.Request.Builder request = UserInterface.UploadDrivingicenseBack.Request.newBuilder();
                                            request.setIdCode(responseData);
                                            photoTask.setBusiData(request.build().toByteArray());
                                            NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                                @Override
                                                public void onSuccessResponse(UUResponseData responseData) {

                                                    if (responseData.getRet() == 0) {
                                                        try {
                                                            UserInterface.UploadDrivingicenseBack.Response response = UserInterface.UploadDrivingicenseBack.Response.parseFrom(responseData.getBusiData());

                                                            if (response.getRet() == 0) {
                                                                mBackRoot.setVisibility(View.INVISIBLE);
                                                                mUserDriverBack.setImageBitmap(bitmap);
                                                                driverUsefulFlag = true;
                                                                userModel.driverBackPic = picPath;
                                                                userModel.driverBackState = "1";
                                                                userService.modifyModel(userModel);
                                                                mJszYxqPhoto.setBackgroundResource(R.drawable.camere_button);
                                                                driverUsefulFlag = true;
                                                                jszyxqTv.setText("点击上传");
                                                                setUserStatus();
                                                                setSureButton();
                                                            }

                                                        } catch (InvalidProtocolBufferException e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        Config.showToast(context, "照片上传失败，请重试！");
                                                        Config.dismissProgress();

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
                    break;
            }
        } else {
            Toast.makeText(context, SysConfig.NETWORK_PHOTO_FAIL, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            finish();
            return false;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
//

    public void getNewEx() {

        Config.showProgressDialog(context, false, null);
        UserInterface.QueryRefusedReasonAndPic.Request.Builder builder = UserInterface.QueryRefusedReasonAndPic.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.QueryRefusedReasonAndPic_VALUE);
        task.setTag("QueryRefusedReasonAndPic");
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        UserInterface.QueryRefusedReasonAndPic.Response response = UserInterface.QueryRefusedReasonAndPic.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            if (errorPic != null) {
                                errorPic.clear();
                            }
                            if (needUploadPic != null) {
                                needUploadPic.clear();
                            }


                            if (response.getUploadedImgTypesCount() > 0) {

                                List<UserCommon.RenterImgType> uploadedImgTypesList = response.getUploadedImgTypesList();
                                for (UserCommon.RenterImgType type : uploadedImgTypesList) {
                                    if (type.getNumber() == UserCommon.RenterImgType.ID_FRONT_VALUE) {
                                        needUploadPic.add("dl_front");
                                    } else if (type.getNumber() == UserCommon.RenterImgType.ID_BACK_VALUE) {
                                        needUploadPic.add("dl_back");
                                    }
                                }
                            }
                            if (needUploadPic != null) {
                                if (needUploadPic.size() > 0) {
                                    if (needUploadPic.contains("dl_front")) {
                                        driverCardFlag = true;
                                    }
                                    if (needUploadPic.contains("dl_back")) {
                                        driverUsefulFlag = true;
                                    }
                                }
                            }
                            if (response.getRefusedImgTypesCount() > 0) {
                                List<UserCommon.RenterImgType> refusedImgTypesList = response.getRefusedImgTypesList();
                                for (UserCommon.RenterImgType type : refusedImgTypesList) {
                                    if (type.getNumber() == UserCommon.RenterImgType.DL_FRONT_VALUE) {
                                        errorPic.add("dl_front");
                                    } else if (type.getNumber() == UserCommon.RenterImgType.DL_BACK_VALUE) {
                                        errorPic.add("dl_back");
                                    }
                                }
                            }

                            if (errorPic != null) {
                                if (errorPic.size() > 0) {
                                    if (errorPic.contains("dl_front")) {
                                        driverCardFlag = false;
                                        findViewById(R.id.step_2).setEnabled(false);
                                        findViewById(R.id.step_2).setSelected(false);
                                        mJszPhoto.setBackgroundResource(R.drawable.error_photo);
                                        jszTv.setText("重新上传");
                                    } else {
//                                        driverCardFlag = true;
                                        mJszPhoto.setBackgroundResource(R.drawable.camere_button);
                                        jszTv.setText("点击上传");
                                    }
                                    if (errorPic.contains("dl_back")) {
                                        driverUsefulFlag = false;
                                        findViewById(R.id.step_2).setEnabled(false);
                                        findViewById(R.id.step_2).setSelected(false);
                                        mJszYxqPhoto.setBackgroundResource(R.drawable.error_photo);
                                        jszyxqTv.setText("重新上传");
                                    } else {
//                                        driverUsefulFlag = true;
                                        mJszYxqPhoto.setBackgroundResource(R.drawable.camere_button);
                                        jszyxqTv.setText("点击上传");
                                    }

                                } else if (errorPic.size() == 0) {
//                                    driverCardFlag = true;
                                    mJszPhoto.setBackgroundResource(R.drawable.camere_button);
                                    jszTv.setText("点击上传");
//                                    driverUsefulFlag = true;
                                    mJszYxqPhoto.setBackgroundResource(R.drawable.camere_button);
                                    jszyxqTv.setText("点击上传");
                                }

                            } else {
//                                driverCardFlag = true;
                                mJszPhoto.setBackgroundResource(R.drawable.camere_button);
                                jszTv.setText("点击上传");
//                                driverUsefulFlag = true;
                                mJszYxqPhoto.setBackgroundResource(R.drawable.camere_button);
                                jszyxqTv.setText("点击上传");
                            }

                            if (driverUsefulFlag && driverCardFlag) {
                                findViewById(R.id.step_2).setEnabled(false);
                                findViewById(R.id.step_2).setSelected(false);
                            } else {
                                findViewById(R.id.step_2).setEnabled(true);
                                findViewById(R.id.step_2).setSelected(true);

                            }
                            setSureButton();

                        } else {
                            Config.showFiledToast(context);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
            }
        });

    }

}
