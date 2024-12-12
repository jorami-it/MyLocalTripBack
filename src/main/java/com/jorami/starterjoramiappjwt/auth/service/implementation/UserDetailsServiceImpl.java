package com.jorami.starterjoramiappjwt.auth.service.implementation;

import com.jorami.starterjoramiappjwt.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    //GET

    /**
     * Charge les détails d'un utilisateur à partir de son adresse e-mail.
     *
     * @param userEmail l'adresse e-mail de l'utilisateur recherché
     * @return un objet {@link UserDetails} contenant les informations de l'utilisateur
     *          (comme le nom, le mot de passe et les rôles)
     * @throws UsernameNotFoundException si aucun utilisateur correspondant à l'adresse e-mail n'est trouvé
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
