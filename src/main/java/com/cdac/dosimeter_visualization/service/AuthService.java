package com.cdac.dosimeter_visualization.service;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import com.cdac.dosimeter_visualization.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.Authenticator;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

@Autowired
private UserRepository userRepository;
@Autowired
private PasswordEncoder passwordEncoder;
@Autowired
private AuthenticationManager authenticationManager;
@Autowired
private JwtUtil jwtUtil;

    public String  createUser(Map<String, Object> response ) {

        String name= response.get("name").toString();
        String password= response.get("password").toString();
        String email= response.get("email").toString();
        String phone= response.get("phone").toString();
        String sex= response.get("sex").toString();
//        System.err.println(name+ password+ email+phone);
        Optional<User> existingUser = userRepository.findByEmail(email);
//        System.err.println("Existing User: " + existingUser);
        if(existingUser.isPresent()){
            throw new RuntimeException("User Already Exists");
        }
        User user = new User();
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhoneNo(phone);
        user.setSex(sex);
        userRepository.save(user);
        return "User registered successfully";
    }

    public String loginUser(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return jwtUtil.generateToken(email);
    }
}
