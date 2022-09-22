package net.arville.easybill.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.model.User;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseOrderDetailEntity {

    private Long id;

    private String orderMenuDesc;

    private BigDecimal price;

    private Integer qty;

    private User user;

}
