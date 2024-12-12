package com.jorami.mylocaltripback.strategy.implementation;

import com.jorami.mylocaltripback.model.User;
import com.jorami.mylocaltripback.strategy.EmailStrategy;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VerifiedAccountEmailStrategy implements EmailStrategy {

    @Override
    public String prepareEmailContent(User user, String code) throws IOException {
        String content = loadTemplate("templates/account_verified_template.html");
        return content.replace("[[name]]", user.getFullName());
    }

    @Override
    public String getSubject() {
        return "Validated account";
    }

    private String loadTemplate(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(new ClassPathResource(path).getURI())));
    }

}
