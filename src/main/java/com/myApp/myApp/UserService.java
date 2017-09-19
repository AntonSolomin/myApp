package com.myApp.myApp;

import java.util.List;

public interface UserService {
    User save(User user);
    List<User> findAll();
}
