package com.myApp.myApp.controller;

import com.myApp.myApp.utilities.ApiUtils;
import com.myApp.myApp.recievers.EditPostReceiver;
import com.myApp.myApp.recievers.ImagesReciever;
import com.myApp.myApp.entities.Post;
import com.myApp.myApp.entities.User;
import com.myApp.myApp.service.PostService;
import com.myApp.myApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;
import java.lang.String;


@RestController
@RequestMapping("/api")
public class MyAppController {
    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    ImagesReciever imagesReciever;

    /*//not necessary
    @GetMapping("/upload")
    public String uploadForm(){
        return "upload";
    }*/

    //TODO refactor http responses and put them to ApiUtils
    //TODO limit number of pictures that can be added and their size
    //TODO add ability to ad pics later
    //TODO upload multiple files
    //TODO create pics thumbnails




    @RequestMapping(path = "/posts", method = RequestMethod.GET)
    public Map<String, Object> getAllPosts(Authentication authentication) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<Post> posts = postService.findAll();
        return ApiUtils.getPostsDTO(posts, user);
    }

    //@PostMapping("/upload") works just as well
    @RequestMapping(path = "/posts", method = RequestMethod.POST)
    public ResponseEntity<Object> createPost(Authentication authentication,
                                             RedirectAttributes redirectAttributes,
                                             @RequestParam("file") List<MultipartFile> files,
                                             @RequestParam("subject")String postSubject,
                                             @RequestParam("body")String postBody,
                                             @RequestParam("price")String postPrice) {

        Map<String, Object> response = new HashMap<>();
        if (files.size() >= 3) {
            ApiUtils.getPostCreationResponce(response, false);
        }

        List<String> urls = imagesReciever.uploadImages(files);

        //TODO refactor. Move to ApiUtils
        //TODO To consider : don't return just post id, return the actual post
        boolean canUserPost = ApiUtils.isUserAuthenticated(authentication);
        boolean postPriceOnlyNumbers = postPrice.matches("[0-9]+");
        if(canUserPost && postPriceOnlyNumbers){
            User user = userService.findByUserName(ApiUtils.currentAuthenticatedUserName(authentication));
            Post post = new Post(user, postSubject, postBody, Integer.valueOf(postPrice), urls);
            postService.save(post);
            long postId = post.getId();
            response.put("post_id", postId);
        } else {
            return ApiUtils.getPostCreationResponce(response, postPriceOnlyNumbers);
        }
        return ApiUtils.getPostCreationResponce(response, canUserPost);
    }

    @RequestMapping(path = "/posts", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deletePost(@RequestBody Long postId,
                                             Authentication authentication) {
        final User user = userService.findByUserName(ApiUtils.currentAuthenticatedUserName(authentication));
        boolean canDelete = false;
        if (postId != null && user != null) {
            Post post = postService.findOne(postId);
            canDelete = ApiUtils.canUserDelete(user, post);
            if (canDelete) postService.delete(post);
        }
        return ApiUtils.getDeletePostResponce(canDelete);
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

    @RequestMapping(path = "/post_view/{postId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getPostView(@PathVariable Long postId,
                                              Authentication authentication) {

        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        Post post = postService.findOne(postId);
        boolean isJoined = false;
        final Map<String,Object> dto = ApiUtils.getSinglePostDTO(post, userName, postId);
        if (dto.size() != 0) isJoined = true;
        return ApiUtils.getPostJoinResponse(isJoined, dto);
    }

    @RequestMapping(path = "/post_view/{postId}/similar", method = RequestMethod.GET)
    public ResponseEntity<Object> getSimilarProducts(@PathVariable Long postId,
                                                     Authentication authentication) {
        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        Post post = postService.findOne(postId);
        boolean isAvailable = false;
        List<Post> posts = postService.findAll();
        final Map<String,Object> dto = ApiUtils.getSimilarProducts(posts, post, userName);
        if (dto.size() != 0) isAvailable = true;
        return ApiUtils.getSimilarProductsResponse(isAvailable, dto);
    }

    @PostMapping("/post/{postId}/upvote")
    public ResponseEntity<Object> upVote (Authentication authentication,
                                          @PathVariable Long postId) {
        //TODO save upvoted posts for the user
        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        User user = userService.findByUserName(userName);
        Post post = postService.findOne(postId);
        return ApiUtils.getUpVoteResponce(user, post);
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


    //TODO Ask Ignasi why this could be not working
    @RequestMapping(path = "/users", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteUser(Authentication authentication) {

        final User user = userService.findByUserName(ApiUtils.currentAuthenticatedUserName(authentication));
        boolean canDelete = false;
        if (user != null) {
            canDelete = true;
            if (canDelete) userService.delete(user);
        }
        return ApiUtils.getDeleteUserResponce(canDelete);
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

    @RequestMapping(path = "/users/{queryUserName}", method = RequestMethod.GET)
    public ResponseEntity<Object> getUserDashboard (Authentication authentication,
                                                 @PathVariable String queryUserName) {
        final String authenticatedUserName = ApiUtils.currentAuthenticatedUserName(authentication);
        boolean canViewDashboard = false;
        if (authenticatedUserName != null) {
            User user = userService.findByUserName(queryUserName);
            canViewDashboard = true;
            Map<String, Object> dto = ApiUtils.getUserDashboardDto(user);
            return ApiUtils.getUserViewDashboardResponse(canViewDashboard, dto);
        } else {
            return ApiUtils.getUserViewDashboardResponse(canViewDashboard, new HashMap <String,Object>());
        }
    }
}




