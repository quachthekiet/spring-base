package com.quachthekiet.base.service.Impl;

import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.quachthekiet.base.dto.UserDTO;
import com.quachthekiet.base.exception.NotFoundException;
import com.quachthekiet.base.mapper.UserMapper;
import com.quachthekiet.base.model.User;
import com.quachthekiet.base.repository.UserRepository;
import com.quachthekiet.base.service.IUserService;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // @Cacheable(value = "users", key = "'all'")
    public Collection<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> userMapper.toDTO(u))
                .toList();
    }

    @CacheEvict(value = "users", allEntries = true)
    public User updateUser(User user) {

        User existingUser = userRepository.findById(user.getId());
        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        return userRepository.save(user);

    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

    @Override
    public User createUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public void deleteUser(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

}
