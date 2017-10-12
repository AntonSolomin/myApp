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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MyAppController {
    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    //not necessary
    @GetMapping("/upload")
    public String uploadForm(){
        return "upload";
    }

    //TODO refactor http responses and put them to ApiUtils
    //TODO create picture entity
    //TODO limit number of pictures that can be added and their size
    //TODO save the url in your db
    //TODO add pictures on createPost
    //TODO add ability to ad pics later
    //TODO send postPicturesUrls along with the post

    @PostMapping("/upload")
    //@RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String singleImageUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
                                    @RequestParam("subject")String subject,
                                    @RequestParam("body")String body,
                                    @RequestParam("price")String price){
        ModelMap model = new ModelMap();
        Cloudinary c = new Cloudinary("cloudinary://535928336455433:EEgAQDFCI0i-Fe86KvrgsQlBvBI@dq4elvg0g");
        if (file.isEmpty()){
            model.addAttribute("message","Please select a file to upload");
            return "upload";
        }
        try {
            File f=Files.createTempFile("temp", file.getOriginalFilename()).toFile();
            file.transferTo(f);
            Map response=c.uploader().upload(f, ObjectUtils.emptyMap());

            return "Ok";

            /*Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dq4elvg0g",
                    "api_key", "EEgAQDFCI0i-Fe86KvrgsQlBvBI",
                    "api_secret", "535928336455433"));*/
            //Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            //Map uploadResult = cloudinary.uploader().upload("http://cdn1-www.dogtime.com/assets/uploads/gallery/border-collie-dog-breed-pictures/1-facethreequarters.jpg", ObjectUtils.emptyMap());
            //Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            //model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
            //model.addAttribute("imageurl", uploadResult.get("url"));
        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "upload";
    }

    @RequestMapping(path = "/posts", method = RequestMethod.GET)
    public Map<String, Object> getPosts(Authentication authentication) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<Post> posts = postService.findAll();
        return ApiUtils.getPostsDTO(posts, user);
    }

    @RequestMapping(path = "/posts", method = RequestMethod.POST)
    public ResponseEntity<Object> createPost(Authentication authentication,
                                             RedirectAttributes redirectAttributes,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("subject")String postSubject,
                                             @RequestParam("body")String postBody,
                                             @RequestParam("price")String postPrice) {
        String url = "";
        Cloudinary cloudinary = new Cloudinary("cloudinary://535928336455433:EEgAQDFCI0i-Fe86KvrgsQlBvBI@dq4elvg0g");
        if (file.isEmpty()){
            return ApiUtils.getPostCreationResponce(false);
        }
        try {
            File f=Files.createTempFile("temp", file.getOriginalFilename()).toFile();
            file.transferTo(f);
            Map response = cloudinary.uploader().upload(f, ObjectUtils.emptyMap());
            JSONObject json=new JSONObject(response);
            url = json.getString("url");

        } catch (IOException e){
            e.printStackTrace();
        }

        boolean canUserPost = ApiUtils.isUserAuthenticated(authentication);
        if(canUserPost){
            User user = userService.findByUserName(ApiUtils.currentAuthenticatedUserName(authentication));
            postService.save(new Post(user, postSubject, postBody, Integer.valueOf(postPrice), url));
        }
        return  ApiUtils.getPostCreationResponce(canUserPost);
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




