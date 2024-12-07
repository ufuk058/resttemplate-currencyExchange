package com.rest_template.service;

import com.rest_template.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> findAll();
    UserDTO create(UserDTO userDTO);
    UserDTO findByUsername(String username);
}

