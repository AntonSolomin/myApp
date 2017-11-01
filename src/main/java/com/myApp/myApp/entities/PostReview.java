package com.myApp.myApp.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Anton on 01-Nov-17.
 */

@Entity
public class PostReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    public PostReview () {}

    public PostReview (User user, Post post) {
        this.timestamp = new Date();
        this.post = post;
        this.user = user;
        post.addReview(this);
        user.addReview(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
