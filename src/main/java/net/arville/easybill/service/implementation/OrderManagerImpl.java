package net.arville.easybill.service.implementation;

import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.dto.request.OrderDetailRequest;
import net.arville.easybill.dto.response.*;
import net.arville.easybill.exception.*;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        var newOrderHeader = this.parseOrderHeaderRequest(addOrderRequest);

        // This will process order and generate bill accordingly
        billManager.generateCorrespondingBills(newOrderHeader);

        var savedOrderHeader = orderHeaderRepository.save(newOrderHeader);

        return this.createOrderHeaderResponse(savedOrderHeader);
    }

    public OrderHeaderResponse getOrderById(User user, Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!orderHeader.getParticipatingUsers().contains(user) && !Objects.equals(user.getId(), orderHeader.getBuyer().getId()))
            throw new UnauthorizedRequestException("This order doesn't include you!", false);

        return this.createOrderHeaderResponse(orderHeader);
    }

    @Override
    public OrderHeaderResponse deleteOrder(User user, Long orderHeaderId) {
        var orderToDelete = orderHeaderRepository
                .findById(orderHeaderId)
                .orElseThrow(() -> new OrderNotFoundException(orderHeaderId));

        if (!Objects.equals(user.getId(), orderToDelete.getBuyer().getId()))
            throw new UnauthorizedRequestException("This order doesn't belongs to you!", false);

        if (orderToDelete.getValidity() == OrderHeaderValidity.ACTIVE
                && orderToDelete.getBillList().stream().anyMatch(bill -> bill.getBillTransactionHeaderList().size() > 0)
        ) throw new IllegalActionException("Cannot delete active order that has bill transaction");

        orderHeaderRepository.delete(orderToDelete);

        return this.createOrderHeaderResponse(orderToDelete);
    }

    @Override
    public void approveOrder(User user, Long orderHeaderId) {
        var orderToUpdate = orderHeaderRepository
                .findById(orderHeaderId)
                .orElseThrow(() -> new OrderNotFoundException(orderHeaderId));

        if (!Objects.equals(user.getId(), orderToUpdate.getBuyer().getId()))
            throw new UnauthorizedRequestException("This order doesn't belongs to you!", false);

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
                                .deletable(order.isDeletable())
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
                                .deletable(order.isDeletable())
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

    @Override
    public AddOrderRequest getOrderJsonDataById(User user, Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!orderHeader.getParticipatingUsers().contains(user) && !Objects.equals(user.getId(), orderHeader.getBuyer().getId()))
            throw new UnauthorizedRequestException("This order doesn't include you!", false);

        var result = orderHeader.getOrderDetailList().stream()
                .collect(Collectors.groupingBy(OrderDetail::getGroupOrderReferenceId))
                .values().stream()
                .map(groupOrderDetail -> {
                    var usersCount = groupOrderDetail.size();

                    var qty = groupOrderDetail.get(0).getQty();
                    var price = usersCount == 1
                            ? groupOrderDetail.get(0).getPrice()
                            : groupOrderDetail.stream()
                            .map(OrderDetail::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(qty), 0, RoundingMode.HALF_UP);
                    var orderMenuDesc = groupOrderDetail.get(0).getOrderMenuDesc();

                    var users = groupOrderDetail.stream()
                            .map(OrderDetail::getUser)
                            .toList();

                    return OrderDetailRequest.builder()
                            .qty(qty)
                            .orderMenuDesc(orderMenuDesc)
                            .users(users)
                            .price(price)
                            .build();
                })
                .toList();

        return AddOrderRequest.builder()
                .orderAt(orderHeader.getOrderAt())
                .orderDescription(orderHeader.getOrderDescription())
                .buyerId(orderHeader.getBuyer().getId())
                .upto(orderHeader.getUpto())
                .discount(orderHeader.getDiscount() * 100)
                .totalPayment(orderHeader.getTotalPayment())
                .orderList(result)
                .build();
    }

    private OrderHeaderResponse createOrderHeaderResponse(OrderHeader orderHeader) {
        var orderDetailListGroupByUser = orderHeader.getOrderDetailList()
                .stream()
                .collect(Collectors.groupingBy(OrderDetail::getUser));

        return OrderHeaderResponse
                .template(orderHeader)
                .deletable(orderHeader.isDeletable())
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
                                            .toList()
                                    )
                                    .build();
                        })
                        .toList()
                )
                .billResponse(orderHeader.getBillList()
                        .stream()
                        .map(BillResponse::map)
                        .toList()
                )
                .build();
    }

    private OrderHeader parseOrderHeaderRequest(AddOrderRequest request) {
        var missingProperties = request.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        User user = userManager.getUserByUserId(request.getBuyerId());

        OrderHeader orderHeader = request.toOriginalEntity();

        var invalidPropertiesValue = new InvalidPropertiesValue();
        if (orderHeader.getOrderDescription().length() < 3 || orderHeader.getOrderDescription().length() > 50)
            invalidPropertiesValue.addInvalidProperty(
                    "order_description",
                    "Order description should only consist of 3 to 50 characters"
            );

        if (orderHeader.getTotalPayment().compareTo(BigDecimal.ZERO) <= 0)
            invalidPropertiesValue.addInvalidProperty(
                    "total_payment",
                    "Total payment should be more than 0"
            );

        if (orderHeader.getUpto().compareTo(BigDecimal.ZERO) < 0)
            invalidPropertiesValue.addInvalidProperty(
                    "upto",
                    "Upto should be equals or more than 0"
            );

        if (orderHeader.getDiscount() < 0 || orderHeader.getDiscount() > 1)
            invalidPropertiesValue.addInvalidProperty(
                    "discount",
                    "Discount should in range of 0 to 100"
            );

        if (invalidPropertiesValue.isThereInvalidProperties())
            throw invalidPropertiesValue;

        orderHeader.setOrderDetailList(
                request.getOrderList()
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

        if (orderHeader.getOtherFee().compareTo(BigDecimal.ZERO) < 0) {
            invalidPropertiesValue.addInvalidProperty("", "Incorrect input resulting negative value");
            throw invalidPropertiesValue;
        }

        return orderHeader;
    }

}
