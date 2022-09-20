package net.arville.easybill.repository;

import net.arville.easybill.model.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {
    @Query("SELECT DISTINCT new OrderHeader(oh.id, oh.user, oh.discount, oh.orderDescription, oh.totalPayment, oh.upto, oh.createdAt, oh.updatedAt) FROM OrderHeader oh JOIN oh.orderDetailList od JOIN od.user u WHERE u.id = ?1")
    List<OrderHeader> findRelevantOrderHeaderForUser(Long userId);
}
