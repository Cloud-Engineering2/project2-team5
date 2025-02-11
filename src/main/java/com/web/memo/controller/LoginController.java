package com.web.memo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html을 반환
    }

//    @GetMapping("/home")
//    public String homePage() {
//        return "home"; // 로그인 성공 시 home.html로 이동
//    }
}
