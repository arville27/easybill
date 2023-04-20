package net.arville.easybill.payload.core;

import lombok.AllArgsConstructor;
import net.arville.easybill.dto.response.PaginationResponse;
import net.arville.easybill.payload.GeneralOutput;
import net.arville.easybill.payload.PaginationOutput;

@AllArgsConstructor
public enum ResponseStatus {
    SUCCESS("Success"),
    NOT_FOUND("Resource not found"),
    USER_NOT_FOUND("User is doesn't exists"),
    PAYMENT_ACCOUNT_DOES_NOT_EXISTS("Payment account is doesn't exists"),
    PAYMENT_ACCOUNT_ALREADY_EXISTS("Payment account is already exists"),
    PARSE_ERROR("Please check if request contain a valid body"),
    UNAUTHORIZED_REQUEST("Unauthorized access"),
    UNAUTHORIZED_RESOURCE_ACCESS("Unauthorized user access, you don't have permission to access this resource"),
    JWT_VERIFICATION_ERROR("Error occur on JWT verification process"),
    ORDER_NOT_FOUND("Order is doesn't exists"),
    USERNAME_ALREADY_EXISTS("Username is already exists"),
    MISSING_REQUIRED_FIELDS("Please verify all required fields is present"),
    INVALID_FIELDS_VALUE("Please verify all fields is correct"),
    UNKNOWN_ERROR("Internal server error, please try again later"),
    METHOD_NOT_ALLOWED("Method not allowed"),
    INVALID_CREDENTIALS("Please verify provided credentials are valid"),
    ILLEGAL_ACTION("Action is not permitted");

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

    public ResponseStructure GeneratePaginationBody(PaginationResponse<?> paginationResponse) {
        var body = PaginationOutput.builder()
                .data(paginationResponse.getData())
                .pageSize(paginationResponse.getPageSize())
                .page(paginationResponse.getPage())
                .totalItems(paginationResponse.getTotalItems())
                .totalPages(paginationResponse.getTotalPages())
                .build();
        return this.GenerateBody(body);
    }
}
