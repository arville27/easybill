package net.arville.easybill.repository;

import net.arville.easybill.model.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long>, PagingAndSortingRepository<OrderHeader, Long> {
    @Query("SELECT DISTINCT oh FROM OrderHeader oh JOIN oh.orderDetailList od JOIN od.user u WHERE u.id = ?1 ORDER BY oh.orderAt DESC, oh.createdAt DESC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "buyer",
                    "billList",
                    "orderDetailList"
            }
    )
    Page<OrderHeader> findRelevantOrderHeaderForUser(Long userId, Pageable pageable);

    @Query("SELECT oh FROM OrderHeader oh WHERE oh.buyer.id = ?1 ORDER BY oh.orderAt DESC, oh.createdAt DESC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"buyer"}
    )
    Page<OrderHeader> findUsersOrderHeaderForUser(Long userId, Pageable pageable);

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
