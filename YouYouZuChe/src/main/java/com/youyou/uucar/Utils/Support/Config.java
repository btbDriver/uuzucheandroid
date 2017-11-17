package com.youyou.uucar.Utils.Support;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uu.client.bean.banner.OperateInterface;
import com.uu.client.bean.banner.common.BannerCommon;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.DB.Model.Car;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserSecurityModel;
import com.youyou.uucar.DB.Service.UserSecurityService;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HexUtil;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.DayArrayAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import kankan.wheel.widget.adapters.SpeedAdapter;

public class Config {

    public static double selectLat = 0;
    public static double selectLng = 0;
    public static String selectAddress = "";
    ////////////////////////////////活动弹窗//////////////////////
    public static OperateInterface.OperatePopWindowPush operatePopWindowPush;
    /////////////////////////////////////////////////////////

    ////////////////////////车主错过的订单提醒////////////////////////////
    public static boolean showPoint = false;//底导显示小红点
    public static String missTip = "";//行程页车主错过的订单显示文字
    public static boolean missDontRefresh = false;//车主错过的订单push,回到主页后不刷新底导,长连接收到push后置成true,进入行程页后再置成false
    public static UuCommon.TipsMsg orderTipMsg;
    ///////////////////////////////////////////////////////////////////
    public static long startTimeLong = 0;
    public static long endTimeLong = 0;
    public static Dialog loginDialog;
    public static boolean isShowChangeCity = true;
    public static boolean changeCityList = false;
    public static List<UserInterface.MyBankCards.Bank> bankList;
    public static CarCommon.CarDetailInfo sCarContentModel;

    public static Car getCar(Activity context) {
        SharedPreferences sp = context.getSharedPreferences("car", Context.MODE_PRIVATE);
        return Config.getCar(context, sp.getString("city", "京 A"), sp.getString("number", ""));
    }

    public static Car getCar(Activity context, String city, String num) {
        SharedPreferences sp = context.getSharedPreferences("car" + city + num, Context.MODE_PRIVATE);
        Car car = new Car();
        try {
            car = new Car(sp);
        } catch (JSONException e) {
        }
        return car;
    }

    public static String[] word = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    /**
     * 产生指定范围的随机整数
     *
     * @param min 指定随机数的最小范围，包括min本身
     * @param max 指定随机数的最大范围，包括max本身
     * @return 随机数
     */
    static Random ran = new Random();

    public static int random(int min, int max) {
        return (ran.nextInt() >>> 1) % (max + 1 - min) + min;
    }

    public static final String UPLOADSMS_ADDRESS = "106901335716";
    public static final String UPLOADSMSBODY = " 尊敬的友友会员，请用前面验证密码完成登录。";
    public static final String[] WEEK = {"零", "一", "二", "三", "四", "五"};

