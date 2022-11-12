package net.arville.easybill.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.arville.easybill.payload.core.OutputStructure;

@AllArgsConstructor
@Getter
@Setter
public class PaginationOutput<T> extends OutputStructure<T> {

    private int page;

    private int pageSize;

    private long totalItems;

    private int totalPages;

    @Builder
    public PaginationOutput(T data, int page, int pageSize, long totalItems, int totalPages) {
        super(data);
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    @Override
    public T getData() {
        return super.data;
    }

    @Override
    public void setData(T data) {
        super.data = data;
    }

}
