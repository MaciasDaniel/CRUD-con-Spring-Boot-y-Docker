package com.docker.cruddockerexample.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.docker.cruddockerexample.config.DBCacheConfig;
import com.docker.cruddockerexample.entity.User;
import com.docker.cruddockerexample.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> existsByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Cacheable(value = DBCacheConfig.CACHE_NAME, unless = "#result == null")
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Cacheable(value = DBCacheConfig.CACHE_NAME, unless = "#result.isEmpty()")
    public List<User> listAll() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return userRepository.findAll();
    }

    @CacheEvict(cacheNames = DBCacheConfig.CACHE_NAME, allEntries = true)
    public boolean deleteUserById(int id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}