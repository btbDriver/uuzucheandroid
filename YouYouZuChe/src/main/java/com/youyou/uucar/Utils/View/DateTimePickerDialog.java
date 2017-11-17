package com.youyou.uucar.Utils.View;

import java.util.Calendar;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateUtils;

import com.youyou.uucar.Utils.Support.MLog;

public class DateTimePickerDialog extends AlertDialog implements
        OnClickListener
{
    private DateTimePicker mDateTimePicker;
    private Calendar mDate = Calendar.getInstance();
    private OnDateTimeSetListener mOnDateTimeSetListener;

    @SuppressWarnings("deprecation")
    public DateTimePickerDialog(Context context, long date, String type)
    {
        super(context);
        mDateTimePicker = new DateTimePicker(context, date, type);
        setView(mDateTimePicker);
        /*
         *实现接口，实现里面的方法
         */
        mDateTimePicker.setOnDateTimeChangedListener(new DateTimePicker.OnDateTimeChangedListener()
        {
            @Override
            public void onDateTimeChanged(DateTimePicker view, int year, int month, int day, int hour, int minute)
            {
                mDate.set(Calendar.YEAR, year);
                mDate.set(Calendar.MONTH, month);
                mDate.set(Calendar.DAY_OF_MONTH, day);
                mDate.set(Calendar.HOUR_OF_DAY, hour);
                mDate.set(Calendar.MINUTE, minute);
                mDate.set(Calendar.SECOND, 0);
                /**
                 * 更新日期
                 */
                updateTitle(mDate.getTimeInMillis());
            }
        });
        setButton("确定", this);
        setButton2("取消", (OnClickListener) null);
        mDate.setTimeInMillis(date);
        updateTitle(mDate.getTimeInMillis());
        mDateTimePicker.onDateTimeChanged();
    }

    public String tag = "DateTimePickerDialog";

    /*
     *接口回調
     *控件 秒数
     */
    public interface OnDateTimeSetListener
    {
        void OnDateTimeSet(AlertDialog dialog, long date);
    }

    /**
     * 更新对话框日期
     *
     * @param date
     */
    private void updateTitle(long date)
    {
//        int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE
//                | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_TIME;
//        setTitle(DateUtils.formatDateTime(this.getContext(), date, flag));
    }

    /*
     * 对外公开方法让Activity实现
     */
    public void setOnDateTimeSetListener(OnDateTimeSetListener callBack)
    {
        mOnDateTimeSetListener = callBack;
    }

    public void onClick(DialogInterface arg0, int arg1)
    {
        if (mOnDateTimeSetListener != null)
        {
            MLog.e(tag, "onClick  year = " + mDate.get(Calendar.YEAR) + "  month = " + mDate.get(Calendar.MONTH) + "    day = " + mDate.get(Calendar.DAY_OF_MONTH) + "   hour  =  " + mDate.get(Calendar.HOUR_OF_DAY) + "  minute  = " + mDate.get(Calendar.MINUTE));
            mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis());
        }
    }
}