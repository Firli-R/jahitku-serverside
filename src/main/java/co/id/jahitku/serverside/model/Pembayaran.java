/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author LENOVO
 */
@Entity
@Table(name = "tb_pembayaran")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pembayaran {

    @Id
    private Long id;

    public enum StatusBayar {
        BELUM_DIBAYAR, SUDAH_DIBAYAR,BAYAR_DITEMPAT,BATAL;
    }

    @Enumerated(EnumType.ORDINAL)
    private StatusBayar statusPembayaran = StatusBayar.BELUM_DIBAYAR;

    @Column(nullable = false)
    private Long totalBiaya = 0L;

    @PrimaryKeyJoinColumn
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @EqualsAndHashCode.Exclude
    private Order order;

}
