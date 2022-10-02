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
    USER_NOT_FOUND("USER_NOT_FOUND", "User is doesn't exists"),
    PARSE_ERROR("PARSE_ERROR", "Please check if request contain a valid body"),
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized access"),
    JWT_VERIFICATION_ERROR("JWT_VERIFICATION_ERROR", "Error occur on JWT verification process"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Order is doesn't exists"),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "Username is already exists"),
    MISSING_REQUIRED_FIELDS("BAD REQUEST", "Please verify all required fields is present"),
    UNKNOWN_ERROR("SERVER_ERROR", "Internal server error, please try again later"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "Method not allowed");

    private final String code;
    private final String message;

    public <T extends OutputStructure<?>> ResponseStructure GenerateBody(T outputStructure) {
        StatusStructure statusStructure = new StatusStructure(this.code, this.message);
        return new ResponseStructure(statusStructure, outputStructure);
    }

    public <T extends OutputStructure<?>> ResponseStructure GenerateBody(T outputStructure, String extraMessage) {
        StatusStructure statusStructure = new StatusStructure(this.code, this.message, extraMessage);
        return new ResponseStructure(statusStructure, outputStructure);
    }

    public ResponseStructure GenerateGeneralBody(Object data) {
        return this.GenerateBody(new GeneralOutput(data));
    }

    public ResponseStructure GenerateGeneralBody(Object data, String extraMessage) {
        return this.GenerateBody(new GeneralOutput(data), extraMessage);
    }
}
