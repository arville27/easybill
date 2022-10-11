package net.arville.easybill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bill_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BillTransaction {
    @Id
    private Long id;

    @ManyToOne
    private User payer;

    @ManyToOne
    private User receiver;

    private BigDecimal amount;

    @OneToMany(mappedBy = "billTransaction")
    private List<BillTransactionHeader> billTransactionHeaderList;
}
