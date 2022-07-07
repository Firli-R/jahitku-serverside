/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.repository;

import co.id.jahitku.serverside.model.User;
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
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernameOrEmail(String username,String email);
    User findByUsername(String username);
    User findByNama(String nama);
    User findByEmail(String email);
    User findByPassword(String password);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE `tb_user` SET is_enable = TRUE WHERE email = ?1",nativeQuery = true)
    int enableAppUser(String email);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE `tb_user` SET `is_enable`= false WHERE `id`= ?1",nativeQuery = true)
    int disableAppUser(Long id);
}
