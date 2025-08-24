package com.quachthekiet.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quachthekiet.base.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    List<User> findAll();

    User findById(int id);

    void deleteById(int id);
}
