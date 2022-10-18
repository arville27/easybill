package net.arville.easybill.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderDetail that = (OrderDetail) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
