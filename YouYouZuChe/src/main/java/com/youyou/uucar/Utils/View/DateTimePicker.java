package com.youyou.uucar.Utils.View;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.youyou.uucar.R;
import com.youyou.uucar.Utils.Support.MLog;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class DateTimePicker extends FrameLayout {
    private static final boolean DEFAULT_ENABLE_STATE = true;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final int HOURS_IN_ALL_DAY = 24;
    private static final int DAYS_IN_ALL_WEEK = 7;
    private static final int DATE_SPINNER_MIN_VAL = 0;
    private static final int DATE_SPINNER_MAX_VAL = DAYS_IN_ALL_WEEK - 1;
    private static final int HOUR_SPINNER_MIN_VAL_24_HOUR_VIEW = 0;
    private static final int HOUR_SPINNER_MAX_VAL_24_HOUR_VIEW = 23;
    private static final int HOUR_SPINNER_MIN_VAL_12_HOUR_VIEW = 1;
    private static final int HOUR_SPINNER_MAX_VAL_12_HOUR_VIEW = 12;
    private static final int MINUT_SPINNER_MIN_VAL = 0;
    private static final int MINUT_SPINNER_MAX_VAL = 59;
    private static final int AMPM_SPINNER_MIN_VAL = 0;
    private static final int AMPM_SPINNER_MAX_VAL = 1;
    private NumberPicker mDateSpinner;
    private NumberPicker mHourSpinner;
    private NumberPicker mMinuteSpinner;
    private NumberPicker mAmPmSpinner;
    private Calendar mDate;
    private String[] mDateDisplayValues = new String[DAYS_IN_ALL_WEEK];
    private boolean mIsAm;
    private boolean mIs24HourView;
    private boolean mIsEnabled = DEFAULT_ENABLE_STATE;
    private boolean mInitialising;
    private OnDateTimeChangedListener mOnDateTimeChangedListener;
    private NumberPicker.OnValueChangeListener mOnDateChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mDate.add(Calendar.DAY_OF_YEAR, newVal - oldVal);
            onDateTimeChanged();
            updateHourControl();
        }
    };
    private NumberPicker.OnValueChangeListener mOnHourChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mDate.set(Calendar.HOUR_OF_DAY, mHourSpinner.getValue());
            onDateTimeChanged();
            updateMinuteControl();
        }
    };
    private NumberPicker.OnValueChangeListener mOnMinuteChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            // int minValue = mMinuteSpinner.getMinValue();
            // int maxValue = mMinuteSpinner.getMaxValue();
            // int offset = 0;
            // if(oldVal == maxValue && newVal == minValue)
            // {
            // offset += 1;
            // }
            // else if(oldVal == minValue && newVal == maxValue)
            // {
            // offset -= 1;
            // }
            // if(offset != 0)
            // {
            // mDate.add(Calendar.HOUR_OF_DAY,offset);
            // mHourSpinner.setValue(getCurrentHour());
            // updateDateControl();
            // int newHour = getCurrentHourOfDay();
            // if(newHour >= HOURS_IN_HALF_DAY)
            // {
            // mIsAm = false;
            // updateAmPmControl();
            // }
            // else
            // {
            // mIsAm = true;
            // updateAmPmControl();
            // }
            // }
            mDate.set(Calendar.MINUTE, newVal);
            onDateTimeChanged();
        }
    };
    private NumberPicker.OnValueChangeListener mOnAmPmChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mIsAm = !mIsAm;
            if (mIsAm) {
                mDate.add(Calendar.HOUR_OF_DAY, -HOURS_IN_HALF_DAY);
            } else {
                mDate.add(Calendar.HOUR_OF_DAY, HOURS_IN_HALF_DAY);
            }
            updateAmPmControl();
            onDateTimeChanged();
        }
    };

    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, int year, int month, int dayOfMonth, int hourOfDay, int minute);
    }

    public DateTimePicker(Context context, String type) {
        this(context, System.currentTimeMillis(), type);
    }

    public DateTimePicker(Context context, long date, String type) {
        this(context, date,/* DateFormat.is24HourFormat(context) */true, type);
    }

    public DateTimePicker(Context context, long date, boolean is24HourView, String type) {
        super(context);
        setDate(context, date, is24HourView, type);
    }

    long currentDateTime;
    public static final String TYPE_START = "start";
    public static final String TYPE_END = "end";

    public String type;

    public void setDate(Context context, long date, boolean is24HourView, String type) {
        this.type = type;
        currentDateTime = date;
        mDate = Calendar.getInstance();
        mInitialising = true;
        mIsAm = getCurrentHourOfDay() >= HOURS_IN_HALF_DAY;
        inflate(context, R.layout.datetime_picker, this);
        mDateSpinner = (NumberPicker) findViewById(R.id.date);
        mDateSpinner.setMinValue(DATE_SPINNER_MIN_VAL);

        if (type.equals(TYPE_START)) {
            mDateSpinner.setMaxValue(DAYS_IN_ALL_WEEK * 4 - 1);
        } else {

            mDateSpinner.setMaxValue(DATE_SPINNER_MAX_VAL);
        }
        mDateSpinner.setWrapSelectorWheel(false);
        mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);
        mHourSpinner = (NumberPicker) findViewById(R.id.hour2);
        mHourSpinner.setOnValueChangedListener(mOnHourChangedListener);
        mMinuteSpinner = (NumberPicker) findViewById(R.id.minute);
        mMinuteSpinner.setMinValue(MINUT_SPINNER_MIN_VAL);
        mMinuteSpinner.setMaxValue(MINUT_SPINNER_MAX_VAL);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);
        String[] stringsForAmPm = new DateFormatSymbols().getAmPmStrings();
        mAmPmSpinner = (NumberPicker) findViewById(R.id.amPm2);
        mAmPmSpinner.setMinValue(AMPM_SPINNER_MIN_VAL);
        mAmPmSpinner.setMaxValue(AMPM_SPINNER_MAX_VAL);
        mAmPmSpinner.setDisplayedValues(stringsForAmPm);
        mAmPmSpinner.setOnValueChangedListener(mOnAmPmChangedListener);
        // update controls to initial state
        updateDateControl();
        updateHourControl();
        updateAmPmControl();
        set24HourView(is24HourView);
        // set to current time
        setCurrentDate(date);
        setEnabled(isEnabled());
        if (type.equals(TYPE_START)) {
            if (mHourSpinner.getValue() >= 20 || mHourSpinner.getValue() < 5) {
                mDateSpinner.setValue(1);
                mDate.add(Calendar.DAY_OF_YEAR, 1);
                updateHourControl();
                mHourSpinner.setValue(9);
                mDate.set(Calendar.HOUR_OF_DAY, mHourSpinner.getValue());
                mMinuteSpinner.setValue(0);
                mDate.set(Calendar.MINUTE, 0);
            } else {
                mHourSpinner.setValue(mHourSpinner.getValue() + 3);
                mDate.set(Calendar.HOUR_OF_DAY, mHourSpinner.getValue());
                updateMinuteControl();
            }
        } else if (type.equals(TYPE_END)) {
            mDateSpinner.setValue(1);
            mDate.add(Calendar.DAY_OF_YEAR, 1);
            updateHourControl();
        }
        // set the content descriptions
        mInitialising = false;

    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mIsEnabled == enabled) {
            return;
        }
        super.setEnabled(enabled);
        mDateSpinner.setEnabled(enabled);
        mMinuteSpinner.setEnabled(enabled);
        mHourSpinner.setEnabled(enabled);
        mAmPmSpinner.setEnabled(enabled);
        mIsEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    /**
     * Get the current date in millis
     *
     * @return the current date in millis
     */
    public long getCurrentDateInTimeMillis() {
        return mDate.getTimeInMillis();
    }

    /**
     * Set the current date
     *
     * @param date The current date in millis
     */
    public void setCurrentDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        setCurrentDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    /**
     * Set the current date
     *
     * @param year       The current year
     * @param month      The current month
     * @param dayOfMonth The current dayOfMonth
     * @param hourOfDay  The current hourOfDay
     * @param minute     The current minute
     */
    public void setCurrentDate(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        setCurrentYear(year);
        setCurrentMonth(month);
        setCurrentDay(dayOfMonth);
        setCurrentHour(hourOfDay);
        setCurrentMinute(minute);
    }

    /**
     * Get current year
     *
     * @return The current year
     */
    public int getCurrentYear() {
        return mDate.get(Calendar.YEAR);
    }

    /**
     * Set current year
     *
     * @param year The current year
     */
    public void setCurrentYear(int year) {
        if (!mInitialising && year == getCurrentYear()) {
            return;
        }
        mDate.set(Calendar.YEAR, year);
        updateDateControl();
        onDateTimeChanged();
    }

    /**
     * Get current month in the year
     *
     * @return The current month in the year
     */
    public int getCurrentMonth() {
        return mDate.get(Calendar.MONTH);
    }

    /**
     * Set current month in the year
     *
     * @param month The month in the year
     */
    public void setCurrentMonth(int month) {
        if (!mInitialising && month == getCurrentMonth()) {
            return;
        }
        mDate.set(Calendar.MONTH, month);
        updateDateControl();
        onDateTimeChanged();
    }

    /**
     * Get current day of the month
     *
     * @return The day of the month
     */
    public int getCurrentDay() {
        return mDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Set current day of the month
     *
     * @param dayOfMonth The day of the month
     */
    public void setCurrentDay(int dayOfMonth) {
        if (!mInitialising && dayOfMonth == getCurrentDay()) {
            return;
        }
        mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateControl();
        onDateTimeChanged();
    }

    /**
     * Get current hour in 24 hour mode, in the range (0~23)
     *
     * @return The current hour in 24 hour mode
     */
    public int getCurrentHourOfDay() {
        return mDate.get(Calendar.HOUR_OF_DAY);
    }

    private int getCurrentHour() {
        if (mIs24HourView) {
            return getCurrentHourOfDay();
        } else {
            int hour = getCurrentHourOfDay();
            if (hour > HOURS_IN_HALF_DAY) {
                return hour - HOURS_IN_HALF_DAY;
            } else {
                return hour == 0 ? HOURS_IN_HALF_DAY : hour;
            }
        }
    }

    /**
     * Set current hour in 24 hour mode, in the range (0~23)
     *
     * @param hourOfDay
     */
    public void setCurrentHour(int hourOfDay) {
        if (!mInitialising && hourOfDay == getCurrentHourOfDay()) {
            return;
        }
        mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        if (!mIs24HourView) {
            if (hourOfDay >= HOURS_IN_HALF_DAY) {
                mIsAm = false;
                if (hourOfDay > HOURS_IN_HALF_DAY) {
                    hourOfDay -= HOURS_IN_HALF_DAY;
                }
            } else {
                mIsAm = true;
                if (hourOfDay == 0) {
                    hourOfDay = HOURS_IN_HALF_DAY;
                }
            }
            updateAmPmControl();
        }
        mHourSpinner.setValue(hourOfDay);
        onDateTimeChanged();
    }

    /**
     * Get currentMinute
     *
     * @return The Current Minute
     */
    public int getCurrentMinute() {
        return mDate.get(Calendar.MINUTE);
    }

    /**
     * Set current minute
     */
    public void setCurrentMinute(int minute) {
        if (!mInitialising && minute == getCurrentMinute()) {
            return;
        }
        mMinuteSpinner.setValue(minute);
        mDate.set(Calendar.MINUTE, minute);
        onDateTimeChanged();
    }

    /**
     * @return true if this is in 24 hour view else false.
     */
    public boolean is24HourView() {
        return mIs24HourView;
    }

    /**
     * Set whether in 24 hour or AM/PM mode.
     *
     * @param is24HourView True for 24 hour mode. False for AM/PM mode.
     */
    public void set24HourView(boolean is24HourView) {
        if (mIs24HourView == is24HourView) {
            return;
        }
        mIs24HourView = is24HourView;
        mAmPmSpinner.setVisibility(is24HourView ? View.GONE : View.VISIBLE);
        int hour = getCurrentHourOfDay();
        updateHourControl();
        setCurrentHour(hour);
        updateAmPmControl();
    }

    private void updateDateControl() {
        MLog.e("picker", "updateDateControl");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mDate.getTimeInMillis());
        // cal.add(Calendar.DAY_OF_YEAR, -DAYS_IN_ALL_WEEK / 2 - 1);
        mDateSpinner.setDisplayedValues(null);
        if (type.equals(TYPE_START)) {
            mDateDisplayValues = new String[DAYS_IN_ALL_WEEK * 4];

            for (int i = 0; i < DAYS_IN_ALL_WEEK * 4; i++) {
                mDateDisplayValues[i] = (String) DateFormat.format("MM月dd日", cal);
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            mDateSpinner.setDisplayedValues(mDateDisplayValues);
            mDateSpinner.setValue(0);
            mDateSpinner.invalidate();
        } else {
            mDateDisplayValues = new String[DAYS_IN_ALL_WEEK];
            for (int i = 0; i < DAYS_IN_ALL_WEEK; i++) {
                mDateDisplayValues[i] = (String) DateFormat.format("MM月dd日", cal);
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            mDateSpinner.setDisplayedValues(mDateDisplayValues);
            mDateSpinner.setValue(0);
            mDateSpinner.invalidate();
        }
    }

    private void updateAmPmControl() {
        if (mIs24HourView) {
            mAmPmSpinner.setVisibility(View.GONE);
        } else {
            int index = mIsAm ? Calendar.AM : Calendar.PM;
            mAmPmSpinner.setValue(index);
            mAmPmSpinner.setVisibility(View.VISIBLE);
        }
    }

    public String tag = "DateTimePicker";

    private void updateHourControl() {
        if (mIs24HourView) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(currentDateTime);
            if (mDateSpinner.getValue() == 0) {
                if (mHourSpinner.getValue() > cal.get(Calendar.HOUR_OF_DAY)) {
                    mHourSpinner.setValue(0);
                }
                mHourSpinner.setMinValue(cal.get(Calendar.HOUR_OF_DAY));
                mHourSpinner.setMaxValue(HOUR_SPINNER_MAX_VAL_24_HOUR_VIEW);
            } else {
                mHourSpinner.setMinValue(HOUR_SPINNER_MIN_VAL_24_HOUR_VIEW);
                mHourSpinner.setMaxValue(HOUR_SPINNER_MAX_VAL_24_HOUR_VIEW);
            }
            updateMinuteControl();
        } else {
            mHourSpinner.setMinValue(HOUR_SPINNER_MIN_VAL_12_HOUR_VIEW);
            mHourSpinner.setMaxValue(HOUR_SPINNER_MAX_VAL_12_HOUR_VIEW);
        }
    }

    public void updateMinuteControl() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentDateTime);
        MLog.e(tag, "mHourSpinner.getValue() = " + mHourSpinner.getValue());
        if (mDateSpinner.getValue() == 0 && mHourSpinner.getValue() == cal.get(Calendar.HOUR_OF_DAY)) {
            if (mMinuteSpinner.getValue() > cal.get(Calendar.MINUTE)) {
                mMinuteSpinner.setValue(0);
            }
            mMinuteSpinner.setMinValue(cal.get(Calendar.MINUTE));
            mMinuteSpinner.setMaxValue(MINUT_SPINNER_MAX_VAL);
        } else {
            mMinuteSpinner.setMinValue(MINUT_SPINNER_MIN_VAL);
            mMinuteSpinner.setMaxValue(MINUT_SPINNER_MAX_VAL);
        }
    }

    /**
     * Set the callback that indicates the 'Set' button has been pressed.
     *
     * @param callback the callback, if null will do nothing
     */
    public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
        mOnDateTimeChangedListener = callback;
    }

    public void onDateTimeChanged() {
        if (mOnDateTimeChangedListener != null) {
            mOnDateTimeChangedListener.onDateTimeChanged(this, getCurrentYear(), getCurrentMonth(), getCurrentDay(), getCurrentHourOfDay(), getCurrentMinute());
        }
    }
}
