package com.youyou.uucar.Service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;
import com.youyou.uucar.Utils.socket.SocketCommunication;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LongConnService extends Service
{
    public static String ACTION = "com.youyou.uucar.Service.LongConnService";
    private SocketCommunication socketCommunication;
    public String tag= "LongConnService";
    public LongConnService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        MLog.e(tag, "onStartCommand");

        startLongConnect();
        ObserverManager.addObserver("LongConnService",stopListener);
        return START_STICKY;
    }

    public ObserverListener stopListener = new ObserverListener()
    {
        @Override public void observer(String from, Object obj)
        {

            MLog.e(tag, "stop");
            closeConnect();
        }
    };

    @Override public void onDestroy()
    {
        super.onDestroy();
        closeConnect();
        MLog.e(tag, "onDestroy");
    }

    private void startLongConnect()
    {
        long l = System.currentTimeMillis();
        if (UserSecurityConfig.b3Key_ticket != null && UserSecurityConfig.userId_ticket > 0 && ((l / 1000) < UserSecurityConfig.ticketFailureTime))
        {
            MLog.e(tag, "startConnect______1");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    socketCommunication = SocketCommunication.getInstance();
                    socketCommunication.createConnect();
                }
            }).start();
        }
    }


    private void closeConnect()
    {

        MLog.e(tag, "colseConnect______1");
        if (socketCommunication != null)
        {
            socketCommunication.close();
        }
        socketCommunication = null;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
