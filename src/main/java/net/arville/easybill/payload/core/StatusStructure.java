package net.arville.easybill.payload.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatusStructure {
    private ResponseStatus code;
    private String message;
    private String extraMessage;

    public StatusStructure(ResponseStatus code, String message) {
        this.code = code;
        this.message = message;
    }
}
