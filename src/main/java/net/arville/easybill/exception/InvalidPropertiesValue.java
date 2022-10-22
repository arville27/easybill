package net.arville.easybill.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InvalidPropertiesValue extends RuntimeException {
    private final Map<String, String> invalidPropertiesMessage;

    public InvalidPropertiesValue() {
        this.invalidPropertiesMessage = new HashMap<>();
    }

    public void addInvalidProperty(String propertyName, String message) {
        this.invalidPropertiesMessage.put(propertyName, message);
    }

    public boolean isThereInvalidProperties() {
        return !this.invalidPropertiesMessage.isEmpty();
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
