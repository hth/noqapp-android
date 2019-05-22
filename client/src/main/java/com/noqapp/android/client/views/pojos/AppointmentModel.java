package com.noqapp.android.client.views.pojos;

public class AppointmentModel {
    private String time;
    private boolean isBooked;

    public String getTime() {
        return time;
    }

    public AppointmentModel setTime(String time) {
        this.time = time;
        return this;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public AppointmentModel setBooked(boolean booked) {
        isBooked = booked;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppointmentModel{");
        sb.append("time='").append(time).append('\'');
        sb.append(", isBooked=").append(isBooked);
        sb.append('}');
        return sb.toString();
    }
}
