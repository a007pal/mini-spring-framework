package com.amit.service;

import com.amit.core.annotation.PostConstruct;
import com.amit.repository.UserRepository;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init(){
        System.out.println("UserService initialized");
    }

    public void process() {
        repository.save();
    }
}
