package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.observer.ObserverListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

//import com.ab.http.AbHttpUtil;
//import com.ab.http.AbRequestParams;
//import com.youyou.uucar.Utils.Support.MyAbStringHttpResponseListener;
//import com.youyou.uucar.Utils.Support.SysConfig;

public class RenterRegisterVerifyError extends BaseActivity {
    public static final String TAG = RenterRegisterVerifyError.class.getSimpleName();
    private static final String SCHEME = "settings";
    private static final String AUTHORITY = "RenterRegisterVerifyError";
    public static final Uri URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).build();
    public String tag = RenterRegisterVerifyError.class.getSimpleName();
    public ObserverListener listener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
//            getNewEx();

        }
    };
    @InjectView(R.id.refuse_reason_textview)
    TextView refuseReasonTv;
    private ArrayList<Integer> errorPic = new ArrayList<Integer>();

    @OnClick(R.id.contact_service)
    public void contactService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("拨打客服电话");
        builder.setMessage(Config.kefuphone);
        builder.setNegativeButton("拨打", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputStr = Config.kefuphone;
                if (inputStr.trim().length() != 0) {
                    MobclickAgent.onEvent(context, "ContactService");
                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                    startActivity(phoneIntent);
                }
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
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

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_renter_register_verify_error);
        ButterKnife.inject(this);
    }


    @OnClick(R.id.reupload)
    public void reUpload() {

        if (errorPic.size() > 0) {
            Intent intent = new Intent();
            intent.setClass(context, RenterRegisterIDActivity.class);
//            for (errorPic)
//            if (errorPic.contains("id_front") || errorPic.contains("id_back")) {
//                intent.setClass(context, RenterRegisterIDActivity.class);
//            } else {
//                intent.setClass(context, RenterRegisterIDActivity.class);
//            }
//            intent.putStringArrayListExtra(SysConfig.REFUSE_PIC, errorPic);
            startActivity(intent);
        }

    }

    public void getNewEx() {
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
                            refuseReasonTv.setText("原因：" + response.getRefusedReason());
                            if (response.getRefusedImgTypesCount() > 0) {
                                List<UserCommon.RenterImgType> refusedImgTypesList = response.getRefusedImgTypesList();
                                for (UserCommon.RenterImgType type : refusedImgTypesList) {
                                    errorPic.add(type.getNumber());
                                }
                            }
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

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getNewEx();
    }
}
