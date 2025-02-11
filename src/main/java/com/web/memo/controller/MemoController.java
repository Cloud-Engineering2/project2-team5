package com.web.memo.controller;


import com.web.memo.dto.MemoDto;
import com.web.memo.dto.MemoRequestDto;
import com.web.memo.service.MemoService;
import com.web.memo.service.SummaryService;
import lombok.RequiredArgsConstructor;
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
    public String movetoRegisterMemoForm(@PathVariable(required = false) Long memo_id, Model model) {

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
        return "/memo/form";
    }

    @PostMapping
    public String registerMemo(@ModelAttribute MemoRequestDto memoRequestDto) throws IOException {

        // memo 저장 서비스 호출
        MemoDto memo = memoService.registerMemo(memoRequestDto.toDto());

        // summary 내용이 존재한다면 저장 서비스 호출
        if (!memoRequestDto.getSummary().isEmpty()) {
            // summaryService를 호출하여 S3에 저장 후 DB에 저장
            summaryService.saveSummary(memoRequestDto.getSummary(), memo.getId());
        }

        return "redirect:/memo";
    }

    @PutMapping("/{memo_id}")
    public String updateMemo(
            @ModelAttribute MemoRequestDto memoRequestDto,
            @PathVariable Long memo_id) throws IOException {

        // 메모 저장 서비스 호출

        MemoDto memo = memoService.updateMemo(memoRequestDto.toDto(), memo_id);

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
    public String deleteMemo(@PathVariable Long memo_id) {

        memoService.deleteMemo(memo_id);

        return "redirect:/memo";
    }


}
