package com.web.memo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

    @GetMapping("/register") // "/register" URL에 GET 요청이 오면 register.html 반환
    public String showRegisterPage() {
        return "register"; // resources/templates/register.html 파일을 반환
    }
}
