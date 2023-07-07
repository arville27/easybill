package net.arville.easybill.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import net.arville.easybill.model.helper.BillTransactionOrigin;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "bill_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BillTransaction {
    @Id
    @SequenceGenerator(name = "bill_transaction_id_seq", sequenceName = "bill_transaction_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "bill_transaction_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private User payer;

    @ManyToOne
    private User receiver;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private BillTransactionOrigin origin;

    @OneToMany(mappedBy = "billTransaction", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    private List<BillTransactionHeader> billTransactionHeaderList;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BillTransaction that = (BillTransaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
