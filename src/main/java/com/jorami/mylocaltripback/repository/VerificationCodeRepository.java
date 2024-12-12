package com.jorami.mylocaltripback.repository;

import com.jorami.mylocaltripback.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    VerificationCode findByCode(String code);

    @Query(value = "SELECT v FROM VerificationCode v WHERE v.user.id = :userId")
    VerificationCode findByIdUser(Long userId);

}
