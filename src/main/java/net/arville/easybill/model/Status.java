package net.arville.easybill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.model.helper.BillStatus;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "status")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Status {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_header_id", referencedColumnName = "id", nullable = false)
    private OrderHeader orderHeader;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private BillStatus status = BillStatus.UNPAID;

    @OneToMany(mappedBy = "billTransaction")
    private List<BillTransactionHeader> billTransactionHeaderList;

}
