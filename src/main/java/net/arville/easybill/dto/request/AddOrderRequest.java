package net.arville.easybill.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleToOriginalEntity;
import net.arville.easybill.dto.util.EnsureRequiredFields;
import net.arville.easybill.model.OrderHeader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class AddOrderRequest implements EnsureRequiredFields, ConvertibleToOriginalEntity<OrderHeader> {

    private String orderDescription;

    private BigDecimal totalPayment;

    private BigDecimal upto;

    private Double discount;

    private Long buyerId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderAt;

    private List<OrderDetailRequest> orderList;

    @Override
    public boolean isAllPresent() {
        return this.orderDescription != null
                && this.totalPayment != null
                && this.upto != null
                && this.discount != null
                && this.orderAt != null
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
        orderHeader.setOrderAt(orderAt);
        return orderHeader;
    }
}
