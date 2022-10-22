package net.arville.easybill.repository;

import net.arville.easybill.model.BillTransaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillTransactionRepository extends JpaRepository<BillTransaction, Long> {
    @EntityGraph(attributePaths = {"payer", "receiver", "billTransactionHeaderList.bill.orderHeader"})
    @Query("SELECT bt FROM BillTransaction bt WHERE bt.payer.id = ?1 OR bt.receiver.id = ?1 ORDER BY bt.createdAt DESC")
    List<BillTransaction> findAllRelevantTransaction(Long id);
}
