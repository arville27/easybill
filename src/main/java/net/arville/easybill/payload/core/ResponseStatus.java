package net.arville.easybill.payload.core;

import lombok.AllArgsConstructor;
import net.arville.easybill.payload.GeneralOutput;

@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS("Success"),
    NOT_FOUND("Resource not found"),
    USER_NOT_FOUND("User is doesn't exists"),
    PARSE_ERROR("Please check if request contain a valid body"),
    UNAUTHORIZED("Unauthorized access"),
    JWT_VERIFICATION_ERROR("Error occur on JWT verification process"),
    ORDER_NOT_FOUND("Order is doesn't exists"),
    USERNAME_ALREADY_EXISTS("Username is already exists"),
    MISSING_REQUIRED_FIELDS("Please verify all required fields is present"),
    INVALID_FIELDS_VALUE("Please verify all fields is correct"),
    UNKNOWN_ERROR("Internal server error, please try again later"),
    METHOD_NOT_ALLOWED("Method not allowed"),
    INVALID_CREDENTIALS("Please verify provided credentials are valid");

    private final String message;

    public <T extends OutputStructure<?>> ResponseStructure GenerateBody(T outputStructure) {
        StatusStructure statusStructure = new StatusStructure(this, this.message);
        return new ResponseStructure(statusStructure, outputStructure);
    }

    public <T extends OutputStructure<?>> ResponseStructure GenerateBody(T outputStructure, String extraMessage) {
        StatusStructure statusStructure = new StatusStructure(this, this.message, extraMessage);
        return new ResponseStructure(statusStructure, outputStructure);
    }

    public ResponseStructure GenerateGeneralBody(Object data) {
        return this.GenerateBody(new GeneralOutput(data));
    }

    public ResponseStructure GenerateGeneralBody(Object data, String extraMessage) {
        return this.GenerateBody(new GeneralOutput(data), extraMessage);
    }
}
