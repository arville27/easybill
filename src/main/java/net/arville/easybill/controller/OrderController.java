package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.manager.OrderManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User's orders", description = "User order related resource")
@SecurityRequirement(name = "Access Token")
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
