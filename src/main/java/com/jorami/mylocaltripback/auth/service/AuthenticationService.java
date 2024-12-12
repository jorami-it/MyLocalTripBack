package com.jorami.mylocaltripback.auth.service;

import com.jorami.mylocaltripback.auth.model.AuthenticationRequest;
import com.jorami.mylocaltripback.auth.model.AuthenticationResponse;
import com.jorami.mylocaltripback.auth.model.RegistrationRequest;
import com.jorami.mylocaltripback.model.User;
import com.jorami.mylocaltripback.model.VerificationCode;

public interface AuthenticationService {

    void register(RegistrationRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    VerificationCode verifyAccount(String code);

    String generatesCode(User user);

    User regenerateCode(AuthenticationRequest request);

}
