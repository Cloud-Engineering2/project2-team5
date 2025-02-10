package com.web.memo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login"; // 기본 페이지를 로그인 페이지로 리다이렉트
    }
}
