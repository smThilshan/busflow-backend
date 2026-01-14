//package com.busflow.management.service;
//
//import com.busflow.management.entity.User;
//import com.busflow.management.exception.UserNotFoundException;
//import com.busflow.management.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserAuthService {
//
//    private final UserRepository userRepository;
//
//    public User getUserbyUsername(String username) {
//        return userRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("User not found"));
//    }
//}
