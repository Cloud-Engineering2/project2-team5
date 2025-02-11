package com.web.memo.controller;

import com.web.memo.dto.UserLoginRequest;
import com.web.memo.entity.User;
import com.web.memo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API (JSON & 폼 요청 모두 가능)
    @PostMapping("/register")
    @ResponseBody // JSON 응답을 보장
    public ResponseEntity<String> register(@RequestParam String email,
                                           @RequestParam String password,
                                           @RequestParam String nickname) {
        try {
            userService.registerUser(email, password, nickname);
            return ResponseEntity.ok("회원가입 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    // JSON 로그인 (Postman 같은 API 요청용)
    @PostMapping(value = "/login", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> loginJson(@RequestBody UserLoginRequest loginRequest, HttpSession session) {
        try {
            boolean success = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword(), session);
            return ResponseEntity.ok("로그인 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("로그인 실패: " + e.getMessage());
        }
    }

    // 브라우저 폼 로그인 (HTML 폼 요청 처리)
// UserController.java
    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public String loginForm(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        System.out.println("로그인 요청 받음 - email: " + email + ", password: " + password); // 디버깅 로그 추가

        try {
            // loginUser 메소드가 성공하면 User 객체를 반환하도록 수정합니다.
            boolean user = userService.loginUser(email, password, session);
            // 로그인 성공 시 세션에 사용자 정보를 저장
            session.setAttribute("loggedInUser", user);

            redirectAttributes.addFlashAttribute("message", "로그인 성공!");
            return "redirect:/home"; // 로그인 성공 시 홈으로 이동
        } catch (IllegalArgumentException e) {
            System.out.println("로그인 실패 - " + e.getMessage()); // 실패 로그 추가
            redirectAttributes.addFlashAttribute("error", "로그인 실패: " + e.getMessage());
            return "redirect:/login"; // 로그인 실패 시 다시 로그인 페이지로 이동
        }
    }




    // 로그인 확인 API
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<String> checkLogin(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("로그인되지 않았습니다.");
        }

        return ResponseEntity.ok("로그인된 사용자: " + authentication.getName());
    }

    // 로그아웃 API
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공!");
    }
}
