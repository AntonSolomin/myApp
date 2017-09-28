package com.myApp.myApp;

import java.util.List;
import java.util.Map;

public interface PostService {
    Post save(Post post);
    Post findOne(long id);
    List<Post> findAll();
    boolean editPost(Map<String,Object> inputEditData);
}
