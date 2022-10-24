package net.arville.easybill.payload.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class OutputStructure<T> {
    public T data;

    abstract public T getData();

    abstract public void setData(T data);
}
