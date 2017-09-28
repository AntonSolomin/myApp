package com.myApp.myApp;

import java.util.Map;

/**
 * Created by Anton on 28.09.2017.
 */
public class EditPostReceiver {

    private Long id = null;
    private String postSubject = null;
    private String postBody = null;
    private Integer postPrice = null;

    public EditPostReceiver() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(Integer postPrice) {
        this.postPrice = postPrice;
    }
}
