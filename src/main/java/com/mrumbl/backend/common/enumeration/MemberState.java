package com.mrumbl.backend.common.enumeration;

import com.mrumbl.backend.common.util.EnumConverter;

public enum MemberState {
    ACTIVE,
    WITHDRAW;

    public static MemberState from(String memberStateStr) {
        return EnumConverter.from(memberStateStr, MemberState.class, "MemberState");
    }
}