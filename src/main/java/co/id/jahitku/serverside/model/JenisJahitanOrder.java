/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author LENOVO
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JenisJahitanOrder {

    @EmbeddedId
    JenisJahitanOrderKey idJenisOrder;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @MapsId("jenisJahitanId")
    @JoinColumn(name = "jenis_jahitan_id")
    JenisJahitan jenisJahitan;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    @EqualsAndHashCode.Exclude
    Order order;

    private Long kuantitas;

    private Long harga;

    private String keterangan;

    private boolean status = false;

}
