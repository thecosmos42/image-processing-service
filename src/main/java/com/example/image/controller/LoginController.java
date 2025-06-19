package com.example.image.controller;

import com.example.image.dto.LoginRequestDto;
import com.example.image.model.Users;
import com.example.image.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    LoginService loginService;

    @PostMapping("/auth/signup")
    public ResponseEntity<String> createLoginCred(@RequestBody Users user){
        String response = loginService.registerUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


    @PostMapping("/auth/login")
    public ResponseEntity<String> validateLoginCred(@RequestBody LoginRequestDto loginRequestDto){
        String jwtToken = loginService.validateUser(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }


}
