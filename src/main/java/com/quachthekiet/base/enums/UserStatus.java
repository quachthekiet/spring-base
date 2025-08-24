package com.quachthekiet.base.enums;

public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    SUSPENDED("suspended");

    private String status;

    UserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static UserStatus fromStatus(String status) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.getStatus().equals(status)) {
                return userStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
