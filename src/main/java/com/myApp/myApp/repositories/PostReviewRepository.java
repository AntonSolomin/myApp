package com.myApp.myApp.repositories;

import com.myApp.myApp.entities.PostReview;
import com.myApp.myApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Anton on 01-Nov-17.
 */
public interface PostReviewRepository extends JpaRepository<PostReview, Long> {
    PostReview findOne (long id);
}
