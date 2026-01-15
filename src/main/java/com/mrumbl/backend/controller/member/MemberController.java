package com.mrumbl.backend.controller.member;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.member.dto.*;
import com.mrumbl.backend.service.MemberService;
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
    public Response<SignUpResDto> signUp(@Valid @RequestBody SignUpReqDto reqDto){
//        log.info("[MemberController] POST /api/members invoked. reqDto={}", reqDto);

        SignUpResDto resDto = memberService.signUp(reqDto.getEmail(),
                reqDto.getPassword(),
                reqDto.getName());
        return Response.ok(resDto);
    }

    @PutMapping("/password")
    public Response<Void> changePassword(@AuthenticationPrincipal JwtUser user,
                                         @Valid @RequestBody ChangePasswordReqDto reqDto){
//        log.info("[MemberController] PUT /api/members/password invoked. email={}", user.getEmail());
        memberService.changePassword(user, reqDto.getPassword());

        return Response.ok(null);
    }

    @PutMapping
    public Response<Void> changeAddress(@AuthenticationPrincipal JwtUser user,
                                        @Valid @RequestBody ChangeAddressReqDto reqDto){
//        log.info("[MemberController] PUT /api/members invoked. email={}, reqDto={}", user.getEmail(), reqDto);
        memberService.changeAddress(user, reqDto.getAddress(), reqDto.getAddressDetail(), reqDto.getPostcode());

        return Response.ok(null);
    }

    @PostMapping("/availability")
    public Response<CheckEmailAvailabilityResDto> checkEmailAvailability(@Valid @RequestBody CheckEmailAvailabilityReqDto reqDto){
//        log.info("[MemberController] POST /api/members/availability invoked. email={}", reqDto.getEmail());

        return Response.ok(memberService.checkEmailAvailability(reqDto.getEmail()));
    }

}
