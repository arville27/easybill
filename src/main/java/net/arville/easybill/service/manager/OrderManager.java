package net.arville.easybill.service.manager;

import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.PaginationResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.model.User;

public interface OrderManager {

    OrderHeaderResponse addNewOrder(AddOrderRequest addOrderRequest);

    OrderHeaderResponse getOrderById(Long orderId);
    
    OrderHeaderResponse deleteOrder(Long orderHeaderId);

    UserResponse getAllPendingOrder(User user);

    PaginationResponse<UserResponse> getUserRelevantOrder(User user, int pageNumber, int pageSize, String keyword, String orderStatus);

    PaginationResponse<UserResponse> getUsersOrder(User user, int pageNumber, int pageSize, String keyword, String orderStatus);

    void approveOrder(User requester, Long orderHeaderId);
}
