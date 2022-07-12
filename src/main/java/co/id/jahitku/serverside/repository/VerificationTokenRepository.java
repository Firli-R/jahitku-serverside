/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.repository;

import co.id.jahitku.serverside.model.VerificationToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Firli
 */
@Repository
@Transactional(readOnly = true)
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
//    @Query(value = "SELECT * FROM `tb_token` WHERE `token`= ?1",nativeQuery = true)
//    Optional<VerificationToken> findByTokenQuery(String token);
    Optional<VerificationToken> findByToken(String token);
    @Transactional
    @Modifying
    @Query(value = "UPDATE tb_token SET confirmed_at=2? WHERE token=1?",nativeQuery = true)
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
    @Query(value="SELECT * FROM tb_token WHERE user_id = ?1",nativeQuery = true)
    Optional<VerificationToken> findByUserId(Long userId);
}
