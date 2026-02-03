package com.mrumbl.backend.controller.test;

import com.mrumbl.backend.service.S3Service;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;

    /**
     * S3 파일 업로드용 Presigned URL 발급
     */
    @GetMapping("/presigned-upload")
    public ResponseEntity<String> getPresignedUploadUrl(@RequestParam String path) {
        String presignedUrl = s3Service.getUploadPresignedURL(path);

        return ResponseEntity.status(HttpStatus.OK).body(presignedUrl);
    }

    /**
     * S3 파일 다운로드용 Presigned URL 발급
     */
    @GetMapping("/presigned-download")
    public ResponseEntity<String> getPresignedDownloadUrl(@RequestParam String path) {
        String presignedUrl = s3Service.getDownloadPresignedURL(path);

        return ResponseEntity.status(HttpStatus.OK).body(presignedUrl);
    }
}
