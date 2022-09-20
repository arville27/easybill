package net.arville.easybill.dto.util;

public interface ConvertibleFromOriginalEntitiy<K,V> {

    K fromOriginalEntity(V entity);

}
