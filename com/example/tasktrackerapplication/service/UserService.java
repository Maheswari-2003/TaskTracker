package com.example.tasktrackerapplication.service;


import com.example.tasktrackerapplication.dto.UserDTO;
import com.example.tasktrackerapplication.entity.JwtUtil;
import com.example.tasktrackerapplication.entity.Users;
import com.example.tasktrackerapplication.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        return optionalUser.map(this::convertToDTO).orElse(null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        Users user = convertToEntity(userDTO);
        Users savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private UserDTO convertToDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    private Users convertToEntity(UserDTO userDTO) {
        Users user = new Users();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

    public UserDTO saveUser(UserDTO userDTO) {

        Users user = convertToEntity(userDTO);
        Users savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }


    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final DataSource dataSource;

    @Autowired
    public UserService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, DataSource dataSource) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.dataSource = dataSource;
    }

    public ResponseEntity<String> authenticateUser(String username, String password) {
        try {
            // Simple authentication with username and password using JDBC
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            // Authentication successful, generate JWT token
            String token = jwtUtil.generateToken(username);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}



