package com.youyou.uucar.Utils.Network;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.head.HeaderCommon;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.exception.InterfaceFallException;
import com.youyou.uucar.Utils.Network.exception.NetWorkFallException;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverManager;
import com.youyou.uucar.Utils.volley.toolbox.BaseMultipartRequest;
import com.youyou.uucar.Utils.volley.toolbox.HttpProtoVolleyRequest;
import com.youyou.uucar.Utils.volley.toolbox.JsonMultipartRequest;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by taurusxi on 14-8-6.
 */
public class VolleyNetworkHelper<T> implements HttpNetwork<T> {
    private static RequestQueue mRequestQueue;
//    private String httpUrl;

    public VolleyNetworkHelper() {
        if (mRequestQueue == null) {
            mRequestQueue = UUAppCar.getRequestQueue();
        }
    }


    @Override
    public void doPost(int seq, NetworkTask networkTask, HttpResponse.NetWorkResponse baseResponse) {
        switch (networkTask.getType()) {
            case NetworkTask.PROTOBUF_TYPE:
                getProtobufPostHttp(seq, networkTask, baseResponse);
                break;
            case NetworkTask.CAMERA_TYPE:
                getCameraPostHttp(seq, networkTask, baseResponse);
                break;
        }
    }

    private void getCameraPostHttp(int seq, NetworkTask networkTask, final HttpResponse.NetWorkResponse baseResponse) {
        String httpUrl = "http://" + NetworkTask.BASEURL + ":28080/fileupload/fileupload.f?public=1";
        networkTask.setHttpUrl(httpUrl);
        MLog.e("getCameraPostHttp + httpURL :", networkTask.getHttpUrl());
        BaseMultipartRequest baseMultipartRequest = new JsonMultipartRequest(networkTask.getHttpUrl(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (baseResponse != null) {
                    baseResponse.networkFinish();
                    baseResponse.onSuccessResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (baseResponse != null) {
                    baseResponse.networkFinish();
                    baseResponse.onError(error);
                }

            }
        });
        UUParams params = networkTask.getUuParams();
        networkTask.setSeq(seq);
//        params.put("appverison", "2.1");
        LinkedHashMap<String, String> uriMap = params.getUrlParams();
        ConcurrentHashMap<String, UUParams.FileWrapper> fileMap = params.getFileParams();
        for (Map.Entry<String, String> uri : uriMap.entrySet()) {
            baseMultipartRequest.addStringBody(uri.getKey(), uri.getValue());
        }
        for (Map.Entry<String, UUParams.FileWrapper> fileWrapper : fileMap.entrySet()) {
            baseMultipartRequest.addFileBody(fileWrapper.getKey(), fileWrapper.getValue().file);
        }
        baseMultipartRequest.buildMultipartEntity();
        baseMultipartRequest.setTag(networkTask.getTag());
        baseMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 0, 1.0f));
        mRequestQueue.add(baseMultipartRequest);
    }


    private void getProtobufPostHttp(final int seq, final NetworkTask networkTask, final HttpResponse.NetWorkResponse<UUResponseData> baseResponse) {
        String httpUrl = "";
        final int type = UserSecurityConfig.getHttpUrl(networkTask);
        if (type == NetworkTask.SSL_NETWORK) {
            httpUrl = "https://" + NetworkTask.BASEURL + ":8083/public";
        } else if (type == NetworkTask.HTTP_NETWORK) {
            httpUrl = "http://" + NetworkTask.BASEURL + ":8081";
        } else if (type == NetworkTask.PUBLIC_NETWORK) {
            httpUrl = "http://" + NetworkTask.BASEURL + ":8081/public";
        }
        networkTask.setSeq(seq);
        HttpProtoVolleyRequest httpProtoVolleyRequest = null;
        httpProtoVolleyRequest = new HttpProtoVolleyRequest(httpUrl, new Response.Listener<HeaderCommon.ResponsePackage>() {
            @Override
            public void onResponse(HeaderCommon.ResponsePackage publicResponsePackage) {
                baseResponse.networkFinish();
                if (publicResponsePackage == null) {
                    baseResponse.onError(new VolleyError("publicResponsePackage为null"));
                    return;
                }
                int ret = publicResponsePackage.getRet();
                final UserSecurityMap.SecurityItem securityItem = UserSecurityMap.get(seq);
                MLog.e("VolleyNetWorkHelper", "ret = " + ret + "__securityItem = " + securityItem.toString());
                if (securityItem.networkItem.getCmd() == CmdCodeDef.CmdCode.LogoutSSL_VALUE) {
                    baseResponse.onSuccessResponse(null);
                    return;
                }
                if (SysConfig.DEVELOP_MODE) {
                    if (ret != 0) {
                        Toast.makeText(Config.currentContext, "securityItem__ret返回:" + ret + "和cmd:" + securityItem.networkItem.getCmd(), Toast.LENGTH_LONG).show();
                    }
                }
                if (ret == 0) {

                    UUResponseData data = new UUResponseData();
                    data.setRet(ret);
                    data.setHttp(true);
                    data.setSeq(publicResponsePackage.getSeq());
                    data.setToastMsg("");
                    //表示接口调用成功
                    boolean isPublic = securityItem.isPublic;
                    byte[] output;
                    if (isPublic) {
                        output = AESUtils.decrypt(UserSecurityConfig.b3Key, publicResponsePackage.getResData().toByteArray());
                    } else {
                        output = AESUtils.decrypt(securityItem.b3KeyItem, publicResponsePackage.getResData().toByteArray());
                    }
                    try {
                        HeaderCommon.ResponseData responseData = HeaderCommon.ResponseData.parseFrom(output);
//                        MLog.e("BasicNetwork", " byte = " + HexUtil.bytes2HexStr(responseData.getBusiData().toByteArray()));
                        data.setCmd(responseData.getCmd());
                        data.setBusiData(responseData.getBusiData().toByteArray());
                        data.setResponseCommonMsg(responseData.getCommonMsg());
                        baseResponse.onSuccessResponse(data);
                    } catch (InvalidProtocolBufferException e) {
                        baseResponse.onError(new InterfaceFallException());
                    }
                } else if (ret == -1) {
                    //表示接口调用失败
                    baseResponse.onError(new NetWorkFallException("网络失败"));
                } else if (ret == -11) {
                    //登录态失效，客户端强制用户重新登陆

                    //如果不是游客模式的话才弹窗
                    if (!NetworkUtils.isShowDialog.get() && !Config.isGuest(Config.currentContext)) {
                        NetworkUtils.isShowDialog.set(true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Config.currentContext);
                        builder.setMessage("你的帐号已经失效，请重新登录");
                        builder.setNegativeButton("登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetworkUtils.isShowDialog.set(false);
                                dialog.dismiss();
                                Intent intent = new Intent(Config.currentContext, LoginActivity.class);
                                intent.putExtra("tomain", true);
                                intent.putExtra(SysConfig.UN_USUAL, true);
                                Config.currentContext.startActivity(intent);
                            }
                        });
                        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetworkUtils.isShowDialog.set(false);
                                ObserverManager.getObserver(ObserverManager.TICKET_LOGOUT).observer("", "");
                                dialog.dismiss();
                            }
                        });
                        Config.loginDialog = builder.create();
                        Config.loginDialog.setCancelable(false);
                        Config.loginDialog.setCanceledOnTouchOutside(false);
                        Config.loginDialog.show();
                    }


                } else if (ret == -12) {
                    //客户端的匿名票据失效，如果server返回该值，客户端需要使用公共密钥再重试一次接口访问，并且启动客户端的后台异步匿名登陆
                    NetworkTask networkItem = securityItem.networkItem;
                    networkItem.setCmd(CmdCodeDef.CmdCode.UpdateTicketSSL_VALUE);
                    networkItem.setUsePublic(true);
                    getProtobufPostHttp(seq, networkItem, baseResponse);
                    UserSecurityConfig.anonymousLoginSSL();
                } else if (ret == -13) {
                    //该用户在另一个设备上登陆，当前设备的登录态失效，客户端需要弹出提示框，让用户去登陆页登陆
                    //TODO 弹窗跳登入
                    if (!NetworkUtils.isShowDialog.get()) {
                        NetworkUtils.isShowDialog.set(true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Config.currentContext);
                        builder.setMessage("您的帐号已在其它设备登录，请重新登录");
                        builder.setNegativeButton("登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetworkUtils.isShowDialog.set(false);
                                dialog.dismiss();
                                Intent intent = new Intent(Config.currentContext, LoginActivity.class);
                                intent.putExtra("tomain", true);
                                intent.putExtra(SysConfig.UN_USUAL, true);
                                Config.currentContext.startActivity(intent);
                            }
                        });
                        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NetworkUtils.isShowDialog.set(false);
                                ObserverManager.getObserver(ObserverManager.TICKET_LOGOUT).observer("", "");
                                dialog.dismiss();
                            }
                        });
                        Config.loginDialog = builder.create();
                        Config.loginDialog.setCancelable(false);
                        Config.loginDialog.setCanceledOnTouchOutside(false);
                        Config.loginDialog.show();
                    }
                } else if (ret == -21) {
                    //频率限制
                    Config.showToast(Config.currentContext, "您操作太快，请稍候重试！");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                baseResponse.networkFinish();
                baseResponse.onError(volleyError);
            }
        }) {
            @Override
            public NetworkTask getNetworkTask() {
                return networkTask;
            }
        };
        httpProtoVolleyRequest.setTag(networkTask.getTag());
        httpProtoVolleyRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 0, 1.0f));
        mRequestQueue.add(httpProtoVolleyRequest);
    }

}
