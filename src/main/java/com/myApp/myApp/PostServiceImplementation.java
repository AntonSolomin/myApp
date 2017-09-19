package com.myApp.myApp;

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
    public Post findOne(long id) {
        return postRepository.findOne(id);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }
}
