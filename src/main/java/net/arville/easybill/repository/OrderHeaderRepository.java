package net.arville.easybill.repository;

import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.repository.extended.OrderHeaderRepositoryExtended;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long>, OrderHeaderRepositoryExtended {


    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "buyer",
                    "billList.user",
                    "billList.billTransactionHeaderList",
                    "orderDetailList"
            }
    )
    @Override
    Optional<OrderHeader> findById(Long id);
}
