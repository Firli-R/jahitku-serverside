/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author LENOVO
 */
@Table(name = "tb_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @GeneratedValue(generator = "noOrderGenerator")
//    @GenericGenerator(name = "noOrderGenerator", 
//            parameters = @Parameter(name = "prefix", value = "JK"),
//            strategy = "co.id.jahitku.serverside.model.generator.NoOrderGenerator")
//    @Column(unique = true)
//    private String noOrder;
    private String noOrder;

    private LocalDateTime tanggalMasuk;

    private LocalDateTime tanggalSelesai;
    
    private String lokasiBarang;

    public enum Status {
        MENUNGGU_APPROVAL, BATAL, BELUM_DIKERJAKAN, DALAM_PROSES, SELESAI;
    }

    private Double progress = 0.0;
    
    private Boolean statusOrder =false ;

    @Enumerated(EnumType.ORDINAL)
    private Status statusPesanan = Status.MENUNGGU_APPROVAL;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "user_id"
    )
    private User user;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Pembayaran pembayaran;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    Set<JenisJahitanOrder> jenisJahitanOrder;

}
