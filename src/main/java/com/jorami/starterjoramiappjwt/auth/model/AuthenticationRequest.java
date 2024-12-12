package com.jorami.starterjoramiappjwt.auth.model;

import com.jorami.starterjoramiappjwt.exception.ConstantMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @NotBlank(message = ConstantMessage.VALIDATION_NOT_BLANK)
    @Email(message = ConstantMessage.VALIDATION_EMAIL)
    private String email;

    @NotBlank(message = ConstantMessage.VALIDATION_NOT_BLANK)
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = ConstantMessage.VALIDATION_PASSWORD_COMPLEXITY
    )
    @Size(min = 8, message = ConstantMessage.VALIDATION_PASSWORD_MIN_LENGTH)
    @Size(max = 100, message = ConstantMessage.VALIDATION_PASSWORD_MAX_LENGTH)
    private String password;

}
