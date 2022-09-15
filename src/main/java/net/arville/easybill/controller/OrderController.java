package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.OrderRequest;
import net.arville.easybill.model.OrderHeader;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import net.arville.easybill.service.OrderNotFoundException;
import net.arville.easybill.service.OrderServices;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    public OrderServices orderServices;

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseStructure> getOrder(@PathVariable Long orderId) {
        ResponseStructure body;

        try {
            var order = orderServices.getOrderById(orderId);
            body = ResponseStatus.SUCCESS.GenerateGeneralBody(order);
        } catch (OrderNotFoundException e) {
            body = ResponseStatus.NOT_FOUND.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            body = ResponseStatus.UNKNOWN_ERROR.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
    
    @PostMapping
    public ResponseEntity<ResponseStructure> addNewOrder(@RequestBody OrderRequest request) {
        ResponseStructure body;

        try {
            OrderHeader newOrderHeader = orderServices.addNewOrder(request);
            body = ResponseStatus.SUCCESS.GenerateGeneralBody(newOrderHeader);
        } catch (MissingRequiredPropertiesException e) {
            body = ResponseStatus.MISSING_REQUIRED_FIELDS.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            body = ResponseStatus.UNKNOWN_ERROR.GenerateGeneralBody(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