    /**
     * 高斯模糊
     *
     * @param bmp
     * @return
     */
    public static Bitmap convertToBlur(Bitmap bmp) {
        // 高斯矩阵
        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int delta = 16; // 值越小图片会越亮，越大则越暗
        int idx = 0;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);
                        newR = newR + pixR * gauss[idx];
                        newG = newG + pixG * gauss[idx];
                        newB = newB + pixB * gauss[idx];
                        idx++;
                    }
                }
                newR /= delta;
                newG /= delta;
                newB /= delta;
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }


    public static boolean RegisterToRenter = false;
    public static boolean RegisterToOwner = false;
    /**
     * 01 = 测试,00 = 正式
     */
    public static String payMode = "00";
    public static String kefuphone = "4000-772-110";
    public static final String NOLOGIN = "nologin";
    /**
     * 0=租客,1=车主
     */
    public static int ToRegisterType = 0;
    /**
     * 开始时间必须大于当前时间30分钟
     */
    public final static long START_TIME_DIFFERENCE = 1000 * 60 * 60;
    /**
     * 起租时间-1小时
     */
    public final static long MIN_HIRE_TIME = 1000 * 60 * 60 * 4;
    public static int IMAGE_CAMERA_RESULT = 1;
    public static int IMAGE_IMAGE_RESULT = 2;
    public static final String sign = "UuZuChE";

    public static int getResourceId(Context context, String name, String type, String packageName) {
        Resources themeResources = null;
        PackageManager pm = context.getPackageManager();
        try {
            themeResources = pm.getResourcesForApplication(packageName);
            return themeResources.getIdentifier(name, type, packageName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void cropImageUri(Activity activity, Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/youyou/uucar/image/temp_image.jpg"));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    static String regEx = "[^0-9]";

    public static boolean isGuest(Context context) {
        if (!getUser(context).sid.equals("") && Integer.parseInt(getUser(context).sid) > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static final String ROLE_TYPE_RENTER = "renter";
    public static final String ROLE_TYPE_OWNER = "owner";
    public static final int WHEEL_TYPE_WAIT = 1;
    public static final int WHEEL_TYPE_PAY1 = 2;
    public static final int WHEEL_TYPE_PAY2 = 3;
    public static final int WHEEL_TYPE_WAIT_START = 4;
    public static final int WHEEL_TYPE_STARTING = 5;
    public static final int WHEEL_TYPE_END = 6;
    public static final int WHEEL_TYPE_CANCEL = 7;
    public static final int WHEEL_TYPE_PINGJIA = 8;
    private static User model;
    private static UserService service;
    private static UserSecurityService userSecurityService;
    private static UserSecurityModel userSecurityModel;

    public static List<OrderFormInterface.ConfirmToPayNew.PayTypeInfo> payList;

    public static User getUser(Context context) {
        try {
            service = new UserService(context);
            List<User> models = service.getUserList(User.class);
            if (models.size() == 0) {
                model = new User();
                model.phone = "";
                boolean flag = service.insModel(model);
                if (flag) {
                    model = service.getUser(User.class, new String[]{""});
                }
            } else {
                model = models.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (model == null) {
                model = new User();
            }
        }
        return model;
    }

    public static void clearUser(Context context) {

        try {
            service = new UserService(context);
            List<User> models = service.getUserList(User.class);
            if (models.size() == 0) {

            } else {
                model = models.get(0);
                service.delModel(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isCarInfoOkey(CarCommon.CarDetailInfo model) {
        boolean flag = false;
        if (model != null) {
            if (model.hasCarId() && !model.getCarId().equals("")) {
                if (model.hasTransmissionType() && !model.getTransmissionType().equals("")) {
                    if (model.hasDisplacementType() && !(model.getDisplacementType() == 0)) {
                        if (model.hasCarRegYear() && !(model.getCarRegYear() == 0)) {
                            if (model.hasSeatsCount() && !(model.getSeatsCount() == 0)) {
                                if (model.hasDrivingKM() && !(model.getDrivingKM() == 0)) {
//                                    //油号
                                    if (model.hasGasType() && !(model.getGasType() == 0)) {
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }

    public static void getUserSecurity(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userSecurity", Context.MODE_PRIVATE);
        if (!sp.getString("b2", "").equals("")) {
            UserSecurityConfig.b2_ticket = HexUtil.hexStr2Bytes(sp.getString("b2", "[]"));
            UserSecurityConfig.b3_ticket = HexUtil.hexStr2Bytes(sp.getString("b3", "[]"));
            UserSecurityConfig.b3Key_ticket = HexUtil.hexStr2Bytes(sp.getString("b3Key", "[]"));
            UserSecurityConfig.b4_ticket = HexUtil.hexStr2Bytes(sp.getString("b4", "[]"));
            UserSecurityConfig.userId_ticket = sp.getInt("userId", 0);
            UserSecurityConfig.ticketStartTime = sp.getInt("startTime", 0);
            UserSecurityConfig.validSecs = sp.getInt("validSecs", 0);
            UserSecurityConfig.ticketFailureTime = sp.getInt("ticketFailureTime", 0);
            UserSecurityConfig.needUpdateTicketTime = UserSecurityConfig.ticketStartTime + UserSecurityConfig.validSecs / 3;
        }
    }

    public static void updateUserSecurity(Context context, UuCommon.UserSecurityTicket ticket, int userId, int startTime) {
        SharedPreferences sp = context.getSharedPreferences("userSecurity", Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.clear();
        edit.commit();
        edit.putString("b2", HexUtil.bytes2HexStr(ticket.getB2().toByteArray()));
        edit.putString("b3", HexUtil.bytes2HexStr(ticket.getB3().toByteArray()));
        edit.putString("b3Key", HexUtil.bytes2HexStr(ticket.getB3Key().toByteArray()));
        edit.putInt("userId", userId);
        edit.putInt("startTime", startTime);
        edit.putInt("validSecs", ticket.getValidSecs());
        edit.putInt("ticketFailureTime", startTime + ticket.getValidSecs());
        edit.putString("b4", HexUtil.bytes2HexStr(ticket.getB4().toByteArray()));
        edit.commit();
        UserSecurityConfig.b2_ticket = ticket.getB2().toByteArray();
        UserSecurityConfig.b3_ticket = ticket.getB3().toByteArray();
        UserSecurityConfig.b3Key_ticket = ticket.getB3Key().toByteArray();
        UserSecurityConfig.b4_ticket = ticket.getB4().toByteArray();
        UserSecurityConfig.userId_ticket = userId;
        UserSecurityConfig.ticketStartTime = startTime;
        UserSecurityConfig.validSecs = ticket.getValidSecs();
        UserSecurityConfig.ticketFailureTime = startTime + ticket.getValidSecs();
        UserSecurityConfig.needUpdateTicketTime = startTime + ticket.getValidSecs() / 3;
    }

    public static void clearUserSecurity(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userSecurity", Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.clear();
        edit.commit();
        UserSecurityConfig.b2_ticket = null;
        UserSecurityConfig.b3_ticket = null;
        UserSecurityConfig.b3Key_ticket = null;
        UserSecurityConfig.b4_ticket = null;
        UserSecurityConfig.userId_ticket = 0;
        UserSecurityConfig.ticketStartTime = 0;
        UserSecurityConfig.validSecs = 0;
        UserSecurityConfig.ticketFailureTime = 0;
        UserSecurityConfig.needUpdateTicketTime = 0;
    }



    public static void SetOrderSN(Activity context, String sn) {
        SharedPreferences sp = context.getSharedPreferences("uucar_user", Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("order_sn", sn);
        edit.commit();
    }

    /**
     * 简称手机号是否合法
     *
     * @param email
     * @return
     */
    public static boolean btnValidatePhoneNum(String email) {
        if (Pattern.compile(regEx).matcher(email).matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static void showFiledToast(Context context) {
        if (context != null) {
            Toast.makeText(context, SysConfig.NETWORK_FAIL, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showErrorDialog(Context context, String content) {
        if (MLog.isDebug && context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(content);
            builder.setNegativeButton("告诉大帅阳", null);
            builder.create().show();
        } else {
            showFiledToast(context);
        }
    }

    public static void showToast(Context context, String msg) {
        if (context != null && msg != null && !msg.trim().equals("")) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static final String WX_APP_ID = "wx9abfa08f7da32b30";

    public static IWXAPI reqToWx(Activity context, IWXAPI api) {
        api = WXAPIFactory.createWXAPI(context, Config.WX_APP_ID, true);
        api.registerApp(Config.WX_APP_ID);
        return api;
    }

    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        //未安装，跳转至market下载该程序
//        {
//            Uri uri = Uri.parse("market://details?id=" + packageName);
//            Intent it = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(it);
//        }
        return false;
    }


    public static boolean isGobackHome = false;
    public static Uri imageUri = Uri.parse("file:///sdcard/youyou/uucar/image/temp_image.jpg");
    public static final String RecordFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyou/uucar/record/";
    public static final String ImageFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyou/uucar/image/";
    public static final String HeadFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyou/uucar/head/";
    public static boolean changeCityMap = false;
    public static Activity currentContext;

    public static void setActivityState(Activity activity) {
        currentContext = activity;
        // activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        UUAppCar.getInstance().addActivity(activity);
        // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (activity.getActionBar() != null) {

            ActionBar actionBar = activity.getActionBar();
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        try {
            ViewConfiguration mconfig = ViewConfiguration.get(activity);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(mconfig, false);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean isOpenRing = true;
    public static boolean isOpenvib = true;
    public static long lastringTime = 0;

    /**
     * 播放声音
     *
     * @return
     * @throws Exception
     * @throws java.io.IOException
     */
    public static void ring(Context context) {
        SharedPreferences sp = context.getSharedPreferences("zaitaxiang_user", Context.MODE_PRIVATE);
        isOpenRing = sp.getBoolean("ring", true);
        if (!isOpenRing) {
            return;
        }
        if (System.currentTimeMillis() - lastringTime < 3000) {
            return;
        }
        try {
            //
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // Uri alert = Uri.parse("android.resource://"+context.getApplicationContext().getPackageName()+"/raw/"+R.raw.ring);
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                player.setLooping(false);
                player.prepare();
                player.start();
            }
        } catch (Exception e) {
            MLog.e(TAG, "error" + e.getMessage());
            e.printStackTrace();
        }
        lastringTime = System.currentTimeMillis();
    }

    public static long lastVibtime = 0;

    /**
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次 long milliseconds ：震动的时长，单位是毫秒 long[] pattern ：自定义震动模式 。 数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     */
    public static void Vibrate(Context Activity/* ,long[] pattern,boolean isRepeat */) {
        SharedPreferences sp = Activity.getSharedPreferences("zaitaxiang_login", Context.MODE_PRIVATE);
        isOpenvib = sp.getBoolean("vib", true);
        MLog.e(TAG, "isOpenvib=" + isOpenvib);
        if (!isOpenvib) {
            return;
        }
        if (System.currentTimeMillis() - lastVibtime < 3000) {
            return;
        }
        Vibrator vib = (Vibrator) Activity.getSystemService(Service.VIBRATOR_SERVICE);
        // vib.vibrate(pattern,isRepeat?1: -1);
        vib.vibrate(500);
        lastVibtime = System.currentTimeMillis();
    }

    public static double lat, lng;
    public static long locationTime = 0;
    private static LocationManagerProxy mAMapLocManager = null;
    private static GDLocationListener gdLocationListener = null;
    static Handler handler = new Handler();
    public static String currentCity = "";
    public static String currentAddress = "";
    public static List<CarInterface.QueryAvailableCitys.City> openCity = new ArrayList<CarInterface.QueryAvailableCitys.City>();
    public static String[] cityShortName = new String[]{"京", "沪", "川", "琼", "闽", "鄂", "赣", "甘", "桂", "贵", "黑", "冀", "吉", "津", "晋", "辽", "鲁", "蒙", "宁", "青", "陕", "苏", "皖", "湘", "新", "豫", "粤", "渝", "云", "藏", "浙"};
    /**
     * 如果是用户主动取消的约车,那么RentingService 不toast提示了就
     */
    public static boolean isUserCancel = false;
    //是否有正在进行的意向单
    public static boolean isSppedIng = false;
    //是否正在一对一或者一对多约车
    public static boolean isOneToOneIng = false;
    //快速约车并且已经有人同意了
    public static boolean speedHasAgree = false;
    /**
     * 是否有待支付订单
     */
    public static boolean hasPayOrder = false;
    /**
     * 待支付订单号
     */
    public static String waitPayOrderId = "";

    /**
     * 定位 获取经纬度
     */
    public static void getCoordinates(final Activity activity, final LocationListener listener) {

//        if (System.currentTimeMillis() - locationTime < 1000 * 60 * 5) {
//            if (listener != null) {
//                listener.locationSuccess(lat, lng, currentAddress);
//            }
//        } else {
        mAMapLocManager = LocationManagerProxy.getInstance(activity);
//        mAMapLocManager.setP
        gdLocationListener = new GDLocationListener(listener);
        // Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
        /*
         * mAMapLocManager.setGpsEnable(false);// 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
         */
        mAMapLocManager.setGpsEnable(true);

        mAMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, gdLocationListener);
//        }

    }

    // 定位相关

    /**
     * 关闭定位
     *
     * @param listener
     */
    public static void disableMyLocation(AMapLocationListener listener) {
        if (mAMapLocManager != null) {
            mAMapLocManager.removeUpdates(listener);
            mAMapLocManager.destroy();
        }
        mAMapLocManager = null;
    }

    /**
     * 从车辆详情进入时间选择的时候赋值
     */
    public static CarCommon.CarSelectRentTime carDisableTime;
    public static CarCommon.CarSelectRentTime carBanTime;
    /**
     * 定位后,当前城市编号(用于高德)
     */
    public static String cityCode = "";
    public static ProgressDialog progress = null;

    public static void showProgressDialog(Context context, boolean canCancel, final ProgressCancelListener listener) {
        dismissProgress();
        progress = new ProgressDialog(context);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("请稍候...");
        progress.setCancelable(canCancel);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        progress.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null) {
                    listener.progressCancel();
                }
            }
        });
    }

    public static void showProgressTextDialog(Context context, String text) {
        dismissProgress();
        progress = new ProgressDialog(context);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage(text);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    public static void writeBannerToDisk(List<BannerCommon.BannerItem> bannerItemsList, final boolean isLogon, FragmentActivity activity) {
        JSONArray jsonArray = new JSONArray();
        for (BannerCommon.BannerItem bannerItem : bannerItemsList) {
            try {
                JSONObject json = new JSONObject();
                json.put("bannerId", bannerItem.getBannerId());
                json.put("imgUrl", bannerItem.getImgUrl());
                json.put("actionUrl", bannerItem.getActionUrl());
                json.put("name", bannerItem.hasName() ? bannerItem.getName() : "");
                json.put("heightPixel", bannerItem.getHeightPixel());
                json.put("widthPixel", bannerItem.getWidthPixel());
                json.put("startTime", bannerItem.getStartTime());
                json.put("endTime", bannerItem.getEndTime());
                json.put("displayDuration", bannerItem.getDisplayDuration());
                json.put("canClose", bannerItem.getCanClose());
                jsonArray.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences banner = activity.getSharedPreferences("banner", Context.MODE_PRIVATE);
        banner.edit().putString("banner", jsonArray.toString()).apply();
        banner.edit().putBoolean("isLogon", isLogon).apply();
    }

    public static List<BannerCommon.BannerItem> getBannerFromDisk(FragmentActivity activity) {
        List<BannerCommon.BannerItem> list = new ArrayList<BannerCommon.BannerItem>();
        SharedPreferences bannerSp = activity.getSharedPreferences("banner", Context.MODE_PRIVATE);
        String banner = bannerSp.getString("banner", new JSONArray().toString());
        try {
            JSONArray jsonArray = new JSONArray(banner);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                BannerCommon.BannerItem.Builder builder = BannerCommon.BannerItem.newBuilder();
                builder.setBannerId(jsonObject.getInt("bannerId"));
                builder.setImgUrl(jsonObject.getString("imgUrl"));
                builder.setActionUrl(jsonObject.getString("actionUrl"));
                builder.setName(jsonObject.getString("name"));
                builder.setHeightPixel(jsonObject.getInt("heightPixel"));
                builder.setWidthPixel(jsonObject.getInt("widthPixel"));
                builder.setStartTime(jsonObject.getInt("startTime"));
                builder.setEndTime(jsonObject.getInt("endTime"));
                builder.setDisplayDuration(jsonObject.getInt("displayDuration"));
                builder.setCanClose(jsonObject.getBoolean("canClose"));
                list.add(builder.build());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isBannerLogon(FragmentActivity activity) {
        SharedPreferences bannerSp = activity.getSharedPreferences("banner", Context.MODE_PRIVATE);
        boolean logon = bannerSp.getBoolean("isLogon", false);
        return logon;
    }

    public static interface ProgressCancelListener {
        public void progressCancel();
    }

    public static void dismissProgress() {
        try {
            if (progress != null /* && progress.isShowing() */) {
                progress.dismiss();
            }
        } catch (Exception e) {
        }
    }


    private static boolean timeScrolled = false;
    private static boolean timeChanged = false;


    public static interface TimePickResult {


        boolean hook();

        void timePickResult(long time);
    }

//    private static TimePickResult timePickResult;
//
//    public static void setTimePickResult(TimePickResult pickResult) {
//        timePickResult = pickResult;
//    }

    public static void showDaySecondPickDialog(final Activity context, long currenttime, final long startTime, final long oldTime, final int availableDay, final TimePickResult timePickResult) {
        if (context == null) {
            return;
        }
        boolean hookBoolean = false;
        if (timePickResult != null) {
            hookBoolean = timePickResult.hook();
        }
        if (!hookBoolean) {
            return;
        }
        Date oldDate = new Date(oldTime);
        Date date = new Date(startTime);
        final Calendar calendar = Calendar.getInstance();
        final Calendar oldCalendar = Calendar.getInstance();
        calendar.setTime(date);
        oldCalendar.setTime(oldDate);
        int minsInt = calendar.get(Calendar.MINUTE);
        int yu = minsInt % 15;
        if (yu != 0) {
            calendar.add(Calendar.MINUTE, 15 - yu);
        }
        calendar.set(Calendar.SECOND, 0);
        int oldMinsInt = oldCalendar.get(Calendar.MINUTE);
        int oldYu = oldMinsInt % 15;
        if (oldYu != 0) {
            oldCalendar.add(Calendar.MINUTE, 15 - oldYu);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.daytime_pick_layout, null);
        final TextView timeTextView = (TextView) view.findViewById(R.id.date_textview);
//        Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        timeTextView.setText(TimeUtils.formatTimeWithHHMM(calendar));
        final WheelView days = (WheelView) view.findViewById(R.id.day);
        final Calendar nowCalendar = Calendar.getInstance();
        final Calendar dayCalendar = Calendar.getInstance();
        final Calendar nowCalendarClone = Calendar.getInstance();
        final Date nowDate = new Date(currenttime);
        dayCalendar.setTime(nowDate);
        nowCalendar.setTime(nowDate);
        nowCalendarClone.setTime(nowDate);
        int rollDay = 0;
        while (nowCalendar.before(calendar)) {
            int nowDay = nowCalendar.get(Calendar.DATE);
            int day = calendar.get(Calendar.DATE);
            if (day == nowDay) {
                int nowMonth = nowCalendar.get(Calendar.MONTH);
                int month = calendar.get(Calendar.MONTH);
                if (nowMonth == month) {
                    break;
                }
            }
            nowCalendar.add(Calendar.DATE, 1);
            rollDay++;
        }

        int avialDay = 0;
        while (nowCalendarClone.before(oldCalendar)) {
            int nowDay = nowCalendarClone.get(Calendar.DATE);
            int day = oldCalendar.get(Calendar.DATE);
            if (day == nowDay) {
                int nowMonth = nowCalendarClone.get(Calendar.MONTH);
                int month = oldCalendar.get(Calendar.MONTH);
                if (nowMonth == month) {
                    break;
                }
            }
            nowCalendarClone.add(Calendar.DATE, 1);
            avialDay++;
        }
        DayArrayAdapter dayArrayAdapter = new DayArrayAdapter(context, dayCalendar);
        days.setViewAdapter(dayArrayAdapter);
        days.setVisibleItems(7);
        final WheelView hours = (WheelView) view.findViewById(R.id.hour);
        hours.setViewAdapter(new NumericWheelAdapter(context, 0, 23));
        hours.setVisibleItems(7);
        final WheelView mins = (WheelView) view.findViewById(R.id.mins);
        mins.setViewAdapter(new SpeedAdapter(context, 45, 15));
        mins.setVisibleItems(7);
        dayArrayAdapter.setDaysCount(availableDay + avialDay);
        days.setCurrentItem(rollDay);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE) / 15);
        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                    timeChanged = true;
                    int daysRoll = days.getCurrentItem();
                    int hoursRoll = hours.getCurrentItem();
                    int minsRoll = mins.getCurrentItem();
                    Calendar selectCalendar = Calendar.getInstance();
                    selectCalendar.setTime(nowDate);
                    selectCalendar.add(Calendar.DATE, daysRoll);
                    selectCalendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                    selectCalendar.set(Calendar.MINUTE, minsRoll * 15);
                    selectCalendar.set(Calendar.SECOND, 0);
                    selectCalendar.set(Calendar.MILLISECOND, 0);
                    timeTextView.setText(TimeUtils.formatTimeWithHHMM(selectCalendar));
                    timeChanged = false;
                }
            }
        };
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);
        days.addChangingListener(wheelListener);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                timeScrolled = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                timeScrolled = false;
                timeChanged = true;
                int daysRoll = days.getCurrentItem();
                int hoursRoll = hours.getCurrentItem();
                int minsRoll = mins.getCurrentItem();
                Calendar selectCalendar = Calendar.getInstance();
                selectCalendar.setTime(nowDate);
                selectCalendar.add(Calendar.DATE, daysRoll);
                selectCalendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                selectCalendar.set(Calendar.MINUTE, minsRoll * 15);
                selectCalendar.set(Calendar.SECOND, 0);
                selectCalendar.set(Calendar.MILLISECOND, 0);
                timeTextView.setText(TimeUtils.formatTimeWithHHMM(selectCalendar));
                timeChanged = false;
            }
        };
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);
        days.addScrollingListener(scrollListener);
        final TextView sureTextView = (TextView) view.findViewById(R.id.sure);
        final TextView cancleTextView = (TextView) view.findViewById(R.id.cancle);
        builder.setView(view);
        timePickDialog = builder.create();
        timePickDialog.setCanceledOnTouchOutside(false);
        timePickDialog.show();
        sureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int daysRoll = days.getCurrentItem();
                int hoursRoll = hours.getCurrentItem();
                int minsRoll = mins.getCurrentItem();
                Calendar selectCalendar = Calendar.getInstance();
                selectCalendar.setTime(nowDate);
                selectCalendar.add(Calendar.DATE, daysRoll);
                selectCalendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                selectCalendar.set(Calendar.MINUTE, minsRoll * 15);
                selectCalendar.set(Calendar.SECOND, 0);
                selectCalendar.set(Calendar.MILLISECOND, 0);
                Date selectCalendarTime = selectCalendar.getTime();
                long selectTime = selectCalendarTime.getTime();
                timePickDialog.dismiss();
                if (timePickResult != null) {
                    timePickResult.timePickResult(selectTime);
                }
            }
        });
        cancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickDialog.dismiss();
            }
        });
    }


    public static void showDayFirstPickDialog(final Activity context, long currenttime, final long startTime, final int availableDay, final TimePickResult timePickResult) {
        if (context == null) {
            return;
        }
        boolean hookBoolean = false;
        if (timePickResult != null) {
            hookBoolean = timePickResult.hook();
        }
        if (!hookBoolean) {
            return;
        }
        if (timePickDialog != null && timePickDialog.isShowing()) {
            timePickDialog.dismiss();
        }
        Date date = new Date(startTime);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minsInt = calendar.get(Calendar.MINUTE);
        int yu = minsInt % 15;
        if (yu != 0) {
            calendar.add(Calendar.MINUTE, 15 - yu);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.daytime_pick_layout, null);
        final TextView timeTextView = (TextView) view.findViewById(R.id.date_textview);
//        Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        timeTextView.setText(TimeUtils.formatTimeWithHHMM(calendar));
        final WheelView days = (WheelView) view.findViewById(R.id.day);
        final Calendar nowCalendar = Calendar.getInstance();
        final Date nowDate = new Date(currenttime);
        final Date dayDate = new Date(currenttime);
        nowCalendar.setTime(nowDate);
        final Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.setTime(dayDate);
        int rollDay = 0;
        while (nowCalendar.before(calendar)) {
            int nowDay = nowCalendar.get(Calendar.DATE);
            int day = calendar.get(Calendar.DATE);
            if (day == nowDay) {
                int nowMonth = nowCalendar.get(Calendar.MONTH);
                int month = calendar.get(Calendar.MONTH);
                if (nowMonth == month) {
                    break;
                }
            }
            nowCalendar.add(Calendar.DATE, 1);
            rollDay++;
        }
        DayArrayAdapter dayArrayAdapter = new DayArrayAdapter(context, dayCalendar);
        days.setViewAdapter(dayArrayAdapter);
        days.setVisibleItems(7);
        final WheelView hours = (WheelView) view.findViewById(R.id.hour);
        hours.setViewAdapter(new NumericWheelAdapter(context, 0, 23));
        hours.setVisibleItems(7);
        final WheelView mins = (WheelView) view.findViewById(R.id.mins);
        mins.setViewAdapter(new SpeedAdapter(context, 45, 15));
        mins.setVisibleItems(7);
        int monthInt = calendar.get(Calendar.MONTH);
        int dayInt = calendar.get(Calendar.DAY_OF_MONTH);

        dayArrayAdapter.setDaysCount(availableDay);
        days.setCurrentItem(rollDay);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE) / 15);
        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                    timeChanged = true;
                    int daysRoll = days.getCurrentItem();
                    int hoursRoll = hours.getCurrentItem();
                    int minsRoll = mins.getCurrentItem();
                    Calendar selectCalendar = Calendar.getInstance();
                    selectCalendar.setTime(nowDate);
                    selectCalendar.add(Calendar.DATE, daysRoll);
                    selectCalendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                    selectCalendar.set(Calendar.MINUTE, minsRoll * 15);
                    selectCalendar.set(Calendar.SECOND, 0);
                    selectCalendar.set(Calendar.MILLISECOND, 0);
                    timeTextView.setText(TimeUtils.formatTimeWithHHMM(selectCalendar));
                    timeChanged = false;
                }
            }
        };
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);
        days.addChangingListener(wheelListener);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                timeScrolled = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                timeScrolled = false;
                timeChanged = true;
                int daysRoll = days.getCurrentItem();
                int hoursRoll = hours.getCurrentItem();
                int minsRoll = mins.getCurrentItem();
                Calendar selectCalendar = Calendar.getInstance();
                selectCalendar.setTime(nowDate);
                selectCalendar.add(Calendar.DATE, daysRoll);
                selectCalendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                selectCalendar.set(Calendar.MINUTE, minsRoll * 15);
                selectCalendar.set(Calendar.SECOND, 0);
                timeTextView.setText(TimeUtils.formatTimeWithHHMM(selectCalendar));
                timeChanged = false;
            }
        };

        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);
        days.addScrollingListener(scrollListener);

        final TextView sureTextView = (TextView) view.findViewById(R.id.sure);
        final TextView cancleTextView = (TextView) view.findViewById(R.id.cancle);

        builder.setView(view);
        timePickDialog = builder.create();
        timePickDialog.setCanceledOnTouchOutside(false);
        timePickDialog.show();
        sureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int daysRoll = days.getCurrentItem();
                int hoursRoll = hours.getCurrentItem();
                int minsRoll = mins.getCurrentItem();
                Calendar selectCalendar = Calendar.getInstance();
                selectCalendar.setTime(nowDate);
                selectCalendar.add(Calendar.DATE, daysRoll);
                selectCalendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                selectCalendar.set(Calendar.MINUTE, minsRoll * 15);
                selectCalendar.set(Calendar.SECOND, 0);
                selectCalendar.set(Calendar.MILLISECOND, 0);
                Date selectCalendarTime = selectCalendar.getTime();
                long selectTime = selectCalendarTime.getTime();
                timePickDialog.dismiss();
                if (timePickResult != null) {
                    timePickResult.timePickResult(selectTime);
                }
            }
        });
        cancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickDialog.dismiss();
            }
        });
    }

    public static int startTimeCount = 30;
    public static int RentTimeCount = 30;
    public static Dialog timePickDialog;

    public static void showHourPickDialog(final Activity context, long time, final TimePickResult timePickResult, String tips) {
        if (context == null) {
            return;
        }
        if (timePickDialog != null && timePickDialog.isShowing()) {
            timePickDialog.dismiss();
        }
        boolean hookBoolean = false;
        if (timePickResult != null) {
            hookBoolean = timePickResult.hook();
        }
        if (!hookBoolean) {
            return;
        }
        Date date = new Date(time);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minsInt = calendar.get(Calendar.MINUTE);
        int yu = minsInt % 15;
        if (yu != 0) {
            calendar.add(Calendar.MINUTE, 15 - yu);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = context.getLayoutInflater().inflate(R.layout.hourtime_pick_layout, null);
        TextView tip = (TextView) view.findViewById(R.id.tips);
        if (tips != null && !tips.equals("")) {
            tip.setVisibility(View.VISIBLE);
            tip.setText(tips);
        } else {
            tip.setVisibility(View.GONE);
        }
        final TextView timeTextView = (TextView) view.findViewById(R.id.date_textview);
        timeTextView.setText(TimeUtils.formatTimeWithHHMM(calendar));
        final WheelView hours = (WheelView) view.findViewById(R.id.hour);
        hours.setViewAdapter(new NumericWheelAdapter(context, 0, 23));
        hours.setVisibleItems(7);
        final WheelView mins = (WheelView) view.findViewById(R.id.mins);
        mins.setViewAdapter(new SpeedAdapter(context, 45, 15));
        mins.setVisibleItems(7);
//        final Calendar nowCalendar = Calendar.getInstance();
//        final Date nowDate = new Date();
//        nowCalendar.setTime(nowDate);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE) / 15);
        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                    timeChanged = true;
                    int hoursRoll = hours.getCurrentItem();
                    int minsRoll = mins.getCurrentItem();
//                    Calendar selectCalendar = Calendar.getInstance();
//                    selectCalendar.setTime(nowDate);
                    calendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                    calendar.set(Calendar.MINUTE, minsRoll * 15);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    timeTextView.setText(TimeUtils.formatTimeWithHHMM(calendar));
                    timeChanged = false;
                }
            }
        };
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);
        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                timeScrolled = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                timeScrolled = false;
                timeChanged = true;
                int hoursRoll = hours.getCurrentItem();
                int minsRoll = mins.getCurrentItem();
