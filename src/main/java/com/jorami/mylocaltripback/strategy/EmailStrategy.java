package com.jorami.mylocaltripback.strategy;

import com.jorami.mylocaltripback.model.User;

import java.io.IOException;

public interface EmailStrategy {

    //MESSAGE
    String EMAIL_VALIDATION_TITLE = "Check validation";
    String EMAIL_VALIDATION_MESSAGE = "Please enter the code below to validate your email.";
    String RESET_PASSWORD_TITLE = "Reset password";
    String RESET_PASSWORD_MESSAGE = "Use the code below to authorize the reset of your password.";

    String prepareEmailContent(User user, String code) throws IOException;
    String getSubject();

}
