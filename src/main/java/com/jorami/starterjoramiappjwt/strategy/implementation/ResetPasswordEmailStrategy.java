package com.jorami.starterjoramiappjwt.strategy.implementation;

import com.jorami.starterjoramiappjwt.model.User;
import com.jorami.starterjoramiappjwt.strategy.EmailStrategy;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResetPasswordEmailStrategy implements EmailStrategy {

    @Override
    public String prepareEmailContent(User user, String code) throws IOException {
        String content = loadTemplate("templates/code_validation_template.html");
        return content.replace("[[Name]]", user.getFullName())
                .replace("[[CODE]]", code)
                .replace("[[Title]]", RESET_PASSWORD_TITLE)
                .replace("[[Message]]", RESET_PASSWORD_MESSAGE);
    }

    @Override
    public String getSubject() {
        return "Forgot password";
    }

    private String loadTemplate(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(new ClassPathResource(path).getURI())));
    }

}
