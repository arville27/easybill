package net.arville.easybill.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor
@Data
public class OrderDetailRequest implements EnsureRequiredFields, ConvertibleToOriginalEntity<OrderDetail> {

    private Long userId;

    private String orderMenuDesc;

    private Integer qty;

    private BigDecimal price;
    @Override
    public OrderDetail toOriginalEntity() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderMenuDesc(orderMenuDesc);
        orderDetail.setQty(qty);
        orderDetail.setPrice(price);
        return orderDetail;
    }

    @Override
    public boolean isAllPresent() {
        return userId != null && orderMenuDesc != null && qty != null && price != null;
    }
}
