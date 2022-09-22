package net.arville.easybill.service;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.OrderHeaderResponse;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.model.User;
import net.arville.easybill.repository.OrderHeaderRepository;
import net.arville.easybill.repository.UserRepository;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServices {

    private OrderHeaderRepository orderHeaderRepository;
    private UserRepository userRepository;

    public List<OrderHeader> getOrders() {
        return orderHeaderRepository.findAll();
    }

    public OrderHeaderResponse addNewOrder(AddOrderRequest addOrderRequest) {

        if (!addOrderRequest.isAllPresent()) {
            throw new MissingRequiredPropertiesException();
        }

        Optional<User> userOptional = userRepository.findById(addOrderRequest.getBuyerId());

        if (userOptional.isEmpty()) throw new UserNotFoundException();

        User user = userOptional.get();
        OrderHeader orderHeader = addOrderRequest.toOriginalEntity();
        orderHeader.setOrderDetailList(
                addOrderRequest.getOrderList()
                        .stream()
                        .map(orderDetailRequest -> {
                            Optional<User> orderByOptional = userRepository.findById(orderDetailRequest.getUserId());
                            if (orderByOptional.isEmpty()) throw new UserNotFoundException();
                            OrderDetail orderDetail = orderDetailRequest.toOriginalEntity();
                            orderDetail.setUser(orderByOptional.get());
                            return orderDetail;
                        })
                        .collect(Collectors.toList())
        );
        orderHeader.setUser(user);
        user.getOrderList().add(orderHeader);


        return OrderHeaderResponse.map(orderHeaderRepository.save(orderHeader));
    }

    public OrderHeaderResponse getOrderById(Long orderId) {
        var result = orderHeaderRepository.findById(orderId);
        if (result.isEmpty())
            throw new OrderNotFoundException();
        return OrderHeaderResponse.map(result.get());
    }
}
