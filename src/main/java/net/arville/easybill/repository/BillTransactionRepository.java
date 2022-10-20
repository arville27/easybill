package net.arville.easybill.repository;

import net.arville.easybill.model.BillTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillTransactionRepository extends JpaRepository<BillTransaction, Long> {

}
