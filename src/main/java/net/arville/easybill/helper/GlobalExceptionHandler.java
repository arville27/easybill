package net.arville.easybill.helper;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.arville.easybill.exception.*;
import net.arville.easybill.payload.core.ResponseStatus;
import net.arville.easybill.payload.core.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Controller exception
    @ExceptionHandler(UserNotFoundException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> userNotFoundException(UserNotFoundException e) {
        var body = ResponseStatus.USER_NOT_FOUND.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> orderNotFoundException(OrderNotFoundException e) {
        var body = ResponseStatus.ORDER_NOT_FOUND.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MissingRequiredPropertiesException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> missingRequiredPropertiesException(MissingRequiredPropertiesException e) {
        var body = ResponseStatus.MISSING_REQUIRED_FIELDS.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> usernameAlreadyExistsException(UsernameAlreadyExists e) {
        var body = ResponseStatus.USERNAME_ALREADY_EXISTS.GenerateGeneralBody(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.UNAUTHORIZED)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> invalidCredentialsException(InvalidCredentialsException e) {
        var body = ResponseStatus.INVALID_CREDENTIALS.GenerateGeneralBody(null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidPropertiesValue.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> invalidPropertiesValue(InvalidPropertiesValue e) {
        var body = ResponseStatus.INVALID_FIELDS_VALUE.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalPageNumberException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> invalidPageNumberValue(IllegalPageNumberException e) {
        var body = ResponseStatus.INVALID_FIELDS_VALUE.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Filter exception
    @ExceptionHandler(JWTVerificationException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> jwtVerificationException(JWTVerificationException e) {
        var body = ResponseStatus.JWT_VERIFICATION_ERROR.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.UNAUTHORIZED)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> unauthorizedRequestException(UnauthorizedRequestException e) {
        var body = e.isForceLogoutUser()
                ? ResponseStatus.UNAUTHORIZED_REQUEST.GenerateGeneralBody(null, e.getMessage())
                : ResponseStatus.UNAUTHORIZED_RESOURCE_ACCESS.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // General exception
    @ExceptionHandler(Exception.class)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseStructure> generalErrorException(Exception e) {
        ResponseStructure body = ResponseStatus.UNKNOWN_ERROR.GenerateGeneralBody(null);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (e instanceof HttpMessageNotReadableException || e instanceof JsonMappingException) {
            body = ResponseStatus.PARSE_ERROR.GenerateGeneralBody(null);
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            body = ResponseStatus.METHOD_NOT_ALLOWED.GenerateGeneralBody(null);
            status = HttpStatus.METHOD_NOT_ALLOWED;
        } else if (e instanceof NoHandlerFoundException) {
            body = ResponseStatus.NOT_FOUND.GenerateGeneralBody(null);
            status = HttpStatus.NOT_FOUND;
        } else {
            e.printStackTrace();
        }

        return ResponseEntity.status(status).body(body);
    }


}
