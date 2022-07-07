/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.id.jahitku.serverside.repository;

import co.id.jahitku.serverside.model.JenisJahitanOrder;
import co.id.jahitku.serverside.model.JenisJahitanOrderKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DELL ALIENWARE
 */
@Repository
public interface JenisJahitanOrderRepository extends JpaRepository<JenisJahitanOrder, JenisJahitanOrderKey> {
    Optional<List<JenisJahitanOrder>> findByOrderId(Long id);
    
    JenisJahitanOrder findByIdJenisOrder(JenisJahitanOrderKey idKey);
}
