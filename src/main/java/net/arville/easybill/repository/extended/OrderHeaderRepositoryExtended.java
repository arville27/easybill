package net.arville.easybill.repository.extended;

import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.helper.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderHeaderRepositoryExtended {
    Page<OrderHeader> findRelevantOrderHeaderForUser(
            Long userId,
            Optional<String> keyword,
            BillStatus orderStatus,
            Pageable pageable
    );

    Page<OrderHeader> findUsersOrderHeaderForUser(
            Long userId,
            Optional<String> keyword,
            BillStatus orderStatus,
            Pageable pageable
    );
}
