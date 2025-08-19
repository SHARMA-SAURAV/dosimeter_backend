package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.dto.LoginDto;
import com.cdac.dosimeter_visualization.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
@Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?>  registerUser(@RequestBody Map<String, Object> request) {
        System.err.println("I am inside the register endpoint");
        try {
//            System.err.println("Response: " + request);
            String response = authService.createUser(request);
//            System.err.println("Response: " + response);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            String message= "User Already Exist";
            return ResponseEntity.badRequest().body("Message" + message);
        }

    }
    @PostMapping("/login")
    public ResponseEntity<LoginDto> loginUser(@RequestBody Map<String, String> request) {

        String email=request.get("email");
        String password=request.get("password");
        String token = authService.loginUser(email, password);
        System.out.println("Token: " + token);
        LoginDto loginDto = new LoginDto();
        loginDto.setToken(token);
        loginDto.setEmail(email);
        return ResponseEntity.ok(loginDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

            SecurityContextHolder.clearContext();
        }
        return ResponseEntity.ok("User logged out successfully");
    }
}
