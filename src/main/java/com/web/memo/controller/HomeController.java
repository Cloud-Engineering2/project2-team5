package com.web.memo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, RedirectAttributes redirectAttributes) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        System.out.println("[홈 화면 접근] 세션 정보: " + loggedInUser);

        if (loggedInUser == null) {
            System.out.println("[세션 없음] 로그인 페이지로 리디렉트됨");
            redirectAttributes.addFlashAttribute("error", "로그인 후 접근하세요.");
            return "redirect:/login";
        }

        return "home";
    }
}
