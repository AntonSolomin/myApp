package com.myApp.myApp.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    @OneToMany(mappedBy="user", fetch=FetchType.EAGER)
    private List<Post> posts = new ArrayList<>();

    @ElementCollection
    @Column(name = "likedPosts")
    private List<Long> likedPosts = new ArrayList<>();

    public User(){}

    public User(String inputfirstName, String inputLastname, String inputUserName, String inputpassword) {
        this.firstName = inputfirstName;
        this.lastName = inputLastname;
        this.userName = inputUserName;
        this.password = inputpassword;
    }

    public List<Long> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Long> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public void addToLikedPosts(Long postId) {
        this.likedPosts.add(postId);
    }

    public void deleteFromLikedPosts (Long postId) {
        this.likedPosts.remove(postId);
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
