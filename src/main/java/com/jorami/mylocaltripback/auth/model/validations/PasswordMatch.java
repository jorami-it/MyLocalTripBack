package com.jorami.mylocaltripback.auth.model.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordMatchValidator.class) // Liée au validateur
@Target(ElementType.TYPE) // Applicable au niveau de la classe
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

    String message() default "Les mots de passe ne correspondent pas.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
