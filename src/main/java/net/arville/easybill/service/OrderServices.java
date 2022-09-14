package net.arville.easybill.service;

import net.arville.easybill.model.OrderDetail;
import net.arville.easybill.repository.OrderDetailRepository;
import net.arville.easybill.repository.OrderHeaderRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class OrderServices {

    public OrderHeaderRepository orderHeaderRepository;
    public OrderDetailRepository orderDetailRepository;

    public List<OrderDetail> getOrders() {
        return List.of(new OrderDetail(), new OrderDetail());
    }

}
