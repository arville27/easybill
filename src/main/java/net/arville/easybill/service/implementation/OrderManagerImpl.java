package net.arville.easybill.service.implementation;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.response.OrderDetailResponse;
import net.arville.easybill.dto.response.OrderHeaderResponse;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.UserRepository;
import net.arville.easybill.service.manager.BillManager;
import net.arville.easybill.service.manager.OrderManager;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderManagerImpl implements OrderManager {

    private final OrderHeaderRepository orderHeaderRepository;
    private final UserRepository userRepository;
    private final BillManager billManager;

    public OrderHeaderResponse addNewOrder(AddOrderRequest addOrderRequest) {

        var missingProperties = addOrderRequest.getMissingProperties();

        if (missingProperties.size() > 0) {
            throw new MissingRequiredPropertiesException(missingProperties);
        }

        User user = userRepository
                .findById(addOrderRequest.getBuyerId())
                .orElseThrow(() -> new UserNotFoundException(addOrderRequest.getBuyerId()));

        OrderHeader orderHeader = addOrderRequest.toOriginalEntity();
        orderHeader.setOrderDetailList(
                addOrderRequest.getOrderList()
                        .stream()
                        .map(orderDetailRequest -> {
                            User orderBy = userRepository
                                    .findById(orderDetailRequest.getUserId())
                                    .orElseThrow(() -> new UserNotFoundException(addOrderRequest.getBuyerId()));
                            OrderDetail orderDetail = orderDetailRequest.toOriginalEntity();
                            orderDetail.setUser(orderBy);
                            return orderDetail;
                        })
                        .collect(Collectors.toList())
        );
        orderHeader.setUser(user);
        user.getOrderList().add(orderHeader);

        var savedOrderHeader = orderHeaderRepository.save(orderHeader);

        // This will process order and generate bill accordingly
        billManager.generateBillsFromOrderHeader(orderHeader);

        return OrderHeaderResponse.customMap(savedOrderHeader,
                (entityBuilder, entity) ->
                        entityBuilder
                                .id(entity.getId())
                                .buyerId(entity.getUser().getId())
                                .orderDetailResponses(entity
                                        .getOrderDetailList().stream()
                                        .map(OrderDetailResponse::map)
                                        .peek(orderDetailResponse -> {
                                            orderDetailResponse.setUserId(orderDetailResponse.getUserData().getId());
                                            orderDetailResponse.setUserData(null);
                                        })
                                        .collect(Collectors.toList())
                                )
                                .upto(entity.getUpto())
                                .discount(entity.getDiscount())
                                .orderDescription(entity.getOrderDescription())
                                .totalPayment(entity.getTotalPayment())
                                .orderAt(entity.getOrderAt())
                                .createdAt(entity.getCreatedAt())
                                .updatedAt(entity.getUpdatedAt())
                                .build()
        );
    }

    public OrderHeaderResponse getOrderById(Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderHeaderResponse.map(orderHeader);
    }
}
