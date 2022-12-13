package net.arville.easybill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.arville.easybill.dto.request.AddOrderRequest;
import net.arville.easybill.helper.AuthenticatedUser;
import net.arville.easybill.payload.core.ResponseStatus;
import net.arville.easybill.payload.core.ResponseStructure;
import net.arville.easybill.service.manager.OrderManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User's orders", description = "User order related resource")
@SecurityRequirement(name = "Access Token")
@RequiredArgsConstructor
public class OrderController {
    private final OrderManager orderManager;

    private final AuthenticatedUser authenticatedUser;

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseStructure> getOrder(@PathVariable Long orderId) {

        var order = orderManager.getOrderById(orderId);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(order);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/reorder/{orderId}")
    public ResponseEntity<ResponseStructure> getOrderJsonData(@PathVariable Long orderId) {

        var orderJson = orderManager.getOrderJsonDataById(orderId);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(orderJson);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/pending-orders")
    public ResponseEntity<ResponseStructure> getPendingOrders() {

        var orders = orderManager.getAllPendingOrder(authenticatedUser.getUser());
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(orders);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/relevant-orders")
    public ResponseEntity<ResponseStructure> getRelevantOrders(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "status", required = false) String orderStatus
    ) {

        var user = orderManager.getUserRelevantOrder(
                authenticatedUser.getUser(),
                pageNumber,
                pageSize,
                keyword,
                orderStatus
        );
        ResponseStructure body = ResponseStatus.SUCCESS.GeneratePaginationBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping("/users-orders")
    public ResponseEntity<ResponseStructure> getUsersOrders(
            @RequestParam(name = "page", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(name = "status", required = false) String orderStatus
    ) {

        var user = orderManager.getUsersOrder(
                authenticatedUser.getUser(),
                pageNumber,
                pageSize,
                keyword,
                orderStatus
        );
        ResponseStructure body = ResponseStatus.SUCCESS.GeneratePaginationBody(user);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping
    public ResponseEntity<ResponseStructure> addNewOrder(@RequestBody AddOrderRequest request) {

        var newOrderHeader = orderManager.addNewOrder(request);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(newOrderHeader);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/{orderHeaderId}/validity")
    public ResponseEntity<ResponseStructure> approveOrderHeader(@PathVariable Long orderHeaderId) {

        orderManager.approveOrder(authenticatedUser.getUser(), orderHeaderId);

        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @DeleteMapping("{orderHeaderId}")
    public ResponseEntity<ResponseStructure> deleteOrder(@PathVariable Long orderHeaderId) {

        var deletedOrder = orderManager.deleteOrder(orderHeaderId);
        ResponseStructure body = ResponseStatus.SUCCESS.GenerateGeneralBody(deletedOrder);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
