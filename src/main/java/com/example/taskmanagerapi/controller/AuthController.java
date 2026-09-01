package com.example.taskmanagerapi.controller;

import com.example.taskmanagerapi.JwtUtil;
import com.example.taskmanagerapi.dto.AuthRequest;
import com.example.taskmanagerapi.dto.AuthResponse;
import com.example.taskmanagerapi.entity.User;
import com.example.taskmanagerapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest authRequest){
        //check if username already exists
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()){
            return new AuthResponse(null, null, "Username already exists");
        }

        //check if email already exists
        if (userRepository.findByEmail(authRequest.getEmail()).isPresent()){
            return new AuthResponse(null, null, "Email already exists");
        }

        //Create new user
        User newUser = new User(
                authRequest.getUsername(),
                authRequest.getPassword(),
                authRequest.getEmail());
        userRepository.save(newUser);

        //Generate JWT Token
        String token = jwtUtil.generateToken(authRequest.getUsername());

        return new AuthResponse(token, authRequest.getUsername(), "User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest){
        User user = userRepository.findByUsername(authRequest.getUsername()).orElse(null);

        //check if user exists and password matches
        if (user == null || !user.getPassword().equals(authRequest.getPassword())){
            return new AuthResponse(null, null, "Invalid username or password");
        }

        //Generate JWT Token
        String token = jwtUtil.generateToken(authRequest.getUsername());

        return new AuthResponse(token, authRequest.getUsername(), "Login Successful");
    }

}
