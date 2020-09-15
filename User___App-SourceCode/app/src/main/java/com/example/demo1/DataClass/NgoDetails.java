package com.example.demo1.DataClass;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class NgoDetails {

    String name="Dummy",address="Dummy",phone_number="ummy",email="Dummy",latitude="0",longitude="0",url="Dummy";

    public NgoDetails() { }

    public NgoDetails(String name, String address, String phone_number, String email, String latitude, String longitude,String url) {
        this.name = name;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url=url;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NgoDetails that = (NgoDetails) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phone_number, that.phone_number) &&
                Objects.equals(email, that.email) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(url,that.url);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(name, address, phone_number, email, latitude, longitude,url);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String geturl(){
        return url;
    }

    public  void seturl(String url){
        this.url=url;
    }

    public String toString(){
        return "Name : "+name+"\nemail : "+email+"\nLongitude : "+longitude+"\n";
    }
}

