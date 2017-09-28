package com.myApp.myApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
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

    @RequestMapping(path = "/posts/queries", method = RequestMethod.POST)
    public Map<String, Object> searchPosts(Authentication authentication,
                                           @RequestBody List<String> query) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<Post> posts = postService.findAll();
        return ApiUtils.getPostsQuery(posts, query, user);
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public Map<String, Object> getUsers(Authentication authentication) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<User> users = userService.findAll();
        return ApiUtils.getUsersDto(users, user);
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

    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(String inputFirstName,
                                             String inputLastName,
                                             String inputUserName,
                                             String password) {
        boolean isOk = userService.isUserOk(inputFirstName, inputLastName, inputUserName, password);
        if (isOk){
            userService.save(new User(inputFirstName, inputLastName, inputUserName, password));
        }
        return ApiUtils.getUserCreatingResponse(isOk);
    }

    @RequestMapping(path = "/posts", method = RequestMethod.PATCH)
    public ResponseEntity<Object> editPost(@RequestBody EditPostReceiver inputEditData,
                                           Authentication authentication) {
        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        boolean isEdited = false;
        if (userName != null && inputEditData != null) {
            isEdited = postService.editPost(inputEditData);
        }
        return ApiUtils.getEditPostResponse(isEdited);
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

    @RequestMapping(path = "/post_view/{postId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPostView(@PathVariable Long postId,
                                              Authentication authentication) {
        final Map<String,Object> dto = new LinkedHashMap<>();
        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);

        Post post = postService.findOne(postId);
        boolean isJoined = false;
        if (userName != null && postId != null) {
            dto.put("post_id", post.getId());
            dto.put("post_subject", post.getPostSubject());
            dto.put("post_body", post.getPostBody());
            dto.put("post_price", post.getPostPrice());
            isJoined = true;
        }
        return ApiUtils.getPostJoinResponse(isJoined, dto);
    }
}
