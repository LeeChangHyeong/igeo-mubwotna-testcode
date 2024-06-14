package com.sparta.igeomubwotna.entity;

public enum UserStatusEnum {
    ACTIVE(Authority.ACTIVE),  // 정상
    WITHDRAWN(Authority.WITHDRAWN);  // 탈퇴

    private final String status;

    UserStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public static class Authority {
        public static final String ACTIVE = "ROLE_STATUS_ACTIVE";
        public static final String WITHDRAWN = "ROLE_STATUS_WITHDRAWN";
    }
}