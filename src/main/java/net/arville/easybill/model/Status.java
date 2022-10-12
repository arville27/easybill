package net.arville.easybill.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.model.helper.BillStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "status")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Status {

    @Id
    @SequenceGenerator(name = "status_id_seq", sequenceName = "status_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "status_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_header_id", referencedColumnName = "id", nullable = false)
    private OrderHeader orderHeader;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @OneToMany(mappedBy = "billTransaction")
    private List<BillTransactionHeader> billTransactionHeaderList;

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
