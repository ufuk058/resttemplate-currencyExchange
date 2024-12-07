package com.rest_template.service.impl;

import com.rest_template.dto.UserDTO;
import com.rest_template.entity.User;
import com.rest_template.repository.UserRepository;
import com.rest_template.service.UserService;
import com.rest_template.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(user -> mapperUtil.convert(user,new UserDTO())).collect(Collectors.toList());
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        User user= userRepository.save(mapperUtil.convert(userDTO, new User()));
        return mapperUtil.convert(user, new UserDTO());
    }

    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new RuntimeException("User: "+username+", not found"));
        return mapperUtil.convert(user,new UserDTO());
    }
}
