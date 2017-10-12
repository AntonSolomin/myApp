package com.myApp.myApp;

import javax.persistence.*;
import java.util.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String postSubject;
    private String postBody;
    private int postPrice;
    private Date postCreationDate;
    String postPicUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    public Post() {}

    public Post(User inputUser, String subject, String body, int price, String url) {
        this.postSubject = subject;
        this.postBody = body;
        this.postPrice = price;
        this.user = inputUser;
        this.postPicUrl = url;
        inputUser.addPost(this);
        postCreationDate = new Date();
    }

    public String getPostPicUrl() {
        return postPicUrl;
    }

    public void setPostPicUrl(String postPicUrl) {
        this.postPicUrl = postPicUrl;
    }

    public int getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(int postPrice) {
        this.postPrice = postPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPostSubject() {
        return postSubject;
    }

    public void setPostSubject(String postSubject) {
        this.postSubject = postSubject;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public Date getPostCreationDate() {
        return postCreationDate;
    }

    public void setPostCreationDate(Date postCreationDate) {
        this.postCreationDate = postCreationDate;
    }
}
