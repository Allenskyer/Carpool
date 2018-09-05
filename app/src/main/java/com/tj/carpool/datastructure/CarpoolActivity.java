package com.tj.carpool.datastructure;

import java.sql.Timestamp;

public class CarpoolActivity {
    private int id;
    private Timestamp createTime;
    private int regUserId;
    private int state;
    private int passengerNum;
    private int targetNum;
    private int driverId;
    private Location departureLoc, targetLoc;
    private long departureTime, bearTime;

    public int getId() {
        return id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public int getRegUserId() {
        return regUserId;
    }

    public int getState() {
        return state;
    }

    public int getPassengerNum() {
        return passengerNum;
    }

    public int getTargetNum() {
        return targetNum;
    }

    public int getDriverId() {
        return driverId;
    }

    public Location getDepartureLoc() {
        return departureLoc;
    }

    public Location getTargetLoc() {
        return targetLoc;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public long getBearTime() {
        return bearTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setRegUserId(int regUserId) {
        this.regUserId = regUserId;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setPassengerNum(int passengerNum) {
        this.passengerNum = passengerNum;
    }

    public void setTargetNum(int targetNum) {
        this.targetNum = targetNum;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public void setDepartureLoc(Location departureLoc) {
        this.departureLoc = departureLoc;
    }

    public void setTargetLoc(Location targetLoc) {
        this.targetLoc = targetLoc;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public void setBearTime(long bearTime) {
        this.bearTime = bearTime;
    }
}
