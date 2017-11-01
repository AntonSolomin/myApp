package com.myApp.myApp.controller;

import com.myApp.myApp.entities.PostReview;
import com.myApp.myApp.recievers.PostReviewReciever;
import com.myApp.myApp.service.PostReviewService;
import com.myApp.myApp.utilities.ApiUtils;
import com.myApp.myApp.recievers.EditPostReceiver;
import com.myApp.myApp.recievers.ImagesReciever;
import com.myApp.myApp.entities.Post;
import com.myApp.myApp.entities.User;
import com.myApp.myApp.service.PostService;
import com.myApp.myApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
    PostReviewService postReviewService;

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

    //todo change to PUT and VOTES (up and down)
    @RequestMapping(path = "/posts/{postId}/vote", method = RequestMethod.PUT)
    public ResponseEntity<Object> votePost (@PathVariable Long postId,
                                          @RequestParam("vote") boolean vote,
                                          Authentication authentication) {

        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        User user = userService.findByUserName(userName);
        Post post = postService.findOne(postId);

        //todo refactor
        List<Long> liked = user.getLikedPosts();
        // first like ever
        if (liked.size() == 0 && vote) {
            user.addToLikedPosts(postId);
            postService.votePost(post, vote);
        }

        for (int i = 0; i < liked.size(); i++) {
            Long aLong = liked.get(i);
            //first and like. I cant like the same post twice
            if (!aLong.equals(postId) && vote) {
                user.addToLikedPosts(postId);
                postService.votePost(post, vote);
            }
            //User has it and unlike. Clearing the liked list if it's !vote
            if (aLong.equals(postId) && !vote) {
                user.deleteFromLikedPosts(aLong);
                postService.votePost(post, vote);
            }
        }

        userService.save(user);
        return ApiUtils.getUpVoteResponce(user);
    }

    @RequestMapping(path = "/posts/{postId}/reviews", method = RequestMethod.POST)
    public ResponseEntity<Object> newReview (@PathVariable Long postId,
                                             PostReviewReciever postReviewReciever,
                                             Authentication authentication) {

        final String userName = ApiUtils.currentAuthenticatedUserName(authentication);
        User user = userService.findByUserName(userName);
        Post post = postService.findOne(postId);
        PostReview postReview = new PostReview(user,post);
        postReviewService.save(postReview);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/postReviews", method = RequestMethod.GET)
    public List<Date> getAllReviews () {
        List<PostReview> reviews = postReviewService.findAll();
        List<Date> dates = new ArrayList<>();
        for (PostReview review : reviews) {
            dates.add(review.getTimestamp());
        }
        return dates;
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




