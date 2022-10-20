package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.dto.response.OrderDetailResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.StatusResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.service.manager.OrderManager;
import net.arville.easybill.service.manager.StatusManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderManagerImpl implements OrderManager {
    private final OrderHeaderRepository orderHeaderRepository;
    private final UserManager userManager;
    private final StatusManager statusManager;

    public OrderHeaderResponse addNewOrder(AddOrderRequest addOrderRequest) {

        var missingProperties = addOrderRequest.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        User user = userManager.getUserByUserId(addOrderRequest.getBuyerId());

        OrderHeader orderHeader = addOrderRequest.toOriginalEntity();
        orderHeader.setOrderDetailList(
                addOrderRequest.getOrderList()
                        .stream()
                        .map(orderDetailRequest -> {
                            User orderBy = userManager.getUserByUserId(orderDetailRequest.getUserId());
                            OrderDetail orderDetail = orderDetailRequest.toOriginalEntity();
                            orderDetail.setUser(orderBy);
                            return orderDetail;
                        })
                        .collect(Collectors.toSet())
        );
        orderHeader.setBuyer(user);
        user.getOrderList().add(orderHeader);

        // This will process order and generate bill accordingly
        var statuses = statusManager.createCorrespondingStatusFromOrderHeader(orderHeader);
        orderHeader.setStatusList(statuses);
        var savedOrderHeader = orderHeaderRepository.save(orderHeader);

        return OrderHeaderResponse
                .template(savedOrderHeader)
                .buyerResponse(UserResponse.mapWithoutDate(savedOrderHeader.getBuyer()))
                .participatingUserCount(savedOrderHeader.getParticipatingUserCount())
                .orderDetailResponses(savedOrderHeader.getOrderDetailList().stream().map(OrderDetailResponse::map).collect(Collectors.toList()))
                .statusResponses(savedOrderHeader.getStatusList()
                        .stream()
                        .map(StatusResponse::map)
                        .collect(Collectors.toList())
                )
                .build();
    }

    public OrderHeaderResponse getOrderById(Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        var orderDetailListGroupByUser = orderHeader.getOrderDetailList()
                .stream()
                .collect(Collectors.groupingBy(OrderDetail::getUser));

        return OrderHeaderResponse
                .template(orderHeader)
                .buyerResponse(UserResponse.mapWithoutDate(orderHeader.getBuyer()))
                .participatingUserCount(orderHeader.getParticipatingUserCount())
                .userOtherFee(orderHeader.getPerUserFee())
                .relatedOrderDetail(orderDetailListGroupByUser.entrySet()
                        .stream()
                        .map(userListEntry -> {
                            var orderOwner = userListEntry.getKey();
                            var orderDetails = userListEntry.getValue();
                            var orderSummary = orderHeader.getRelevantOrderSummarization(orderOwner);
                            return UserResponse.template(orderOwner)
                                    .totalOrder(orderSummary.getTotalOrder())
                                    .discountTotal(orderSummary.getTotalDiscount())
                                    .totalOrderAfterDiscount(orderSummary.getTotalOrderAfterDiscount())
                                    .userOrders(orderDetails
                                            .stream()
                                            .map(orderDetail -> OrderDetailResponse.template(orderDetail).build())
                                            .collect(Collectors.toList())
                                    )
                                    .build();
                        })
                        .collect(Collectors.toList())
                )
                .statusResponses(orderHeader.getStatusList()
                        .stream()
                        .map(StatusResponse::map)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
