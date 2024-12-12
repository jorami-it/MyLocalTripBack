package com.jorami.mylocaltripback.service;

import com.jorami.mylocaltripback.model.User;
import com.jorami.mylocaltripback.strategy.EmailStrategy;

public interface EmailService {

    void sendEmail(User user, String code, EmailStrategy emailStrategy) throws IllegalStateException;

}
