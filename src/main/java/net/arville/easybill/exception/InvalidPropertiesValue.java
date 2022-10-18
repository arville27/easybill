package net.arville.easybill.exception;

import java.util.Map;
import java.util.stream.Collectors;

public class InvalidPropertiesValue extends RuntimeException {

    private Map<String, String> invalidPropertiesMessage;

    public InvalidPropertiesValue(Map<String, String> invalidPropertiesMessage) {
        this.invalidPropertiesMessage = invalidPropertiesMessage;
    }

    @Override
    public String getMessage() {
        var invalidPropertiesMessage = this.invalidPropertiesMessage
                .entrySet()
                .stream()
                .map(propertiesMessageSet -> propertiesMessageSet.getKey() + ": " + propertiesMessageSet.getValue())
                .collect(Collectors.joining(", "));
        return "The following properties were invalid: [" + invalidPropertiesMessage + "]";
    }
}
