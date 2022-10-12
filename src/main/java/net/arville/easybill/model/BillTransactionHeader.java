package net.arville.easybill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_transaction_headers")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillTransactionHeader {
    @Id
    @SequenceGenerator(name = "bill_transaction_header_id_seq", sequenceName = "bill_transaction_header_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "bill_transaction_header_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_transaction_id")
    private BillTransaction billTransaction;

    private BigDecimal paidAmount = BigDecimal.valueOf(0);
}
