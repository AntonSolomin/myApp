package com.myApp.myApp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this makes the warnings to bbe ignored by intelliJ
@SuppressWarnings("WeakerAccess")
public class ApiUtils {
    // Private constructor to avoid instantiation of the class
    private ApiUtils() {}

    public static Map<String, Object> getPostsDTO(List<Post> posts, String authedUser) {
        Map<String, Object> returnDto = new HashMap<>();
        List<Object> postsValue = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> newPost = new HashMap<>();
            newPost.put("post_id", post.getId());
            newPost.put("post_creation_date", post.getPostCreationDate());
            newPost.put("post_subject", post.getPostSubject());
            newPost.put("post_body", post.getPostBody());
            newPost.put("post_price", post.getPostPrice());
            postsValue.add(newPost);
        }
        returnDto.put("posts", postsValue);
        returnDto.put("logged_in_user_id", authedUser);
        return returnDto;
    }

    public static Map<String,  Object> getUsersDto(List<User> users, String authedUser) {
        Map<String, Object> returnDto = new HashMap<>();
        List<Object> userValue = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("user_id", user.getUserId());
            newUser.put("user_username", user.getUserName());
            newUser.put("user_first_name", user.getFirstName());
            newUser.put("user_last_name", user.getLastName());
            newUser.put("user_number_of_posts", user.getPosts().size());
            userValue.add(newUser);
        }
        returnDto.put("users", userValue);
        returnDto.put("logged_in_user_id", authedUser);
        return returnDto;
    }

    @org.jetbrains.annotations.Contract("true -> !null; false -> !null")
    public static ResponseEntity<Object> getUserCreatingResponse(boolean isSaved) {
        if (isSaved) {
            return new ResponseEntity<Object>("player_created", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<Object>("not_created", HttpStatus.FORBIDDEN);
        }
    }

    @Contract("true -> !null; false -> !null")
    public static ResponseEntity<Object> getEditUserResponse(boolean isEdited) {
        if (isEdited) {
            return new ResponseEntity<Object>("user_edited", HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>("user_not_edited", HttpStatus.FORBIDDEN);
        }
    }

    @Contract("true -> !null; false -> !null")
    public static ResponseEntity<Object> getEditPostResponse(boolean isEdited) {
        if (isEdited) {
            return new ResponseEntity<Object>("post_edited", HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>("post_not_edited", HttpStatus.FORBIDDEN);
        }
    }

    @Contract("_, true -> !null; _, false -> !null")
    public static ResponseEntity<Object> getPostCreationResponce(boolean canUserPost) {
        if (canUserPost) {
            return new ResponseEntity<Object>("post_created", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<Object>("post_not_created", HttpStatus.FORBIDDEN);
        }
    }

    @Nullable
    public static String currentAuthenticatedUserName (Authentication authentication) {
        if (isGuest(authentication)) {
            return null;
        }
        return authentication.getName();
    }

    @Contract(value = "null -> true", pure = true)
    public static boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @Contract(pure = true)
    public static boolean isUserAuthenticated (Authentication authentication) {
        if (isGuest(authentication)) {
            return false;
        }
        return true;
    }
}
