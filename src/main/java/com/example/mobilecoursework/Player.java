package com.example.mobilecoursework;

public class Player{
    private String userName;
    private long lev1score;
    private long lev2score;

    public Player() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getLev1score() {
        return lev1score;
    }

    public void setLev1score(long lev1score) {
        this.lev1score = lev1score;
    }

    public long getLev2score() {
        return lev2score;
    }

    public void setLev2score(long lev2score) {
        this.lev2score = lev2score;
    }
}
