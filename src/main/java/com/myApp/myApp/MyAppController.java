package com.myApp.myApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MyAppController {
    @Autowired
    PostService postService;

    @Autowired
    UserService userService;
    //test
    @RequestMapping(path = "/posts", method = RequestMethod.GET)
    public Map<String, Object> getPosts(Authentication authentication) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<Post> posts = postService.findAll();
        return ApiUtils.getPostsDTO(posts, user);
    }

    @RequestMapping(path = "/posts", method = RequestMethod.POST)
    public ResponseEntity<Object> createPost(Authentication authentication,
                                             String postBody,
                                             String postSubject,
                                             int postPrice) {

        boolean canUserPost = ApiUtils.isUserAuthenticated(authentication);

        if(canUserPost){
            User user = userService.findByUserName(ApiUtils.currentAuthenticatedUserName(authentication));
            postService.save(new Post(user, postSubject, postBody, postPrice));
        }
        return  ApiUtils.getPostCreationResponce(canUserPost);
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public Map<String, Object> getUsers(Authentication authentication) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<User> users = userService.findAll();
        return ApiUtils.getUsersDto(users, user);
    }

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public ResponseEntity<Object> signUpPlayer(String inputFirstName,
                                               String inputLastName,
                                               String inputUserName,
                                               String password) {
        boolean isOk = userService.isUserOk(inputFirstName, inputLastName, inputUserName, password);
        if (isOk){
            userService.save(new User(inputFirstName, inputLastName, inputUserName, password));
        }
        return ApiUtils.getUserCreatingResponse(isOk);
    }

    @RequestMapping(path = "/users", method = RequestMethod.PATCH)
    public ResponseEntity<Object> editUser(@RequestBody Map<String, String> inputEditData,
                                           Authentication authentication){

        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        boolean isEdited = false;
        if (userName != null && inputEditData != null) {
            isEdited = userService.editUser(userName, inputEditData);
        }
        return ApiUtils.getEditUserResponse(isEdited);
    }

    @RequestMapping(path = "/posts", method = RequestMethod.PATCH)
    public ResponseEntity<Object> editPost(@RequestBody Map<String,Object> inputEditData,
                                           Authentication authentication) {
        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        boolean isEdited = false;
        if (userName != null && inputEditData != null) {
            isEdited = postService.editPost(inputEditData);
        }
        return ApiUtils.getEditPostResponse(isEdited);
    }

}
