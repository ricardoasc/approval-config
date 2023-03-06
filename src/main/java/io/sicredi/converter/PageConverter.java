package io.sicredi.converter;

import io.sicredi.dto.PageDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageConverter {
    public static PageDTO convert(
            Long totalElements,
            Integer page,
            Integer size,
            Sort sortDirection,
            String sortedBy,
            List<?> content) {
        int totalPages = calculateTotalPages(size, totalElements);
        return new PageDTO(totalElements, page, size, totalPages, sortDirection, sortedBy, content);
    }

    private static int calculateTotalPages(Integer size, Long totalElements) {
        return (int) ((totalElements + (size - 1)) / size);
    }

}
