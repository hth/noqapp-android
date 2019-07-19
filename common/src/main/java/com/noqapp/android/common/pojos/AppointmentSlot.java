package com.noqapp.android.common.pojos;

public class AppointmentSlot {
    private String timeSlot;
    private boolean isBooked;

    public String getTimeSlot() {
        return timeSlot;
    }

    public AppointmentSlot setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
        return this;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public AppointmentSlot setBooked(boolean booked) {
        isBooked = booked;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppointmentModel{");
        sb.append("timeSlot='").append(timeSlot).append('\'');
        sb.append(", isBooked=").append(isBooked);
        sb.append('}');
        return sb.toString();
    }
}
