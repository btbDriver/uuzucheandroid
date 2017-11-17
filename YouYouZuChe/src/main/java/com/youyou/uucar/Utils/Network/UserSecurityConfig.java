package com.youyou.uucar.Utils.Network;

import android.os.SystemClock;

import com.android.volley.VolleyError;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.login.LoginInterface;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;

import java.nio.charset.Charset;

/**
 * Created by taurusxi on 14-8-16.
 */
public class UserSecurityConfig {

    public static byte[] b3Key = "uuvr-20140814-fixed-b3-key".getBytes(Charset.forName("utf-8"));
    //    public static byte[] b2 = "uu-fixed-b3-key".getBytes(Charset.forName("utf-8"));
//    public static byte[] b3 = "uu-fixed-b3-key".getBytes(Charset.forName("utf-8"));
    public static int ticketStartTime; //票据开始时间
    public static int validSecs; //票据有效时间
    public static int needUpdateTicketTime; //需要更换票据时间
    public static int ticketFailureTime; //票据失效时间
    public static int userId = 0;
    public static byte[] b3Key_ticket;
    public static byte[] b2_ticket;
    public static byte[] b3_ticket;
    public static byte[] b4_ticket;
    public static int userId_ticket;


    public static boolean isUpdatingTicket = false;
    public static boolean isAnonymousLogin = false;

    public static String UUID;

    public static int getHttpUrl(final NetworkTask networkTask) {
        int cmd = networkTask.getCmd();
        if (cmd < 1000) {
            return NetworkTask.SSL_NETWORK;
        } else {

            if (UserSecurityConfig.ticketFailureTime * 1000 <= SystemClock.currentThreadTimeMillis()) {
                return NetworkTask.PUBLIC_NETWORK;
            } else {
                return NetworkTask.HTTP_NETWORK;
            }
        }
    }

    public static boolean isPuilc(final NetworkTask networkTask) {

        boolean flag = false;
        int cmd = networkTask.getCmd();
        if (cmd == CmdCodeDef.CmdCode.SmsLoginSSL_VALUE || cmd == CmdCodeDef.CmdCode.GetSmsVerifyCodeSSL_VALUE || cmd == CmdCodeDef.CmdCode.AnonymousLoginSSL_VALUE || cmd == CmdCodeDef.CmdCode.AccountLoginSSL_VALUE || cmd == CmdCodeDef.CmdCode.GetVoiceCallVerifyCodeSSL_VALUE/* || cmd == CmdCodeDef.CmdCode.SetPasswordSSL_VALUE*/) {
            return true;
        }
        if (UserSecurityConfig.b3Key_ticket == null) {
            //说明票据为空

            flag = true;
        } else {
            if (UserSecurityConfig.ticketFailureTime <= (SystemClock.currentThreadTimeMillis() / 1000)) {
                //票据失效

                flag = true;
            } else {
                //票据有效

                if (UserSecurityConfig.needUpdateTicketTime <= (SystemClock.currentThreadTimeMillis() / 1000)) {
                    if (!isUpdatingTicket) {
                        updateTicket();
                    }
                }

                flag = false;
            }
        }

        if (flag) {
            if (!isAnonymousLogin) {
                anonymousLoginSSL();
            }
        }


        return flag;
    }

    public static void updateTicket() {
        if (UserSecurityConfig.userId_ticket != 0) {
            if (UserSecurityConfig.b2_ticket != null) {
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UpdateTicketSSL_VALUE);
                networkTask.setTag("updateTicket");
                LoginInterface.UpdateTicketSSL.Request.Builder request = LoginInterface.UpdateTicketSSL.Request.newBuilder();
                request.setUserId(userId_ticket);
                request.setB2(ByteString.copyFrom(b2_ticket));
                networkTask.setBusiData(request.build().toByteArray());
                isUpdatingTicket = true;
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                    @Override
                    public void onSuccessResponse(UUResponseData responseData) {
                        if (responseData.getRet() == 0) {
                            int startTime = (int) (System.currentTimeMillis() / 1000);
                            try {
                                LoginInterface.UpdateTicketSSL.Response response = LoginInterface.UpdateTicketSSL.Response.parseFrom(responseData.getBusiData());
                                Config.updateUserSecurity(UUAppCar.getInstance().getApplicationContext(), response.getUserSecurityTicket(), userId_ticket, startTime);

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
                        isUpdatingTicket = false;
                    }
                });
            }
        }
    }


    public static void anonymousLoginSSL() {

        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AnonymousLoginSSL_VALUE);
        networkTask.setTag("updateTicket");
        LoginInterface.AnonymousLoginSSL.Request.Builder request = LoginInterface.AnonymousLoginSSL.Request.newBuilder();
        networkTask.setBusiData(request.build().toByteArray());

        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    int startTime = (int) (System.currentTimeMillis() / 1000);
                    try {
                        LoginInterface.AnonymousLoginSSL.Response response = LoginInterface.AnonymousLoginSSL.Response.parseFrom(responseData.getBusiData());
                        Config.updateUserSecurity(Config.currentContext, response.getUserSecurityTicket(), response.getUserId(), startTime);
                        isAnonymousLogin = true;
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {

                MLog.e("UserSecurityConfig", "anonymousLoginSSL error = " + errorResponse);
            }

            @Override
            public void networkFinish() {
                isAnonymousLogin = false;
            }
        });


    }
}
