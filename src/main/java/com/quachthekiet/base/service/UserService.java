package com.quachthekiet.base.service;

import java.util.Collection;

import com.quachthekiet.base.dto.UserDTO;
import com.quachthekiet.base.model.User;

public interface UserService {

    User getUserById(int id);

    Collection<UserDTO> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);
}
