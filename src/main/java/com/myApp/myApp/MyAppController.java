package com.myApp.myApp;


import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.cloudinary.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.Map;
import java.lang.String;

import static java.util.stream.Collectors.toList;

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
    public Map<String, Object> getPosts(Authentication authentication) {
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


        if (files.size() >= 3) {
            ApiUtils.getPostCreationResponce(false);
        }

        //check if this works works
        List<String> urls = imagesReciever.uploadImages(files);


        boolean canUserPost = ApiUtils.isUserAuthenticated(authentication);
        boolean postPriceOnlyNumbers = postPrice.matches("[0-9]+");
        if(canUserPost && postPriceOnlyNumbers){
            User user = userService.findByUserName(ApiUtils.currentAuthenticatedUserName(authentication));
            postService.save(new Post(user, postSubject, postBody, Integer.valueOf(postPrice), urls));
        } else {
            return ApiUtils.getPostCreationResponce(postPriceOnlyNumbers);
        }
        return ApiUtils.getPostCreationResponce(canUserPost);
    }

    //TODO delete user
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
            return ApiUtils.getUserViewDashboard(canViewDashboard, dto);
        } else {
            return ApiUtils.getUserViewDashboard(canViewDashboard, new HashMap <String,Object>());
        }
    }
}