//                Calendar selectCalendar = Calendar.getInstance();
//                selectCalendar.setTime(nowDate);
                calendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                calendar.set(Calendar.MINUTE, minsRoll * 15);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                timeTextView.setText(TimeUtils.formatTimeWithHHMM(calendar));
                timeChanged = false;
            }
        };
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);
        final TextView sureTextView = (TextView) view.findViewById(R.id.sure);
        final TextView cancleTextView = (TextView) view.findViewById(R.id.cancle);
        builder.setView(view);
        timePickDialog = builder.create();
        timePickDialog.setCanceledOnTouchOutside(false);
        timePickDialog.show();
        sureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hoursRoll = hours.getCurrentItem();
                int minsRoll = mins.getCurrentItem();
//                Calendar selectCalendar = Calendar.getInstance();
//                selectCalendar.setTime(nowDate);
                calendar.set(Calendar.HOUR_OF_DAY, hoursRoll);
                calendar.set(Calendar.MINUTE, minsRoll * 15);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date selectCalendarTime = calendar.getTime();
                long selectTime = selectCalendarTime.getTime();
                timePickDialog.dismiss();
                if (timePickResult != null) {
                    timePickResult.timePickResult(selectTime);
                }
            }
        });
        cancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickDialog.dismiss();
            }
        });
    }


    /**
     * 将图片写入到磁盘
     *
     * @param img      图片数据流
     * @param fileName 文件保存时的名称
     */
    public static void writeImageToDisk(byte[] img, String fileName) {
        try {
            File file = new File(SysConfig.SD_IMAGE_PATH + fileName);
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DP转PX
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * PX转DP
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean de = file.delete();
        }
    }

    /*
     * 以屏幕大小为界
     */
    public static void setNewopts(Activity context, BitmapFactory.Options opts, int srcW, int srcH) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // int minSrc = Math.min(srcW, srcH);
        // int maxSrc = Math.max(srcW, srcH);
        // int minSrn = Math.min(dm.widthPixels, dm.heightPixels);
        // int maxSrn = Math.max(dm.widthPixels, dm.heightPixels);
        //
        // if(minSrc * maxSrn < maxSrc * minSrn) {
        // opts.outWidth = (srcW * maxSrn) / maxSrc;
        // opts.outHeight = (srcH * maxSrn) / maxSrc;
        // } else {
        // opts.outWidth = (srcW * minSrn) / minSrc;
        // opts.outHeight = (srcH * minSrn) / minSrc;
        // }
        /*
         * 同向
         */
        if ((srcH > srcW && dm.heightPixels > dm.widthPixels) || (srcH < srcW && dm.heightPixels < dm.widthPixels)) {
            opts.inSampleSize = Math.min(srcH / dm.heightPixels, srcW / dm.widthPixels);
        } else {
            opts.inSampleSize = Math.min(srcW / dm.heightPixels, srcH / dm.widthPixels);
        }
        // DebugLog.logi("opts.inSampleSize:" + opts.inSampleSize);
        // DebugLog.logi("scaleToHalf: srcW:" + srcW + "  srcH:" + srcH);
        // DebugLog.logi("scaleToHalf: screenW:" + dm.widthPixels + "  screenH:" + dm.heightPixels);
        // DebugLog.logi("scaleToHalf: opts.outWidth:"+opts.outWidth + "  opts.outHeight:"+opts.outHeight);
        // opts.inSampleSize = 4;
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    /*
     * 以屏幕大小为界
     */
    public static void setSmallNewopts(Activity context, BitmapFactory.Options opts, int srcW, int srcH) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int minSrc = Math.min(srcW, srcH);
        int maxSrc = Math.max(srcW, srcH);
        int minSrn = Math.min(dm.widthPixels, dm.heightPixels);
        int maxSrn = Math.max(dm.widthPixels, dm.heightPixels);
        if (minSrc * maxSrn < maxSrc * minSrn) {
            opts.outWidth = (srcW * maxSrn) / maxSrc;
            opts.outHeight = (srcH * maxSrn) / maxSrc;
        } else {
            opts.outWidth = (srcW * minSrn) / minSrc;
            opts.outHeight = (srcH * minSrn) / minSrc;
        }
        /*
         * 同向
         */
        if ((srcH > srcW && dm.heightPixels > dm.widthPixels) || (srcH < srcW && dm.heightPixels < dm.widthPixels)) {
            opts.inSampleSize = Math.min(srcH / dm.heightPixels, srcW / dm.widthPixels);
        } else {
            opts.inSampleSize = Math.min(srcW / dm.heightPixels, srcH / dm.widthPixels);
        }
        // DebugLog.logi("opts.inSampleSize:" + opts.inSampleSize);
        // DebugLog.logi("scaleToHalf: srcW:" + srcW + "  srcH:" + srcH);
        // DebugLog.logi("scaleToHalf: screenW:" + dm.widthPixels + "  screenH:" + dm.heightPixels);
        // DebugLog.logi("scaleToHalf: opts.outWidth:"+opts.outWidth + "  opts.outHeight:"+opts.outHeight);
        // opts.inSampleSize = 4;
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public static Bitmap small(Bitmap bitmap, float sx, float sy) {
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public static String TAG = "Config";

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileIsExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    private static long exitTime = 0;

    public static boolean onKeyDown(int keyCode, KeyEvent event, Activity context) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Intent intent = new Intent(Intent.ACTION_MAIN);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            // intent.addCategory(Intent.CATEGORY_HOME);
            // context.startActivity(intent);
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(context, "再按一次退出", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    // Intent intent = new Intent(Intent.ACTION_MAIN);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                    // intent.addCategory(Intent.CATEGORY_HOME);
                    // context.startActivity(intent);


                    UUAppCar.getInstance().exit();
                }
                return true;
            }
            return true;
        }
        return false;
    }

