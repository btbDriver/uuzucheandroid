package com.youyou.uucar.UI.Main.rent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.timessquare.CalendarPickerView;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.PB.impl.ReserveInformationModel;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.FindCarFragment.SelectAddressActivity;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UI.Renter.filter.FilteredCarListActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.observer.ObserverManager;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.DateTimePicker;
import com.youyou.uucar.Utils.View.DateTimePickerDialog;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class SelectTime extends BaseActivity {
    public final long DAY_TIMES = 1000 * 60 * 60 * 24;
    public final long HOUR_TIMES = 1000 * 60 * 60;
    public final long MINUTE_TIMES = 1000 * 60;
    public String tag = "SelectTime";
    public Activity context;
    public String car_sn = "";
    DateTimePicker start_mDateTimePicker, end_mDateTimePicker;
    @InjectView(R.id.start_time)
    TextView start_time_text;
    @InjectView(R.id.end_time)
    TextView end_time_text;
    double currentLat, currentLng;
    @InjectView(R.id.start_time_root)
    RelativeLayout mStartTimeRoot;
    @InjectView(R.id.end_time_root)
    RelativeLayout mEndTimeRoot;
    //    @InjectView(R.id.rent_time)
//    TextView rent_time;
//    @InjectView(R.id.rent_title)
//    TextView rentTitle;
    @InjectView(R.id.car_info_content)
    TextView car_info_content;
    @InjectView(R.id.car_beizhu_content)
    TextView car_beizhu_content;
    @InjectView(R.id.car_more_info)
    RelativeLayout car_more_info;
    @InjectView(R.id.tv_zuqi)
    TextView tv_zuqi;
    @InjectView(R.id.tv_zujin)
    TextView tv_zujin;
    @InjectView(R.id.tv_baoxian)
    TextView tv_baoxian;
    @InjectView(R.id.tv_yajin)
    TextView tv_yajin;
//    @InjectView(R.id.car_info_price)
//    TextView       car_info_price;

    @InjectView(R.id.sure)
    TextView sure;

//    @OnClick(R.id.car_zujin_wenhao)
//    public void zujin() {
//        if (!Config.isNetworkConnected(context)) {
//            Config.showFiledToast(context);
//            return;
//        }
//        Intent intent = new Intent(context, URLWebView.class);
//        intent.putExtra("url", ServerMutualConfig.yclc);
//        intent.putExtra(SysConfig.TITLE, "用车流程");
//        context.startActivity(intent);
//    }

    @InjectView(R.id.car_baoxian_wenhao)
    ImageView car_baoxian_wenhao;

    @InjectView(R.id.car_yajin_wenhao)
    ImageView car_yajin_wenhao;


    public void sureClick() {
        if (Config.isGuest(context)) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
            Intent intent = new Intent(context, RenterRegisterIDActivity.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
            Intent intent = new Intent(context, RenterRegisterVerify.class);
            startActivity(intent);
        } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO)) {
            Intent intent = new Intent(context, RenterRegisterVerifyError.class);
            intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
            startActivity(intent);
        } else if (start_long == 0) {
            showToast("请选择取车时间");
            return;
        } else if (end_long == 0) {
            showToast("请选择还车时间");
            return;
        } else {
//                String startTime = "2014年" + start_time;
//                String endTime = "2014年" + end_time;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间


            String currentTime = formatter.format(curDate);
            Date /*startDate = null, endDate = null,*/ currentDate = null;
            try {
                Date startDate = new Date(start_long);
                Date endDate = new Date(end_long);
                currentDate = formatter.parse(currentTime);
                long start = startDate.getTime();
                long end = endDate.getTime();
                long current = currentDate.getTime();
                if (start - current < Config.START_TIME_DIFFERENCE) {
                    Toast.makeText(context, "取车时间最早是1小时之后", Toast.LENGTH_SHORT).show();
                } else if (start > end) {
                    Toast.makeText(context, "还车时间不能小于取车时间", Toast.LENGTH_SHORT).show();
                } else if (end - start < Config.MIN_HIRE_TIME) {
                    Toast.makeText(context, "最少租用4小时", Toast.LENGTH_SHORT).show();
                } else {
                    Config.startTimeLong = start_long;
                    Config.endTimeLong = end_long;
                    hire(false);
                }
            } catch (Exception e) {
                Toast.makeText(context, "请选择取车时间和还车时间", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    long currentTime = 0;


    private RelativeLayout dialogView;
    private AlertDialog theDialog;
    CalendarPickerView calendarView;

    private boolean sameDate(Calendar cal, Calendar selectedDate) {
        return cal.get(MONTH) == selectedDate.get(MONTH)
                && cal.get(YEAR) == selectedDate.get(YEAR)
                && cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
    }

    /**
     * 日期时间差
     *
     * @param old     开始时间
     * @param current 当前选择时间
     * @return
     */
    public int getDifferDay(Calendar old, Calendar current) {
        old.set(Calendar.HOUR_OF_DAY, 0);
        old.set(Calendar.MINUTE, 0);
        old.set(Calendar.SECOND, 0);
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
//        long dayCount = ((current.getTimeInMillis() - old.getTimeInMillis()) / (24 * 60 * 60 * 1000));//从间隔毫秒变成间隔天数


        BigDecimal dayCount = new BigDecimal((double) ((double) (current.getTimeInMillis() - old.getTimeInMillis()) / (double) (24 * 60 * 60 * 1000))).setScale(0, BigDecimal.ROUND_HALF_UP);
        return (int) (Integer.parseInt(dayCount.toString()) + 1);
    }

    public int banType = 0;
    public final int BAN_TYPE_NO = 0;
    public final int BAN_TYPE_AM = 1;
    public final int BAN_TYPE_PM = 2;
    public final int BAN_TYPE_ALL = 3;

    @OnClick(R.id.start_time_root)
    public void startClick() {
        if (getIntent().hasExtra("noSelectTime") && getIntent().getBooleanExtra("noSelectTime", false)) {
            return;
        }
        currentTime = System.currentTimeMillis();
        if (getIntent().getBooleanExtra("fromCarInfo", false)) {
            fromCarInfoStartClick();
        } else {
            hourStartClick();

        }
    }

    public void hourStartClick() {
        SimpleDateFormat tipformatter = new SimpleDateFormat("HH");
        long startTimeLong = System.currentTimeMillis();

        if (start_long != 0) {
            startTimeLong = start_long;
        } else {
            int hour = Integer.parseInt(tipformatter.format(System.currentTimeMillis()));
            if (hour >= 18)//18点以后
            {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                cal.set(Calendar.HOUR_OF_DAY, 10);
                cal.set(Calendar.MINUTE, 0);
                startTimeLong = cal.getTimeInMillis();
            } else if (hour < 6) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 10);
                cal.set(Calendar.MINUTE, 0);
                startTimeLong = cal.getTimeInMillis();
            } else {
                startTimeLong += 4 * HOUR_TIMES;
            }
        }

        Config.showDayFirstPickDialog(context, System.currentTimeMillis(), startTimeLong, Config.startTimeCount, new Config.TimePickResult() {
            @Override
            public boolean hook() {
                return true;
            }

            @Override
            public void timePickResult(long date) {
                if (date < System.currentTimeMillis() + HOUR_TIMES) {
                    showToast("取车时间最早为1小时以后");
                    return;
                }
                start_long = date;
                updateTimeText(start_time_text, date);
                setRentTime();
                if (end_long == 0) {
                    end_long = date + DAY_TIMES;
                    updateTimeText(end_time_text, end_long);
                    setRentTime();
                } else {
                    long start, end;
                    start = start_long;
                    end = end_long;
                    if (end < start || end - start < HOUR_TIMES * 4 || end - start > DAY_TIMES * 7) {
                        end_long = 0;
                        end_time_text.setText("");
                    }
                    setRentTime();
                }
            }
        });
    }

    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    //从车辆详情进来的
    public void fromCarInfoStartClick() {
        {
            showProgress(false);
            if (theDialog != null) {
                theDialog.dismiss();
                theDialog = null;
            }
            SimpleDateFormat tipformatter = new SimpleDateFormat("HH");
            SimpleDateFormat tipformatter2 = new SimpleDateFormat("mm");
            int hour = Integer.parseInt(tipformatter.format(System.currentTimeMillis()));
            int minute = Integer.parseInt(tipformatter2.format(System.currentTimeMillis()));
            final Calendar lastYear = Calendar.getInstance();
            final Calendar nextYear = Calendar.getInstance();
            if (hour >= 22 && minute >= 45) {
                lastYear.add(Calendar.DATE, 1);
                nextYear.add(Calendar.DATE, Config.startTimeCount + 1);
            } else {
                nextYear.add(Calendar.DATE, Config.startTimeCount);
            }
            Date minDate = new Date();
            minDate.setTime(lastYear.getTimeInMillis());
            dialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.time_square_dialog, null);
            calendarView = (CalendarPickerView) dialogView.findViewById(R.id.calendar_view);
            calendarView.init(lastYear.getTime(), nextYear.getTime(), Config.carDisableTime) //
                    /*.withSelectedDate(minDate)*/;
            final TextView banText = (TextView) dialogView.findViewById(R.id.ban_text);
            banText.setText(getIntent().getStringExtra("ban_time"));
            calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {


                    final Calendar cal = Calendar.getInstance();
                    long time = 0;
                    if (date.getTime() > System.currentTimeMillis())//大于今天
                    {
                        cal.setTime(date);
                        cal.set(Calendar.HOUR_OF_DAY, 10);
                        cal.set(Calendar.MINUTE, 0);
                        time = cal.getTimeInMillis();

                    } else//今天
                    {
                        SimpleDateFormat tipformatter = new SimpleDateFormat("HH");
                        int hour = Integer.parseInt(tipformatter.format(System.currentTimeMillis()));
                        if (hour < 8) {
                            cal.set(Calendar.HOUR_OF_DAY, 10);
                            cal.set(Calendar.MINUTE, 0);
                        } else if (hour < 23) {
                            cal.add(Calendar.HOUR_OF_DAY, 1);
                        }
                        time = cal.getTimeInMillis();
                    }

                    /**
                     * 是否显示限行提示
                     */
                    String hourDialogTip = "";
                    boolean hasBanDay = false;
                    MLog.e(tag, "diffDay = " + getDifferDay(Calendar.getInstance(), cal));
                    for (int i = 0; i < Config.carBanTime.getUnavailableTimeCount(); i++) {
                        if ((Config.carBanTime.getUnavailableTimeList().get(i).getUnavailableDay()) == getDifferDay(Calendar.getInstance(), cal)) {
                            hourDialogTip = "该天限行，请合理安排驾驶时间。";
                            hasBanDay = true;
                            break;
                        }
                    }
                    banType = BAN_TYPE_NO;
                    for (int i = 0; i < Config.carDisableTime.getUnavailableTimeCount(); i++) {

                        if (Config.carDisableTime.getUnavailableTimeList().get(i).getUnavailableDay() == getDifferDay(Calendar.getInstance(), cal)) {
                            if (Config.carDisableTime.getUnavailableTimeList().get(i).getType().equals(CarCommon.CarUnavailableTimeType.AM)) {
                                banType = BAN_TYPE_AM;
                                if (hasBanDay)//如果当天限行
                                {
                                    hourDialogTip = "该天限行且14:00以前不可租";
                                } else {
                                    hourDialogTip = "该天14:00以前不可租";
                                }
                            } else {
                                banType = BAN_TYPE_PM;
                                if (hasBanDay)//如果当天限行
                                {
                                    hourDialogTip = "该天限行且14:00以后不可租";
                                } else {
                                    hourDialogTip = "该天14:00以后不可租";
                                }
                            }
                            break;
                        }
                    }

                    Config.showHourPickDialog(context, time, new Config.TimePickResult() {
                        @Override
                        public boolean hook() {
                            return true;
                        }

                        @Override
                        public void timePickResult(long time) {

                            if (banType == BAN_TYPE_AM) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(time);
                                if (cal.get(Calendar.HOUR_OF_DAY) < 14) {
                                    showToast("该天14:00以前不可租");
                                    return;
                                }
                            } else if (banType == BAN_TYPE_PM) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(time);
                                if (cal.get(Calendar.HOUR_OF_DAY) > 14) {
                                    showToast("该天14:00以后不可租");
                                    return;
                                }

                            }

                            if (time < System.currentTimeMillis() + HOUR_TIMES) {
                                showToast("取车时间最早为1小时以后");
                                return;
                            }
                            start_long = time;
                            updateTimeText(start_time_text, time);
                            setRentTime();
                            if (end_long == 0) {
                                if (Config.carDisableTime != null) {
                                    try {
                                        boolean isBanday = false;
                                        for (int i = 0; i < Config.carDisableTime.getUnavailableTimeList().size(); i++) {

                                            Calendar today = Calendar.getInstance();
                                            today.set(Calendar.HOUR_OF_DAY, 0);
                                            today.set(Calendar.MINUTE, 0);
                                            today.set(Calendar.SECOND, 0);
                                            Calendar cal2 = Calendar.getInstance();
                                            cal2.setTimeInMillis(time + DAY_TIMES);
                                            cal2.set(Calendar.HOUR_OF_DAY, 0);
                                            cal2.set(Calendar.MINUTE, 0);
                                            cal2.set(Calendar.SECOND, 0);
                                            if (Config.carDisableTime.getUnavailableTimeList().get(i).getUnavailableDay() == (daysBetween(today.getTime(), cal2.getTime()) + 1)) {
                                                isBanday = true;
                                                break;
                                            }


                                        }
                                        if (!isBanday) {
                                            end_long = time + DAY_TIMES;
                                            updateTimeText(end_time_text, end_long);
                                            setRentTime();
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            } else {
                                long start, end;
                                start = start_long;
                                end = end_long;
                                if (end < start || end - start < HOUR_TIMES * 4 || end - start > DAY_TIMES * Config.RentTimeCount) {
                                    end_long = 0;
                                    end_time_text.setText("");
                                }
                                setRentTime();
                            }
                            theDialog.dismiss();
                        }
                    }, hourDialogTip);
                }

                @Override
                public void onDateUnselected(Date date) {

                }
            });
            theDialog =
                    new AlertDialog.Builder(context)
                            .setView(dialogView)
                            .setNeutralButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
            theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    calendarView.fixDialogDimens();
                }
            });
            theDialog.show();
            dismissProgress();
        }
    }


    @OnClick(R.id.end_time_root)
    public void endClick() {

        if (getIntent().hasExtra("noSelectTime") && getIntent().getBooleanExtra("noSelectTime", false)) {
            return;
        }
        if (start_long == 0) {
            showToast("请先选择取车时间");
        } else {
//            if (currentTime == 0) {
//                currentTime = start_long;
//            }
            if (getIntent().getBooleanExtra("fromCarInfo", false)) {
                fromCarInfoEndClick();
            } else {
                hourEndClick();
            }
        }
    }

    //不是从车辆详情来的时候,得用
    public void hourEndClick() {
        long current = 0;
        if (end_long == 0) {
            current = start_long + HOUR_TIMES * 4;
        } else {
            current = end_long;
        }
        Config.showDaySecondPickDialog(context, System.currentTimeMillis(), current, start_long, Config.RentTimeCount, new Config.TimePickResult() {
            @Override
            public boolean hook() {
                return true;
            }

            @Override
            public void timePickResult(long date) {

                if (date < start_long + HOUR_TIMES * 4) {
                    showToast("租期最少4个小时");
                    return;
                } else if (date > start_long + (DAY_TIMES * Config.RentTimeCount)) {
                    showToast("最长租期" + Config.RentTimeCount + "天");
                    return;
                }
                end_long = date;
                updateTimeText(end_time_text, end_long);
                setRentTime();
            }
        });
    }

    //从车辆详情进来的
    public void fromCarInfoEndClick() {
        {
            showProgress(false);
            if (theDialog != null) {
                theDialog.dismiss();
                theDialog = null;
            }
            SimpleDateFormat tipformatter = new SimpleDateFormat("HH");
            SimpleDateFormat tipformatter2 = new SimpleDateFormat("mm");
            int hour = Integer.parseInt(tipformatter.format(System.currentTimeMillis()));
            int minute = Integer.parseInt(tipformatter2.format(System.currentTimeMillis()));
            final Calendar lastYear = Calendar.getInstance();
            lastYear.setTime(new Date(start_long));
            final Calendar nextYear = Calendar.getInstance();
            nextYear.setTime(new Date(start_long));
            if (hour >= 22 && minute >= 45) {
                lastYear.add(Calendar.DATE, 1);
                nextYear.add(Calendar.DATE, Config.RentTimeCount + 1);
            } else {
                nextYear.add(Calendar.DATE, Config.RentTimeCount + 1);
            }
            dialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.time_square_dialog, null);
            calendarView = (CalendarPickerView) dialogView.findViewById(R.id.calendar_view);
            calendarView.init(lastYear.getTime(), nextYear.getTime(), Config.carDisableTime) //
                        /*.withSelectedDate(new Date(start_long))*/;

            TextView banText = (TextView) dialogView.findViewById(R.id.ban_text);
            banText.setText(getIntent().getStringExtra("ban_time"));
            calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {

                    long time = 0;
                    Calendar cal = Calendar.getInstance();
                    if (date.getTime() > System.currentTimeMillis())//大于今天
                    {
                        cal.setTime(date);
                        cal.set(Calendar.HOUR_OF_DAY, 10);
                        cal.set(Calendar.MINUTE, 0);
                        time = cal.getTimeInMillis();
                    } else//今天
                    {
                        SimpleDateFormat tipformatter = new SimpleDateFormat("HH");
                        int hour = Integer.parseInt(tipformatter.format(System.currentTimeMillis()));
                        if (hour < 9) {
                            cal.set(Calendar.HOUR_OF_DAY, 10);
                            cal.set(Calendar.MINUTE, 0);
                        } else if (hour < 23) {
                            cal.add(Calendar.HOUR_OF_DAY, 1);
                        }
                        time = cal.getTimeInMillis();
                    }
                    /**
                     * 是否显示限行提示
                     */
                    String hourDialogTip = "";
                    boolean hasBanDay = false;
                    MLog.e(tag, "diffDay = " + getDifferDay(Calendar.getInstance(), cal));
                    for (int i = 0; i < Config.carBanTime.getUnavailableTimeCount(); i++) {
                        if ((Config.carBanTime.getUnavailableTimeList().get(i).getUnavailableDay()) == getDifferDay(Calendar.getInstance(), cal)) {
                            hourDialogTip = "该天限行，请合理安排驾驶时间。";
                            hasBanDay = true;
                            break;
                        }
                    }
                    banType = BAN_TYPE_NO;
                    for (int i = 0; i < Config.carDisableTime.getUnavailableTimeCount(); i++) {
                        if (Config.carDisableTime.getUnavailableTimeList().get(i).getUnavailableDay() == getDifferDay(Calendar.getInstance(), cal)) {
                            if (Config.carDisableTime.getUnavailableTimeList().get(i).getType().equals(CarCommon.CarUnavailableTimeType.AM)) {
                                banType = BAN_TYPE_AM;
                                if (hasBanDay)//如果当天限行
                                {
                                    hourDialogTip = "该天限行且14:00以前不可租";
                                } else {
                                    hourDialogTip = "该天14:00以前不可租";
                                }
                            } else {
                                banType = BAN_TYPE_PM;
                                if (hasBanDay)//如果当天限行
                                {
                                    hourDialogTip = "该天限行且14:00以后不可租";
                                } else {
                                    hourDialogTip = "该天14:00以后不可租";
                                }
                            }
                            break;
                        }
                    }

                    Config.showHourPickDialog(context, time, new Config.TimePickResult() {
                        @Override
                        public boolean hook() {
                            return true;
                        }

                        @Override
                        public void timePickResult(long time) {
                            if (banType == BAN_TYPE_AM) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(time);
                                if (cal.get(Calendar.HOUR_OF_DAY) < 14) {
                                    showToast("该天14:00以前不可租");
                                    return;
                                }
                            } else if (banType == BAN_TYPE_PM) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(time);
                                if (cal.get(Calendar.HOUR_OF_DAY) > 14) {
                                    showToast("该天14:00以后不可租");
                                    return;
                                }

                            }


                            if (time < start_long + HOUR_TIMES * 4) {
                                showToast("租期最少4个小时");
                                return;
                            } else if (time > start_long + (DAY_TIMES * Config.RentTimeCount)) {
                                showToast("最长租期" + Config.RentTimeCount + "天");
                                return;
                            }
                            end_long = time;
                            updateTimeText(end_time_text, end_long);
                            setRentTime();
                            theDialog.dismiss();
                        }
                    }, hourDialogTip);
                }

                @Override
                public void onDateUnselected(Date date) {

                }
            });
            theDialog =
                    new AlertDialog.Builder(context)
                            .setView(dialogView)
                            .setNeutralButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
            theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    calendarView.fixDialogDimens();
                }
            });
            theDialog.show();
            dismissProgress();
        }
    }

    public void showDialog(long l, final String type, String title) {
        DateTimePickerDialog dialog = new DateTimePickerDialog(this, l, type);
        /**
         * 实现接口
         */
        dialog.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
            public void OnDateTimeSet(AlertDialog dialog, long date) {

                if (type.equals(DateTimePicker.TYPE_START)) {
                    start_long = date;
                    Config.startTimeLong = start_long;
                    updateTimeText(start_time_text, date);
                    setRentTime();
                    if (end_long == 0) {
                        end_long = date + DAY_TIMES;
                        Config.endTimeLong = end_long;
                        updateTimeText(end_time_text, end_long);
                        setRentTime();
                    } else {
                        long start, end;
                        start = start_long;
                        end = end_long;
                        if (end < start || end - start < HOUR_TIMES * 4 || end - start > DAY_TIMES * 7) {
                            end_long = 0;
                            Config.endTimeLong = end_long;
                            end_time_text.setText("");
                        }
                        setRentTime();
                    }
                } else if (type.equals(DateTimePicker.TYPE_END)) {
                    end_long = date;
                    Config.endTimeLong = end_long;
                    updateTimeText(end_time_text, end_long);
                    setRentTime();
                }
            }
        });
        dialog.setTitle(title);
        dialog.show();
    }

    long start_long, end_long = 0;
    String[] car_sns;
    @InjectView(R.id.address_root)
    RelativeLayout addressRoot;
    @InjectView(R.id.address)
    TextView address;
    int displayPosition = 1;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.select_time);
        ButterKnife.inject(this);
        if (getIntent().hasExtra("noSelectTime") && getIntent().getBooleanExtra("noSelectTime", false)) {
            findViewById(R.id.start_time_root).setEnabled(false);
            findViewById(R.id.end_time_root).setEnabled(false);
            findViewById(R.id.start_time_arrow).setVisibility(View.GONE);
            findViewById(R.id.end_time_arrow).setVisibility(View.GONE);
        }
        if (getIntent().hasExtra(SysConfig.CAR_SN)) {
            car_sn = getIntent().getStringExtra(SysConfig.CAR_SN);
        }
        if (getIntent().hasExtra(SysConfig.CAR_SNS)) {
            car_sns = getIntent().getStringArrayExtra(SysConfig.CAR_SNS);
        }
        car_info_content.setText(getIntent().getStringExtra("CAR_NAME"));
        car_beizhu_content.setText(getIntent().getStringExtra("BEIZHU"));
        if (getIntent().hasExtra("displayPosition")) {
            displayPosition = getIntent().getIntExtra("displayPosition", 1);
        }
        Config.getCoordinates(this, new LocationListener() {
            @Override
            public void locationSuccess(double lat, double lng, String addr) {
//                address.setText(addr);
                currentLat = lat;
                currentLng = lng;
            }
        });
        if (Config.selectLat != 0 && Config.selectLng != 0 && !Config.selectAddress.equals("")) {
            address.setText(Config.selectAddress);
        }
        if (getIntent().hasExtra("start") && getIntent().hasExtra("end")) {
            start_long = (long) (Integer.parseInt(getIntent().getStringExtra("start")) * 1000L);
            end_long = (long) (Integer.parseInt(getIntent().getStringExtra("end")) * 1000L);
            updateTimeText(start_time_text, start_long);
            updateTimeText(end_time_text, end_long);
        }
        if (displayPosition == 1) {
            addressRoot.setVisibility(View.GONE);
        } else {
            addressRoot.setVisibility(View.VISIBLE);
        }
        setRentTime();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureClick();
            }
        });
    }

    @OnClick(R.id.address_root)
    public void addressClick() {
        Intent intent = new Intent(context, SelectAddressActivity.class);
        SharedPreferences citysp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        intent.putExtra("city", citysp.getString("city", "北京"));
        intent.putExtra("lat", currentLat);
        intent.putExtra("lng", currentLng);
        intent.getBooleanExtra("isexpaand", false);
        intent.putExtra("current_address", address.getText().toString());
        startActivityForResult(intent, 154);
    }

    private void updateSure() {
        sure.setEnabled(false);
        if (start_long != 0 && end_long != 0) {
            getAndSetDate();
        }
    }

    boolean isServiceSuccess = false;

    private void getAndSetDate() {
        showProgress(false);
        final ReserveInformationModel.ReserveInformationRequestModel model = new ReserveInformationModel.ReserveInformationRequestModel();
        model.carSn = car_sn;
        model.leaseStart = (int) (start_long / 1000);
        model.leaseEnd = (int) (end_long / 1000);
        CarInterface.ReserveInformationRequest.Builder builder = CarInterface.ReserveInformationRequest.newBuilder();
        builder.setCarSn(model.carSn);
        builder.setLeaseStart(model.leaseStart);
        builder.setLeaseEnd(model.leaseEnd);

        final NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.ReserveInformation_VALUE);
        task.setBusiData(builder.build().toByteArray());
        task.setTag("ReserveInformation");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                try {
                    if (responseData.getRet() == 0) {
                        showResponseCommonMsg(responseData.getResponseCommonMsg());
                        final CarInterface.ReserveInformationResponse response = CarInterface.ReserveInformationResponse.parseFrom(responseData.getBusiData());

                        if (response.getRet() != 0) {
                            isServiceSuccess = false;
                            tv_zuqi.setText("—");
                            tv_zujin.setText("—");
                            tv_baoxian.setText("—");
                            tv_yajin.setText("—");
                            car_more_info.setVisibility(View.VISIBLE);
//                    Toast.makeText(SelectTime.this, "获取数据失败，请重试", Toast.LENGTH_LONG).show();
                            return;
                        }
                        isServiceSuccess = true;
                        tv_zuqi.setText(response.getLeaseTerm());
                        tv_zujin.setText(String.format("%.2f", response.getRent()) + "元");
                        tv_baoxian.setText(String.format("%.2f", response.getInsurance()) + "元");
                        tv_yajin.setText(String.format("%.2f", response.getDeposit()) + "元");
                        car_baoxian_wenhao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!Config.isNetworkConnected(context)) {
                                    Config.showFiledToast(context);
                                    return;
                                }
                                Intent intent = new Intent(context, URLWebView.class);
                                intent.putExtra("url", response.getInsuranceURL());
                                intent.putExtra(SysConfig.TITLE, "保险");
                                context.startActivity(intent);
                            }
                        });
                        car_yajin_wenhao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!Config.isNetworkConnected(context)) {
                                    Config.showFiledToast(context);
                                    return;
                                }
                                Intent intent = new Intent(context, URLWebView.class);
                                intent.putExtra("url", response.getDepositURL());
                                intent.putExtra(SysConfig.TITLE, "押金");
                                context.startActivity(intent);
                            }
                        });
                        car_more_info.setVisibility(View.VISIBLE);
                        if (displayPosition == 0 && address.getText().toString().trim().equals("") && isServiceSuccess) {
                            sure.setEnabled(false);
                        } else {
                            sure.setEnabled(true);
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                dismissProgress();
                tv_zuqi.setText("—");
                tv_zujin.setText("—");
                tv_baoxian.setText("—");
                tv_yajin.setText("—");
                Toast.makeText(SelectTime.this, "获取数据失败，请重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void networkFinish() {
                dismissProgress();
            }
        });

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.select_time_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }

    private void updateTimeText(TextView view, long date) {
//        int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;
//        flag |= DateUtils.FORMAT_24HOUR;
//        view.setText(DateUtils.formatDateTime(context, date, flag));


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String dateString = formatter.format(date);
        view.setText(dateString);
    }

    public void hire(boolean isCancel) {
        showProgress(false);
        OrderFormInterface26.RenterStartPreOrder.Request.Builder builder = OrderFormInterface26.RenterStartPreOrder.Request.newBuilder();
        if (!car_sn.equals("")) {
            builder.addCarIds(car_sn);
        }
        if (car_sns != null) {
            for (int i = 0; i < car_sns.length; i++) {
                builder.addCarIds(car_sns[i]);
            }
        }
        if (displayPosition == 0) {
            builder.setPositionDesc(address.getText().toString());
            UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
            position.setLat(Config.selectLat);
            position.setLng(Config.selectLng);
            builder.setPosition(position);
        }
        builder.setStartTime((int) (start_long / 1000L));
        builder.setEndTime((int) (end_long / 1000L));
        builder.setCancelLastPreOrder(isCancel);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterStartPreOrder_VALUE);
        task.setTag("RenterStartPreOrder");
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        OrderFormInterface26.RenterStartPreOrder.Response response = OrderFormInterface26.RenterStartPreOrder.Response.parseFrom(responseData.getBusiData());

                        showResponseCommonMsg(responseData.getResponseCommonMsg());
