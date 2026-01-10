package com.mrumbl.backend.service.external;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.UtilErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
    private final JavaMailSender javaMailSender;

    // 기본 구현 (내부용) - 기존 시그니처 유지
    public void sendMimeMessage(String[] receivers, String subject, String content, boolean isHtml){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            messageHelper.setTo(receivers);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, isHtml);

            javaMailSender.send(mimeMessage);
            log.info("Mail sent successfully to {}", Arrays.toString(receivers));

        } catch (MessagingException e) {
            log.error("Failed to send mail to {}: {}", Arrays.toString(receivers), e.getMessage(), e);
            throw new BusinessException(UtilErrorCode.MAIL_SEND_FAILED);
        }
    }

    public void sendMimeMessage(String subject, String content, boolean isHtml, String... receivers) {
        sendMimeMessage(receivers, subject, content, isHtml);
    }

    public void sendMimeMessage(String subject, String content, String... receivers) {
        sendMimeMessage(receivers, subject, content, false);
    }

    public void sendMimeMessage(String[] receivers, String subject, String content){
        sendMimeMessage(receivers, subject, content, false);
    }
}
