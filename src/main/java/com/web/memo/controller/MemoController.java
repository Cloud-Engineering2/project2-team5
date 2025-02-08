package com.web.memo.controller;


import com.web.memo.dto.MemoDto;
import com.web.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public String getAllMemos(Model model) {

//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        Long userId = ((CustomUserDetails) userDetails).getUserId(); // ID 가져오기

        // userDetails 생기면 삭제
        Long userId = 1L;

        // 해당 사용자의 메모 조회

        List<MemoDto> memos = memoService.getMemos(userId);
        model.addAttribute("memos", memos);

        return "main";
    }

    @GetMapping("/{memo_id}")
    public String getMemo(@PathVariable Long memo_id, Model model) {

        // 현재 로그인한 사용자 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        Long userId = ((CustomUserDetails) userDetails).getUserId(); // 로그인한 사용자 ID


        // 메모 조회
        MemoDto memoDto = memoService.getMemo(memo_id);

        // 해당 메모가 로그인한 사용자의 것이 맞는지 확인
//        if (!memoDto.getUserId().equals(userId)) {
//            throw new AccessDeniedException("해당 메모를 조회할 권한이 없습니다.");
//        }

        model.addAttribute("memo", memoDto);

        return "memo/detail";
    }

    @GetMapping("/edit")
    public String movetoRegisterMemoForm() {
        return "/memo/form";
    }

    @PostMapping
    public String registerMemo(@ModelAttribute MemoDto memoDto) {

        memoService.registerMemo(memoDto);

        return "redirect:/memo";
    }


//    @PutMapping("/{memo_id}")

//    @DeleteMapping("/{memo_id}")

}
