/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.id.jahitku.serverside.repository;

import co.id.jahitku.serverside.model.Order;
import co.id.jahitku.serverside.model.Order.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DELL ALIENWARE
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM tb_order ORDER BY tanggal_masuk DESC LIMIT 5", nativeQuery = true)
    List<Order> findAllOrderByTanggalMasukDesc();

    @Query(value = "SELECT no_order FROM tb_order WHERE id=?1", nativeQuery = true)
    String findNoOrderById(Long id);

    @Query(value = "SELECT * FROM tb_order o JOIN tb_user u on o.user_id = u.id WHERE u.username = ?1", nativeQuery = true)
    List<Order> findByUsername(String username);

    @Query(value = "SELECT * FROM tb_order o JOIN tb_pembayaran p ON o.id = p.id WHERE p.status_pembayaran=?1", nativeQuery = true)
    List<Order> findByStatusPembayaran(Long statusPembayaran);

    @Query(value = "SELECT COUNT(*) FROM jenis_jahitan_order WHERE order_id = ?1 AND status = true", nativeQuery = true)
    Long countStatusJahitanByOrderId(Long OrderId);

    @Query(value = "SELECT COUNT(*) FROM tb_order o JOIN tb_pembayaran p ON o.id = p.id WHERE p.status_pembayaran=0", nativeQuery = true)
    String countUnpaid();

    @Query(value = "SELECT COUNT(IF(status_pesanan=0,0,null)), "
            + "COUNT(IF(status_pesanan=1,1,null)), "
            + "COUNT(IF(status_pesanan=2,2,null)), "
            + "COUNT(IF(status_pesanan=3,3,null)), "
            + "COUNT(IF(status_pesanan=4,4,null)) "
            + "FROM tb_order", nativeQuery = true)
    String countByStatusPesanan();
    
    Optional<Order> findByNoOrder (String noOrder);
    

}
