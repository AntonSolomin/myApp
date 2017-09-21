package com.myApp.myApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOne(long id) {return userRepository.findOne(id);}

    @Override
    public User findByUserName(String name) {return userRepository.findByUserName(name);}

    @Override
    public boolean isUserOk(String firstName, String inputLastname, String inputUserName, String password) {
        if (firstName.isEmpty() || inputLastname.isEmpty() || inputUserName.isEmpty() || password.isEmpty()) {
            return false;
        }

        final User existingUser = findByUserName(inputUserName);
        if (existingUser != null) {
            return false;
        }

        return true;
    }


}
