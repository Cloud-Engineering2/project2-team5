package com.web.memo.service;

import com.web.memo.dto.SummaryDto;
import com.web.memo.entity.Memo;
import com.web.memo.entity.Summary;
import com.web.memo.repository.MemoRepository;
import com.web.memo.repository.SummaryRepository;
import com.web.memo.service.enums.MemoType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class SummaryService {

    private final OpenAIService openAIService;
    private final S3Service s3Service;
    private final SummaryRepository summaryRepository;
    private final MemoRepository memoRepository;


    public String setSummary(String summary) {

        String summaryText = openAIService.summarizeText(summary);

        return summaryText;
    }

    public SummaryDto getSummary(Long summaryId) {

        SummaryDto summaryDto = SummaryDto.fromEntity(summaryRepository.findById(summaryId).orElseThrow());
        String summaryText = s3Service.readObjectFromS3(summaryDto.getSummary());
        summaryDto.setSummary(summaryText);

        return summaryDto;
    }

    @Transactional
    public void saveSummary(String summaryText, Long memoId) throws IOException {

        SummaryDto summaryDto = SummaryDto.of(summaryText);

        String fileName = s3Service.writeMemoToS3(summaryDto.getSummary(), MemoType.SUMMARY);

        // SummaryDto를 Summary 엔티티로 변환하여 DB에 저장
        summaryDto.setSummary(fileName);

        Memo memo = memoRepository.findById(memoId).orElseThrow();
        Summary summary = summaryDto.toEntity(memo);

        summaryRepository.save(summary);

    }

    @Transactional
    public void updateSummary(String summaryText, Long memoId) throws IOException {

        SummaryDto summaryDto = SummaryDto.of(summaryText);

        // 이전 summary에 대해 저장된 s3 파일 삭제
        Summary summary = summaryRepository.findByMemoId(memoId);
        s3Service.deleteFileFromS3(summary.getSummary());

        String fileName = s3Service.writeMemoToS3(summaryDto.getSummary(), MemoType.SUMMARY);

        // DB에 새로 바뀐 s3 파일명 저장
        summary.updateContent(fileName);
    }
}
