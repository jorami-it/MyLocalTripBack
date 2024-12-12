package com.jorami.starterjoramiappjwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "_user")
@EntityListeners({AuditingEntityListener.class})
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private LocalDate birthDate;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean accountLocked;

    private boolean verified;

    private boolean deleted;

    @Version
    private Long version;

    @Column(updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(insertable = false)
    private String modifiedBy;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime modifiedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;


    /**
     * @return l'email de ce Principal.
     */
    @Override
    public String getName() {
        return email;
    }

    /**
     * Renvoie la collection des autorisations accordées à l'utilisateur.
     *
     * @return La collection des autorisations, représentées par des instances `GrantedAuthority`.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(r -> new SimpleGrantedAuthority((r.getName())))
                .collect(Collectors.toList());
    }

    /**
     * @return le mot de passe de cet utilisateur.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @return l'email de cet utilisateur.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * @return 'true' si le compte n'est pas expiré, 'false' sinon.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @return 'true' si le compte est déverrouillé, 'false' sinon.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    /**
     * @return 'true' si les informations d'identification de l'utilisateur sont valides et non périmées, 'false' sinon.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return 'true' si le compte est activé, 'false' sinon.
     */
    @Override
    public boolean isEnabled() {
        return verified;
    }

    public String getFullName() {
        return firstname.substring(0, 1).toUpperCase() + firstname.substring(1).toLowerCase() + " " + lastname.toUpperCase();
    }

}
