package com.web.memo.service;

import com.web.memo.entity.User;
import com.web.memo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String email, String password, String nickname) {
        if (userRepository.existsByEmail(email)) { // ✅ UserRepository에 추가한 메서드 사용
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        try {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // 비밀번호 해싱
            user.setNickname(nickname);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("DB 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public void loginUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
    }
}
