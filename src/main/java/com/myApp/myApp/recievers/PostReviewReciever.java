package com.myApp.myApp.recievers;

/**
 * Created by Anton on 01-Nov-17.
 */
public class PostReviewReciever {

    private String body = null;
    private String subject = null;
    private Integer evaluation = null;

    public PostReviewReciever() {}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Integer evaluation) {
        this.evaluation = evaluation;
    }
}
