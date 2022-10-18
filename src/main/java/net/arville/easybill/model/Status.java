package net.arville.easybill.model;

import lombok.*;
import net.arville.easybill.model.helper.BillStatus;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "status")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Status {

    @Id
    @SequenceGenerator(name = "status_id_seq", sequenceName = "status_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "status_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_header_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private OrderHeader orderHeader;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @OneToMany(mappedBy = "billTransaction", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    private List<BillTransactionHeader> billTransactionHeaderList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Status status = (Status) o;
        return id != null && Objects.equals(id, status.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Transient
    private BigDecimal oweAmount;

    public BigDecimal getOweAmount() {
        BigDecimal perUserFee = this.orderHeader
                .getOtherFee()
                .divide(
                        BigDecimal.valueOf(this.orderHeader.getParticipatingUserCount()),
                        3,
                        RoundingMode.HALF_UP
                );

        return this.orderHeader
                .getOrderDetailList()
                .stream()
                .filter(order -> Objects.equals(order.getUser().getId(), this.user.getId()))
                .map(order -> order.getPrice()
                        .multiply(BigDecimal.valueOf(order.getQty()))
                        .subtract(order.getItemDiscount())
                )
                .reduce(BigDecimal.valueOf(0), BigDecimal::add)
                .add(perUserFee);
    }
}
