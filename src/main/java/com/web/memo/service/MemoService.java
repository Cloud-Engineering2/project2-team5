package com.web.memo.service;

import com.web.memo.dto.MemoDto;
import com.web.memo.entity.Memo;
import com.web.memo.entity.User;
import com.web.memo.repository.MemoRepository;
import com.web.memo.repository.UserRepository;
import com.web.memo.service.enums.MemoType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
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

                    // summary가 존재하면 memoDto에 summaryId 할당
                    if (memo.getSummary() != null) {
                        dto.setSummaryId(memo.getSummary().getId());
                    }
                    return dto;
                })
                .orElseThrow();
    }

    @Transactional
    public MemoDto registerMemo(Long userId, MemoDto memoDto) throws IOException {

        memoDto.setUserId(userId);

        User user = userRepository.findById(userId).orElseThrow(()
                                                -> new NoSuchElementException("No user present"));

        // Memo 내용(content)을 S3에 저장
        String fileName = s3Service.writeMemoToS3(memoDto.getContent(), MemoType.MEMO);

        // MemoDto를 Memo 엔티티로 변환하여 DB에 저장
        memoDto.setContentToFileName(fileName);
        Memo newMemo = memoDto.toEntity(user);
        memoRepository.save(newMemo);

        return MemoDto.fromEntity(newMemo);

    }

    @Transactional
    public MemoDto updateMemo(Long userId, MemoDto memoDto, Long memoId) throws IOException {

        // 사용자 정보 확인
        // 임시로
        memoDto.setUserId(userId);
        User user = userRepository.getReferenceById(memoDto.getUserId());
        Memo memo = memoRepository.getReferenceById(memoId);

        if (user.getId().equals(memo.getUser().getId())) {

            // Memo의 이전 내용(content)을 삭제 후 새로운 내용을 S3에 저장
            s3Service.deleteFileFromS3(memo.getContent());
            String fileName = s3Service.writeMemoToS3(memoDto.getContent(), MemoType.MEMO);

            // MemoDto를 Memo 엔티티로 변환하여 DB에 저장
            memo.updateMemo(memoDto.getTitle(), fileName);
        } else {
            throw new IllegalArgumentException("이 메모를 수정할 권한이 없습니다.");
        }

        return MemoDto.fromEntity(memo);
    }

    public void deleteMemo(Long userId, Long memoId) {

        // 사용자 정보 확인
        Memo memo = memoRepository.findByIdWithSummary(memoId).orElseThrow();

        if (memo.getUser().getId().equals(userId)) {
            // s3 서비스 호출해서 s3 파일 삭제
            String fileName = memo.getContent();
            s3Service.deleteFileFromS3(fileName);

            // 요약이 존재한다면 요약에 대한 s3 파일도 삭제
            if (memo.getSummary() != null) {
                s3Service.deleteFileFromS3(memo.getSummary().getSummary());
            }

            // 디비에서 메모 삭제
            // 메모에 대한 요약이 있다면 요약까지 자동 삭제됨
            memoRepository.deleteById(memoId);
        } else {
            throw new IllegalArgumentException("이 메모를 수정할 권한이 없습니다.");
        }

    }
}
