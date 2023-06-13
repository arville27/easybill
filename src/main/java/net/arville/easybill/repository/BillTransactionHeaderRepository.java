package net.arville.easybill.repository;

import net.arville.easybill.model.BillTransactionHeader;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillTransactionHeaderRepository extends JpaRepository<BillTransactionHeader, Long> {
    @EntityGraph(attributePaths = {"bill.orderHeader"})
    @Query("SELECT bth FROM BillTransactionHeader bth ORDER BY bth.createdAt DESC")
    List<BillTransactionHeader> findRelatedBillTransactionHeader(List<Long> listBillTransactionId);
}
