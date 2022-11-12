package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.dto.request.OrderDetailRequest;
import net.arville.easybill.dto.response.BillResponse;
import net.arville.easybill.dto.response.OrderDetailResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.response.UserResponse;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.service.manager.BillManager;
import net.arville.easybill.service.manager.OrderManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderManagerImpl implements OrderManager {
    private final OrderHeaderRepository orderHeaderRepository;
    private final UserManager userManager;
    private final BillManager billManager;

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
                        .peek(orderDetailRequest -> {
                            var orderDetailRequestMissingProperties = orderDetailRequest.getMissingProperties();

                            if (orderDetailRequestMissingProperties.size() > 0) {
                                throw new MissingRequiredPropertiesException(orderDetailRequestMissingProperties);
                            }
                        })
                        .flatMap(OrderDetailRequest::toOriginalEntity)
                        .peek(orderDetail -> {
                            User orderBy = userManager.getUserByUserId(orderDetail.getUser().getId());
                            orderDetail.setOrderHeader(orderHeader);
                            orderDetail.setUser(orderBy);
                        })
                        .collect(Collectors.toSet())
        );

        orderHeader.setBuyer(user);
        user.getOrderList().add(orderHeader);

        // This will process order and generate bill accordingly
        var bills = billManager.generateCorrespondingBills(orderHeader);
        orderHeader.setBillList(bills);
        var savedOrderHeader = orderHeaderRepository.save(orderHeader);

        return createOrderHeaderResponse(savedOrderHeader);
    }

    public OrderHeaderResponse getOrderById(Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return createOrderHeaderResponse(orderHeader);
    }

    @Override
    public OrderHeaderResponse deleteOrder(Long orderHeaderId) {
        var orderToDelete = orderHeaderRepository
                .findById(orderHeaderId)
                .orElseThrow(() -> new OrderNotFoundException(orderHeaderId));

        orderHeaderRepository.delete(orderToDelete);

        return createOrderHeaderResponse(orderToDelete);
    }

    private OrderHeaderResponse createOrderHeaderResponse(OrderHeader orderHeader) {
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
                .billResponse(orderHeader.getBillList()
                        .stream()
                        .map(BillResponse::map)
                        .collect(Collectors.toList())
                )
                .build();
    }

}
