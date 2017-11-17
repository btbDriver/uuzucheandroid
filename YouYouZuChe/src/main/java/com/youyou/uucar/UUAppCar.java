package com.youyou.uucar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.youyou.uucar.DB.Model.UserSecurityModel;
import com.youyou.uucar.DB.Service.UserSecurityService;
import com.youyou.uucar.Service.LongConnService;
import com.youyou.uucar.Service.RentingService;
import com.youyou.uucar.Utils.observer.ObserverManager;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.Utils.DisplayUtil;
import com.youyou.uucar.Utils.LogCatch.LogCrashHandler;
import com.youyou.uucar.Utils.Network.HexUtil;
import com.youyou.uucar.Utils.Network.LruImageCache;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.DBUtils.DataBaseHelper;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.volley.toolbox.VolleyHurlStack;
import com.youyou.uucar.pay.MD5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;


public class UUAppCar extends MultiDexApplication
{
    private volatile static UUAppCar instance;
    private static          Context  context;
    public               String         tag          = "MyApplication";
    private              List<Activity> activityList = new LinkedList<Activity>();
    private static final Object         lockObj      = new Object();
    private volatile static RequestQueue requestQueue;
    public static volatile  ImageLoader  imageLoader;

    // 单例模式中获取唯一的MyApplication实例
    public static UUAppCar getInstance()
    {
        if (instance == null)
        {
            synchronized (UUAppCar.class)
            {
                if (instance == null)
                {
                    instance = new UUAppCar();
                }
            }
        }
        return instance;
    }

    public Context getContext()
    {
        return Config.currentContext;
    }

    public Activity getActivity()
    {
        return Config.currentContext;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        if (!SysConfig.DEVELOP_MODE)
        {
            LogCrashHandler.getInstance().init(this);
        }
        context = getApplicationContext();
        PropertyConfigurator.getConfigurator(this).configure();
        initDB();
        initDisplayOpinion();
        final String ua = "A_" + SysConfig.getAppVerSion(getApplicationContext()) + "&" + Build.VERSION.RELEASE + "&" + android.os.Build.MODEL + "&" + getChannel();
        NetworkTask.DEFEALT_UA = ua;
        initUUID(ua);
        if (SysConfig.DEVELOP_MODE)
        {
            initDevelopMode();
        }
        //TODO 为了纪念这次合体
//        PBUtils.getModel(new CalculateModel.RequestModel()){}
    }


    public static final String LONG_CONN_KEY = "LONG_CONN_KEY";
    public static final String RENTING_KEY   = "RENTING_KEY";

