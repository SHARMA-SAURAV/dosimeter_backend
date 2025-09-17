//package com.cdac.dosimeter_visualization.controller;
//
//import com.cdac.dosimeter_visualization.model.DemoJson;
//import com.cdac.dosimeter_visualization.model.User;
//import com.cdac.dosimeter_visualization.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class AdminController {
//    @Autowired
//    private UserRepository userRepository;
//
//
//    @GetMapping("/user/all")
//    ResponseEntity <?> getAllUsers() {
//
//        List<User> users =userRepository.findAll();
//        // This method should return a list of all users.
//        // For now, we will return a placeholder response.
//        return ResponseEntity.ok(users);
//    }
//
//    @PostMapping("/add/userdata")
//    ResponseEntity<?> addUserData(User user) {
//
//
//
//        return ResponseEntity.ok("User added successfully");
//    }
//
//    @GetMapping("/get/json")
//    ResponseEntity<DemoJson> getJsonDate(){
//        return ResponseEntity.ok(new DemoJson());
//    }
//
//
//}








package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.model.DemoJson;
import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
//Get All user so that we can assign dosimeter to user
    @PostMapping("/user")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    // Demo: return a random DemoJson (not saved here)
    @GetMapping("/get/json")
    public ResponseEntity<DemoJson> getJsonDate() {
        return ResponseEntity.ok(new DemoJson());
    }
}
