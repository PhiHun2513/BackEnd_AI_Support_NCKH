// File: src/main/java/com/nckh/backend/service/UserService.java
package com.nckh.backend.service;

import com.nckh.backend.entity.User;
import com.nckh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        String assignedRole = "USER";
        if (userRepository.count() == 0) {
            assignedRole = "ADMIN";
            System.out.println(">>> KHỞI TẠO: Tài khoản '" + username + "' là ADMIN hệ thống.");
        }


        User newUser = User.builder()
                .username(username)
                .password(password)
                .role(assignedRole)
                .build();

        return userRepository.save(newUser);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Sai mật khẩu!");
        }
        return user;
    }
}