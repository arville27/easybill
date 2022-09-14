package net.arville.easybill.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatusStructure {

    private String code;
    private String message;

}
