package com.jorami.starterjoramiappjwt.service.implementation;

import com.jorami.starterjoramiappjwt.model.User;
import com.jorami.starterjoramiappjwt.service.EmailService;
import com.jorami.starterjoramiappjwt.strategy.EmailStrategy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;


    /**
     * Envoie un email à un utilisateur en utilisant une stratégie de contenu d'email fournie.
     *
     * @param user l'utilisateur à qui l'email est destiné
     * @param code le code (par exemple, un code de vérification) à inclure dans l'email
     * @param emailStrategy la stratégie utilisée pour préparer le contenu de l'email
     *
     * @throws IllegalStateException si l'envoi de l'email échoue
     * @throws RuntimeException si une erreur d'entrée/sortie se produit lors de l'envoi de l'email
     */
    @Override
    public void sendEmail(User user, String code, EmailStrategy emailStrategy) throws IllegalStateException {
        try {
            this.invalidCertificate();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String content = emailStrategy.prepareEmailContent(user, code);

            helper.setText(content, true);
            helper.setTo(user.getEmail());
            helper.setSubject(emailStrategy.getSubject());
            helper.setFrom("changeme@jorami.be", "StarterAppJoramiJWT");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Failed to send email");
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Impossible to send email");
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Configure une stratégie SSL qui ignore la validation du certificat SSL.
     *
     * @throws NoSuchAlgorithmException si le type d'algorithme SSL spécifié n'est pas disponible
     * @throws KeyManagementException si un problème survient lors de la gestion de la clé SSL
     */
    private void invalidCertificate() throws NoSuchAlgorithmException, KeyManagementException {
        // Créer un SSLContext qui ignore la validation du certificat
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() { return null; }
        }}, new java.security.SecureRandom());

        // Appliquer le SSLContext à toutes les connexions SSL
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

}
