package com.example.tasktrackerapp.service;


import com.example.tasktrackerapp.dto.UserDTO;
import com.example.tasktrackerapp.entity.Users;
import com.example.tasktrackerapp.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}

