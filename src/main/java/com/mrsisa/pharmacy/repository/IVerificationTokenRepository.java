package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);



    @Query("select vt from VerificationToken vt where vt.expirationDate <= current_timestamp ")
    List<VerificationToken> getExpiredTokens();

    void deleteVerificationTokenById(Long id);

}
