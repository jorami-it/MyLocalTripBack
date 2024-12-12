package com.jorami.starterjoramiappjwt.auth.service;

import com.jorami.starterjoramiappjwt.auth.model.AuthenticationRequest;
import com.jorami.starterjoramiappjwt.auth.model.AuthenticationResponse;
import com.jorami.starterjoramiappjwt.auth.model.RegistrationRequest;
import com.jorami.starterjoramiappjwt.model.User;
import com.jorami.starterjoramiappjwt.model.VerificationCode;

public interface AuthenticationService {

    void register(RegistrationRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    VerificationCode verifyAccount(String code);

    String generatesCode(User user);

    User regenerateCode(AuthenticationRequest request);

}
