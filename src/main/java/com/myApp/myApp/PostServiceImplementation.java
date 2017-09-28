package com.myApp.myApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostServiceImplementation implements PostService {
    @Autowired
    PostRepository postRepository;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post findOne(long id) {
        return postRepository.findOne(id);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public boolean editPost (Map<String,Object> inputEditData){

        Object x = inputEditData.get("inputPostId");

        Long inputPostId = null;

        // SECOND STATEMENT: if x is [null] instanceof returns false
        if (x != null && x instanceof Integer) {
            inputPostId = new Long((Integer) x);
        } else {
            return false;
        }

        Post postEdited = postRepository.findOne(inputPostId);
        if (postEdited == null) {
            return false;
        }

        boolean isEdited = false;

        Object postBody = inputEditData.get("inputPostBody");
        if (postBody instanceof String) {
            String inputPostBody = (String) postBody;
            postEdited.setPostBody(inputPostBody);
            isEdited = true;
        }

        Object postSubject = inputEditData.get("inputPostSubject");
        if (postSubject instanceof String) {
            String inputPostSubject = (String) postSubject;
            postEdited.setPostSubject(inputPostSubject);
            isEdited = true;
        }

        Object inputPrice = inputEditData.get("inputPrice");
        if (inputPrice instanceof Integer) {
            Integer inputPostPrice = (Integer) inputPrice;
            postEdited.setPostPrice(inputPostPrice);
            isEdited = true;
        }

        postRepository.save(postEdited);
        return isEdited;
    }
}
