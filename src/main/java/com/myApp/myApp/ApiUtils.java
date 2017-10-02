package com.myApp.myApp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.*;

// this makes the warnings to bbe ignored by intelliJ
@SuppressWarnings("WeakerAccess")
public class ApiUtils {
    // Private constructor to avoid instantiation of the class
    private ApiUtils() {}

    public static Map<String, Object> getPostsDTO(List<Post> posts, String authedUser) {
        Map<String, Object> dto = new HashMap<>();
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
        dto.put("posts", postsValue);
        dto.put("logged_in_user_id", authedUser);
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

    public static Map<String, Object> getUserDashboardDto(User user){
        Map<String, Object> dto = new HashMap<>();
        dto.put("first_name",user.getFirstName());
        dto.put("last_name",user.getLastName());
        dto.put("user_id",user.getUserId());
        dto.put("username",user.getUserName());
        dto.put("posts", getUserDashBoardPosts(user.getPosts()));
        return dto;
    }

    public static Map<String,Object> getUserDashBoardPosts(Set<Post> posts) {
        Map<String, Object> dashboardPosts = new HashMap<>();
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
        dashboardPosts.put("posts", postsValue);
        return dashboardPosts;
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
    public static ResponseEntity<Object> getPostCreationResponce(boolean canUserPost) {
        if (canUserPost) {
            return new ResponseEntity<Object>("post_created", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<Object>("post_not_created", HttpStatus.FORBIDDEN);
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
