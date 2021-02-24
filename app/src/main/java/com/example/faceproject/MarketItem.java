package com.example.faceproject;

public class MarketItem {
    int no;
    String writerID;
    String writerNickname;
    String writerProfileUrl;
    String msg;
    String msgImage;
    String likesNum;
    String commentsNum;
    String date;
    int favor;

    public MarketItem() {
    }

    public MarketItem(int no, String writerID, String writerNickname, String writerProfileUrl, String msg, String msgImage, String likesNum, String commentsNum, String date,int favor) {
        this.no = no;
        this.writerID = writerID;
        this.writerNickname = writerNickname;
        this.writerProfileUrl = writerProfileUrl;
        this.msg = msg;
        this.msgImage = msgImage;
        this.likesNum = likesNum;
        this.commentsNum = commentsNum;
        this.date = date;
        this.favor = favor;
    }
}