package com.web.memo.service;

import com.web.memo.dto.MemoDto;
import com.web.memo.entity.Memo;
import com.web.memo.entity.User;
import com.web.memo.repository.MemoRepository;
import com.web.memo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    public List<MemoDto> getMemos(Long userId) {

        return memoRepository.findByUserId(userId)
                .stream()
                .map(MemoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public MemoDto getMemo(Long mid) {

        return memoRepository.findByIdWithSummary(mid)
                            .map(MemoDto::fromEntity)
                            .orElseThrow();
    }

    public void registerMemo(MemoDto memoDto) {

        // 사용자 정보 가져오기
        // User 작업 완료 시 SecurityContext에서 사용자 정보 가져오기

        // 일단은 임시로 그냥 설정
        memoDto.setUserId(1L);
        Long userId = memoDto.getUserId();

        User user = userRepository.findById(userId).orElseThrow(()
                                                -> new NoSuchElementException("No user present"));

        // MemoDto를 Memo 엔티티로 변환
        Memo newMemo = memoDto.toEntity(user);

        // Summary가 존재하는 경우, summaryService를 호출하여 Summary를 DB에 저장
//        if (memoDto.getSummary() != null) {
//            Summary summary = new Summary();
//            summary.setMemo(newMemo); // Memo와 연결
//            summary.setSummary(memoDto.getSummary()); // 메모의 요약 설정
//
//            // DB에 저장
//            summaryService.save(summary);
//        }

        // Memo 엔티티를 DB에 저장
        memoRepository.save(newMemo);

        // Memo 내용(content)을 S3에 저장

        // Summary가 존재하는 경우, Summary의 내용(summary)도 S3에 저장

    }

}
