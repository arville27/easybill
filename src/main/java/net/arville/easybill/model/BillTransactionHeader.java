package net.arville.easybill.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "bill_transaction_headers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BillTransactionHeader {
    @Id
    @SequenceGenerator(name = "bill_transaction_header_id_seq", sequenceName = "bill_transaction_header_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "bill_transaction_header_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    @ToString.Exclude
    private Bill bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_transaction_id")
    @ToString.Exclude
    private BillTransaction billTransaction;

    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BillTransactionHeader that = (BillTransactionHeader) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
