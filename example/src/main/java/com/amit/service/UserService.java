package com.amit.service;

import com.amit.repository.UserRepository;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void process() {
        repository.save();
    }
}
