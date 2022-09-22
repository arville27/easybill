package net.arville.easybill.dto.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import net.arville.easybill.dto.util.ConvertibleFromOriginalEntitiy;
import net.arville.easybill.model.Bill;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class BaseUserEntity {

    private Long id;

    private String username;

    private String password;

    private List<OrderHeader> orderList;

    private List<OrderDetail> orderDetailList;

    private List<Bill> billList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
