package net.arville.easybill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetail {
    @Id
    @SequenceGenerator(name = "order_detail_id_seq", sequenceName = "order_detail_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "order_detail_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "order_menu_desc")
    private String orderMenuDesc;

    private BigDecimal price;

    private Integer qty;

    private BigDecimal itemDiscount;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
