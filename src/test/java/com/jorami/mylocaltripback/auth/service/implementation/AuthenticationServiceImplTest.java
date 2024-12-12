package com.jorami.mylocaltripback.auth.service.implementation;

import com.jorami.mylocaltripback.auth.model.AuthenticationRequest;
import com.jorami.mylocaltripback.auth.model.AuthenticationResponse;
import com.jorami.mylocaltripback.auth.model.RegistrationRequest;
import com.jorami.mylocaltripback.auth.repository.TokenRepository;
import com.jorami.mylocaltripback.auth.service.JwtService;
import com.jorami.mylocaltripback.model.Enum;
import com.jorami.mylocaltripback.model.Role;
import com.jorami.mylocaltripback.model.User;
import com.jorami.mylocaltripback.model.VerificationCode;
import com.jorami.mylocaltripback.repository.RoleRepository;
import com.jorami.mylocaltripback.repository.UserRepository;
import com.jorami.mylocaltripback.repository.VerificationCodeRepository;
import com.jorami.mylocaltripback.service.EmailService;
import com.jorami.mylocaltripback.strategy.EmailStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.jorami.mylocaltripback.exception.ConstantMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @Nested
    @DisplayName("Tests méthode register")
    class TestsRegister {

        private RegistrationRequest registrationRequest;
        private Role role;
        private User user;

        @BeforeEach
        void setUp() {
            registrationRequest = RegistrationRequest.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@example.com")
                    .password("MyP@22word")
                    .build();

            role = Role.builder()
                    .name(Enum.UserRole.USER.name())
                    .build();

            user = User.builder()
                    .id(1L)
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@example.com")
                    .password("encodedPassword")
                    .accountLocked(false)
                    .verified(false)
                    .deleted(false)
                    .roles(List.of(role))
                    .build();
        }

        @Test
        @DisplayName("Test register normal")
        void testRegister() {
            //Given
            //In setUp()


            //When
            when(roleRepository.findRoleByName(Enum.UserRole.USER.name())).thenReturn(Optional.ofNullable(role));
            when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            authenticationService.register(registrationRequest);


            //Then
            assertNotNull(role);
            assertNotNull(user);
            assertEquals("encodedPassword", user.getPassword());
            verify(userRepository, Mockito.times(1)).save(any(User.class));
            verify(emailService, Mockito.times(1)).sendEmail(any(User.class), Mockito.anyString(), any(EmailStrategy.class));
        }

        @Test
        void register_ShouldRegisterUserSuccessfully_WhenRequestIsValid() {
            // Arrange
            RegistrationRequest request = registrationRequest = RegistrationRequest.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@example.com")
                    .password("MyP@22word")
                    .build();

            Role mockRole = Role.builder()
                    .id(1L)
                    .name(Enum.UserRole.USER.toString())
                    .build();

            when(roleRepository.findRoleByName(Enum.UserRole.USER.toString()))
                    .thenReturn(Optional.of(mockRole));
            when(passwordEncoder.encode("MyP@22word")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Retourne l'utilisateur enregistré

            // Act
            authenticationService.register(request);

            // Assert
            verify(roleRepository).findRoleByName(Enum.UserRole.USER.toString());
            verify(passwordEncoder).encode("MyP@22word");
            verify(userRepository).save(any(User.class));
            verify(emailService).sendEmail(any(User.class), anyString(), any());
        }

        @Test
        void register_ShouldThrowException_WhenRoleNotFound() {
            // Arrange
            RegistrationRequest request = RegistrationRequest.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@example.com")
                    .password("MyP@22word")
                    .build();

            when(roleRepository.findRoleByName(Enum.UserRole.USER.toString()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    authenticationService.register(request)
            );

            assertEquals(ROLE_USER_NOT_INITIALIZED, exception.getMessage());
            verify(roleRepository).findRoleByName(Enum.UserRole.USER.toString());
            verifyNoInteractions(passwordEncoder, userRepository, emailService); // Ces dépendances ne doivent pas être appelées
        }

        @Test
        void register_ShouldThrowException_WhenEmailAlreadyExists() {
            // Arrange
            RegistrationRequest request = RegistrationRequest.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@example.com")
                    .password("MyP@22word")
                    .build();

            Role mockRole = Role.builder()
                    .id(1L)
                    .name(Enum.UserRole.USER.toString())
                    .build();

            when(roleRepository.findRoleByName(Enum.UserRole.USER.toString()))
                    .thenReturn(Optional.of(mockRole));
            when(passwordEncoder.encode("MyP@22word")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate email"));

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () ->
                    authenticationService.register(request)
            );

            assertEquals(EMAIL_ALREADY_EXISTS, exception.getMessage());
            verify(roleRepository).findRoleByName(Enum.UserRole.USER.toString());
            verify(passwordEncoder).encode("MyP@22word");
            verify(userRepository).save(any(User.class));
            verifyNoInteractions(emailService); // Email ne doit pas être envoyé en cas d'erreur
        }

        @Test
        void register_ShouldSendConfirmationEmail_WhenUserIsRegistered() {
            // Arrange
            RegistrationRequest request = RegistrationRequest.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@example.com")
                    .password("securePassword")
                    .build();

            Role mockRole = Role.builder()
                    .id(1L)
                    .name(Enum.UserRole.USER.toString())
                    .build();

            User mockUser = User.builder()
                    .id(1L)
                    .firstname("Jane")
                    .lastname("Smith")
                    .email("jane.smith@example.com")
                    .password("encodedPassword")
                    .verified(false)
                    .accountLocked(false)
                    .deleted(false)
                    .roles(List.of(mockRole))
                    .build();

            when(roleRepository.findRoleByName(Enum.UserRole.USER.toString()))
                    .thenReturn(Optional.of(mockRole));
            when(passwordEncoder.encode("securePassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // Act
            authenticationService.register(request);

            // Assert
            verify(emailService).sendEmail(any(User.class), anyString(), any());
        }
    }


    @Nested
    @DisplayName("Tests méthode authenticate")
    class TestsAuthenticate {

        @Test
        void authenticate_ShouldReturnToken_WhenAuthenticationIsSuccessful() {
            // Arrange
            AuthenticationRequest request = AuthenticationRequest.builder()
                    .email("user@example.com")
                    .password("password")
                    .build();

            User mockUser = User.builder()
                    .id(1L)
                    .email("user@example.com")
                    .verified(true)
                    .build();

            Authentication mockAuth = new UsernamePasswordAuthenticationToken(mockUser, null);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuth);

            when(jwtService.generateToken(mockUser)).thenReturn("mockJwtToken");

            // Act
            AuthenticationResponse response = authenticationService.authenticate(request);

            // Assert
            assertNotNull(response);
            assertEquals("mockJwtToken", response.getToken());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService).generateToken(mockUser);
            verify(tokenRepository).save(any());
        }

        @Test
        void authenticate_ShouldThrowDisabledException_WhenUserIsNotVerified() {
            // Arrange
            AuthenticationRequest request = AuthenticationRequest.builder()
                    .email("user@example.com")
                    .password("password")
                    .build();

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new DisabledException(USER_NOT_VERIFIED));

            // Act & Assert
            DisabledException exception = assertThrows(DisabledException.class, () ->
                    authenticationService.authenticate(request)
            );

            assertEquals(USER_NOT_VERIFIED, exception.getMessage());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(jwtService, tokenRepository); // Ces dépendances ne doivent pas être appelées
        }

        @Test
        void authenticate_ShouldRevokeOldTokensAndSaveNewToken() {
            // Arrange
            AuthenticationRequest request = AuthenticationRequest.builder()
                    .email("user@example.com")
                    .password("password")
                    .build();

            User mockUser = User.builder()
                    .id(1L)
                    .email("user@example.com")
                    .verified(true)
                    .build();

            Authentication mockAuth = new UsernamePasswordAuthenticationToken(mockUser, null);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuth);

            when(jwtService.generateToken(mockUser)).thenReturn("mockJwtToken");

            // Act
            AuthenticationResponse response = authenticationService.authenticate(request);

            // Assert
            assertNotNull(response);
            assertEquals("mockJwtToken", response.getToken());

            // Vérifie que les anciens tokens ont été révoqués
            verify(tokenRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Tests méthode verifyAccount")
    class TestsVerifyAccount {

        @Test
        void verifyAccount_ShouldReturnNull_WhenCodeIsExpired() {
            // Arrange
            String code = "123456";
            VerificationCode mockCode = new VerificationCode();
            mockCode.setCode(code);
            mockCode.setExpirationTime(new Date(System.currentTimeMillis() - 1000)); // Expired

            when(verificationCodeRepository.findByCode(code)).thenReturn(mockCode);

            // Act
            VerificationCode result = authenticationService.verifyAccount(code);

            // Assert
            assertNull(result);
            verify(verificationCodeRepository).delete(mockCode);
            verify(verificationCodeRepository).flush();
        }

        @Test
        void verifyAccount_ShouldReturnCode_WhenCodeIsValid() {
            // Arrange
            String code = "123456";

            // Création du User mocké
            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setEmail("test@example.com");
            mockUser.setVerified(false);

            // Création du VerificationCode mocké
            VerificationCode mockCode = new VerificationCode();
            mockCode.setCode(code);
            mockCode.setExpirationTime(new Date(System.currentTimeMillis() + 10000)); // Not expired
            mockCode.setUser(mockUser);

            // Mock du repository
            when(verificationCodeRepository.findByCode(code)).thenReturn(mockCode);
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // Act
            VerificationCode result = authenticationService.verifyAccount(code);

            // Assert
            assertNotNull(result);
            assertEquals(mockCode, result);
            assertTrue(mockUser.isVerified());
            verify(verificationCodeRepository, never()).delete(any());
            verify(verificationCodeRepository, never()).flush();
            verify(userRepository).save(mockUser); // Vérifie que le User a été sauvegardé
        }

        @Test
        void verifyAccount_ShouldReturnNull_WhenCodeDoesNotExist() {
            // Arrange
            // Arrange
            String code = "123456";

            // Création du User mocké
            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setEmail("test@example.com");
            mockUser.setVerified(false);

            // Création du VerificationCode mocké
            VerificationCode mockCode = new VerificationCode();
            mockCode.setCode(code);
            mockCode.setExpirationTime(new Date(System.currentTimeMillis() + 10000)); // Not expired
            mockCode.setUser(mockUser);

            when(verificationCodeRepository.findByCode(code)).thenReturn(null);

            // Act
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    authenticationService.verifyAccount(code)
            );

            // Assert
            assertEquals(CODE_EMAIL_INVALID, exception.getMessage());
            verify(verificationCodeRepository, never()).delete(any());
            verify(verificationCodeRepository, never()).flush();
        }
    }


    @Nested
    @DisplayName("Tests méthode generatesCode")
    class TestsGeneratesCode {

        @Test
        @DisplayName("Test generatesCode normal")
        void testGenerateCode() {
            User user = new User();
            user.setId(1L);
            Mockito.when(verificationCodeRepository.findByIdUser(1L)).thenReturn(null);

            String code = authenticationService.generatesCode(user);

            assertNotNull(code);
            assertEquals(6, code.length()); // Assuming the code has 6 digits
            Mockito.verify(verificationCodeRepository, Mockito.times(1)).save(Mockito.any(VerificationCode.class));
        }
    }

}