//                        if (response.hasMsg() && response.getMsg().length() > 0)
//                        {
//                            showToast(responseData.getToastMsg());
//                            if (response.getTipsType() == 0)//toast
//                            {
//                                showToast(response.getMsg());
//                            }
//                            else
//                            {
//                                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context);
//                                msgBuilder.setMessage(response.getMsg());
//                                msgBuilder.setNeutralButton("知道了", null);
//                                msgBuilder.create().show();
//                            }

//                        }
                        if (response.getRet() == 0) {
                            Config.isSppedIng = false;
                            Config.speedHasAgree = false;

                            if (getIntent().hasExtra("noSelectTime") && getIntent().getBooleanExtra("noSelectTime", false)) {
                                finish();
                                ObserverManager.getObserver(car_sn).observer("", "renting");
                            } else {
                                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                                startActivity(intent);
                                setResult(RESULT_OK);
                                finish();
                            }
//                            Intent intent = new Intent(context, OneToOneWaitActivity.class);
//                            startActivity(intent);
//                            setResult(RESULT_OK);
//                            finish();
                            FilteredCarListActivity.isNeedRefresh = true;
                        } else {
                            sure.setEnabled(false);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                dismissProgress();
            }
        });
    }

//    public void setRentTime() {
//        int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME;
//        flag |= DateUtils.FORMAT_24HOUR;
//        int day = 0;
//        int hour = 0;
//        int minute = 0;
//        long alltime = end_long - start_long + MINUTE_TIMES;
//        if (alltime > DAY_TIMES) {
//            day = (int) ((alltime) / DAY_TIMES);
//            long over_times = ((alltime) - (day * DAY_TIMES));
//            if (over_times > HOUR_TIMES) {
//                hour = (int) (over_times / HOUR_TIMES);
//                if (hour >= 8 && hour < 24) {
//                    alltime -= hour * HOUR_TIMES;
//                    hour = 0;
//                    day += 1;
//                } else {
//                    minute = (int) ((over_times - hour * HOUR_TIMES) / MINUTE_TIMES);
//                    if (minute >= 30) {
//                        hour += 1;
//                        minute -= 30;
//                    }
//                    if (hour >= 8 && hour < 24) {
//                        hour -= 8 * HOUR_TIMES;
//                        day += 1;
//                    }
//                }
//            } else {
//                minute = (int) ((over_times - hour * HOUR_TIMES) / MINUTE_TIMES);
//                if (minute >= 30) {
//                    hour += 1;
//                    minute -= 30;
//                }
//                if (hour >= 8 && hour < 24) {
//                    hour -= 8 * HOUR_TIMES;
//                    day += 1;
//                }
//            }
//        } else {
//            if (alltime > HOUR_TIMES) {
//                hour = (int) (alltime / HOUR_TIMES);
//                MLog.e(tag, "hour > 8 && hour < 24" + (hour > 8 && hour < 24));
//                if (hour >= 8 && hour < 24) {
//                    alltime -= hour * HOUR_TIMES;
//                    hour = 0;
//                    day += 1;
//                } else {
//                    minute = (int) ((alltime - (hour * HOUR_TIMES)) / MINUTE_TIMES);
//                    MLog.e(tag, "minute=" + minute);
//                    if (minute >= 30) {
//                        hour += 1;
//                        minute -= 30;
//                    }
//                    if (hour >= 8 && hour < 24) {
//                        hour -= 8 * HOUR_TIMES;
//                        day += 1;
//                    }
//                }
//            } else {
//                minute = (int) ((alltime - (hour * HOUR_TIMES)) / MINUTE_TIMES);
//                if (minute >= 30) {
//                    hour += 1;
//                    minute -= 30;
//                }
//                if (hour >= 8 && hour < 24) {
//                    hour -= 8 * HOUR_TIMES;
//                    day += 1;
//                }
//            }
//        }
//        String rent_times = "";
//        if (day > 0) {
//            rent_times = day + "天";
//        }
//        if (hour > 0) {
//            rent_times += hour + "小时";
//        }
//        if (minute > 0) {
//            rent_times += minute + "分钟";
//        }
//        rent_time.setText(rent_times);
//    }

    public void setRentTime() {
        long time = end_long - start_long;
        long day = time / 86400000; // 以天数为单位取整
        long hour = time % 86400000 / 3600000; // 以小时为单位取整
        long min = time % 86400000 % 3600000 / 60000; // 以分钟为单位取整
        String string = "";
        if (day > 0) {
            string = "" + day + "天";
        }
        if (hour > 0) {
            string += hour + "小时";
        }
        if (min > 0) {
            string += min + "分钟";
        }
        if (end_long <= start_long) {
            tv_zuqi.setText("—");
            tv_zujin.setText("—");
            tv_baoxian.setText("—");
            tv_yajin.setText("—");
        } else {
            updateSure();
        }
//        if (day > 0 || hour > 0 || min > 0) {
//            rentTitle.setVisibility(View.VISIBLE);
//            rent_time.setVisibility(View.VISIBLE);
//            rent_time.setText(string);
//        } else {
//            rentTitle.setVisibility(View.GONE);
//            rent_time.setVisibility(View.GONE);
//        }
    }

    public void onResume() {
        super.onResume();
        if (!isOnActivityResult) {

            updateSure();
        } else {

            isOnActivityResult = false;
        }
    }

    boolean isOnActivityResult = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 165) {
                setResult(RESULT_OK);
                finish();
            }
            if (requestCode == 154) {
                if (data.getBooleanExtra("current", false)) {
                } else {
                    isOnActivityResult = true;
                    String name = data.getStringExtra("address");
                    Config.selectLat = data.getDoubleExtra("lat", 0);
                    Config.selectLng = data.getDoubleExtra("lng", 0);
                    Config.selectAddress = name;
                    address.setText(name);
                    updateSure();
                }
            }
        }
    }

}
