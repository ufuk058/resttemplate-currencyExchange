package com.rest_template.controller;

import com.rest_template.dto.ResponseWrapper;
import com.rest_template.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper> getAllUser(){

        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                .message("All users successfully retrieved")
                .code(HttpStatus.OK.value())
                .data(userService.findAll()).build());
    }

    @GetMapping("/{username}")
    public ResponseEntity<ResponseWrapper> getUserByUsername(@PathVariable("username") String username){
        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                .message("User: "+username+" successfully retrieved")
                .code(HttpStatus.OK.value())
                .data(userService.findByUsername(username)).build());
    }
}

