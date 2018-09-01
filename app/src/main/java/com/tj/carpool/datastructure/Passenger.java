package com.tj.carpool.datastructure;

import java.io.Serializable;
import java.util.Date;

public class Passenger implements Serializable {

    private int ID;
    private String Name;
    private String Gender;
    private String PhoneNum;
    private String UserName;
    private String Password;
    //private String Regtime;

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getGender() {
        return Gender;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public String getUsername() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }

//    public String getRegtime() {
//        return Regtime;
//    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public void setUserName(String username) {
        UserName = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

//    public void setRegtime(String regtime) {
//        Regtime = regtime;
//    }
}
