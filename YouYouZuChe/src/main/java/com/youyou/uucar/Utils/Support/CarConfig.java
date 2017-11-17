package com.youyou.uucar.Utils.Support;

public class CarConfig
{
    /**
     * 商务车
     */
    public static final String   TYPE_SHANGWUCHE = "1";
    /**
     * SUV
     */
    public static final String   TYPE_SUV        = "2";
    /**
     * 旅行车
     */
    public static final String   TYPE_LVXING     = "3";
    /**
     * 家用车
     */
    public static final String   TYPE_JIAYONG    = "4";
    public static final String[] CAR_TYPE        = {"商务车", "SUV", "旅行车", "家用车"};
    // -------------------------------------------------------
    /**
     * 汽油型号-92
     */
    public static final String   OIL_92          = "1";
    /**
     * 汽油型号 - 95
     */
    public static final String   OIL_95          = "2";
    public static final String[] OIL_TYPE        = {"#92/#93", "#95"};
    // ----------------------------------------------------------
    /**
     * 颜色-白
     */
    public static final String   COLOR_WHITE     = "1";
    /**
     * 颜色-银
     */
    public static final String   COLOR_SILVER    = "2";
    /**
     * 颜色-黄
     */
    public static final String   COLOR_YELLOW    = "3";
    /**
     * 颜色-红
     */
    public static final String   COLOR_RED       = "4";
    /**
     * 颜色-蓝
     */
    public static final String   COLOR_BLUE      = "5";
    /**
     * 颜色-绿
     */
    public static final String   COLOR_GREEN     = "6";
    /**
     * 颜色-黑
     */
    public static final String   COLOR_BLACK     = "7";
    public static final String[] COLOR_TYPE      = {"白", "银", "黄", "红", "蓝", "绿", "黑"};
    // ----------------------------------------------------------------
    /**
     * 里程-2W以下
     */
    public static final String   MILEAGE_2W      = "1";
    /**
     * 里程-2-5
     */
    public static final String   MILEAGE_5W      = "2";
    /**
     * 里程-5-10
     */
    public static final String   MILEAGE_10W     = "3";
    /**
     * 里程-10+
     */
    public static final String   MILEAGE_11W     = "4";
    public static final String[] MILEAGE_TYPE    = {"2W以下", "2W-5W", "5W-10W", "10W以上"};
    // ----------------------------------------------------------------
    /**
     * 座位数 5
     */
    public static final String   SEAT_5          = "1";
    /**
     * 座位数 7
     */
    public static final String   SEAT_7          = "2";
    public static final String[] SEAT_TYPE       = {"5座", "7座"};
    // -----------------------------------------------------------------
    /**
     * 波箱 手动挡
     */
    public static final String   BOXIANG_MANUAL  = "1";
    /**
     * 波箱 自动挡
     */
    public static final String   BOXIANG_AUTO    = "2";
    public static final String[] BOXIANG_TYPE    = {"手动档", "自动档"};
    // --------------------------------------------------------------------
    /**
     * 排量 1.6-
     */
    public static final String   CC_16           = "1";
    /**
     * 排量 1.6-1.9
     */
    public static final String   CC_19           = "2";
    /**
     * 排量 2.0-2.3
     */
    public static final String   CC_23           = "3";
    /**
     * 排量 2.4+
     */
    public static final String   CC_24           = "4";
    public static final String[] CC_TYPE         = {"1.6L以下", "1.6L-1.9L", "2.0L-2.3L", "2.4L以上"};
    // --------------------------------------------
    /**
     * 冷气
     */
    public static final String   HASAIR          = "1";
    /**
     * 冷气
     */
    public static final String   NOAIR           = "2";
    public static final String[] AIR_TYPE        = {"空调", ""};

    // --------------------------------------------

    /**
     * 获取汽车类型
     *
     * @param type
     * @return
     */
    public static String getTYPE(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return CAR_TYPE[index];
    }

    /**
     * 获取油号
     */
    public static String getOIL(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return OIL_TYPE[index];
    }

    /**
     * 获取颜色
     */
    public static String getCOLOR(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return COLOR_TYPE[index];
    }

    /**
     * 获取里程
     */
    public static String getMILEAGE(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return MILEAGE_TYPE[index];
    }

    /**
     * 获取座位
     */
    public static String getSEAT(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return SEAT_TYPE[index];
    }

    /**
     * 获取波箱
     */
    public static String getBOXIANG(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return BOXIANG_TYPE[index];
    }

    /**
     * 获取CC
     */
    public static String getCC(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return CC_TYPE[index];
    }

    /**
     * 获取AIR
     */
    public static String getAIR(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return AIR_TYPE[index];
    }

    /**
     * 行李箱
     */
    public static final String[] XINGLI_TYPE = {"x1", "x2"};

    public static String getXINGLI(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return XINGLI_TYPE[index];
    }

    /**
     * 车门
     */
    public static final String[] CHEMEN_TYPE = {"x4", "x2"};

    public static String getCheMen(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return CHEMEN_TYPE[index];
    }

    /**
     * GPS
     */
    public static final String[] GPS_TYPE = {"GPS", ""};

    public static String getGPS(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return GPS_TYPE[index];
    }

    /**
     * 倒车雷达
     */
    public static final String[] GPS_DAOCHELEIDA = {"倒车雷达", ""};

    public static String getDaocheleida(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return GPS_DAOCHELEIDA[index];
    }

    /**
     * USB
     */
    public static final String[] USB_TYPE = {"USB", ""};

    public static String getUSB(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return USB_TYPE[index];
    }

    /**
     * 音频输入
     */
    public static final String[] YINPINSHURU_TYPE = {"音频输入", ""};

    public static String getYINPIN(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return YINPINSHURU_TYPE[index];
    }

    /**
     * 蓝牙
     */
    public static final String[] BLUETOOTH_TYPE = {"蓝牙", ""};

    public static String getBLUETOOTH(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return BLUETOOTH_TYPE[index];
    }

    /**
     * 座椅加热
     */
    public static final String[] ZUOYI_TYPE = {"座椅加热", ""};

    public static String getZUOYI(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return ZUOYI_TYPE[index];
    }

    /**
     * 行车记录仪
     */
    public static final String[] JILUYI_TYPE = {"行车记录仪", ""};

    public static String JILUYI_TYPE(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return JILUYI_TYPE[index];
    }

    /**
     * 倒车雷达
     */
    public static final String[] DAOCHELEIDA_TYPE = {"倒车雷达", ""};

    public static String DaocheLeida(String type)
    {
        int index = Integer.parseInt(type) - 1;
        return DAOCHELEIDA_TYPE[index];
    }
}
