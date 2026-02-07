package com.mrumbl.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
//@ConditionalOnProperty(name = "cloud.aws.active", havingValue = "true")
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String getUploadPresignedURL(String key) {
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                req -> req.signatureDuration(Duration.ofMinutes(15)) // 15분 유효기간
                        .putObjectRequest(
                                PutObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(key)
                                        .build()
                        )
        );
        return presignedRequest.url().toString();
    }

    public String getDownloadPresignedURL(String key) {
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                req -> req.signatureDuration(Duration.ofMinutes(15)) // 15분 유효기간
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(key)
                                        .build()
                        )
        );
        return presignedRequest.url().toString();
    }

    public static String completeImageUrl(String path){
        return String.format("https://%s.s3.%s.amazonaws.com/%s", "mrumbl-bucket", "ap-northeast-2", path);
    }
}



