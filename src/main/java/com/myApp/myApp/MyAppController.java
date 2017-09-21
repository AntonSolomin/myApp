package com.myApp.myApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public Map<String, Object> getUsers(Authentication authentication) {
        final String user = ApiUtils.currentAuthenticatedUserName(authentication);
        List<User> users = userService.findAll();
        return ApiUtils.getUsersDto(users, user);
    }
    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public ResponseEntity<Object> signUpPlayer(String firstName,
                                               String inputLastname,
                                               String inputUserName,
                                               String password) {
        boolean isOk = userService.isUserOk(firstName, inputLastname, inputUserName, password);
        if (isOk){
            userService.save(new User(firstName, inputLastname, inputUserName, password));
        }
        return ApiUtils.getUserCreatingResponse(isOk);
    }

}
