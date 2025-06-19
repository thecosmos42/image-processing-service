package com.example.image.service;

import com.example.image.dto.LoginRequestDto;
import com.example.image.model.Users;
import com.example.image.repository.UsersRepository;
import com.example.image.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class LoginService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public String registerUser(Users user){
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return "User is registered";
    }

    public String validateUser(LoginRequestDto loginRequestDto){
        Users user = userRepository.findByUserName(loginRequestDto.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Password is incorrect");
        }
//        return "True";
        String response = jwtUtils.generateToken(loginRequestDto.getUserName());
        return response;

    }
}
