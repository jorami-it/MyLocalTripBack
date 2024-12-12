package com.jorami.mylocaltripback.auth.model;

import com.jorami.mylocaltripback.auth.model.validations.PasswordMatch;
import com.jorami.mylocaltripback.exception.ConstantMessage;
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
@PasswordMatch(message = ConstantMessage.VALIDATION_PASSWORDS_NOT_MATCHING)
public class ResetPasswordRequest {

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

    @NotBlank(message = ConstantMessage.VALIDATION_NOT_BLANK)
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = ConstantMessage.VALIDATION_PASSWORD_COMPLEXITY
    )
    @Size(min = 8, message = ConstantMessage.VALIDATION_PASSWORD_MIN_LENGTH)
    @Size(max = 100, message = ConstantMessage.VALIDATION_PASSWORD_MAX_LENGTH)
    private String resetPassword;

    @NotBlank(message = ConstantMessage.VALIDATION_NOT_BLANK)
    @Size(min = 6, max = 6, message = ConstantMessage.VALIDATION_CODE_LENGTH)
    @Pattern(regexp = "^[0-9]*$", message = ConstantMessage.VALIDATION_CODE_NUMERIC)
    private String code;

}
