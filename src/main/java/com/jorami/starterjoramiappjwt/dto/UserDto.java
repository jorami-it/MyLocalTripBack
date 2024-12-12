package com.jorami.starterjoramiappjwt.dto;

import com.jorami.starterjoramiappjwt.model.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String firstname;
    private String lastname;
    private LocalDate birthDate;
    private String email;
    private Long version;
    private List<Role> roles;

}
