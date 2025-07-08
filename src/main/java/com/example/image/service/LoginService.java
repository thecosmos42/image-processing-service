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

    /**
     * Registers a new user by encoding their password and saving the user object in the database.
     *
     * @param user The user object containing the raw (plaintext) password and other user details.
     * @return A confirmation message upon successful registration.
     */
    public String registerUser(Users user){
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return "User is registered";
    }

    /**
     * Validates a user's login credentials. If valid, returns a JWT token.
     *
     * @param loginRequestDto DTO containing username and password for login.
     * @return A JWT token string upon successful authentication.
     * @throws UsernameNotFoundException if the username is not found.
     * @throws BadCredentialsException if the password does not match the stored hash.
     */
    public String validateUser(LoginRequestDto loginRequestDto){
        Users user = userRepository.findByUserName(loginRequestDto.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Password is incorrect");
        }
        return jwtUtils.generateToken(loginRequestDto.getUserName());

    }
}
