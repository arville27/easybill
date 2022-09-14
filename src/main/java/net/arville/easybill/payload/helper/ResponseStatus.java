package net.arville.easybill.payload.helper;

import lombok.AllArgsConstructor;
import net.arville.easybill.payload.GeneralOutput;
import net.arville.easybill.payload.OutputStructure;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.StatusStructure;

@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS("SUCCESS", "Success"),
    NOT_FOUND("NOT_FOUND", "Resource not found"),
    UNKNOWN_ERROR("SERVER_ERROR", "Internal server error, please try again later");

    private final String code;
    private final String message;

    public <T extends OutputStructure<?>> ResponseStructure GenerateBody(T outputStructure) {
        StatusStructure statusStructure = new StatusStructure(this.code, this.message);
        return new ResponseStructure(statusStructure, outputStructure);
    }

    public ResponseStructure GenerateGeneralBody(Object data) {
        return this.GenerateBody(new GeneralOutput(data));
    }
}
