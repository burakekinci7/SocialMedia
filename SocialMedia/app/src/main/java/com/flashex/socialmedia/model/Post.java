package com.flashex.socialmedia.model;

public class Post {
    public String email;
    public String comment;
    public String downloadUri;

    public Post(String email, String comment, String downloadUri) {
        this.email = email;
        this.comment = comment;
        this.downloadUri = downloadUri;
    }
}