//    public static boolean onKeyDownExit(int keyCode, KeyEvent event, Activity context) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            // Intent intent = new Intent(Intent.ACTION_MAIN);
//            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
//            // intent.addCategory(Intent.CATEGORY_HOME);
//            // context.startActivity(intent);
//            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                if ((System.currentTimeMillis() - exitTime) > 2000) {
//                    Toast.makeText(context, "再按一次退出", Toast.LENGTH_SHORT).show();
//                    exitTime = System.currentTimeMillis();
//                } else {
//                    UUAppCar.getInstance().stopAll();
//                }
//                return true;
//            }
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断参数中activity是否运行
     * 参数是activity的包名
     */
    public static boolean isRun(Context context, String activity) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        if (rti.get(0).topActivity.getClassName().equals(activity)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean outApp(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        if (rti.get(0).topActivity.getPackageName().indexOf("com.youyou") == -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * MD5加密，32位
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase();
    }

    public static String micromenter(String strResults) {
        if (strResults.startsWith("-")) {
            String s = fmtMicrometer(strResults.substring(1, strResults.length()));
            return "-" + s;
        } else {
            return fmtMicrometer(fmtMicrometer(strResults));
        }
    }

    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

    public static String getFormatTime(String timeSecond) {
        Long time = Long.parseLong(timeSecond);
        Date date = new Date(time * 1000L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);


        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        String result = sdf.format(date);
        return getWeek(week) + " " + result;
    }


    public static String getFormatTime(int timeSecond) {
        Date date = new Date(timeSecond * 1000L);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int week = calendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String result = sdf.format(date);
        return result;
    }

    public static String getFormatTimeYYYY_MM_DD(int timeSecond) {
        Date date = new Date(timeSecond * 1000L);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int week = calendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String result = sdf.format(date);
        return result;
    }

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int week = calendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        String result = sdf.format(date);
        return result;
    }

    public static String getShortFormatTime(int timeSecond) {
        Date date = new Date(timeSecond * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String result = sdf.format(date);
        return result;
    }


    private static String getWeek(int week) {

        if (week == 1) {
            return "周日";
        } else if (week == 2) {
            return "周一";
        } else if (week == 3) {
            return "周二";
        } else if (week == 4) {
            return "周三";
        } else if (week == 5) {
            return "周四";
        } else if (week == 6) {
            return "周五";
        } else if (week == 7) {
            return "周六";
        }
        return "";
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private static PoiSearch.Query query;

    private static PoiResult poiResult; // poi返回的结果

    /**
     * 高德地图 POI搜索
     */
    public static void searchPoi(Context context, String key, String city, int page, final PoiResultCallback callback) {
        query = new PoiSearch.Query(key, "", city);
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);
        PoiSearch poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode) {
                MLog.e(TAG, "result = " + result + "  code = " + rCode);
                if (rCode == 0) {
                    if (result != null && result.getQuery() != null) {
                        if (result.getQuery().equals(query)) {
                            poiResult = result;
                            if (callback != null) {
                                callback.callback(poiResult.getPois());
                            }
//                            maxPage = poiResult.getPageCount();
//                            poiItems.addAll(poiResult.getPois());
//                            adapter.notifyDataSetChanged();
//                            Config.dismissProgress();
                        }
                    }
                }
            }

            @Override
            public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
            }
        });
    }

    /**
     * POI搜索回调
     */
    public interface PoiResultCallback {
        public void callback(List<PoiItem> list);
    }


    /**
     * author:lc
     * desc:APP版本号转换为Integer如：3.2.0 转化 320
     */
    public static final int changeVersionNameToCode(String versionCode) {
        //根据正则表达式判断版本号是否合法
        try {
            String pattern = "\\d\\.\\d\\.\\d";
            if (versionCode.matches(pattern)) {
                String[] nums = versionCode.split("\\.");
                if (nums != null && nums.length > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (String num : nums) {
                        sb.append(num);
                    }

                    return Integer.valueOf(sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
