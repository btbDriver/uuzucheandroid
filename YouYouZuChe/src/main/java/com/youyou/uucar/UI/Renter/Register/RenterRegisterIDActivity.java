package com.youyou.uucar.UI.Renter.Register;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.my.URLWebView;
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

//import com.ab.http.AbHttpUtil;
//import com.ab.http.AbRequestParams;
//import com.youyou.uucar.Utils.Support.MyAbStringHttpResponseListener;

public class RenterRegisterIDActivity extends BaseActivity {
    public static final int GOTONEXT = 10;
    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    public static final String KEY_PHOTO_PATH = "photo_path";
    private static final String TAG = "RenterRegisterIDActivity";
    public final int ID_FRONT = 1;
    public final int ID_BACK = 2;
    public String tag = RenterRegisterIDActivity.class.getSimpleName();
    public int current = 0;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("请选择 ");
            switch (v.getId()) {
                case R.id.front_icon:
                    stringBuilder.append("身份证正面信息 照片");
                    current = ID_FRONT;
                    break;
                case R.id.front_root_transport:
                    stringBuilder.append("身份证正面信息 照片");
                    current = ID_FRONT;
                    break;
                case R.id.back_icon:
                    stringBuilder.append("身份证背面信息 照片");
                    current = ID_BACK;
                    break;
                case R.id.back_root_transport:
                    stringBuilder.append("身份证背面信息 照片");
                    current = ID_BACK;
                    break;
            }

            new AlertDialog.Builder(context)
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
    public String phone;
    @InjectView(R.id.next)
    TextView mNext;
    boolean idCardFlag = false, idCardBackFlag = false;
    @InjectView(R.id.user_id)
    ImageView mUserId;
    @InjectView(R.id.front_root)
    RelativeLayout mFrontRoot;
    @InjectView(R.id.user_id_back)
    ImageView mUserIdBack;
    @InjectView(R.id.back_root)
    RelativeLayout mBackRoot;
    @InjectView(R.id.front_icon)
    ImageView mFrontIcon;
    @InjectView(R.id.front_upload_text)
    TextView mFrontUploadText;
    @InjectView(R.id.back_icon)
    ImageView mBackIcon;
    @InjectView(R.id.back_upload_text)
    TextView mBackUploadText;
    @InjectView(R.id.front_root_transport)
    RelativeLayout frontTransport;
    @InjectView(R.id.back_root_transport)
    RelativeLayout backTransport;
    @InjectView(R.id.sign)
    ImageView sign;

    @OnClick(R.id.sign)
    public void signClick() {
        sign.setSelected(!sign.isSelected());
    }

    @OnClick(R.id.sign_url)
    public void signUrlClick() {
        //TODO 租客服务协议URL
        Intent intent = new Intent(context, URLWebView.class);
        intent.putExtra("url", ServerMutualConfig.ruleurl);
        intent.putExtra(SysConfig.TITLE, "租客服务协议");
        context.startActivity(intent);

    }

    Bitmap bitmap;
    Cursor cursor;
    UserModel model;
    UserService service;
    private Uri photoUri;
    private String planteNumber, carType;
    /**
     * 获取到的图片路径
     */
    private String picPath;
    private String bigPicPath;
    private ArrayList<String> errorPic = new ArrayList<String>();
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
        if (!sign.isSelected()) {
            showToast("请勾选租客服务协议");
            return;
        }
        showProgress(false);

        if (!idCardBackFlag) {
            showToast("请上传身份证反面照片");
            return;
        }
        if (!idCardFlag) {
            showToast("请上传身份证正面照片");
            return;
        }

