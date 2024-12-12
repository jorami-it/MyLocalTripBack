package com.jorami.starterjoramiappjwt.service;

import com.jorami.starterjoramiappjwt.auth.model.AuthenticationRequest;
import com.jorami.starterjoramiappjwt.auth.model.ResetPasswordRequest;
import com.jorami.starterjoramiappjwt.model.User;

public interface UserService {

    void confirmResetPassword(AuthenticationRequest request);

    User resetPassword(ResetPasswordRequest request);

}
