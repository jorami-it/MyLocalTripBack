package com.jorami.starterjoramiappjwt.service;

import com.jorami.starterjoramiappjwt.model.User;
import com.jorami.starterjoramiappjwt.strategy.EmailStrategy;

public interface EmailService {

    void sendEmail(User user, String code, EmailStrategy emailStrategy) throws IllegalStateException;

}
