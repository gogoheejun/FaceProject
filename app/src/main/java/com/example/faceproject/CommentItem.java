package com.example.faceproject;

public class CommentItem {
    String writerId;
    String writerNickname;
    String writerProfileUrl;
    String msg;
    String parentNo;

    public CommentItem() {
    }

    public CommentItem(String writerId, String writerNickname, String writerProfileUrl, String msg, String parentNo) {
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.writerProfileUrl = writerProfileUrl;
        this.msg = msg;
        this.parentNo = parentNo;
    }
}
