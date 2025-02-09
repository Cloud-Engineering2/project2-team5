package com.web.memo.service;

import com.web.memo.service.enums.MemoType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.web.memo.service.enums.MemoType.*;
import static java.nio.charset.StandardCharsets.*;


@Service
@Transactional
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Client s3Client;

    private static final String MEMO_DIR = "memo";
    private static final String SUMMARY_DIR = "summary";
    private static final String DELIMITER = "_";
    private static final String DIR_DELIMITER = "/";
    private static final String BASE_PATH = "/Users/cj/CE/97.data/";


    public String readObjectFromS3(String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(getS3Key(fileName))
                    .build();

            // S3 객체 가져오기
            ResponseInputStream<?> s3Object = s3Client.getObject(getObjectRequest);

            // 파일을 읽어서 문자열로 변환
            String str = new BufferedReader(new InputStreamReader(s3Object, UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            System.out.println("반환된 파일의 문자열 - " + str);
            return str;

        } catch (S3Exception e) {
            throw new RuntimeException("S3에서 파일을 가져오는 중 오류 발생: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    // 로컬에 파일 생성해서 저장, S3에 txt 파일 업로드 후 파일 이름 반환
    public String writeMemoToS3(String content, MemoType type) throws IOException {

        // 파일명을 UUID 기반으로 생성 (중복 방지)
        String uniqueId = UUID.randomUUID().toString();

        // 타입에 따라 디렉토리 변경
        String directory = (type == MEMO) ? MEMO_DIR : SUMMARY_DIR;
        // uuid 붙여서 파일명 생성
        String fileName = uniqueId + DELIMITER + type.name().toLowerCase() + ".txt";
        // local 에 파일 저장할 경로 지정
        String localPath = BASE_PATH + directory;
        // file 이 저장되는 위치
        String localFilePath = localPath + DIR_DELIMITER + fileName;

        // 디렉토리 없으면 생성
        Files.createDirectories(Paths.get(localPath));

        // 업로드 파일
        uploadFileProcess(content, localFilePath, directory + DIR_DELIMITER + fileName);

        return fileName;
    }

    private void uploadFileProcess(String content, String localFilePath, String s3Key) {
        try {
            // 로컬에 파일 생성
            File file = createLocalFile(content, localFilePath);

            // S3 업로드
            uploadFileToS3(file, s3Key);

            // 로컬 파일 삭제
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류 발생", e);
        }
    }

    // S3에서 파일 삭제
    public void deleteFileFromS3(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(getS3Key(fileName))
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // fileName 으로 s3Key 반환
    private String getS3Key(String fileName) {
        return fileName.contains("_memo.txt") ? MEMO_DIR + DIR_DELIMITER + fileName
                : SUMMARY_DIR + DIR_DELIMITER + fileName;
    }

    // 로컬에 파일 생성해서 반환
    private File createLocalFile(String content, String filePath) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    // S3에 파일 업로드
    private void uploadFileToS3(File file, String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
        } catch (S3Exception e) {
            throw new RuntimeException("S3 업로드 중 오류 발생: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}
