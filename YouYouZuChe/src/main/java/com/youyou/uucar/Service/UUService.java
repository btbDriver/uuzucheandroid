//package com.youyou.uucar.Service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.IBinder;
//
//import com.youyou.uucar.Utils.Network.UserSecurityConfig;
//import com.youyou.uucar.Utils.Support.MLog;
//import com.youyou.uucar.Utils.Support.SysConfig;
//import com.youyou.uucar.Utils.socket.SocketCommunication;
//
//public class UUService extends Service {
//
//    private IBinder mBinder = new UUBinder();
//
//    private SocketCommunication socketCommunication;
//
//
//    private static final Object lock = new Object();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//
//    @Override
//    public void onRebind(Intent intent) {
//        super.onRebind(intent);
//    }
//
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return true;
//    }
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        if (intent != null) {
//            String stringExtra = intent.getStringExtra(SysConfig.LONG_CONNECT);
//            if (stringExtra != null) {
//                if (stringExtra.equals(SysConfig.START_LONG_CONNECT)) {
//                    startLongConnect();
//                } else if (stringExtra.equals(SysConfig.CLOSE_LONG_CONNECT)) {
//                    closeConnect();
//                }
//            }
//        }
//        return START_STICKY;
//    }
//
//    private void startLongConnect() {
//
//        long l = System.currentTimeMillis();
//        if (UserSecurityConfig.b3Key_ticket != null && UserSecurityConfig.userId_ticket > 0 && ((l / 1000) < UserSecurityConfig.ticketFailureTime)) {
//            MLog.e("SocketCommunication", "startConnect______1");
//
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    socketCommunication = SocketCommunication.getInstance();
//                    socketCommunication.createConnect();
//                }
//            }).start();
//        }
//    }
//
//    private void closeConnect() {
//
//        MLog.e("SocketCommunication", "colseConnect______1");
//        if (socketCommunication != null) {
//            socketCommunication.close();
//        }
//    }
//
//
//    private class UUBinder extends Binder {
//        public UUService getService() {
//            return UUService.this;
//        }
//    }
//}
