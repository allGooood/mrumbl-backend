package com.mrumbl.backend.controller.member;

import com.mrumbl.backend.common.Response;
import com.mrumbl.backend.common.jwt.JwtUser;
import com.mrumbl.backend.controller.member.dto.ChangeAddressReqDto;
import com.mrumbl.backend.controller.member.dto.ChangePasswordReqDto;
import com.mrumbl.backend.controller.member.dto.SignUpReqDto;
import com.mrumbl.backend.controller.member.dto.SignUpResDto;
import com.mrumbl.backend.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/password")
    public Response<Void> changePassword(@AuthenticationPrincipal JwtUser user,
                                         @Valid @RequestBody ChangePasswordReqDto reqDto){
        memberService.changePassword(user, reqDto.getPassword());

        return Response.ok(null);
    }

    @PutMapping
    public Response<Void> changeAddress(@AuthenticationPrincipal JwtUser user,
                                        @Valid @RequestBody ChangeAddressReqDto reqDto){
        memberService.changeAddress(user, reqDto.getAddress(), reqDto.getAddressDetail(), reqDto.getPostcode());
        return Response.ok(null);
    }

}
