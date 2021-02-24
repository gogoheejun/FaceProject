package com.example.faceproject;

public class CommentItem {
    String writerId;
    String writerNickname;
    String writerProfile;
    String commentMsg;
    String parentNo;

    public CommentItem() {
    }

    public CommentItem(String writerId, String writerNickname, String writerProfile, String commentMsg, String parentNo) {
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.writerProfile = writerProfile;
        this.commentMsg = commentMsg;
        this.parentNo = parentNo;
    }
}
