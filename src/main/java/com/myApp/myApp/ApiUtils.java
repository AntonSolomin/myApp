package com.myApp.myApp;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

// this makes the warnings to bbe ignored by intelliJ
@SuppressWarnings("WeakerAccess")
public class ApiUtils {
    // Private constructor to avoid instantiation of the class
    private ApiUtils() {}

    //can use later
    /*public static Map<String,Object> jsonToMap (JSONObject json) throws JSONException{
        Map<String, Object> retMap = new HashMap<String, Object>();
        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }*/

    public static Map<String, Object> getPostsDTO(List<Post> posts, String authedUser) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("posts", getAllPostsDto(posts));
        dto.put("logged_in_user_id", authedUser);
        return dto;
    }

    public static Map<String,Object> getSinglePostDTO (Post post, String authedUser, Long postId){
        Map<String,Object> dto = new HashMap<>();
        if (authedUser != null && postId != null) {
            dto.put("post_id", post.getId());
            dto.put("post_subject", post.getPostSubject());
            dto.put("post_body", post.getPostBody());
            dto.put("post_price", post.getPostPrice());
            dto.put("url", post.getPostPicUrl());
        }
        return dto;
    }

    public static Map<String, Object> getUsersDto(List<User> users,
                                                  String authedUser) {
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

    public static Map<String, Object> getPostsQuery(List<Post> posts,
                                                   List<String> query,
                                                   String user) {
        Set<Post> postsToSend = new HashSet<>();
        for (String aQuery : query) {
            for (Post current : posts) {
                String postSubject = current.getPostSubject();
                List<String> items = Arrays.asList(postSubject.trim().split("\\s+"));
                for (String item : items) {
                    if (aQuery.toLowerCase().equals(item.toLowerCase())) {
                        postsToSend.add(current);
                    }
                }
            }
        }
        //if no match return all posts
        if (postsToSend.size() == 0) {
            return getPostsDTO(posts, user);
        }

        // if there's a match return the result
        Map<String, Object> dto = new HashMap<>();
        List<Object> postsValue = new ArrayList<>();

        for (Post post : postsToSend) {
            Map<String, Object> newPost = new HashMap<>();
            newPost.put("post_id", post.getId());
            newPost.put("post_subject", post.getPostSubject());
            newPost.put("post_subject", post.getPostPrice());
            postsValue.add(newPost);
        }
        dto.put("posts", postsValue);
        return dto;
    }

    public static boolean canUserDelete(User userName, Post inputPost) {
        boolean hasPost = false;
        //TODO write stuff here
        for (Post userPost : userName.getPosts()) {
            if (userPost.getId() == inputPost.getId()) {
                hasPost = true;
            }
        }
        return hasPost;
    }

    @Contract("_, true -> !null; _, false -> !null")
    public static ResponseEntity<Object> getDeletePostResponce(boolean canUserDelete) {
        if (canUserDelete) {
            return new ResponseEntity<Object>("post_deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>("post_not_deleted", HttpStatus.FORBIDDEN);
        }
    }

    @Contract("_, true -> !null; _, false -> !null")
    public static ResponseEntity<Object> getDeleteUserResponce(boolean canUserDelete) {
        if (canUserDelete) {
            return new ResponseEntity<Object>("user_deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>("user_not_deleted", HttpStatus.FORBIDDEN);
        }
    }

    public static Map<String, Object> getUserDashboardDto(User user){
        Map<String, Object> dto = new HashMap<>();
        dto.put("first_name",user.getFirstName());
        dto.put("last_name",user.getLastName());
        dto.put("user_id",user.getUserId());
        dto.put("username",user.getUserName());
        dto.put("posts", getAllPostsDto(user.getPosts()));
        return dto;
    }

    public static List<Object> getAllPostsDto(List<Post> posts) {
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
        return postsValue;
    }

    @Contract("true -> !null; false -> !null")
    public static ResponseEntity<Object> getUserViewDashboard(boolean canView,
                                                              Map<String,Object> dto) {
        if (canView) {
            return new ResponseEntity<Object>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(dto, HttpStatus.FORBIDDEN);
        }
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
    public static ResponseEntity<Object> getPostCreationResponce(Map<String, Object> response, boolean canUserPost) {
        if (canUserPost) {
            response.put("post_creation","post_successfully_created");
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        } else {
            response.put("post_creation","post_not_created");
            return new ResponseEntity<Object>(response, HttpStatus.FORBIDDEN);
        }
    }

    @Contract("true -> !null; false -> !null")
    public static ResponseEntity<Object> getPostJoinResponse (boolean isJoined,
                                                              Map<String,Object> dto) {
        if (isJoined) {
            return new ResponseEntity<Object>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(dto, HttpStatus.FORBIDDEN);
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
