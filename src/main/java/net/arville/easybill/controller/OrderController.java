package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.OrderManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderManager orderManager;

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseStructure> getOrder(@PathVariable Long orderId) {

        var order = orderManager.getOrderById(orderId);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(order);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> addNewOrder(@RequestBody AddOrderRequest request) {

        var newOrderHeader = orderManager.addNewOrder(request);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(newOrderHeader);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
