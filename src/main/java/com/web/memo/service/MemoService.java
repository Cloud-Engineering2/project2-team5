package com.web.memo.service;

import com.web.memo.dto.MemoDto;
import com.web.memo.entity.Memo;
import com.web.memo.entity.User;
import com.web.memo.repository.MemoRepository;
import com.web.memo.repository.UserRepository;
import com.web.memo.service.enums.MemoType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public List<MemoDto> getMemos(Long userId) {

        return memoRepository.findByUserId(userId)
                .stream()
                .map(memo -> {
                    MemoDto dto = MemoDto.fromEntity(memo);
                    dto.setContent(s3Service.readObjectFromS3(memo.getContent())); // S3에서 읽어온 값으로 content 변경
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public MemoDto getMemo(Long memoId) {

        return memoRepository.findByIdWithSummary(memoId)
                            .map(memo -> {
                                MemoDto dto = MemoDto.fromEntity(memo);
                                dto.setContent(s3Service.readObjectFromS3(memo.getContent())); // S3에서 읽어온 값으로 content 변경
                                return dto;
                            })
                            .orElseThrow();
    }

    @Transactional
    public MemoDto registerMemo(MemoDto memoDto) throws IOException {

        // 사용자 정보 확인
        // User 작업 완료 시 SecurityContext에서 사용자 정보 가져오기

        // 일단은 임시로 그냥 설정
        memoDto.setUserId(1L);
        Long userId = memoDto.getUserId();

        User user = userRepository.findById(userId).orElseThrow(()
                                                -> new NoSuchElementException("No user present"));

        // Memo 내용(content)을 S3에 저장
        String fileName = s3Service.writeMemoToS3(memoDto.getContent(), MemoType.MEMO);

        // MemoDto를 Memo 엔티티로 변환하여 DB에 저장
        memoDto.setContentToFileName(fileName);
        Memo newMemo = memoDto.toEntity(user);
        memoRepository.save(newMemo);

        return memoDto;

    }

    @Transactional
    public MemoDto updateMemo(MemoDto memoDto, Long memoId) throws IOException {

        // 사용자 정보 확인
        // 임시로
        memoDto.setUserId(1L);
        User user = userRepository.getReferenceById(memoDto.getUserId());
        Memo memo = memoRepository.getReferenceById(memoId);

        if (user.getId().equals(memo.getUser().getId())) {

            // Memo 내용(content)을 S3에 저장
            s3Service.deleteFileFromS3(memo.getContent());
            String fileName = s3Service.writeMemoToS3(memoDto.getContent(), MemoType.MEMO);

            // MemoDto를 Memo 엔티티로 변환하여 DB에 저장
            memoDto.setContentToFileName(fileName);
            Memo newMemo = memoDto.toEntity(user);

            memo.updateMemo(memoDto.getTitle(),
                    memoDto.getContent());
        }

        return MemoDto.fromEntity(memo);
    }

    public void deleteMemo(Long memoId) {

        // 사용자 정보 확인


        // s3 서비스 호출해서 s3 파일 삭제
        String fileName = memoRepository.findByIdWithSummary(memoId).orElseThrow().getContent();
        s3Service.deleteFileFromS3(fileName);

        // 메모, 요약이 존재한다면 요약까지 두 번 호출 필요

        // 디비에서 메모 삭제
        memoRepository.deleteById(memoId);


    }
}
