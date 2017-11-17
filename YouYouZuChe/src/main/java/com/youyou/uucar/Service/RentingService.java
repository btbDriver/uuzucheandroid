package com.youyou.uucar.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.socket.SocketCommunication;

import java.util.Timer;
import java.util.TimerTask;

public class RentingService extends Service {
    public String tag = "RentingService";

    public RentingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.e(tag, "start");
//
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        if (task != null) {
            task = null;
        }
        if (task == null) {
            task = new Task();
        }
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(task, 0, 1000 * 20);
        return START_STICKY;
    }


    public Timer timer;
    Task task;

    Handler handler = new Handler();

    public class Task extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setTabNum();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        MLog.e(tag, "onDestroy");
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = null;
        super.onDestroy();
    }

    /**
     * 查询车辆品牌,当本地版本号和服务器不同步的时候调用
     */
    public void setTabNum() {
        UserInterface.StartQueryInterface.Request.Builder builder = UserInterface.StartQueryInterface.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.StartQueryInterface_VALUE);
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        final UserInterface.StartQueryInterface.Response response = UserInterface.StartQueryInterface.Response.parseFrom(responseData.getBusiData());
                        //用户相关接口
                        UserCommon.UserStatus userStatus = response.getUserStatus();
                        if (userStatus.getHasPreOrdering()) {
                            switch (userStatus.getPreOrderType()) {
                                case 1:
                                    Config.isSppedIng = true;
                                    break;
                                case 2:
                                    Config.isOneToOneIng = true;
                                    break;
                                case 3:
                                    Config.isSppedIng = true;
                                    Config.speedHasAgree = true;
                                    break;
                            }
                        } else {

                            if (!Config.isUserCancel)//如果不是用户主动取消的
                            {
                                if (Config.isSppedIng)//如果之前正在快速约车
                                {
                                    if (Config.outApp(RentingService.this)) {
                                        SocketCommunication.showNotification("友友租车", "您没有及时选车,约车已失效", MainActivityTab.GOTO_RENTER_FIND_CAR);
                                    } else {
                                        if (SysConfig.DEVELOP_MODE) {

                                            Config.showToast(Config.currentContext, "DEVELOP_MODE__您没有及时选车,约车已失效");
                                        } else {
                                            Config.showToast(Config.currentContext, "您没有及时选车,约车已失效");
                                        }

                                    }
                                    stopService(new Intent(RentingService.this, RentingService.class));
                                }
                            } else {
                                Config.isUserCancel = false;
                            }

                            Config.isSppedIng = false;
                            Config.isOneToOneIng = false;
                        }
                        if (userStatus.getWaitPayOrderIdCount() > 0) {
                            Config.hasPayOrder = true;
                            Config.waitPayOrderId = userStatus.getWaitPayOrderId(0);
                        } else {
                            Config.hasPayOrder = false;
                        }
                        if (MainActivityTab.instance != null) {
                            if (Config.isSppedIng || Config.isOneToOneIng) {
                                MainActivityTab.instance.speed.name.setText("约车中...");
                            } else if (Config.hasPayOrder) {
                                MainActivityTab.instance.speed.name.setText("约车成功");
                            } else {
                                MainActivityTab.instance.speed.name.setText("一键约车");
                            }
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

            }
        });
    }
}
