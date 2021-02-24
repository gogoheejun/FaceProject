package com.example.faceproject;

public class MarketItem {
    int no;
    String nickname;
    String msg;
    String likes;
    String file;
    int favor;
    String date;

    public MarketItem() {
    }

    public MarketItem(int no, String name,String msg, String price, String file, int favor, String date) {
        this.no = no;
        this.nickname = name;
        this.msg = msg;
        this.likes = price;
        this.file = file;
        this.favor = favor;
        this.date = date;
    }
}
