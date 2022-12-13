package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.dto.request.OrderDetailRequest;
import net.arville.easybill.dto.response.*;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.exception.UnauthorizedRequestException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.model.helper.BillStatus;
import net.arville.easybill.model.helper.OrderHeaderValidity;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.helper.PageableBuilder;
import net.arville.easybill.service.manager.BillManager;
import net.arville.easybill.service.manager.OrderManager;
import net.arville.easybill.service.manager.UserManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderManagerImpl implements OrderManager {
    private final OrderHeaderRepository orderHeaderRepository;
    private final UserManager userManager;
    private final BillManager billManager;
    private final PageableBuilder pageableBuilder = PageableBuilder.builder();

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

        return this.createOrderHeaderResponse(savedOrderHeader);
    }

    public OrderHeaderResponse getOrderById(Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return this.createOrderHeaderResponse(orderHeader);
    }

    @Override
    public OrderHeaderResponse deleteOrder(Long orderHeaderId) {
        var orderToDelete = orderHeaderRepository
                .findById(orderHeaderId)
                .orElseThrow(() -> new OrderNotFoundException(orderHeaderId));

        orderHeaderRepository.delete(orderToDelete);

        return this.createOrderHeaderResponse(orderToDelete);
    }

    @Override
    public void approveOrder(User requester, Long orderHeaderId) {
        var orderToUpdate = orderHeaderRepository
                .findById(orderHeaderId)
                .orElseThrow(() -> new OrderNotFoundException(orderHeaderId));

        if (!Objects.equals(requester.getId(), orderToUpdate.getBuyer().getId()))
            throw new UnauthorizedRequestException("The order doesn't belong to the user");

        orderToUpdate.setValidity(OrderHeaderValidity.ACTIVE);

        orderHeaderRepository.save(orderToUpdate);
    }

    @Override
    public UserResponse getAllPendingOrder(User user) {
        var pendingOrders = orderHeaderRepository.findPendingOrderHeaderForUser(user.getId());

        return UserResponse
                .template(user)
                .pendingOrders(pendingOrders.stream()
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatusForUsersOrder())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();
    }

    public PaginationResponse<UserResponse> getUserRelevantOrder(
            User user,
            int pageNumber,
            int pageSize,
            String keyword,
            String orderStatus
    ) {
        var relevantOrderList = orderHeaderRepository
                .findRelevantOrderHeaderForUser(
                        user.getId(),
                        Optional.ofNullable(keyword),
                        BillStatus.fromString(orderStatus),
                        pageableBuilder.setPageNumber(pageNumber).setPageSize(Math.min(pageSize, 25)).build()
                );

        var data = UserResponse
                .template(user)
                .orderHeaderResponseList(relevantOrderList.stream()
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatus(user))
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();

        return PaginationResponse.<UserResponse>builder()
                .data(data)
                .page(relevantOrderList.getTotalPages() == 0 ? 0 : pageNumber)
                .pageSize(relevantOrderList.getNumberOfElements())
                .totalPages(relevantOrderList.getTotalPages())
                .totalItems(relevantOrderList.getTotalElements())
                .build();
    }

    public PaginationResponse<UserResponse> getUsersOrder(
            User user,
            int pageNumber,
            int pageSize,
            String keyword,
            String orderStatus
    ) {

        var usersOrderList = orderHeaderRepository
                .findUsersOrderHeaderForUser(
                        user.getId(),
                        Optional.ofNullable(keyword),
                        BillStatus.fromString(orderStatus),
                        pageableBuilder.setPageNumber(pageNumber).setPageSize(Math.min(pageSize, 25)).build()
                );


        var data = UserResponse
                .template(user)
                .orderHeaderResponseList(usersOrderList.stream()
                        .map(order -> OrderHeaderResponse
                                .template(order)
                                .buyerResponse(UserResponse.mapWithoutDate(order.getBuyer()))
                                .relevantStatus(order.getRelevantStatusForUsersOrder())
                                .build()
                        )
                        .collect(Collectors.toList())
                )
                .build();

        return PaginationResponse.<UserResponse>builder()
                .data(data)
                .page(usersOrderList.getTotalPages() == 0 ? 0 : pageNumber)
                .pageSize(usersOrderList.getNumberOfElements())
                .totalPages(usersOrderList.getTotalPages())
                .totalItems(usersOrderList.getTotalElements())
                .build();
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
