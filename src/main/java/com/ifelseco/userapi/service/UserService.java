package com.ifelseco.userapi.service;

import com.ifelseco.userapi.entity.User;
import com.ifelseco.userapi.entity.UserRole;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    User createUser(User user, Set<UserRole> userRole);
    User save(User user);
    User findByUsername(String username);

   // Optional<User> findById(Long id);

    User findByEmail(String email);

    //Optional<User> findById(Long id);

}
