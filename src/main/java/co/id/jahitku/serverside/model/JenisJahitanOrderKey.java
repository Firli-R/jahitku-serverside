/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Firli
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JenisJahitanOrderKey implements Serializable {

    @Column(name = "jenis_jahitan_id")
    private Long jenisJahitanId;

    @Column(name = "order_id")
    private Long orderId;
}
