package com.myApp.myApp.service;

import com.myApp.myApp.entities.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User save(User user);
    void delete(User user);
    User findOne(long id);
    List<User> findAll();
    User findByUserName(String name);
    boolean isUserOk(String firstName,
                  String inputLastName,
                  String inputUserName,
                  String password);

    boolean editUser(String userName,
                     Map<String,String> inputEditData);
}
