package com.youyou.uucar.Utils.socket;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.banner.OperateInterface;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.head.HeaderCommon;
import com.uu.client.bean.longconnection.LongConnectionInterface;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.rent.OneToOneWaitActivity;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Welcome.StartActivity;
import com.youyou.uucar.UI.operate.OperatePopActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.AESUtils;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by taurusxi on 14-8-16.
 */
public class SocketCommunication
{
    public static final  String SCENE_OWNER_CAR_MANAGER = "scene_owner_car_manager";
    private static final String DEFAULT_IP              = NetworkTask.BASEURL;
    private static final int    DEFALUT_PORT            = 8086;
    private static final String PUSH_TEST_SERVER        = DEFAULT_IP + ":" + DEFALUT_PORT;
    private                 String              ipLinkId;  // ip 地址
    private                 String              ipLinkAddr; // ip 网站
    private                 int                 port; // 端口
    private                 Selector            mSelector;
    private static volatile SocketCommunication socketCommunication;
    private static final Object lockObj = new Object();
    private SocketChannelReader socketChannleReader;
    //    private SocketTimeOutThread timeout;
    private SocketChannel       mApiSocketChannel;
    private SelectionKey        mApiSelectionKey;
    private              boolean usePushTest          = false;// 测试环境联调长连接
    private              boolean isApiSocketAvailable = false;
    private static final int     CONNECT_MODE_DOMAIN  = 1;
    private static final int     CONNECT_MODE_IP      = 2;
    private              int     HANDLE_SHAKE_SEQ     = 100000;
    /**
     * 连接的模式，是ip还是域名
     */
    private              int     mConnectUseMode      = CONNECT_MODE_IP;
    private static final boolean DEBUG                = true;
    private static final String  TAG                  = SocketCommunication.class.getSimpleName();

    private final static int                                    PUSH         = 1;
    private final static int                                    LONG_CONNECR = 2;
    private final static int                                    RE_CONNECT   = 3;
    private              ConcurrentHashMap<Integer, SocketTask> mRequest     = new ConcurrentHashMap<Integer, SocketTask>();

    //    private final AtomicBoolean resetBoolean = new AtomicBoolean(false);
    private Handler mHandler;
    private static final Logger logger = LoggerFactory.getLogger(SocketCommunication.class);

