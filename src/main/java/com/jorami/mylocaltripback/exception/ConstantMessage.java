package com.jorami.mylocaltripback.exception;

public class ConstantMessage {

    //VALIDATIONS
    public static final String VALIDATION_NOT_BLANK = "This field cannot be empty.";
    public static final String VALIDATION_EMAIL = "Please provide a valid email address.";
    public static final String VALIDATION_PASSWORD_COMPLEXITY = "The password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@#$%^&+=).";
    public static final String VALIDATION_PASSWORD_MIN_LENGTH = "The password must be at least 8 characters long.";
    public static final String VALIDATION_PASSWORD_MAX_LENGTH = "The password cannot exceed 100 characters.";
    public static final String VALIDATION_PASSWORDS_NOT_MATCHING = "The password and confirmation password must match.";
    public static final String VALIDATION_CODE_LENGTH = "The code must be exactly 6 characters long.";
    public static final String VALIDATION_CODE_NUMERIC = "The code must contain only numeric digits.";
    public static final String VALIDATE_TOKEN = "The token is not valid or expired.";

    //EMAIL
    public static final String EMAIL_DOES_NOT_EXISTS = "This email address does not exist.";
    public static final String EMAIL_ALREADY_EXISTS = "This email address already exists.";

    //CODE
    public static final String CODE_EMAIL_INVALID = "Your code is incorrect.";

    //ROLE
    public static final String ROLE_USER_NOT_INITIALIZED = "Role USER was not initialized.";

    //USER
    public static final String USER_NOT_VERIFIED = "Your account is not verified. Please activate your account or contact support.";


    //EXCEPTIONS
    public static final String ITEM_NOT_FOUND = "The item you are searching is not found.";
    public static final String OPTIMISTIC_LOCKING_EXCEPTION = "It seems that this item is deleted or has been already updated.";

}
