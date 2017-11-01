package com.myApp.myApp.service;

import com.myApp.myApp.recievers.EditPostReceiver;
import com.myApp.myApp.entities.Post;
import com.myApp.myApp.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImplementation implements PostService {
    @Autowired
    PostRepository postRepository;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        postRepository.removeByPostId(post.getId());
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
    public boolean editPost (EditPostReceiver inputEditData){

        Long postId = inputEditData.getId();

        if (postId == null) {
            return false;
        }

        Post postToEdit = postRepository.findOne(postId);
        if (postToEdit == null) {
            return false;
        }

        boolean isEdited = false;

        String postBody = inputEditData.getPostBody();
        if (postBody != null) {
            postToEdit.setPostBody(postBody);
            isEdited = true;
        }

        String postSubject = inputEditData.getPostSubject();
        if (postSubject != null) {
            postToEdit.setPostSubject(postSubject);
            isEdited = true;
        }

        Integer inputPrice = inputEditData.getPostPrice();
        if (inputPrice != null) {
            postToEdit.setPostPrice(inputPrice);
            isEdited = true;
        }

        postRepository.save(postToEdit);
        return isEdited;
    }

    @Override
    public void votePost(Post post, boolean vote) {
        if(vote) {
            post.incrementUpvotes();
        } else {
            post.decrementUpvotes();
        }
        postRepository.save(post);
    }
}
