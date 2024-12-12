package com.jorami.starterjoramiappjwt.service.implementation;

import com.jorami.starterjoramiappjwt.auth.model.AuthenticationRequest;
import com.jorami.starterjoramiappjwt.auth.model.ResetPasswordRequest;
import com.jorami.starterjoramiappjwt.auth.service.AuthenticationService;
import com.jorami.starterjoramiappjwt.model.User;
import com.jorami.starterjoramiappjwt.model.VerificationCode;
import com.jorami.starterjoramiappjwt.repository.UserRepository;
import com.jorami.starterjoramiappjwt.repository.VerificationCodeRepository;
import com.jorami.starterjoramiappjwt.service.EmailService;
import com.jorami.starterjoramiappjwt.service.UserService;
import com.jorami.starterjoramiappjwt.strategy.implementation.ResetPasswordEmailStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.jorami.starterjoramiappjwt.exception.ConstantMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;


    /**
     * Confirme la réinitialisation du mot de passe en envoyant un code de réinitialisation à l'utilisateur.
     *
     * @param request la demande d'authentification contenant l'email de l'utilisateur
     *
     * @throws NoSuchElementException si l'utilisateur correspondant à l'email n'est pas trouvé
     */
    @Override
    public void confirmResetPassword(AuthenticationRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(() -> new NoSuchElementException(ITEM_NOT_FOUND));

        String code = authenticationService.generatesCode(user);
        ResetPasswordEmailStrategy resetPasswordEmailStrategy = new ResetPasswordEmailStrategy();
        emailService.sendEmail(user, code, resetPasswordEmailStrategy);
    }

    /**
     * Réinitialise le mot de passe de l'utilisateur en utilisant le code de vérification fourni.
     *
     * @param request la demande de réinitialisation de mot de passe contenant l'email, le code et le nouveau mot de passe
     * @return l'utilisateur mis à jour avec le nouveau mot de passe
     *
     * @throws NoSuchElementException si le code de vérification est invalide ou si l'utilisateur correspondant à l'email n'est pas trouvé
     * @throws OptimisticLockingFailureException si une exception de verrouillage optimiste se produit lors de l'enregistrement du mot de passe
     */
    @Override
    public User resetPassword(ResetPasswordRequest request) {
        VerificationCode verificationCode = verificationCodeRepository.findByCode(request.getCode());
        if(verificationCode == null) {
            throw new NoSuchElementException(CODE_EMAIL_INVALID);
        }

        User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(() -> new NoSuchElementException(ITEM_NOT_FOUND));

        try {
            user.setPassword(passwordEncoder.encode(request.getResetPassword()));
            user = userRepository.saveAndFlush(user);
        } catch (OptimisticLockingFailureException e) {
            throw new OptimisticLockingFailureException(OPTIMISTIC_LOCKING_EXCEPTION);
        }

        return user;
    }

}
