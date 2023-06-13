package net.arville.easybill.repository.helper;

import net.arville.easybill.exception.IllegalPageNumberException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PageableBuilder {
    private final List<String> sortFields;
    private Integer pageSize;
    private Integer pageNumber;
    private Sort.Direction sortType;

    private PageableBuilder() {
        this.sortFields = new ArrayList<>();
        this.pageSize = 10;
        this.pageNumber = 0;
        this.sortType = Sort.DEFAULT_DIRECTION;
    }

    public static PageableBuilder builder() {
        return new PageableBuilder();
    }

    public Pageable build() {
        if (sortFields.isEmpty())
            return PageRequest.of(this.pageNumber, this.pageSize);
        String[] fields = new String[sortFields.size()];
        sortFields.toArray(fields);
        return PageRequest.of(pageNumber, pageSize, sortType, fields);
    }

    public PageableBuilder setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageableBuilder setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber - 1;
        if (this.pageNumber < 0) {
            throw new IllegalPageNumberException("The page number should be more than 0!");
        }
        return this;
    }

    public PageableBuilder addSortField(List<String> sortFields) {
        this.sortFields.addAll(sortFields);
        return this;
    }

    public PageableBuilder addSortField(String field) {
        this.sortFields.add(field);
        return this;
    }

    public PageableBuilder setSortType(Sort.Direction sortType) {
        this.sortType = sortType;
        return this;
    }
}