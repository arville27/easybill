package net.arville.easybill.service;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.OrderRequest;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.repository.OrderDetailRepository;
import net.arville.easybill.repository.OrderHeaderRepository;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@AllArgsConstructor
public class OrderServices {

    public OrderHeaderRepository orderHeaderRepository;
    public OrderDetailRepository orderDetailRepository;

    public List<OrderHeader> getOrders() {
        return orderHeaderRepository.findAll();
    }

    public OrderHeader addNewOrder(OrderRequest orderHeader) {

        if (!orderHeader.isAllPresent()) {
            throw new MissingRequiredPropertiesException();
        }

        return orderHeaderRepository.save(orderHeader.toOriginalEntity());
    }

    public OrderHeader getOrderById(Long orderId) {
        var result = orderHeaderRepository.findById(orderId);
        if (result.isEmpty())
            throw new OrderNotFoundException();
        return result.get();
    }
}
