package com.web.memo.controller;


import com.web.memo.dto.MemoDto;
import com.web.memo.dto.SummaryDto;
import com.web.memo.service.MemoService;
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

        // 메모에 대한 summary가 존재한다면
        if (memoDto.getSummaryId() != null) {

            // id로 summary 찾아와서 model에 추가
        } else {
            model.addAttribute("summary", new SummaryDto());
        }

        return "memo/detail";
    }

    @GetMapping({"/edit", "/edit/{memo_id}"})
    public String movetoRegisterMemoForm(@PathVariable(required = false) Long memo_id, Model model) {

        if (memo_id != null) {
            MemoDto memo = memoService.getMemo(memo_id);
            model.addAttribute("memo", memo);

            // summary가 있을 경우 가져오기
            Long summaryId = memo.getSummaryId();
            if (summaryId != null) {
                // SummaryDto summary = summaryService.getSummary();
                // model.addAttribute("summary", summary);
            } else {
                model.addAttribute("summary", new SummaryDto());
            }
        } else {
            model.addAttribute("memo", new MemoDto());
            model.addAttribute("summary", new SummaryDto());
        }

        return "/memo/form";
    }

    @PostMapping
    public String registerMemo(
            @ModelAttribute MemoDto memoDto, @ModelAttribute SummaryDto summaryDto
    ) throws IOException {

        // memo 저장 서비스 호출
        MemoDto memo = memoService.registerMemo(memoDto);

        // summary 내용이 존재한다면 저장 서비스 호출
//        if (summaryDto.getSummary() != null) {
//            // 내용을 S3에 저장
//
//            // summaryService를 호출하여 DB에 저장
//            // memo를 같이 넘겨서 저장
//
//        }

        return "redirect:/memo";
    }

    @PutMapping("/{memo_id}")
    public String updateMemo(
            @ModelAttribute MemoDto memoDto,
            @ModelAttribute SummaryDto summaryDto,
            @PathVariable Long memo_id) throws IOException {

        // 메모 저장 서비스 호출
        MemoDto memo = memoService.updateMemo(memoDto, memo_id);

        // summary 내용이 존재한다면 요약 저장 서비스 호출
        if (summaryDto.getSummary() != null) {

            // 내용을 S3에 저장

            // 이전에 기존 메모에 대한 summary가 존재했다면 update, 아니면 register
            // memo를 같이 넘겨서 저장
            if (memo.getSummaryId() != null) {
                // summaryService의 update 메서드 호출
                // 이때 update 메서드에서 기존 summary에 대해 저장한 S3 파일의 삭제까지 해줘야 함

            } else {
                // summaryService의 register 메서드 호출
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
