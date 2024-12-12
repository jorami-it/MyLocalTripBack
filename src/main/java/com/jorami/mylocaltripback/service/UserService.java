package com.jorami.mylocaltripback.service;

import com.jorami.mylocaltripback.auth.model.AuthenticationRequest;
import com.jorami.mylocaltripback.auth.model.ResetPasswordRequest;
import com.jorami.mylocaltripback.model.User;

public interface UserService {

    void confirmResetPassword(AuthenticationRequest request);

    User resetPassword(ResetPasswordRequest request);

}
