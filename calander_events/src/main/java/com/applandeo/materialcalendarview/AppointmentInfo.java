package com.applandeo.materialcalendarview;

public class AppointmentInfo {
    private String patientName;
    private String info;
    private String appointmentDate;
    private String appointmentTime;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppointmentInfo{");
        sb.append("patientName='").append(patientName).append('\'');
        sb.append(", info='").append(info).append('\'');
        sb.append(", appointmentDate='").append(appointmentDate).append('\'');
        sb.append(", appointmentTime='").append(appointmentTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