        UserInterface.UserSignAgreement.Request.Builder builder = UserInterface.UserSignAgreement.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.UserSignAgreement_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("UserAgreement");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        UserInterface.UserSignAgreement.Response response = UserInterface.UserSignAgreement.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            Intent intent = new Intent(context, RenterRegisterDriverActivity.class);
                            intent.putStringArrayListExtra(SysConfig.REFUSE_PIC, errorPic);
                            startActivityForResult(intent, GOTONEXT);
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
                dismissProgress();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id);
        getModel();
        findViewById(R.id.step_1).setSelected(true);
        findViewById(R.id.step_1_text).setSelected(true);
        ButterKnife.inject(this);
        sign.setSelected(true);
        initListener();
        showData();
        setSureButton();
        getNewEx();
    }

    public void setUserStatus() {
        if (model.idCardFrontState.equals("1") && model.idCardBackState.equals("1") && model.driverFrontState.equals("1") && model.driverBackState.equals("1")) {
            model.userStatus = User.USER_STATUS_ALL;
            saveModel();
        }
    }

    private boolean saveModel() {
        boolean flag = service.modifyModel(model);
        return flag;
    }

    private void showData() {
        if (model.idCardFrontState != null && model.idCardFrontState.equals("1")) {
            mFrontRoot.setVisibility(View.INVISIBLE);
            bitmap = BitmapUtils.getInSampleBitmap(model.idCardFrontPic, 152, 96);
            mUserId.setImageBitmap(bitmap);
            mFrontUploadText.setText("上传成功");
            mFrontIcon.setBackgroundResource(R.drawable.camere_button);
            idCardFlag = true;
        }
        if (model.idCardBackState != null && model.idCardBackState.equals("1")) {
            mBackRoot.setVisibility(View.INVISIBLE);
            bitmap = BitmapUtils.getInSampleBitmap(model.idCardBackPic, 152, 96);
            mUserIdBack.setImageBitmap(bitmap);
            mBackIcon.setBackgroundResource(R.drawable.camere_button);
            mBackUploadText.setText("上传成功");
            idCardBackFlag = true;
        }
        setSureButton();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initListener() {
        mFrontRoot.setOnClickListener(onClickListener);
        mBackRoot.setOnClickListener(onClickListener);
        frontTransport.setOnClickListener(onClickListener);
        backTransport.setOnClickListener(onClickListener);
    }

    public UserModel getModel() {
        service = new UserService(context);
        List<UserModel> models = service.getModelList(UserModel.class);
        model = models.get(0);
        return model;
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
        int id = item.getItemId();
        if (id == android.R.id.home || id == 0) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getPhotoFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {

            case ID_FRONT:
                fileName.append(planteNumber + "_zhuce_idcard_front.jpg");
                break;
            case ID_BACK:
                fileName.append(planteNumber + "_zhuce_idcard_back.jpg");
                break;
        }
        return fileName.toString();
    }

    private String getPhotoBigFileName(int current) {
        StringBuilder fileName = new StringBuilder();

        switch (current) {

            case ID_FRONT:
                fileName.append(planteNumber + "_big_zhuce_idcard_front.jpg");
                break;
            case ID_BACK:
                fileName.append(planteNumber + "_big_zhuce_idcard_back.jpg");
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
            if (requestCode == GOTONEXT) {
                setResult(RESULT_OK);
                finish();
            } else {
                try {
                    doPhoto(requestCode, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            {


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
//                    setImageView(bitmap, current);
                    setImageViewByVolley(bitmap, current);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        setSureButton();
    }

    private void setSureButton() {
        if (idCardFlag && idCardBackFlag) {
            mNext.setEnabled(true);
            findViewById(R.id.step_1).setSelected(true);
        } else {
            mNext.setEnabled(false);
        }
        setUserStatus();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String path = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
//            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
//            Cursor cursor = loader.loadInBackground();
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
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
        if (Config.isNetworkConnected(RenterRegisterIDActivity.this)) {
            UUParams params = new UUParams();
            showProgress(false);
            switch (current) {
                case ID_FRONT:
                    params.put("idfront", new File(picPath));
                    NetworkTask networkTask = new NetworkTask(params);
                    networkTask.setTag("idfront");
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
                                            dismissProgress();
                                        } else {
                                            NetworkTask photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadIdCardFront_VALUE);
                                            photoTask.setTag("idfront");
                                            UserInterface.UploadIdCardFront.Request.Builder request = UserInterface.UploadIdCardFront.Request.newBuilder();
                                            request.setIdCode(responseData);
                                            request.setType(1);
                                            request.setScene(2);//zu租客上传
                                            photoTask.setBusiData(request.build().toByteArray());
                                            NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                                @Override
                                                public void onSuccessResponse(UUResponseData responseData) {

                                                    if (responseData.getRet() == 0) {
                                                        try {
                                                            UserInterface.UploadIdCardFront.Response response = UserInterface.UploadIdCardFront.Response.parseFrom(responseData.getBusiData());
                                                            if (response.getRet() == 0) {
                                                                mFrontRoot.setVisibility(View.INVISIBLE);
                                                                mUserId.setImageBitmap(bitmap);
                                                                idCardFlag = true;
                                                                model.idCardFrontPic = picPath;
                                                                model.idCardFrontState = "1";
                                                                mFrontIcon.setBackgroundResource(R.drawable.camere_button);
                                                                service.modifyModel(model);
                                                                mFrontUploadText.setText("点击上传");
                                                                mFrontUploadText.setTextColor(getResources().getColor(R.color.c8));
                                                                idCardFlag = true;
                                                                setSureButton();
                                                            }

                                                        } catch (InvalidProtocolBufferException e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        Config.showToast(context, "照片上传失败，请重试！");
                                                        dismissProgress();

                                                    }
                                                }

                                                @Override
                                                public void onError(VolleyError errorResponse) {

                                                }

                                                @Override
                                                public void networkFinish() {
                                                    dismissProgress();
                                                }
                                            });


                                        }

                                    }


                                } else {
                                    dismissProgress();
                                    //表示 照片上传失败
                                    Config.showToast(context, "照片上传失败，请重新上传！");

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(VolleyError errorResponse) {
                            dismissProgress();
                        }

                        @Override
                        public void networkFinish() {

                        }
                    });

                    break;
                case ID_BACK:

                    params.put("idback", new File(picPath));
                    NetworkTask networkBackTask = new NetworkTask(params);
                    networkBackTask.setTag("idback");
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
                                            NetworkTask photoTask = new NetworkTask(CmdCodeDef.CmdCode.UploadIdCardFront_VALUE);
                                            photoTask.setTag("idback");
                                            UserInterface.UploadIdCardFront.Request.Builder request = UserInterface.UploadIdCardFront.Request.newBuilder();
                                            request.setIdCode(responseData);
                                            request.setType(2);
                                            request.setScene(2);
                                            photoTask.setBusiData(request.build().toByteArray());
                                            NetworkUtils.executeNetwork(photoTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                                @Override
                                                public void onSuccessResponse(UUResponseData responseData) {
                                                    if (responseData.getRet() == 0) {
                                                        try {
                                                            UserInterface.UploadIdCardFront.Response response = UserInterface.UploadIdCardFront.Response.parseFrom(responseData.getBusiData());

                                                            if (response.getRet() == 0) {
                                                                mBackRoot.setVisibility(View.INVISIBLE);
                                                                mUserIdBack.setImageBitmap(bitmap);
                                                                idCardBackFlag = true;
                                                                model.idCardBackPic = picPath;
                                                                model.idCardBackState = "1";
                                                                service.modifyModel(model);
                                                                mBackIcon.setBackgroundResource(R.drawable.camere_button);
                                                                idCardBackFlag = true;
                                                                mBackUploadText.setText("点击上传");
                                                                mBackUploadText.setTextColor(getResources().getColor(R.color.c8));
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

    //
//
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

                            if (response.getRefusedImgTypesCount() > 0) {
                                List<UserCommon.RenterImgType> refusedImgTypesList = response.getRefusedImgTypesList();
                                for (UserCommon.RenterImgType type : refusedImgTypesList) {
                                    if (type.getNumber() == UserCommon.RenterImgType.ID_FRONT_VALUE) {
                                        errorPic.add("id_front");
                                    } else if (type.getNumber() == UserCommon.RenterImgType.ID_BACK_VALUE) {
                                        errorPic.add("id_back");
                                    }
                                }
                            }


                            if (response.getUploadedImgTypesCount() > 0) {

                                List<UserCommon.RenterImgType> uploadedImgTypesList = response.getUploadedImgTypesList();
                                for (UserCommon.RenterImgType type : uploadedImgTypesList) {
                                    if (type.getNumber() == UserCommon.RenterImgType.ID_FRONT_VALUE) {
                                        needUploadPic.add("id_front");
                                    } else if (type.getNumber() == UserCommon.RenterImgType.ID_BACK_VALUE) {
                                        needUploadPic.add("id_back");
                                    }
                                }
                            }


                            if (needUploadPic != null) {
                                if (needUploadPic.size() > 0) {

                                    if (needUploadPic.contains("id_font")) {

                                        idCardFlag = true;
                                    }

                                    if (needUploadPic.contains("id_back")) {

                                        idCardBackFlag = true;

                                    }


                                }
                            }

                            if (errorPic != null) {
                                if (errorPic.size() > 0) {
                                    if (errorPic.contains("id_front")) {
                                        idCardFlag = false;
                                        findViewById(R.id.step_1).setEnabled(false);
                                        findViewById(R.id.step_1).setSelected(false);
                                        mFrontIcon.setBackgroundResource(R.drawable.error_photo);
                                        mFrontUploadText.setText("重新上传");
                                        mFrontUploadText.setTextColor(getResources().getColor(R.color.c3));
                                    } else {
//                                        idCardFlag = true;
                                        mFrontIcon.setBackgroundResource(R.drawable.camere_button);
                                        mFrontUploadText.setText("点击上传");
                                        mFrontUploadText.setTextColor(getResources().getColor(R.color.c8));
                                    }
                                    if (errorPic.contains("id_back")) {
                                        idCardBackFlag = false;
                                        findViewById(R.id.step_1).setEnabled(false);
                                        findViewById(R.id.step_1).setSelected(false);
                                        mBackIcon.setBackgroundResource(R.drawable.error_photo);
                                        mBackUploadText.setText("重新上传");
                                        mBackUploadText.setTextColor(getResources().getColor(R.color.c3));
                                    } else {
//                                        idCardBackFlag = true;
                                        mBackIcon.setBackgroundResource(R.drawable.camere_button);
                                        mBackUploadText.setText("点击上传");
                                        mBackUploadText.setTextColor(getResources().getColor(R.color.c8));
                                    }


                                } else if (errorPic.size() == 0) {
//                                    idCardBackFlag = true;
//                                    idCardFlag = true;
                                    mFrontUploadText.setText("点击上传");
                                    mBackUploadText.setText("点击上传");
                                    mBackUploadText.setTextColor(getResources().getColor(R.color.c8));
                                    mFrontUploadText.setTextColor(getResources().getColor(R.color.c8));
                                    mBackIcon.setBackgroundResource(R.drawable.camere_button);
                                    mFrontIcon.setBackgroundResource(R.drawable.camere_button);

                                }

                            } else {
//                                idCardBackFlag = true;
//                                idCardFlag = true;
                                mFrontUploadText.setText("点击上传");
                                mBackUploadText.setText("点击上传");
                                mBackUploadText.setTextColor(getResources().getColor(R.color.c8));
                                mFrontUploadText.setTextColor(getResources().getColor(R.color.c8));
                                mBackIcon.setBackgroundResource(R.drawable.camere_button);
                                mFrontIcon.setBackgroundResource(R.drawable.camere_button);

                            }
                            if (idCardBackFlag && idCardFlag) {
                                findViewById(R.id.step_1).setEnabled(false);
                                findViewById(R.id.step_1).setSelected(false);
                            } else {
                                findViewById(R.id.step_1).setEnabled(true);
                                findViewById(R.id.step_1).setSelected(true);

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
