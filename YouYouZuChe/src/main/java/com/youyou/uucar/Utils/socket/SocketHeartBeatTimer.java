package com.youyou.uucar.Utils.socket;

import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.head.HeaderCommon;
import com.uu.client.bean.longconnection.LongConnectionInterface;
import com.youyou.uucar.Utils.Network.AESUtils;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.Config;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * socket心跳
 *
 * @author philzhang
 */
public class SocketHeartBeatTimer {

    private static final int TIME_INTERVAL = 40 * 1000;

    private Timer timer = null;
    private volatile static SocketHeartBeatTimer mSocketHeartBeatTimer = null;
    private SocketTimeTask mSocketTimeTask;
    private SocketCommunication mSocketConnectComminucator;

    private String mRequest = "heart";

    private SocketHeartBeatTimer(SocketCommunication socket) {
        this.mSocketConnectComminucator = socket;
    }

    public static SocketHeartBeatTimer getInstance(SocketCommunication socket) {
        if (mSocketHeartBeatTimer == null) {
            synchronized (SocketHeartBeatTimer.class) {
                if (mSocketHeartBeatTimer == null) {
                    mSocketHeartBeatTimer = new SocketHeartBeatTimer(socket);
                }
            }
        }
        return mSocketHeartBeatTimer;
    }

    public void startTimer() {
        if (mRequest != null) {
            //TODO 修改Timer可能为null
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            mSocketTimeTask = new SocketTimeTask();
            timer.schedule(mSocketTimeTask, TIME_INTERVAL);
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (mSocketTimeTask != null) {
            mSocketTimeTask.cancel();
        }
        mSocketTimeTask = null;
        timer = null;
    }

    private class SocketTimeTask extends TimerTask {
        @Override
        public void run() {
            if (mRequest != null) {
                sendHearBeat();
                mSocketTimeTask = new SocketTimeTask();
                if (timer == null){
                    timer = new Timer();
                }else {
                    timer.schedule(mSocketTimeTask, TIME_INTERVAL);
                }

            }
        }

    }

    public void sendHearBeat() {
        this.mSocketConnectComminucator.add2ApiQueue(createHeartBeatPackage());
    }

    private byte[] createHeartBeatPackage() {

        try {
            HeaderCommon.CommonReqHeader.Builder reqHeaderBuilder = HeaderCommon.CommonReqHeader.newBuilder();
//        reqHeaderBuilder.setB2(ByteString.copyFrom(UserSecurityConfig.b2));
            reqHeaderBuilder.setCmd(CmdCodeDef.CmdCode.HeartBeat_VALUE);
            reqHeaderBuilder.setSeq(NetworkUtils.seq.getAndIncrement());
            reqHeaderBuilder.setUa(NetworkTask.DEFEALT_UA);
            if (UserSecurityConfig.b2_ticket == null) {
                return null;
            }
            reqHeaderBuilder.setB2(ByteString.copyFrom(UserSecurityConfig.b2_ticket));
            reqHeaderBuilder.setUuid(UserSecurityConfig.UUID);
            LongConnectionInterface.HeartBeat.Request.Builder longConnect = LongConnectionInterface.HeartBeat.Request.newBuilder();
            longConnect.setTime(System.currentTimeMillis());
            HeaderCommon.RequestData.Builder reqDataBuilder = HeaderCommon.RequestData.newBuilder();
            reqDataBuilder.setHeader(reqHeaderBuilder.build());
            reqDataBuilder.setBusiData(longConnect.build().toByteString());
            HeaderCommon.RequestPackage.Builder requestPackageBuilder = HeaderCommon.RequestPackage.newBuilder();
            if (UserSecurityConfig.b3_ticket == null) {
                return null;
            }
            requestPackageBuilder.setB3(ByteString.copyFrom(UserSecurityConfig.b3_ticket));
            if (UserSecurityConfig.userId_ticket == 0) {
                return null;
            }
            requestPackageBuilder.setUserId(UserSecurityConfig.userId_ticket);
            if (UserSecurityConfig.b3Key_ticket == null) {
                return null;
            }
            requestPackageBuilder.setReqData(ByteString.copyFrom(AESUtils.encrypt(UserSecurityConfig.b3Key_ticket, reqDataBuilder.build().toByteArray())));
            byte[] reqData = requestPackageBuilder.build().toByteArray();
            ByteBuffer requestBuffer = ByteBuffer.allocate(4 + reqData.length);
            requestBuffer.putInt(reqData.length);
            requestBuffer.put(reqData);
            Log.e("SocketCommunication", "发送心跳————createHeartBeatPackage___userId:" + requestPackageBuilder.getUserId());
            Toast.makeText(Config.currentContext, "长连接心跳, createHeartBeatPackage___userId:" + requestPackageBuilder.getUserId(), Toast.LENGTH_SHORT).show();
            byte[] heart = requestBuffer.array();
            return heart;
        } catch (Exception e) {
            return null;
        }
    }
}
