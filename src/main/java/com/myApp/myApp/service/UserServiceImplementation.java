package com.myApp.myApp.service;

import com.myApp.myApp.entities.User;
import com.myApp.myApp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.removeByUserId(user.getUserId());
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
    public boolean isUserOk(String firstName,
                            String inputLastname,
                            String inputUserName,
                            String password) {
        if (firstName.isEmpty() || inputLastname.isEmpty() || inputUserName.isEmpty() || password.isEmpty()) {
            return false;
        }

        final User existingUser = findByUserName(inputUserName);
        if (existingUser != null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean editUser(String userName,
                            Map<String, String> inputEditData) {
        User editedUser = findByUserName(userName);
        boolean isEdited = false;

        String inputFirstName = inputEditData.get("inputFirstName");
        String inputLastName = inputEditData.get("inputLastName");
        String psw = inputEditData.get("inputPassword");

        if (psw != null) {
            editedUser.setPassword(psw);
            isEdited = true;
        }
        if (inputLastName != null) {
            editedUser.setLastName(inputLastName);
            isEdited = true;
        }
        if (inputFirstName != null) {
            editedUser.setFirstName(inputFirstName);
            isEdited = true;
        }
        userRepository.save(editedUser);
        return isEdited;
    }


}
