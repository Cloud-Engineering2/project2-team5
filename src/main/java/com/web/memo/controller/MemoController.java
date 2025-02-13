package com.web.memo.controller;


import com.web.memo.dto.MemoDto;
import com.web.memo.dto.MemoRequestDto;
import com.web.memo.entity.User;
import com.web.memo.service.MemoService;
import com.web.memo.service.SummaryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController {

    private final MemoService memoService;
    private final SummaryService summaryService;

    @GetMapping
    public String getAllMemos(HttpSession session, Model model) {

        // user 정보 조회
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("nickname", user.getNickname());
        Long userId = user.getId();

        // 해당 사용자의 메모 조회
        List<MemoDto> memos = memoService.getMemos(userId);
        model.addAttribute("memos", memos);

        return "main";
    }

    @GetMapping("/{memo_id}")
    public String getMemo(HttpSession session, @PathVariable Long memo_id, Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("nickname", user.getNickname());

        // 메모 조회
        MemoDto memoDto = memoService.getMemo(memo_id);

        Long summaryId = memoDto.getSummaryId();
        MemoRequestDto memoRequestDto;
        if (summaryId != null) {
            memoRequestDto = MemoRequestDto.fromDtoWithSummary(memoDto, summaryService.getSummary(summaryId).getSummary());
        } else {
            memoRequestDto = MemoRequestDto.fromDtoWithNoSummary(memoDto);
        }
        model.addAttribute("memo", memoRequestDto);

        return "memo/detail";
    }

    @GetMapping({"/edit", "/edit/{memo_id}"})
    public String movetoRegisterMemoForm(HttpSession session, @PathVariable(required = false) Long memo_id, Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("nickname", user.getNickname());

        if (memo_id != null) {
            MemoDto memoDto = memoService.getMemo(memo_id);

            Long summaryId = memoDto.getSummaryId();
            MemoRequestDto memo;
            if (memoDto.getSummaryId() != null) {
                memo = MemoRequestDto.fromDtoWithSummary(memoDto, summaryService.getSummary(summaryId).getSummary());
            } else {
                memo = MemoRequestDto.fromDtoWithNoSummary(memoDto);
            }
            model.addAttribute("memo", memo);

        } else {
            model.addAttribute("memo", new MemoRequestDto());
        }
        return "memo/form";
    }

    @PostMapping
    public String registerMemo(HttpSession session, @ModelAttribute MemoRequestDto memoRequestDto) throws IOException {

        // user 정보 조회
        User user = (User) session.getAttribute("loggedInUser");
        Long userId = user.getId();

        // memo 저장 서비스 호출
        MemoDto memo = memoService.registerMemo(userId, memoRequestDto.toDto());

        // summary 내용이 존재한다면 저장 서비스 호출
        if (!memoRequestDto.getSummary().isEmpty()) {
            // summaryService를 호출하여 S3에 저장 후 DB에 저장
            summaryService.saveSummary(memoRequestDto.getSummary(), memo.getId());
        }

        return "redirect:/memo";
    }

    @PutMapping("/{memo_id}")
    public String updateMemo(
            HttpSession session,
            @ModelAttribute MemoRequestDto memoRequestDto,
            @PathVariable Long memo_id) throws IOException {

        // 메모 저장 서비스 호출
        User user = (User) session.getAttribute("loggedInUser");
        Long userId = user.getId();
        MemoDto memo = memoService.updateMemo(userId, memoRequestDto.toDto(), memo_id);

        // summary 내용이 존재한다면 요약 저장 서비스 호출
        if (!memoRequestDto.getSummary().isEmpty()) {

            // 이전에 기존 메모에 대한 summary가 존재했다면 update, 아니면 save
            // memo를 같이 넘겨서 저장
            if (memo.getSummaryId() != null) {
                // 이전에 기존 메모에 대한 summary가 존재 -> summaryService의 update 메서드 호출
                summaryService.updateSummary(memoRequestDto.getSummary(), memo_id);

            } else {
                // 이전에 기존 메모에 대한 summary가 존재 X -> summaryService의 save 메서드 호출
                summaryService.saveSummary(memoRequestDto.getSummary(), memo_id);
            }
        }

        return "redirect:/memo/" + memo_id;
    }

    @DeleteMapping("/{memo_id}")
    public String deleteMemo(HttpSession session, @PathVariable Long memo_id) {

        User user = (User) session.getAttribute("loggedInUser");
        Long userId = user.getId();

        memoService.deleteMemo(userId, memo_id);

        return "redirect:/memo";
    }


}
