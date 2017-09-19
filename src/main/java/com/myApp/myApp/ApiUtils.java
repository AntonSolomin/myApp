package com.myApp.myApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this makes the wornings to bbe ignored by intelliJ
@SuppressWarnings("WeakerAccess")
public class ApiUtils {
    // Private constructor to avoid instantiation of the class
    private ApiUtils() {}

    public static Map<String, Object> getPostsDTO(List<Post> posts) {
        Map<String, Object> returnDto = new HashMap<>();
        for (Post post : posts) {
            returnDto.put("post_id", post.getId());
        }
        return returnDto;
    }
}
