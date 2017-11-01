package com.myApp.myApp.service;

import com.myApp.myApp.entities.Post;
import com.myApp.myApp.entities.PostReview;
import com.myApp.myApp.repositories.PostRepository;
import com.myApp.myApp.repositories.PostReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Anton on 01-Nov-17.
 */
@Service
public class PostReviewServiceImplementation implements PostReviewService {

    @Autowired
    PostReviewRepository postReviewRepository;

    @Override
    public PostReview save(PostReview postReview) { return postReviewRepository.save(postReview); }

    @Override
    public PostReview findOne(long id) {
        return postReviewRepository.findOne(id);
    }

    @Override
    public List<PostReview> findAll() {
        return postReviewRepository.findAll();
    }
}
