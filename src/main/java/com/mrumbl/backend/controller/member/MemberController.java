package com.mrumbl.backend.controller.member;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.controller.member.dto.SignUpReqDto;
import com.mrumbl.backend.controller.member.dto.SignUpResDto;
import com.mrumbl.backend.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public Response<SignUpResDto> signUp(@Valid @RequestBody SignUpReqDto reqDto){
        SignUpResDto resDto = memberService.signUp(reqDto.getEmail(),
                reqDto.getPassword(),
                reqDto.getName());
        return Response.ok(resDto);
    }
}
