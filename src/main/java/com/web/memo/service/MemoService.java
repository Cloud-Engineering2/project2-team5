package com.web.memo.service;

import com.web.memo.dto.MemoDto;
import com.web.memo.entity.Memo;
import com.web.memo.entity.User;
import com.web.memo.repository.MemoRepository;
import com.web.memo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public MemoDto getMemo(Long memoId) {

        return memoRepository.findByIdWithSummary(memoId)
                            .map(MemoDto::fromEntity)
                            .orElseThrow();
    }

    @Transactional
    public MemoDto registerMemo(MemoDto memoDto) {

        // 사용자 정보 확인
        // User 작업 완료 시 SecurityContext에서 사용자 정보 가져오기

        // 일단은 임시로 그냥 설정
        memoDto.setUserId(1L);
        Long userId = memoDto.getUserId();

        User user = userRepository.findById(userId).orElseThrow(()
                                                -> new NoSuchElementException("No user present"));

        // Memo 내용(content)을 S3에 저장

        // MemoDto를 Memo 엔티티로 변환하여 DB에 저장
        Memo newMemo = memoDto.toEntity(user);
        memoRepository.save(newMemo);

        return memoDto;

    }

    @Transactional
    public MemoDto updateMemo(MemoDto memoDto, Long memoId) {

        // 사용자 정보 확인
        // 임시로
        memoDto.setUserId(1L);
        User user = userRepository.getReferenceById(memoDto.getUserId());
        Memo memo = memoRepository.getReferenceById(memoId);

        if (user.getId().equals(memo.getUser().getId())) {
            memo.updateMemo(memoDto.getTitle(),
                    memoDto.getContent());
        }

        return MemoDto.fromEntity(memo);
    }

    public static void deleteMemo(Long memoId) {

        // 사용자 정보 확인

        // s3 서비스 호출해서 s3 파일 삭제
        // 메모, 요약이 존재한다면 요약까지 두 번 호출 필요

        // 디비에서 메모 삭제


    }
}
