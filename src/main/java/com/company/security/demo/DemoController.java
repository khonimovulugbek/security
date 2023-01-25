package com.company.security.demo;

import com.company.security.config.JwtService;
import com.company.security.repository.UserRepository;
import com.company.security.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Author : Khonimov Ulugbek
 * Date : 1/19/2023
 */
@RestController
@RequestMapping("/api/v1/demo-controller")
@RequiredArgsConstructor
public class DemoController {
    private final JwtService jwtService;
    private final UserRepository repository;
    @GetMapping
    public ResponseEntity<String> sayHello(@NonNull HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        Optional<User> user = repository.findByEmail(userEmail);
        System.out.println(user.get());

        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
