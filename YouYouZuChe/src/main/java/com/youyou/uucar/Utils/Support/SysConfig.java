package com.youyou.uucar.Utils.Support;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * Created by TaurusXi on 2014/7/3.
 */
public class SysConfig {
    public static Dialog dialog;
    public static final String TITLE = "title";
    public static final String PRE_ORDER_DETAIL = "PRE_ORDER_DETAIL";
    public static final String ORDER_ID = "ORDER_ID";  // 订单号
    public static final boolean DEVELOP_MODE = true;
    public static final boolean ISMONKEY = false;//是否是monkey版本,如果等于true 退出登录和立即登录将会失效
    public static final String PLATE_NUMBER = "PLATE_NUMBER";
    public static final String CAR_TYPE = "CAR_TYPE";
    public static final String CAR_NAME = "CAR_NAME";
    public static final String NEED_SUBMIT = "NEED_SUBMIT";
    public static final String CAR_SN = "CAR_SN";
    public static final String CAR_SNS = "CAR_SNS";//多辆车
    public static final String REFUSE_PIC = "REFUSE_PIC";
    public static final String S_ID = "S_ID";
    public static final String S_OWNER = "S_OWNER";
    public static final String CAR_PRICE = "CAR_PRICE";
    public static final String UPDATE_FRAGMENT = "UPDATE_FRAGMENT";
    public static final String FIND_CAR = "FIND_CAR";
    public static final String CAR_MANAGER = "CAR_MANAGER";
    public static final String R_SN = "R_SN";
    public static final String SHOW_EDIT_BT = "SHOW_EDIT_BT";
    public static final String UN_USUAL = "UN_USUAL";
    public static final String NEED_RECHANGE_AMOUNT = "NEED_RECHANGE_AMOUNT";
    public static final String IS_FROM_LIST = "islist";
    public static final String FIND_CAR_LIST_INDEX = "index";
    public static final String CITY = "CITY";
    public static final String LONG_CONNECT = "LONG_CONNECT";
    public static final String START_LONG_CONNECT = "START_LONG_CONNECT";
    public static final String CLOSE_LONG_CONNECT = "CLOSE_LONG_CONNECT";
    public static final String NETWORK_FAIL = "操作失败,请打开网络后重试!";
    public static final String NETWORK_PHOTO_FAIL = "网络出错，请检查网络后重新上传!";
    public static final String COUPON_ID = "coupon_id";
    public static final int WAIT_FOR_SERVICE = 112;
    public static final int ADD_CAR = 111;
    public static final int RELEASE_WAIT_SERVICE = 113;
    public static final int RENTER_PAY = 1231;
    public static final String BEI_JING_CITY = "北京市";
    public static final int COLLET_TO_PHONE = 12321;
    public static String SD_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyou/uucar/image/";

    /**
     * 租客同意，传送给同意的车主的场景
     */
    public static String SCENE_RENTER_RENTER_AGREE = "renter_agree";
    /**
     * 我的出租
     */
    public static String OBSERVER_MY_RENTER = "OBSERVER_OWNER";

    public static String getAppVerSion(Context context) {
        String pkName = context.getPackageName();
        try {
            return context.getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

}
