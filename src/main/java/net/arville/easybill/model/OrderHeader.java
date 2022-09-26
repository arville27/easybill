package net.arville.easybill.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_headers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderHeader {
    @Id
    @SequenceGenerator(name = "order_header_id_seq", sequenceName = "order_header_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "order_header_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "order_description", nullable = false)
    private String orderDescription;

    @Column(name = "total_payment", nullable = false)
    private BigDecimal totalPayment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private BigDecimal upto;

    @Column(nullable = false)
    private Double discount;

    @Column(name = "total_order_amount")
    private BigDecimal totalOrderAmount;

    @Column(name = "other_fee")
    private BigDecimal otherFee;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_header_id", referencedColumnName = "id", nullable = false)
    private List<OrderDetail> orderDetailList;

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

    public OrderHeader(Long id, User user, Double discount, String orderDescription, BigDecimal totalPayment, BigDecimal upto, LocalDateTime orderAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.discount = discount;
        this.orderDescription = orderDescription;
        this.totalPayment = totalPayment;
        this.upto = upto;
        this.orderAt = orderAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
