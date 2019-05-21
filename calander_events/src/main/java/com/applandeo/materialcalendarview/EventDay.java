package com.applandeo.materialcalendarview;

import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.RestrictTo;

import com.applandeo.materialcalendarview.utils.DateUtils;

import java.util.Calendar;

/**
 * This class represents an event of a day. An instance of this class is returned when user click
 * a day cell. This class can be overridden to make calendar more functional. A list of instances of
 * this class can be passed to CalendarView object using setEvents() method.
 * <p>
 * Created by Mateusz Kornakiewicz on 23.05.2017.
 */

public class EventDay {
    private Calendar mDay;
    private Object mDrawable;
    private boolean mIsDisabled;
    private AppointmentInfo appointmentInfo;

    /**
     * @param day Calendar object which represents a date of the event
     */
    public EventDay(Calendar day) {
        mDay = day;
        this.appointmentInfo = null;
    }



    /**
     * @param day Calendar object which represents a date of the event
     */
    public EventDay(Calendar day,AppointmentInfo appointmentInfo) {
        mDay = day;
        this.appointmentInfo = appointmentInfo;
    }

    /**
     * @param day      Calendar object which represents a date of the event
     * @param drawable Drawable resource which will be displayed in a day cell
     */
    public EventDay(Calendar day, @DrawableRes int drawable,AppointmentInfo appointmentInfo) {
        DateUtils.setMidnight(day);
        mDay = day;
        mDrawable = drawable;
        this.appointmentInfo = appointmentInfo;
    }

    /**
     * @param day      Calendar object which represents a date of the event
     * @param drawable Drawable which will be displayed in a day cell
     */
    public EventDay(Calendar day, Drawable drawable,AppointmentInfo appointmentInfo) {
        DateUtils.setMidnight(day);
        mDay = day;
        mDrawable = drawable;
        this.appointmentInfo = appointmentInfo;
    }


    /**
     * @return An image resource which will be displayed in the day row
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Object getImageDrawable() {
        return mDrawable;
    }


    /**
     * @return Calendar object which represents a date of current event
     */
    public Calendar getCalendar() {
        return mDay;
    }


    /**
     * @return Boolean value if day is not disabled
     */
    public boolean isEnabled() {
        return !mIsDisabled;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setEnabled(boolean enabled) {
        mIsDisabled = enabled;
    }

    public AppointmentInfo getAppointmentInfo() {
        return appointmentInfo;
    }

    public EventDay setAppointmentInfo(AppointmentInfo appointmentInfo) {
        this.appointmentInfo = appointmentInfo;
        return this;
    }
}
