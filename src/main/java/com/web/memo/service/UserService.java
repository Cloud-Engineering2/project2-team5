package com.web.memo.service;

import com.web.memo.entity.User;
import com.web.memo.repository.UserRepository;
import com.web.memo.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(String email, String password, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화 저장
        user.setNickname(nickname);
        userRepository.save(user);
    }

    public boolean loginUser(String email, String password, HttpSession session) {
        System.out.println("이메일로 사용자 조회 시도: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        System.out.println("사용자 조회 성공: " + user.getEmail());

        if (!user.getPassword().equals(password)) {
            System.out.println("[비밀번호 불일치] 입력한 비밀번호: " + password);
            System.out.println("[DB 저장된 비밀번호] " + user.getPassword());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // SecurityContextHolder에 사용자 정보 저장 (Spring Security에서 인증 유지)
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션에도 SecurityContext 저장 (이 설정이 없으면 요청 종료 후 초기화됨)
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        session.setAttribute("loggedInUser", user);

        System.out.println("[SecurityContext 설정 완료] 사용자: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return true;
    }

}
