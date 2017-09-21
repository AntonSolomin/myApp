package com.myApp.myApp;

import java.util.List;

public interface UserService {
    User save(User user);
    User findOne(long id);
    List<User> findAll();
    User findByUserName(String name);
    boolean isUserOk(String firstName,
                  String inputLastname,
                  String inputUserName,
                  String password);
}
