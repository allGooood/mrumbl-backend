package com.mrumbl.backend.controller.test;

import com.mrumbl.backend.service.external.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/mail/html")
    public void sendMimeMessage(){
        mailService.sendMimeMessage("title", "content", "ming920331@gmail.com");
    }
}
