package com.quachthekiet.base.service;

import java.util.Collection;

import com.quachthekiet.base.model.User;

public interface IUserService {

    User getUserById(int id);

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);
}
