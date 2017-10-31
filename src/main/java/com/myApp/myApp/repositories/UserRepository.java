package com.myApp.myApp.repositories;

import com.myApp.myApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName (String name);

    @Transactional
    List<User> removeByUserId(Long userId);
}
