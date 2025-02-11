package com.web.memo.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        // 인증 객체가 null이 아니고, 인증된 상태이며, 익명 사용자가 아니라면 이미 로그인된 것으로 판단
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "login"; // 로그인하지 않은 경우에만 로그인 페이지를 보여줌
    }
}
