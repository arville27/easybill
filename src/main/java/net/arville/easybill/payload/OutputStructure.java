package net.arville.easybill.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class OutputStructure<T> {
    T data;

    abstract public T getData();

    abstract public void setData(T data);
}
