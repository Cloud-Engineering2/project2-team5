package com.web.memo.controller;

import com.web.memo.dto.UserLoginRequest;
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

    // 회원가입 API (폼 요청 처리)
    // 클래스 레벨 매핑이 "/user"이므로, 메소드 매핑은 "/register"가 되어 전체 URL은 "/user/register"
    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String nickname,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(email, password, nickname);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해 주세요.");
            return "redirect:/login";  // 회원가입 성공 후 로그인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
            return "redirect:/register";
        }
    }

    // JSON 로그인 (Postman 같은 API 요청용)
    @PostMapping(value = "/login", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> loginJson(@RequestBody UserLoginRequest loginRequest, HttpSession session) {
        try {
            // User 객체를 반환하도록 수정 (반환타입이 boolean이 아니라 User)
            boolean user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword(), session);
            // (필요하다면) 세션에 로그인한 사용자 저장
            session.setAttribute("loggedInUser", user);
            return ResponseEntity.ok("로그인 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("로그인 실패: " + e.getMessage());
        }
    }

    // 브라우저 폼 로그인 (HTML 폼 요청 처리)
    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public String loginForm(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        System.out.println("로그인 요청 받음 - email: " + email + ", password: " + password);

        try {
            // User 객체를 반환하도록 수정
            boolean user = userService.loginUser(email, password, session);
            // 로그인 성공 시, 세션에 User 객체 저장
            session.setAttribute("loggedInUser", user);

            redirectAttributes.addFlashAttribute("message", "로그인 성공!");
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            System.out.println("로그인 실패 - " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "로그인 실패: " + e.getMessage());
            return "redirect:/login";
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
}
