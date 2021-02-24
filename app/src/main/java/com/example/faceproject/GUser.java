package com.example.faceproject;

public class GUser {
    static long userId;
    static String nickname;
    static String profileUrl;

    public GUser(long userId, String nickname, String profileUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }
}
