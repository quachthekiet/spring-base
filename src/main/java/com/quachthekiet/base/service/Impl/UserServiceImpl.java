package com.quachthekiet.base.service.Impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.quachthekiet.base.dto.UserDTO;
import com.quachthekiet.base.exception.NotFoundException;
import com.quachthekiet.base.mapper.UserMapper;
import com.quachthekiet.base.model.User;
import com.quachthekiet.base.repository.UserRepository;
import com.quachthekiet.base.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(""));
    }

    @Override
    public Collection<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public User createUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public User updateUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

}