    //    public void startLongConn() {
//        quitLongConn();
//        Bundle bundle = longConnTrack.getArguments();
//        bundle.putInt("stat", LongConnTask.START);
//        longConnTrack.setArguments(bundle);
//        CoreTask task = new LongConnTask().setIsLoop(60 * 1000, LONG_CONN_KEY).setCallback(longConnTrack).build();
//        getCoreLooper().addTask(task);
//    }
//
//    public void quitLongConn() {
//        SocketCommunication.CREATE_CONNECT = false;
//        Bundle bundle = longConnTrack.getArguments();
//        bundle.putInt("stat", LongConnTask.CLOSE);
//        longConnTrack.setArguments(bundle);
//    }
//////////////////////////AlarmManager
    public void startLongConn()
    {
        quitLongConn();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LongConnService.class);
        intent.setAction(LongConnService.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime();
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, 60 * 1000, pendingIntent);
    }

    public void quitLongConn()
    {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LongConnService.class);
        intent.setAction(LongConnService.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
        ObserverManager.getObserver("LongConnService").observer("", "stop");

    }


    public void startRenting()
    {
        quitRenting();
//        Bundle bundle = rentingTrack.getArguments();
//        bundle.putInt("stat", RentingTask.START);
//        rentingTrack.setArguments(bundle);
//        CoreTask task = new RentingTask().setIsLoop(1 * 1000, RENTING_KEY).setCallback(rentingTrack).build();
//        getCoreLooper().addTask(task);
        startService(new Intent(this, RentingService.class));
    }

    public void quitRenting()
    {
//        Bundle bundle = rentingTrack.getArguments();
//        bundle.putInt("stat", RentingTask.CLOSE);
//        rentingTrack.setArguments(bundle);
        stopService(new Intent(this, RentingService.class));
    }


    private void initDevelopMode()
    {
        SharedPreferences network = getSharedPreferences("network", 0);
        boolean check = network.getBoolean("check", true);
        if (check)
        {
            NetworkTask.BASEURL = "42.96.249.15"; //正式
        }
        else
        {
            NetworkTask.BASEURL = "115.28.82.160"; //测试
        }
    }

    private void initUUID(String ua)
    {
        SharedPreferences userInfo = getSharedPreferences("user_info", 0);
        String uuid = userInfo.getString("uuid", "");
        if (uuid == null || uuid.trim().equals(""))
        {
            uuid = MD5.getMessageDigest((ua + System.currentTimeMillis()).getBytes());
            userInfo.edit().putString("uuid", uuid).apply();

        }
        UserSecurityConfig.UUID = uuid;

    }

    private void initDisplayOpinion()
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
        DisplayUtil.statusBarHight = getStatusBarHeight(context);
    }

    private int getStatusBarHeight(Context context)
    {
        int statusBarHeight = 0;
        try
        {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            //反射出该对象中status_bar_height字段所对应的在R文件的id值
            //该id值由系统工具自动生成,文档描述如下:
            //The desired resource identifier, as generated by the aapt tool.
            int id = Integer.parseInt(field.get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(id);
        }
        catch (Exception e)
        {
        }
        return statusBarHeight;
    }

    private void initDB()
    {

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                getPowerCheckDB();
//                getUserSecurity(context);
            }
        }).start();

    }

    private UserSecurityModel getUserSecurity(Context context)
    {
        UserSecurityModel userSecurityModel;
        try
        {
            UserSecurityService userSecurityService = new UserSecurityService(context);
            List<UserSecurityModel> models = userSecurityService.getModelList(UserSecurityModel.class);
            if (models.size() == 0)
            {

                return null;
            }
            else
            {
                userSecurityModel = models.get(0);
                UserSecurityConfig.b2_ticket = HexUtil.hexStr2Bytes(userSecurityModel.b2);
                UserSecurityConfig.b3_ticket = HexUtil.hexStr2Bytes(userSecurityModel.b3);
                UserSecurityConfig.b3Key_ticket = HexUtil.hexStr2Bytes(userSecurityModel.b3Key);
                UserSecurityConfig.userId_ticket = userSecurityModel.userId;

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return userSecurityModel;
    }


    /**
     * 将原有sqlite数据库存储到手机内
     */
    public void getPowerCheckDB()
    {
        String databaseFilename = "";
        File dbFilePath = null;
        File imagePath = null;
        if (false)
        {
            databaseFilename = DataBaseHelper.SD_DB_PATH
                    + DataBaseHelper.DATABASE_NAME;
            dbFilePath = new File(DataBaseHelper.SD_DB_PATH);
            imagePath = new File(SysConfig.SD_IMAGE_PATH);
        }
        else
        {
            databaseFilename = DataBaseHelper.SYSDB_PATH
                    + DataBaseHelper.DATABASE_NAME;
            dbFilePath = new File(DataBaseHelper.SYSDB_PATH);
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            imagePath = new File(SysConfig.SD_IMAGE_PATH);
            if (imagePath != null)
            {
                if (!imagePath.exists())
                {
                    imagePath.mkdirs();
                }
            }
        }
        // 如果/data/data/pad.android.repair/databases/目录不存在，创建这个目录
        if (!dbFilePath.exists())
        {
            dbFilePath.mkdirs();
        }
        // new File(databaseFilename).delete();
        // 如果在目录中不存在 power_checkdb.db文件，
        // 则从res\raw目录中复制这个文件到，SD卡或内存的目录（/sdcard/pda_mrData/database）
        SharedPreferences userInfo = getSharedPreferences("user_info", 0);
        boolean isFirstCreated = userInfo.getBoolean("isFrist", true);

        if (!(new File(databaseFilename)).exists() || isFirstCreated)
        {
//        if (true) {
            try
            {
                // 获得封装pad_repair.db文件的InputStream对象
                InputStream in = getResources().openRawResource(
                        R.raw.uuzuche_pb);
                FileOutputStream dbFile = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[1024];
                int count = 0;
                // 开始复制pad_repair.db文件
                try
                {
                    while ((count = in.read(buffer)) > 0)
                    {
                        dbFile.write(buffer, 0, count);
                    }
                    dbFile.flush();
                    userInfo.edit().putBoolean("isFrist", false).commit();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (dbFile != null)
                        {
                            dbFile.close();
                        }
                        if (in != null)
                        {
                            in.close();
                        }
                    }
                    catch (Exception e2)
                    {
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }


    // 添加Activity到容器中
    public void addActivity(Activity activity)
    {
        activityList.add(activity);
    }

    public List<Activity> getActivitys()
    {
        return activityList;
    }

    public void stopAll()
    {
        for (Activity activity : activityList)
        {
            if (activity instanceof MainActivityTab || activity.getClass().getName().equals(MainActivityTab.class.getName()))
            {

            }
            else
            {
                activityList.remove(activity);
                activity.finish();

            }
        }
//        activityList.clear();
    }

    public void stopAndMain()
    {
        for (Activity activity : activityList)
        {
            activity.finish();
        }
        activityList.clear();
    }

    // 遍历所有Activity并finish
    public void exit()
    {
        for (Activity activity : activityList)
        {
            activity.finish();
        }
        activityList.clear();
        System.exit(0);
    }

    // 遍历所有Activity并finish
    public void exitCrash()
    {
        for (Activity activity : activityList)
        {
            activity.finish();
        }
        activityList.clear();
    }


    /**
     * 加载图片
     *
     * @param imgurl
     * @param imageView
     * @param defaultPicId
     */
    public void display(String imgurl, NetworkImageView imageView, int defaultPicId)
    {
        imageLoader = getImageLoaderInstance();
        imageView.setDefaultImageResId(defaultPicId);
        imageView.setImageUrl(imgurl, imageLoader);
    }

    public void display(String imgurl, NetworkImageView imageView)
    {
        imageLoader = getImageLoaderInstance();
        imageView.setImageUrl(imgurl, imageLoader);
    }

    public static ImageLoader getImageLoaderInstance()
    {
        if (imageLoader == null)
        {
            synchronized (lockObj)
            {
                if (imageLoader == null)
                {
                    imageLoader = new ImageLoader(getRequestQueue(), LruImageCache.getInstance());
                }
            }
        }
        return imageLoader;
    }

    public static RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            synchronized (lockObj)
            {
                if (requestQueue == null)
                {
                    requestQueue = Volley.newRequestQueue(context, new VolleyHurlStack());
                }
            }
        }
        return requestQueue;
    }

    /**
     * activity MetaData读取
     */
    private String getChannel()
    {
        ApplicationInfo appInfo;
        try
        {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}