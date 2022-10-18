package net.arville.easybill.dto.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
