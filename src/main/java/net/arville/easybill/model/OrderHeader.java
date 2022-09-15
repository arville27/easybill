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

    @Column(nullable = false)
    private BigDecimal upto;

    @Column(nullable = false)
    private Double discount;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_header_id", referencedColumnName = "id", nullable = false)
    private List<OrderDetail> orderList;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
