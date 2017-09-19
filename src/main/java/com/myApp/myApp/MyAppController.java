package com.myApp.myApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MyAppController {
    @Autowired
    PostService postService;

    @Autowired
    UserService userService;
    //test
    @RequestMapping(path = "/posts", method = RequestMethod.GET)
    public Map<String, Object> getGames() {
        List<Post> posts = postService.findAll();
        return ApiUtils.getPostsDTO(posts);
    }
}
