package com.jorami.mylocaltripback.auth.controller;

import com.jorami.mylocaltripback.auth.model.AuthenticationRequest;
import com.jorami.mylocaltripback.auth.model.AuthenticationResponse;
import com.jorami.mylocaltripback.auth.model.RegistrationRequest;
import com.jorami.mylocaltripback.auth.service.AuthenticationService;
import com.jorami.mylocaltripback.auth.service.LogoutService;
import com.jorami.mylocaltripback.dto.UserDto;
import com.jorami.mylocaltripback.model.VerificationCode;
import com.jorami.mylocaltripback.util.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Transactional
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@ControllerAdvice
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final LogoutService logoutService;


    //GET
    /**
     * Point de terminaison pour vérifier l'état de santé de l'API.
     * Ce point de terminaison retourne un simple message texte indiquant que l'API fonctionne et est saine.
     * Il peut être utilisé pour des contrôles de base et la surveillance du service.
     * @return Un message texte indiquant l'état de santé de l'API.
     */
    @GetMapping("/health")
    public String getHealth() {
        return "The API is healthy";
    }


    //POST
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    //PUT
    @PutMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestParam String code) {
        VerificationCode verificationCode = authenticationService.verifyAccount(code);
        return ResponseEntity.ok(verificationCode);
    }

    @PutMapping("/regenerate-code")
    public ResponseEntity<?> regenerateCode(@RequestBody @Valid AuthenticationRequest request) {
        UserDto userDto = userMapper.toUserDto(authenticationService.regenerateCode(request));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody HttpServletRequest request, @RequestBody HttpServletResponse response, @RequestBody Authentication authentication) {
        logoutService.logout(request, response, authentication);
    }

}
