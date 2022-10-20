package net.arville.easybill.repository;

import net.arville.easybill.model.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    @Query("SELECT s FROM Status s WHERE s.status = 'UNPAID' AND s.user.id = ?1 ORDER BY s.orderHeader.orderAt DESC, s.orderHeader.createdAt DESC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "orderHeader",
                    "orderHeader.buyer",
                    "orderHeader.orderDetailList"
            }
    )
    List<Status> findAllUsersStatus(Long userId);

    @Query("SELECT s FROM Status s WHERE s.status = 'UNPAID' AND s.user.id = ?1 AND s.orderHeader.buyer.id = ?2 ORDER BY s.orderHeader.orderAt ASC, s.orderHeader.createdAt ASC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "orderHeader",
                    "orderHeader.buyer",
                    "orderHeader.orderDetailList"
            }
    )
    List<Status> findAllUsersBillsToSpecificUser(Long userId, Long targetUserId);

    @Query("SELECT s FROM Status s JOIN s.orderHeader oh WHERE s.status = 'UNPAID' AND oh.buyer.id = ?1 ORDER BY s.orderHeader.orderAt DESC, s.orderHeader.createdAt DESC")
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "user",
                    "orderHeader",
                    "orderHeader.orderDetailList"
            }
    )
    List<Status> findAllStatusToUser(Long userId);

}
