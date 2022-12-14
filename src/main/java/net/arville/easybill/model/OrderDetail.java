package net.arville.easybill.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import net.arville.easybill.model.helper.OrderDetailType;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@Builder
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

    @Column(name = "group_order_reference_id")
    private int groupOrderReferenceId;

    private BigDecimal price;

    private Integer qty;

    @Enumerated(EnumType.STRING)
    private OrderDetailType orderType;

    @Transient
    private BigDecimal itemDiscount;

    @Transient
    private BigDecimal orderSubtotalPrice;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_header_id", referencedColumnName = "id", nullable = false)
    private OrderHeader orderHeader;

    public BigDecimal getItemDiscount() {
        if (this.itemDiscount == null) {
            var totalOrderDetail = this.getOrderSubtotalPrice();
            this.itemDiscount = totalOrderDetail
                    .multiply(this.orderHeader.getDiscountAmount())
                    .divide(this.orderHeader.getTotalOrderAmount(), 0, RoundingMode.HALF_UP);
        }
        return this.itemDiscount;
    }

    public BigDecimal getOrderSubtotalPrice() {
        if (this.orderSubtotalPrice == null) {
            this.orderSubtotalPrice = this.orderType == OrderDetailType.MULTI_USER
                    ? this.price
                    : BigDecimal.valueOf(this.qty).multiply(this.price);
        }
        return this.orderSubtotalPrice;
    }

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
