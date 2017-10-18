package com.myApp.myApp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName (String name);

    @Transactional
    List<User> removeByUserId(Long userId);
}
