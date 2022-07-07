/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.id.jahitku.serverside.repository;

import co.id.jahitku.serverside.model.JenisJahitan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DELL ALIENWARE
 */
@Repository
public interface JenisJahitanRepository extends JpaRepository<JenisJahitan, Long> {

    JenisJahitan findByNama(String nama);

}
