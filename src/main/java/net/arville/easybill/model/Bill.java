package net.arville.easybill.model;

import jakarta.persistence.*;
import lombok.*;
import net.arville.easybill.model.helper.BillStatus;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "bills")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Bill {

    @Id
    @SequenceGenerator(name = "bill_id_seq", sequenceName = "bill_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "bill_id_seq", strategy = GenerationType.SEQUENCE)
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

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<BillTransactionHeader> billTransactionHeaderList;

    @Transient
    private BigDecimal oweAmount;

    public Bill addBillTransactionHeader(BillTransactionHeader billTransactionHeader) {
        if (this.billTransactionHeaderList == null)
            this.billTransactionHeaderList = Collections.emptySet();
        this.billTransactionHeaderList.add(billTransactionHeader);
        return this;
    }

    public BigDecimal getOweAmount() {
        BigDecimal perUserFee = this.orderHeader
                .getOtherFee()
                .divide(BigDecimal.valueOf(this.orderHeader.getParticipatingUserCount()), 0, RoundingMode.HALF_UP);

        return this.orderHeader
                .getOrderDetailList()
                .stream()
                .filter(order -> Objects.equals(order.getUser().getId(), this.user.getId()))
                .map(order -> order.getOrderSubtotalPrice().subtract(order.getItemDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(perUserFee);
    }

    public BigDecimal getOweAmountWithBillTransaction() {
        return this.getOweAmount().subtract(this.getTotalPaidAmount());
    }

    public User getOrderHeaderBuyer() {
        return this.orderHeader.getBuyer();
    }

    public BigDecimal getTotalPaidAmount() {
        if (this.billTransactionHeaderList == null)
            this.billTransactionHeaderList = Collections.emptySet();
        return this.billTransactionHeaderList
                .stream()
                .map(BillTransactionHeader::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Bill bill = (Bill) o;
        return getId() != null && Objects.equals(getId(), bill.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}
