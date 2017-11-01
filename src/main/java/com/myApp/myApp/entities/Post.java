package com.myApp.myApp.entities;

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
    private int upvotes;

    @ElementCollection
    @Column(name = "urls")
    List<String> postPicUrl = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    public Post() {}

    public Post(User inputUser, String subject, String body, int price, List<String> urls) {
        this.postSubject = subject;
        this.postBody = body;
        this.postPrice = price;
        this.user = inputUser;
        this.postPicUrl = urls;
        this.upvotes = 0;
        inputUser.addPost(this);
        postCreationDate = new Date();
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void incrementUpvotes () {
        this.upvotes ++;
    }

    public void decrementUpvotes () {
        this.upvotes --;
    }

    public  List<String> getPostPicUrl() {
        return postPicUrl;
    }

    public void addToPostPicsUrls(String postPicUrl) {
        this.postPicUrl.add(postPicUrl);
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
