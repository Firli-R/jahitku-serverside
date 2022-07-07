/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.repository;

import co.id.jahitku.serverside.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Firli
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByNama(String nama);
}
