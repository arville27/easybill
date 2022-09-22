package net.arville.easybill.dto.util;

import java.util.Set;

public interface EnsureRequiredFields {

    Set<String> getMissingProperties();
}
