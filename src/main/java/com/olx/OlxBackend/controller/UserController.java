package com.olx.OlxBackend.controller;

import com.olx.OlxBackend.DTO.UserDto;
import com.olx.OlxBackend.model.ApplicationUser;
import com.olx.OlxBackend.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rest")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/user/signup")
    public ResponseEntity singUp(@RequestBody UserDto userDto) {
        try {
            ApplicationUser savedUser = userService.signUp(userDto);
            return new ResponseEntity<>("Sign up successful", HttpStatus.ACCEPTED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/user/signin")
    public ResponseEntity signIn(@PathVariable("userName") String userName, @PathVariable("password") String password) {
        try {
            String message = (String) userService.signIn(userName, password);
            return new ResponseEntity<>(message, HttpStatus.ACCEPTED);
        }catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/user/verify")
    public ResponseEntity userVerify(@PathVariable("otp") String otp, @RequestBody ApplicationUser applicationUser) {
        try {
            HashMap<String, Object> result = new HashMap<>();
            userService.userVerify(otp, applicationUser, result);
            if (result.containsKey("success")) return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
            else return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
