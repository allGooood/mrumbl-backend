package com.mrumbl.backend.controller.member;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.member.dto.*;
import com.mrumbl.backend.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public Response<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest reqDto){
        return Response.ok(memberService.signUp(reqDto.getEmail(), reqDto.getPassword(), reqDto.getName()));
    }

    @PutMapping("/password")
    public Response<Void> changePassword(@AuthenticationPrincipal JwtUser user,
                                         @Valid @RequestBody ChangePasswordRequest reqDto){
        memberService.changePassword(user.getEmail(), reqDto.getPassword());
        return Response.ok(null);
    }

    @PutMapping
    public Response<Void> changeAddress(@AuthenticationPrincipal JwtUser user,
                                        @Valid @RequestBody ChangeAddressRequest reqDto){
        memberService.changeAddress(user.getEmail(), reqDto.getAddress(), reqDto.getAddressDetail(), reqDto.getPostcode());
        return Response.ok(null);
    }

    @PostMapping("/availability")
    public Response<EmailAvailabilityResponse> checkEmailAvailability(@Valid @RequestBody EmailAvailabilityRequest reqDto){
        return Response.ok(memberService.checkEmailAvailability(reqDto.getEmail()));
    }

}
