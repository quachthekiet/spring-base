package com.quachthekiet.base.service;

import java.util.Collection;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.quachthekiet.base.model.User;
import com.quachthekiet.base.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "users", key = "'all'")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }
    

}
