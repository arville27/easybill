package net.arville.easybill.dto.helper;

public interface EntityBuilder<X, Y, Z> {

    X createCustomEntity(Y entityBuilder, Z entity);

}
