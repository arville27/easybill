package net.arville.easybill.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class PaginationResponse<T> {
    
    private int page;

    private int pageSize;

    private long totalItems;

    private int totalPages;

    private T data;
}
