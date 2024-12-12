package com.jorami.mylocaltripback.auth.model.validations;

import com.jorami.mylocaltripback.auth.model.ResetPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, ResetPasswordRequest> {

    @Override
    public boolean isValid(ResetPasswordRequest request, ConstraintValidatorContext context) {
        // Vérifier si password et resetPassword correspondent
        if (request.getPassword() == null || request.getResetPassword() == null) {
            return false;
        }
        boolean matches = request.getPassword().equals(request.getResetPassword());

        // Ajouter un message d'erreur pour le champ resetPassword (si besoin)
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Les mots de passe ne correspondent pas.")
                    .addPropertyNode("resetPassword")
                    .addConstraintViolation();
        }

        return matches;
    }

}