    //TODO IP切换有问题，加TODO
    private SocketCommunication()
    {
        try
        {
//            AsyncTask
            //TODO 长连接问题
            mSelector = Selector.open();
            startLooper();
            startHandlerLooper();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void startHandlerLooper()
    {

    }

    private void startLooper()
    {
        if (socketChannleReader == null)
        {
            socketChannleReader = new SocketChannelReader();
            socketChannleReader.setName("socketchannel_thread");
            socketChannleReader.setOnReaderListener(mOnSocketResponseListener);
            socketChannleReader.start();
        }
    }

    public static SocketCommunication getInstance()
    {
        if (socketCommunication == null)
        {
            synchronized (lockObj)
            {
                if (socketCommunication == null)
                {
                    socketCommunication = new SocketCommunication();
                }
            }
        }
        return socketCommunication;
    }

    public static boolean CREATE_CONNECT = false;

    public void createConnect()
    {
        if (CREATE_CONNECT)
        {
            return;
        }
        if (mHandler != null)
        {
            loggerSdCard("createConnect____mHandler:释放前");
            MLog.e(TAG, "createConnect____mHandler:释放前");
            Looper looper = mHandler.getLooper();
            looper.getThread().interrupt();
            looper.quit();
            loggerSdCard("createConnect____mHandler:释放后");
            MLog.e(TAG, "createConnect____mHandler:释放后");
        }
        if (!isSocketApiUsable())
        {
            setApiSocketAvailable(false);
            try
            {
                mApiSocketChannel = createSocketChannel();
                InetSocketAddress socketInetAddress = getSocketAddress();
                if (socketInetAddress != null && mApiSocketChannel != null)
                {
                    mApiSocketChannel.connect(socketInetAddress);
                    if (mSelector != null)
                    {
                        mSelector.wakeup();
                        mApiSelectionKey = mApiSocketChannel.register(mSelector, SelectionKey.OP_CONNECT, new UUSocketHandler(this));
                        if (DEBUG)
                        {
                            loggerSdCard("createSocketChannel register");
                            MLog.e(TAG, "createSocketChannel register");
                        }
                        if (mConnectListener != null)
                        {
                            mConnectListener.startConnect();
                        }
                    }
                }

                loggerSdCard("开始发送长连接握手协议包");
                MLog.e(TAG, "______开始发送长连接握手协议包");
                creatLongBegin();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        Looper.prepare();
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case PUSH:
                        UUResponseData data = (UUResponseData) msg.obj;
                        loggerSdCard("UUResponseData_PUSH___CMD:" + data.getCmd() + "___SEQ___" + data.getSeq());
                        MLog.e(TAG, "UUResponseData_PUSH___CMD:" + data.getCmd() + "___SEQ___" + data.getSeq());
                        makePush(data);
                        break;
                    case LONG_CONNECR:
                        break;
//                    case RE_CONNECT:
//                        loggerSdCard("长连接重连：UserSecurityConfig.userId_ticket：" + UserSecurityConfig.userId_ticket);
//                        MLog.e(TAG, "长连接重连：UserSecurityConfig.userId_ticket：" + UserSecurityConfig.userId_ticket);
//                        if (UserSecurityConfig.userId_ticket > 0 && UserSecurityConfig.b3_ticket != null) {
//                            loggerSdCard("开始重连接");
//                            MLog.e(TAG, "开始重连接");
////                            Intent intent = new Intent(Config.currentContext, UUService.class);
////                            intent.putExtra(SysConfig.LONG_CONNECT, SysConfig.START_LONG_CONNECT);
////                            Config.currentContext.startService(intent);
//                            ((UUAppCar) Config.currentContext.getApplication()).startLongConn();
//                        }
//                        break;
                }
            }
        };
        Looper.loop();
        CREATE_CONNECT = true;
    }

    Dialog dialog;

    private void makePush(UUResponseData data)
    {
        try
        {
            Config.missDontRefresh = false;
            Config.showPoint = false;
            Config.missTip = "";
            /**
             * 用户身份变化
             */

            if (SysConfig.DEVELOP_MODE)
            {
                loggerSdCard("PUSH__cmd:" + data.getCmd() + "__seq:" + data.getSeq());
                Config.showToast(Config.currentContext, "PUSH__cmd:" + data.getCmd() + "__seq:" + data.getSeq());/**/
                MLog.e("Push", "cmd = " + data.getCmd() + "   ");
            }
            if (data.getCmd() == CmdCodeDef.CmdCode.UserStatusChangePush_VALUE)
            {
                UserInterface.UserStatusChangePush userStatusChangePush = UserInterface.UserStatusChangePush.parseFrom(data.getBusiData());
                UuCommon.TipsMsg tip = userStatusChangePush.getMy();
                CarInterface.CarStatusChangePush push = CarInterface.CarStatusChangePush.parseFrom(data.getBusiData());


                if (Config.outApp(Config.currentContext))
                {
                    showNotification("友友租车", push.getContentMsg(), MainActivityTab.GOTO_RENTER_FIND_CAR);
                }
                else
                {
                    if (push.hasContentMsg() && push.getContentMsg() != null && !push.getContentMsg().equals(""))
                    {
                        Config.showToast(Config.currentContext, push.getContentMsg());
                    }
                }
            }
            //车辆状态变化
            else if (data.getCmd() == CmdCodeDef.CmdCode.CarStatusChangePush_VALUE)
            {
                CarInterface.CarStatusChangePush push = CarInterface.CarStatusChangePush.parseFrom(data.getBusiData());
                if (Config.outApp(Config.currentContext))
                {
                    showNotification("友友租车", push.getContentMsg(), MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                }
                else
                {
                    if (push.hasContentMsg() && push.getContentMsg() != null && !push.getContentMsg().equals(""))
                    {
                        Config.showToast(Config.currentContext, push.getContentMsg());
                    }
                }
            }
            //行程列表push
            else if (data.getCmd() == CmdCodeDef.CmdCode.TripListPush_VALUE)
            {
                OrderFormInterface26.TripListPush push = OrderFormInterface26.TripListPush.parseFrom(data.getBusiData());
                UuCommon.TipsMsg msg = push.getMsg();
                if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals(""))
                {
                    if (Config.outApp(Config.currentContext))
                    {
                        showNotification("友友租车", msg.getNoticeBarMsg(), MainActivityTab.GOTO_RENTER_STROKE);
                    }
                    else
                    {
                        if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals(""))
                        {
                            Config.showToast(Config.currentContext, msg.getToastMsg());
                        }
                    }
                }
                ObserverManager.getObserver(ObserverManager.OWNERORDERDETAIL).observer("push", push.getOrderId());
            }
            //快速约车列表页push
            else if (data.getCmd() == CmdCodeDef.CmdCode.QuickRentCarPush_VALUE)
            {
                ObserverManager.getObserver(ObserverManager.SPEEDRENT).observer("", "");
                OrderFormInterface26.QuickRentCarPush push = OrderFormInterface26.QuickRentCarPush.parseFrom(data.getBusiData());
                UuCommon.TipsMsg msg = push.getMsg();
                if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals(""))
                {
                    if (Config.outApp(Config.currentContext))
                    {
                        showNotification("友友租车", msg.getNoticeBarMsg(), MainActivityTab.GOTO_SPEED_RENT);
                    }
                    else
                    {
                        if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals(""))
                        {
                            Config.showToast(Config.currentContext, msg.getToastMsg());
                        }
                    }
                }

                ObserverManager.getObserver(ObserverManager.QUICKRENTCARPUSH).observer("", "");
            }
            // 车主同意租客的约车请求，建立起了订单，场景是租客发出点对点、点对多请求后，有车主同意租客的约车请求，建立起订单后，对租客下发的push
            else if (data.getCmd() == CmdCodeDef.CmdCode.NewOrderCreatedPush_VALUE)
            {
                final OrderFormInterface26.NewOrderCreatedPush push = OrderFormInterface26.NewOrderCreatedPush.parseFrom(data.getBusiData());
                UuCommon.TipsMsg msg = push.getMsg();
                if (Config.outApp(Config.currentContext))
                {
                    orderId = push.getOrderId();
                    showNotification("友友租车", msg.getNoticeBarMsg(), MainActivityTab.GOTO_RENTER_STROKE);
                }
                else
                {
//                    if (ObserverManager.dialogBool.get() == false) {
//                        ObserverManager.dialogBool.set(true);
//                    if(dialog != null)
//                    {
//                        dialog.dismiss();
//                        dialog = null;
//                    }
//                        AlertDialog.Builder builder = new AlertDialog.Builder(Config.currentContext);
//                        if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals("")) {
//                            builder.setMessage(msg.getToastMsg());
//                        }
//                        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (Config.currentContext.getClass().getName().equals(OneToOneWaitActivity.class.getName()))//如果当前在一对一约车页面去行程页,如果不是,就不动
//                                {
//                                    Intent intent = new Intent(Config.currentContext, MainActivityTab.class);
//                                    intent.putExtra("goto", MainActivityTab.GOTO_RENTER_STROKE);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    Config.currentContext.startActivity(intent);
//                                }
//                            }
//                        });
//                        builder.setNeutralButton("查看并支付", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(Config.currentContext, RenterOrderInfoActivity.class);
//                                intent.putExtra(SysConfig.R_SN, push.getOrderId());
//                                Config.currentContext.startActivity(intent);
//                            }
//                        });
//                        dialog = builder.create();
//                        dialog.setCancelable(true);
//                        dialog.setCanceledOnTouchOutside(false);
//                        dialog.show();
//                    }


                    ObserverManager.getObserver(Config.currentContext.getClass().getName()).observer("showDialog", push);
                    ObserverManager.getObserver(ObserverManager.RENTERTOPAY).observer("agree", push);
                    ObserverManager.getObserver(ObserverManager.RENTERTOPAY_STROKE).observer("agree", push);
                }
            }
            //租客订单完成充值，对租客进行push
            else if (data.getCmd() == CmdCodeDef.CmdCode.OrderCompleteRechargePush_VALUE)
            {
                ObserverManager.getObserver(ObserverManager.RENTERORDERFINISH).observer("", "");
            }
            //租客发起一对一、一对多约车请求后，如果车主拒绝或者超时未响应，则给租客下发该push
            else if (data.getCmd() == CmdCodeDef.CmdCode.PreOrderCanceledPush_VALUE)
            {
//                ObserverManager.getObserver(ObserverManager.RENTERTOPAY).observer("refuse", "");
                ObserverManager.getObserver(ObserverManager.RENTERORDERFAILURE).observer("refuse", data);
                final OrderFormInterface26.PreOrderCanceledPush push = OrderFormInterface26.PreOrderCanceledPush.parseFrom(data.getBusiData());
                UuCommon.TipsMsg msg = push.getMsg();
//                loggerSdCard("PreOrderCanceledPush:toastMsg__" + msg.getToastMsg());
//                MLog.e(TAG, "PreOrderCanceledPush:toastMsg__" + msg.getToastMsg());
                if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals("") && Config.currentContext != null)
                {
                    if (Config.outApp(Config.currentContext))
                    {
                        showNotification("友友租车", msg.getNoticeBarMsg(), MainActivityTab.GOTO_RENTER_STROKE);
                    }
                    else
                    {
                        if (msg.hasToastMsg() && msg.getToastMsg() != null && !msg.getToastMsg().equals("") && Config.currentContext != null)
                        {
                            Config.showToast(Config.currentContext, msg.getToastMsg());
                        }
                    }
                }
                ObserverManager.getObserver(push.getCarSn()).observer("", "");
            }
            //车主错过的订单提醒
            else if (data.getCmd() == CmdCodeDef.CmdCode.CarOwnerMissRentCarPush_VALUE)
            {
                OrderFormInterface26.CarOwnerMissRentCarPush push = OrderFormInterface26.CarOwnerMissRentCarPush.parseFrom(data.getBusiData());
                Config.missDontRefresh = true;
                Config.showPoint = true;
                Config.missTip = push.getMsg().getToastMsg();
                Config.orderTipMsg = push.getMsg();
                ObserverManager.getObserver(ObserverManager.MYSTROKEFRAGMENT).observer("miss", data);
            }
            //蛋蛋活动弹窗,将来有可能是其他活动也弹窗
            else if (data.getCmd() == CmdCodeDef.CmdCode.OperatePopWindowPush_VALUE)
            {
                Config.operatePopWindowPush = OperateInterface.OperatePopWindowPush.parseFrom(data.getBusiData());

                if (Config.outApp(Config.currentContext))
                {
                    showNotification("友友租车", Config.operatePopWindowPush.getTitle(), MainActivityTab.GOTO_OPERATE_POP);
                }
                else
                {
                    Intent intent = new Intent(Config.currentContext, OperatePopActivity.class);
                    intent.putExtra("canClose", Config.operatePopWindowPush.getCanClose());
                    intent.putExtra("title", Config.operatePopWindowPush.getTitle());
                    intent.putExtra("wording", Config.operatePopWindowPush.getWording());
                    intent.putExtra("imgUrl", Config.operatePopWindowPush.getImgUrl());
                    intent.putExtra("actionUrl", Config.operatePopWindowPush.getActionUrl());
                    Config.currentContext.startActivity(intent);
                }


            }
            if (!Config.missDontRefresh)
            {
                ObserverManager.getObserver(data.getCmd() + "").observer("PUSH", data);
                ObserverManager.getObserver(ObserverManager.MAINTABNUM).observer("push", data);
            }
            else
            {
                ObserverManager.getObserver("Miss").observer("", "");
            }
        }
        catch (InvalidProtocolBufferException e)
        {
            e.printStackTrace();
        }
    }

    private void creatLongBegin()
    {
        byte[] longConnectBegin = creatLongBytePacage();
        add2ApiQueue(longConnectBegin);
    }

    private byte[] creatLongBytePacage()
    {
        HeaderCommon.CommonReqHeader.Builder reqHeaderBuilder = HeaderCommon.CommonReqHeader.newBuilder();
//        reqHeaderBuilder.setB2(ByteString.copyFrom(UserSecurityConfig.b2));
        reqHeaderBuilder.setCmd(CmdCodeDef.CmdCode.LongConnection_VALUE);
        reqHeaderBuilder.setSeq(NetworkUtils.seq.getAndIncrement());
        reqHeaderBuilder.setUa(NetworkTask.DEFEALT_UA);
        reqHeaderBuilder.setB2(ByteString.copyFrom(UserSecurityConfig.b2_ticket));
        reqHeaderBuilder.setUuid(UserSecurityConfig.UUID);
        LongConnectionInterface.LongConnection.Request.Builder longConnect = LongConnectionInterface.LongConnection.Request.newBuilder();
        longConnect.setTime(System.currentTimeMillis());
        longConnect.setNetType(2);
        HeaderCommon.RequestData.Builder reqDataBuilder = HeaderCommon.RequestData.newBuilder();
        reqDataBuilder.setHeader(reqHeaderBuilder.build());
        reqDataBuilder.setBusiData(longConnect.build().toByteString());
        HeaderCommon.RequestPackage.Builder requestPackageBuilder = HeaderCommon.RequestPackage.newBuilder();
        requestPackageBuilder.setB3(ByteString.copyFrom(UserSecurityConfig.b3_ticket));
        requestPackageBuilder.setUserId(UserSecurityConfig.userId_ticket);
        requestPackageBuilder.setReqData(ByteString.copyFrom(AESUtils.encrypt(UserSecurityConfig.b3Key_ticket, reqDataBuilder.build().toByteArray())));
        byte[] reqData = requestPackageBuilder.build().toByteArray();
        ByteBuffer requestBuffer = ByteBuffer.allocate(4 + reqData.length);
        requestBuffer.putInt(reqData.length);
        requestBuffer.put(reqData);
        byte[] heart = requestBuffer.array();
        return heart;

    }

    private void handHeartBeak()
    {
        SocketHeartBeatTimer heartBeat = SocketHeartBeatTimer.getInstance(this);
        heartBeat.startTimer();
    }


    private InetSocketAddress getSocketAddress() throws UnknownHostException
    {
        return changeIpState();
    }

    private InetSocketAddress changeIpState() throws UnknownHostException
    {
        String ip = DEFAULT_IP;
        int port = DEFALUT_PORT;
        if (mConnectUseMode == CONNECT_MODE_IP)
        {
            String currServer = DEFAULT_IP;
            if (usePushTest)
            {
                currServer = PUSH_TEST_SERVER;
            }
            int portStart = currServer.indexOf(":");
            if (portStart > 0)
            {
                ip = currServer.substring(0, portStart);
                port = Integer.parseInt(currServer.substring(portStart + 1));
            }
            else
            {
                ip = currServer;
            }
//            updateNacMode(server, ApiState.NAC_MODE_IP);
            return new InetSocketAddress(ip, port);
        }
        else if (mConnectUseMode == CONNECT_MODE_DOMAIN)
        {
//            updateNacMode(server, ApiState.NAC_MODE_DOMAIN);
            if (true)
            {// 测试环境，用ip连--rambo TODO
                loggerSdCard(" DEFAULT_IP=" + DEFAULT_IP + ", DEFALUT_PORT="
                                     + DEFALUT_PORT);
                MLog.e(TAG, " DEFAULT_IP=" + DEFAULT_IP + ", DEFALUT_PORT="
                        + DEFALUT_PORT);
            }

            return new InetSocketAddress(DEFAULT_IP, DEFALUT_PORT);
        }
        else
        {
            return new InetSocketAddress(DEFAULT_IP, DEFALUT_PORT);
        }
    }

    public SocketChannel createSocketChannel() throws IOException
    {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        return sc;

    }

    public void close()
    {
        try
        {
            if (mApiSelectionKey != null)
            {
                mApiSelectionKey.cancel();
            }
            if (mApiSocketChannel != null && mApiSocketChannel.isOpen())
            {
                mApiSocketChannel.close();
            }
            clearQueue();
            mSelector.selectNow();
            setApiSocketAvailable(false);
            SocketHeartBeatTimer heartBeat = SocketHeartBeatTimer.getInstance(this);
            heartBeat.stopTimer();
            Thread.sleep(300);
            mApiSocketChannel = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void clearQueue()
    {
        if (socketChannleReader != null)
        {
            socketChannleReader.clearQueue();
        }
        Enumeration<SocketTask> socketTaskList = mRequest.elements();
        while (socketTaskList.hasMoreElements())
        {
            SocketTask socketTask = socketTaskList.nextElement();
            if (socketTask != null)
            {
                if (socketTask.socketResquestListener != null)
                {
//                    if (socketTask.data != null && socketTask.request != null) {
//                        socketTask.apiRequestListener.onSocketApiResponse(	socketTask.request.seq,
//                                ApiRequestListener.RESULT_CODE_HTTP_ERROR,
//
//             WeiboConstants.ERROR_HTTP_CONNECT_TIMEOUT, null,
//                                socketTask.data);
//                    }
                }

            }
        }
    }

    public void stop()
    {
        if (socketChannleReader != null)
        {
            socketChannleReader.quit();
        }

//        if (timeout != null) {
//            timeout.quit();
//        }
    }


    public void reset()
    {

        close();
        if (mConnectListener != null)
        {
            if (mApiSelectionKey != null)
            {
                mConnectListener.closeConnect();
            }
        }
        loggerSdCard("Socket长连接reset");

        Log.e(TAG, "Socket长连接reset");
        if (UserSecurityConfig.userId_ticket > 0 && UserSecurityConfig.b3Key_ticket != null)
        {
            mHandler.sendEmptyMessageDelayed(RE_CONNECT, 5000);
        }
        else
        {
            loggerSdCard("票据失效！");

            MLog.e(TAG, "票据失效！");
        }
    }


    public ConnectListener mConnectListener = new ConnectListener()
    {
        @Override
        public void startConnect()
        {
            loggerSdCard("connect start");
            Log.e(TAG, "connect start");
        }

        @Override
        public void connectFinnish()
        {
            loggerSdCard("connect finish");
            Log.e(TAG, "connect finish");
        }

        @Override
        public void closeConnect()
        {
            loggerSdCard("connect close");
            Log.e(TAG, "connect close");


        }
    };

    public static interface ConnectListener
    {

        void startConnect();

        void connectFinnish();

        void closeConnect();

    }

    public class SocketChannelReader extends Thread
    {

        private LinkedList<byte[]> mQueue = new LinkedList<byte[]>();

        private Listener mListener;
        private boolean isRunning = true;

        public void setOnReaderListener(Listener lis)
        {
            this.mListener = lis;
        }

        public void quit()
        {
            isRunning = false;
        }

        private void clearQueue()
        {
            mQueue.clear();
        }

        @Override
        public void run()
        {

            while (isRunning)
            {
                try
                {
                    Thread.sleep(10);
                    int n = mSelector.select(100);
                    Iterator<SelectionKey> it = mSelector.selectedKeys().iterator();
                    while (it.hasNext())
                    {
                        SelectionKey key = it.next();
                        it.remove();
                        if (!key.isValid())
                        {
                            continue;
                        }
                        if (key.isConnectable())
                        {
                            SocketChannel channel = (SocketChannel) key.channel();
//                            UUSocketHandler handler = (UUSocketHandler) key.attachment();
                            channel.configureBlocking(false);
                            try
                            {
                                boolean isfinish = channel.finishConnect();
                                if (isfinish)
                                {
                                    if (key.equals(mApiSelectionKey))
                                    {
                                        setApiSocketAvailable(true);
                                        if (channel.isOpen())
                                        {
                                            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                        }
                                    }
                                    if (mConnectListener != null)
                                    {
                                        mConnectListener.connectFinnish();
                                    }
                                }
                            }
                            catch (IOException e)
                            {
                                if (mConnectListener != null)
                                {
                                    mConnectListener.connectFinnish();
                                }
//                                close();
                                loggerSdCard("key___连接失败");
                                MLog.e(TAG, "key___连接失败");
                                reset();
                                e.printStackTrace();
                            }
                        }
                        if (key.isWritable())
                        {
                            try
                            {
                                // TODO poll可能异常
                                if (key.equals(mApiSelectionKey))
                                {
                                    SocketChannel channel = (SocketChannel) key.channel();
                                    UUSocketHandler hanlder = (UUSocketHandler) key.attachment();
                                    if (mQueue.size() > 0)
                                    {
                                        byte[] data = mQueue.remove(0);
                                        if (data != null)
                                        {
                                            hanlder.write(channel, data);
                                        }
                                    }
                                    if (channel.isOpen())
                                    {
                                        // mApiSelectionKey = channel.register(mSelector, SelectionKey.OP_READ |
                                        // SelectionKey.OP_WRITE, hanlder);
                                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                    }
                                }
                            }
                            catch (Exception e)
                            {
//                                close();
                                if (mConnectListener != null)
                                {
                                    mConnectListener.closeConnect();
                                }
                                loggerSdCard("key___write失败");
                                MLog.e(TAG, "key___write失败");
                                reset();
                                e.printStackTrace();
                            }
                        }
                        if (key.isReadable())
                        {
                            try
                            {
                                if (key.equals(mApiSelectionKey))
                                {
                                    SocketChannel channel = (SocketChannel) key.channel();
                                    UUSocketHandler hanlder = (UUSocketHandler) key.attachment();
                                    hanlder.read(mApiSelectionKey, channel, mListener);
                                    if (channel.isOpen())
                                    {
                                        // mApiSelectionKey = channel.register(mSelector, SelectionKey.OP_READ |
                                        // SelectionKey.OP_WRITE, hanlder);
                                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                    }
                                }
                            }
                            catch (IOException e)
                            {
//                                close();
                                if (mConnectListener != null)
                                {
                                    mConnectListener.closeConnect();
                                }
                                loggerSdCard("key___reader失败");
                                MLog.e(TAG, "key___reader失败");
                                reset();
                                e.printStackTrace();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    if (mConnectListener != null)
                    {
                        if (mApiSelectionKey != null)
                        {
                            mConnectListener.closeConnect();
                        }
                    }
//                    close();
                    loggerSdCard("key___通道失败");
                    MLog.e(TAG, "key___通道失败");
                    reset();
                    e.printStackTrace();
                }
            }

        }

        public void add2ApiQueue(byte[] data)
        {
            if (!mQueue.contains(data))
            {
                mQueue.add(data);
            }
        }
    }

    private Listener mOnSocketResponseListener = new Listener()
    {

        @Override
        public void onReader(final int seq, boolean isPush, final UUResponseData responseData)
        {
            if (responseData == null || responseData.getBusiData() == null)
            {
                return;
            }
            if (responseData.getRet() == 0 && UserSecurityConfig.userId_ticket > 0)
            {
                if (isPush)
                {

                    loggerSdCard("push返回____cmd:" + responseData.getCmd() + "__seq:" + responseData.getSeq());
                    MLog.e(TAG, "push返回____cmd:" + responseData.getCmd() + "__seq:" + responseData.getSeq());

                    mHandler.obtainMessage(PUSH, responseData).sendToTarget();

                }
                else
                {
                    //握手协议返回
                    loggerSdCard("握手协议返回____cmd:" + responseData.getCmd() + "__seq:" + responseData.getSeq() + "__ret:" + responseData.getRet());
                    MLog.e(TAG, "握手协议返回____cmd:" + responseData.getCmd() + "__seq:" + responseData.getSeq() + "__ret:" + responseData.getRet());
                    if (responseData.getRet() == 0 && UserSecurityConfig.userId_ticket > 0)
                    {

                        loggerSdCard("开始发送心跳");
                        MLog.e(TAG, "开始发送心跳");

                        handHeartBeak();
                        mHandler.obtainMessage(LONG_CONNECR, responseData).sendToTarget();
                    }
                    else
                    {
                        loggerSdCard("票据无效，关闭长连接");
                        MLog.e(TAG, "票据无效，关闭长连接");
                        Config.clearUserSecurity(Config.currentContext);
                        close();
                    }
                }
            }
            else
            {
                loggerSdCard("票据无效，关闭长连接");
                MLog.e(TAG, "票据无效，关闭长连接");
                Config.clearUserSecurity(Config.currentContext);
                close();
            }
        }
    };

    public interface Listener
    {
        public void onReader(int seq, boolean isPush, UUResponseData socketResponse);
    }


    private boolean isSocketApiUsable()
    {
        return mApiSocketChannel != null && mApiSocketChannel.isOpen();
    }


    public boolean isApiSocketAvailable()
    {
        return isApiSocketAvailable;
    }

    public synchronized void setApiSocketAvailable(boolean isAvailable)
    {
        this.isApiSocketAvailable = isAvailable;
    }


    public void add2ApiQueue(byte[] data)
    {
        socketChannleReader.add2ApiQueue(data);
    }


    public static NotificationManager mNotificationManager = null;

    private static String orderId = "";
    private static String carSn   = "";

    /**
     * @param title
     * @param message
     */
    public static void showNotification(String title, String message, String gotos)
    {
        if (title.equals("") || title == null)
        {
            title = Config.currentContext.getString(R.string.app_name);
        }
        // 消息通知栏
        // 定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        if (mNotificationManager != null)
        {
            mNotificationManager.cancelAll();
        }
        if (mNotificationManager == null)
        {
            mNotificationManager = (NotificationManager) Config.currentContext.getSystemService(ns);
        }
        // 定义通知栏展现的内容信息
        int icon = R.drawable.icon_notify;
        CharSequence tickerText = title;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        // 定义下拉通知栏时要展现的内容信息
        Class back = StartActivity.class;
        // Intent notificationIntent = new Intent(service,back);
        // PendingIntent contentIntent = PendingIntent.getActivity(service,0,notificationIntent,0);
        Intent intent = new Intent(Config.currentContext, back);
        intent.putExtra("goto", gotos);

        //如果是活动弹窗,把数据传过去
        if (gotos.equals(MainActivityTab.GOTO_OPERATE_POP))
        {
            intent.putExtra("canClose", Config.operatePopWindowPush.getCanClose());
            intent.putExtra("wording", Config.operatePopWindowPush.getWording());
            intent.putExtra("imgUrl", Config.operatePopWindowPush.getImgUrl());
            intent.putExtra("actionUrl", Config.operatePopWindowPush.getActionUrl());
        }
        if (orderId != null && !orderId.equals(""))
        {
            intent.putExtra(SysConfig.R_SN, orderId);
            orderId = "";
        }
        if (carSn != null && !carSn.equals(""))
        {
            intent.putExtra(SysConfig.CAR_SN, carSn);
            orderId = "";
        }
        intent.setFlags(/*Intent.FLAG_ACTIVITY_SINGLE_TOP | */Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(Config.currentContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(Config.currentContext, title, message, contentIntent);
        // 用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(1, notification);
        Config.ring(Config.currentContext);
    }

    private void loggerSdCard(String msg)
    {
        if (SysConfig.DEVELOP_MODE)
        {
            logger.debug(Config.getCurrentTime() + " " + msg);
        }
    }
}