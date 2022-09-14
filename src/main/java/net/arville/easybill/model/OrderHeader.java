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

    @Column(name = "order_store", nullable = false)
    private String orderStore;

    @Column(name = "total_payment", nullable = false)
    private BigDecimal totalPayment;

    private BigDecimal upto;

    private Double discount;

    private Long buyer;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
