package net.arville.easybill.dto.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

public interface EnsureRequiredFields {

    @JsonIgnore
    Set<String> getMissingProperties();
}
