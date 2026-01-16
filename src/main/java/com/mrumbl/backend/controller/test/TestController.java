package com.mrumbl.backend.controller.test;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.test.dto.TestDto;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Hidden
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public Response<String> hello(){
        log.info("TestController - hello");
        return Response.ok("hello");
    }

    @PostMapping("/password")
    public Response<String> password() {
        log.info("TestController - password");

        return Response.ok("password verify");
    }

    @GetMapping("/members")
    public Response<TestDto> members(){
        log.info("TestController - GET members");

        return Response.ok(TestDto.builder()
                        .accessToken("accessToken")
                        .refreshToken("refreshToken")
                .build());
    }

    @PutMapping("/members")
    public Response<String> members2() {
        log.info("TestController - PUT members");

        return Response.ok("PUT members");
    }
}
