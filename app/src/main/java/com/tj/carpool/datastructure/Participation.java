package com.tj.carpool.datastructure;

public class Participation {
    private int id;
    private int userId;
    private int activityId;
    private float payment;
    private int star, number, state;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getActivityId() {
        return activityId;
    }

    public float getPayment() {
        return payment;
    }

    public int getStar() {
        return star;
    }

    public int getNumber() {
        return number;
    }

    public int getState() {
        return state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setState(int state) {
        this.state = state;
    }
}
