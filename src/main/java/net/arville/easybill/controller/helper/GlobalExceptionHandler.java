package net.arville.easybill.controller.helper;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.arville.easybill.exception.MissingRequiredPropertiesException;
import net.arville.easybill.exception.OrderNotFoundException;
import net.arville.easybill.exception.UserNotFoundException;
import net.arville.easybill.exception.UsernameAlreadyExists;
import net.arville.easybill.payload.ResponseStructure;
import net.arville.easybill.payload.helper.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Controller exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseStructure> userNotFoundException(UserNotFoundException e) {
        var body = ResponseStatus.USER_NOT_FOUND.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ResponseStructure> orderNotFoundException(OrderNotFoundException e) {
        var body = ResponseStatus.ORDER_NOT_FOUND.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MissingRequiredPropertiesException.class)
    public ResponseEntity<ResponseStructure> missingRequiredPropertiesException(MissingRequiredPropertiesException e) {
        var body = ResponseStatus.ORDER_NOT_FOUND.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ResponseEntity<ResponseStructure> usernameAlreadyExistsException(UsernameAlreadyExists e) {
        var body = ResponseStatus.USERNAME_ALREADY_EXISTS.GenerateGeneralBody(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Filter exception
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ResponseStructure> jwtVerificationException(JWTVerificationException e) {
        var body = ResponseStatus.JWT_VERIFICATION_ERROR.GenerateGeneralBody(null, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // General exception
    @ExceptionHandler(Exception.class)
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
