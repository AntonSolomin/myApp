package com.myApp.myApp.service;

import com.myApp.myApp.entities.Post;
import com.myApp.myApp.entities.PostReview;
import com.myApp.myApp.recievers.EditPostReceiver;

import java.util.List;

/**
 * Created by Anton on 01-Nov-17.
 */
public interface PostReviewService {
    PostReview save (PostReview postReview);
    PostReview findOne(long id);
    List<PostReview> findAll();
}
