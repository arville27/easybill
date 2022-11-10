package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.dto.response.OrderHeaderResponse;

public interface OrderManager {

    OrderHeaderResponse addNewOrder(AddOrderRequest addOrderRequest);

    OrderHeaderResponse getOrderById(Long orderId);
    
    OrderHeaderResponse deleteOrder(Long orderHeaderId);
}
