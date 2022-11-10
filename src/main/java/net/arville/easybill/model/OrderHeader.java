package net.arville.easybill.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import net.arville.easybill.model.helper.BillStatus;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "order_headers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderHeader {
    @Id
    @SequenceGenerator(name = "order_header_id_seq", sequenceName = "order_header_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "order_header_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "order_description", nullable = false)
    private String orderDescription;

    @Column(name = "total_payment", nullable = false)
    private BigDecimal totalPayment;
    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private User buyer;

    @Column(nullable = false)
    private BigDecimal upto;

    @Column(nullable = false)
    private Double discount;

    @Transient
    private BigDecimal totalOrderAmount;

    @Transient
    private BigDecimal otherFee;

    @Transient
    private BigDecimal discountAmount;

    @Transient
    private Integer participatingUserCount;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orderHeader")
    @ToString.Exclude
    private Set<OrderDetail> orderDetailList;
    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Bill> billList;

    @Column(name = "order_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderAt;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public BigDecimal getTotalOrderAmount() {
        if (this.totalOrderAmount == null) {
            this.totalOrderAmount = this.orderDetailList.stream()
                    .map(OrderDetail::getOrderSubtotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return this.totalOrderAmount;
    }

    public BigDecimal getOtherFee() {
        if (this.otherFee == null) {
            this.otherFee = this.totalPayment
                    .add(this.getDiscountAmount())
                    .subtract(this.getTotalOrderAmount());
        }
        return this.otherFee;
    }

    public BigDecimal getDiscountAmount() {
        if (this.discountAmount == null) {
            BigDecimal discountAmountBeforeUpto = this.getTotalOrderAmount()
                    .multiply(BigDecimal.valueOf(this.discount))
                    .setScale(0, RoundingMode.HALF_UP);
            this.discountAmount = discountAmountBeforeUpto.compareTo(upto) > 0 ? upto : discountAmountBeforeUpto;
        }
        return this.discountAmount;
    }

    public int getParticipatingUserCount() {
        if (this.participatingUserCount == null)
            this.participatingUserCount = this.getParticipatingUsers().size();
        return this.participatingUserCount;
    }

    public Set<User> getParticipatingUsers() {
        return this.orderDetailList.stream().map(OrderDetail::getUser).collect(Collectors.toSet());
    }

    public BillStatus getRelevantStatus(User user) {
        var bill = this.getRelevantBill(user);

        return bill.getStatus();
    }

    public BillStatus getRelevantStatusForUsersOrder() {
        return this.billList
                .stream()
                .allMatch(bill -> bill.getStatus() == BillStatus.PAID) ? BillStatus.PAID : BillStatus.UNPAID;
    }

    public Bill getRelevantBill(User user) {
        var bills = this.billList
                .stream()
                .filter(s -> Objects.equals(s.getUser().getId(), user.getId()))
                .findFirst();

        return bills.get();
    }

    public BigDecimal getPerUserFee() {
        return this.getOtherFee().divide(BigDecimal.valueOf(this.getParticipatingUserCount()), 0, RoundingMode.HALF_UP);
    }

    public OrderHeaderSummary getRelevantOrderSummarization(User user) {
        var userOrderDetails = this.orderDetailList
                .stream()
                .filter(order -> Objects.equals(order.getUser().getId(), user.getId()))
                .collect(Collectors.toList());
        var totalOrder = userOrderDetails.stream()
                .map(OrderDetail::getOrderSubtotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalDiscount = userOrderDetails.stream()
                .map(OrderDetail::getItemDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return OrderHeaderSummary.builder()
                .totalOrder(totalOrder)
                .totalDiscount(totalDiscount)
                .totalOrderAfterDiscount(totalOrder.subtract(totalDiscount))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderHeader that = (OrderHeader) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class OrderHeaderSummary {
        private final BigDecimal totalOrder;
        private final BigDecimal totalDiscount;
        private final BigDecimal totalOrderAfterDiscount;
    }
}
