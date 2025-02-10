package com.web.memo.controller;

import com.web.memo.dto.UserLoginRequest;
import com.web.memo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller  // @RestController → @Controller 변경
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam String nickname,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(email, password, nickname);
            redirectAttributes.addFlashAttribute("message", "회원가입 성공!");
            return "redirect:/login"; // 회원가입 후 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register"; // 실패하면 회원가입 페이지로 다시 이동
        }
    }

    // 로그인 API
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        RedirectAttributes redirectAttributes) {
        try {
            userService.loginUser(email, password);
            redirectAttributes.addFlashAttribute("message", "로그인 성공!");
            return "redirect:/home"; // 로그인 성공 시 home.html로 이동
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "로그인 실패: " + e.getMessage());
            return "redirect:/login"; // 로그인 실패 시 다시 로그인 페이지로 이동
        }
    }
}
