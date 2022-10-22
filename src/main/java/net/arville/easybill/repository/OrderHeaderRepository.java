package net.arville.easybill.repository;

import net.arville.easybill.model.OrderHeader;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {
    @Query("SELECT DISTINCT oh FROM OrderHeader oh JOIN oh.orderDetailList od JOIN od.user u WHERE u.id = ?1 ORDER BY oh.orderAt DESC, oh.createdAt DESC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "buyer",
                    "billList"
            }
    )
    List<OrderHeader> findRelevantOrderHeaderForUser(Long userId);

    @Query("SELECT oh FROM OrderHeader oh WHERE oh.buyer.id = ?1 ORDER BY oh.orderAt DESC, oh.createdAt DESC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"buyer"}
    )
    List<OrderHeader> findUsersOrderHeaderForUser(Long userId);

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
