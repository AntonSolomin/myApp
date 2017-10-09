package com.myApp.myApp;

import java.util.List;

public interface PostService {
    Post save(Post post);
    void delete(Post post);
    Post findOne(long id);
    List<Post> findAll();
    boolean editPost(EditPostReceiver inputEditData);

}
