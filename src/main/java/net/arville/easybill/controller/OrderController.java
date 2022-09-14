package net.arville.easybill.controller;

import lombok.AllArgsConstructor;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.service.OrderServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    public OrderServices orderServices;

    @GetMapping("/test")
    public ResponseEntity<ResponseStructure> getOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
