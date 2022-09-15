package net.arville.easybill.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertableToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Data
public class OrderRequest implements EnsureRequiredFields, ConvertableToOriginalEntity<OrderHeader> {

    private String orderDescription;

    private BigDecimal totalPayment;

    private BigDecimal upto;

    private Double discount;

    private Long buyerId;

    private List<OrderDetail> orderList;

    @Override
    public boolean isAllPresent() {
        return this.orderDescription != null
                && this.totalPayment != null
                && this.upto != null
                && this.discount != null
                && this.buyerId != null
                && this.orderList != null;
    }

    @Override
    public OrderHeader toOriginalEntity() {
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderDescription(orderDescription);
        orderHeader.setTotalPayment(totalPayment);
        orderHeader.setUpto(upto);
        orderHeader.setDiscount(discount);
        orderHeader.setBuyerId(buyerId);
        orderHeader.setOrderList(orderList);
        return orderHeader;
    }
}
