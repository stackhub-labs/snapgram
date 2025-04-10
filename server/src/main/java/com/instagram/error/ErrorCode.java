package com.instagram.error;

public class ErrorCode {
    public static final int SUCCESS = 0;

    public static final int EMAIL_ALREADY_EXISTS = 1001;
    public static final int NICKNAME_ALREADY_EXISTS = 1002;
    public static final int INVALID_EMAIL_FORMAT = 1003;
    public static final int INVALID_PASSWORD_FORMAT = 1004;
    public static final int TERMS_NOT_ACCEPTED = 1005;
    public static final int USER_NOT_FOUND = 1006;
    public static final int PASSWORD_MISMATCH = 1007;

    public static final int INTERNAL_SERVER_ERROR = 2000;
}