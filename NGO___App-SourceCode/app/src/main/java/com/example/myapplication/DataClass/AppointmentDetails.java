package com.example.myapplication.DataClass;

import java.util.Objects;

public class AppointmentDetails {

    String ngoEmail,ngoName;
    String userEmail,userName;
    String date,meetingcode,purpose;

    public AppointmentDetails() {
    }

    public AppointmentDetails(String ngoEmail, String ngoName, String userEmail, String userName, String date, String meetingcode, String purpose) {
        this.ngoEmail = ngoEmail;
        this.ngoName = ngoName;
        this.userEmail = userEmail;
        this.userName = userName;
        this.date = date;
        this.meetingcode = meetingcode;
        this.purpose = purpose;
    }

    public String getNgoEmail() {
        return ngoEmail;
    }

    public void setNgoEmail(String ngoEmail) {
        this.ngoEmail = ngoEmail;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMeetingcode() {
        return meetingcode;
    }

    public void setMeetingcode(String meetingcode) {
        this.meetingcode = meetingcode;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
