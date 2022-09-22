package net.arville.easybill.exception;

import java.util.Set;

public class MissingRequiredPropertiesException extends org.springframework.core.env.MissingRequiredPropertiesException {

    public MissingRequiredPropertiesException(Set<String> missingProperties) {
        super();
        super.getMissingRequiredProperties().addAll(missingProperties);
    }

}
