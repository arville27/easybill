package net.arville.easybill.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
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

    @OneToMany(mappedBy = "billTransaction")
    @ToString.Exclude
    private List<BillTransactionHeader> billTransactionHeaderList;

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
