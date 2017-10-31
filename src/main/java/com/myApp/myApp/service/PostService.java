package com.myApp.myApp.service;

import com.myApp.myApp.recievers.EditPostReceiver;
import com.myApp.myApp.entities.Post;

import java.util.List;

public interface PostService {
    Post save(Post post);
    void delete(Post post);
    Post findOne(long id);
    List<Post> findAll();
    boolean editPost(EditPostReceiver inputEditData);

}
