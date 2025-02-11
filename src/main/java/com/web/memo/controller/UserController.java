package com.web.memo.controller;

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
    // 클래스 레벨 매핑 "/user" 하에서, 전체 URL은 "/user/register"
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

    // 로그인 확인 API (필요시 사용)
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
