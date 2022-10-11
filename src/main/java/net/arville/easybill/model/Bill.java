package net.arville.easybill.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Bill {
    @Id
    @SequenceGenerator(name = "bill_id_seq", sequenceName = "bill_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "bill_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "owe_to", referencedColumnName = "id")
    private User owe;
    @Column(name = "owe_total")
    private BigDecimal oweTotal = new BigDecimal(0);

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public Bill addOweTotal(Long amount) {
        return addOweTotal(BigDecimal.valueOf(amount));
    }

    public Bill addOweTotal(BigDecimal amount) {
        this.oweTotal = this.oweTotal.add(amount);
        return this;
    }

    public Bill decreaseOweTotal(Long amount) {
        return decreaseOweTotal(BigDecimal.valueOf(amount));
    }

    public Bill decreaseOweTotal(BigDecimal amount) {
        this.oweTotal = this.oweTotal.min(amount);
        return this;
    }
}
