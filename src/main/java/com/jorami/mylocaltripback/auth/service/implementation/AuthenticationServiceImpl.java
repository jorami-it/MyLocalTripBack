package com.jorami.mylocaltripback.auth.service.implementation;

import com.jorami.mylocaltripback.auth.model.AuthenticationRequest;
import com.jorami.mylocaltripback.auth.model.AuthenticationResponse;
import com.jorami.mylocaltripback.auth.model.RegistrationRequest;
import com.jorami.mylocaltripback.auth.repository.TokenRepository;
import com.jorami.mylocaltripback.auth.service.AuthenticationService;
import com.jorami.mylocaltripback.auth.service.JwtService;
import com.jorami.mylocaltripback.model.Enum;
import com.jorami.mylocaltripback.model.*;
import com.jorami.mylocaltripback.repository.RoleRepository;
import com.jorami.mylocaltripback.repository.UserRepository;
import com.jorami.mylocaltripback.repository.VerificationCodeRepository;
import com.jorami.mylocaltripback.service.EmailService;
import com.jorami.mylocaltripback.strategy.implementation.ValidationEmailStrategy;
import com.jorami.mylocaltripback.strategy.implementation.VerifiedAccountEmailStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static com.jorami.mylocaltripback.exception.ConstantMessage.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    //POST

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param request La requête contenant les informations nécessaires pour l'enregistrement d'un utilisateur.
     * @throws DataIntegrityViolationException Si l'adresse e-mail existe déjà dans la base de données.
     */
    @Override
    public void register(RegistrationRequest request) {
        Role userRole = roleRepository.findRoleByName(Enum.UserRole.USER.toString())
                .orElseThrow(() -> new NoSuchElementException(ROLE_USER_NOT_INITIALIZED));

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .verified(false)
                .deleted(false)
                .roles(List.of(userRole))
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(EMAIL_ALREADY_EXISTS);
        }

        confirmEmail(user);
    }

    /**
     * Authentifie un utilisateur avec ses identifiants (e-mail et mot de passe).
     *
     * @param request La requête contenant les informations d'authentification de l'utilisateur.
     * @return Un objet de réponse contenant le JWT généré pour l'utilisateur authentifié.
     * @throws DisabledException Si l'utilisateur n'est pas vérifié.
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication auth = null;
        try {
             auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new DisabledException(USER_NOT_VERIFIED);
        }

        User user = ((User) auth.getPrincipal());
        revokeAllUserTokens(user);
        String jwtToken = jwtService.generateToken(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Vérifie le code de vérification pour un utilisateur.
     *
     * @param code Le code de vérification à valider.
     * @return L'objet VerificationCode associé à ce code, ou null si le code est expiré.
     */
    @Override
    public VerificationCode verifyAccount(String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code);
        if(verificationCode == null) {
            throw new NoSuchElementException(CODE_EMAIL_INVALID);
        }
        Calendar calendar = Calendar.getInstance();

        if (verificationCode != null && (verificationCode.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationCodeRepository.delete(verificationCode);
            verificationCodeRepository.flush();
            verificationCode = null;
        }
        if(verificationCode != null) {
            validateCode(verificationCode);
        }

        return verificationCode;
    }

    /**
     * Régénère le code de vérification pour un utilisateur.
     *
     * @param request La requête contenant l'e-mail de l'utilisateur pour lequel régénérer le code.
     * @return L'utilisateur concerné par la régénération du code.
     * @throws DataIntegrityViolationException Si l'utilisateur est déjà vérifié.
     * @throws NoSuchElementException Si l'utilisateur n'est pas trouvé.
     */
    @Override
    public User regenerateCode(AuthenticationRequest request) {
        User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(() -> new NoSuchElementException(EMAIL_DOES_NOT_EXISTS));

        if(user.isVerified()) {
            throw new DataIntegrityViolationException(EMAIL_ALREADY_EXISTS);
        } else {
            this.confirmEmail(user);
        }

        return user;
    }

    /**
     * Valide un code de vérification en mettant à jour l'état de l'utilisateur comme vérifié.
     *
     * @param verificationCode L'objet VerificationCode contenant le code de vérification.
     */
    private void validateCode(VerificationCode verificationCode) {
        User user = verificationCode.getUser();
        user.setVerified(true);
        userRepository.save(user);
        this.validateAccountEmail(user);
    }

    /**
     * Envoie un e-mail pour notifier un utilisateur que son compte a été vérifié avec succès.
     *
     * @param user L'utilisateur dont le compte a été vérifié.
     */
    private void validateAccountEmail(User user) {
        VerifiedAccountEmailStrategy verifiedAccountEmailStrategy = new VerifiedAccountEmailStrategy();
        emailService.sendEmail(user, null, verifiedAccountEmailStrategy);
    }

    /**
     * Envoie un e-mail de confirmation de l'adresse e-mail d'un utilisateur.
     *
     * @param user L'utilisateur pour lequel envoyer l'e-mail de confirmation.
     */
    private void confirmEmail(User user) {
        String code = this.generatesCode(user);
        ValidationEmailStrategy validationEmailStrategy = new ValidationEmailStrategy();
        emailService.sendEmail(user, code, validationEmailStrategy);
    }

    /**
     * Génère un nouveau code de vérification pour un utilisateur.
     *
     * @param user L'utilisateur pour lequel générer le code.
     * @return Le code généré.
     */
    @Override
    public String generatesCode(User user) {
        VerificationCode verificationCode = verificationCodeRepository.findByIdUser(user.getId());
        if(verificationCode != null) {
            verificationCodeRepository.delete(verificationCode);
            verificationCodeRepository.flush();
        }
        String code = generateCode();
        verificationCode = new VerificationCode(code, user);
        verificationCodeRepository.save(verificationCode);
        return code;
    }

    /**
     * Génère un code aléatoire à 6 chiffres.
     *
     * @return Le code généré sous forme de chaîne de caractères.
     */
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);

        return Integer.toString(code);
    }

    /**
     * Révoque tous les tokens valides d'un utilisateur.
     *
     * @param user L'utilisateur dont les tokens doivent être révoqués.
     */
    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Enregistre un token JWT pour un utilisateur.
     *
     * @param user L'utilisateur pour lequel enregistrer le token.
     * @param jwtToken Le token JWT à enregistrer.
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

}
